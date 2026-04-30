package com.anordine.completions.api.webflux.helper.history;

import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.message.CompletionUserMessage;
import com.anordine.completions.api.webflux.model.message.abs.CompletionMessage;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryHistoryManagerTest {

    @Test
    void loadChatAndGetChatUseDefensiveCopies() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        CompletionRequest request = new CompletionRequest()
                .withModel("gpt-5.4")
                .addDeveloperMessage("Be concise")
                .addUserMessage("Hello");

        historyManager.loadChat("chat-1", request).block();
        request.addUserMessage("Changed after load");

        CompletionRequest loaded = historyManager.getChat("chat-1").block();
        assertNotNull(loaded);
        assertEquals("gpt-5.4", loaded.getModel());
        assertEquals(2, loaded.getMessages().size());

        loaded.addUserMessage("Changed after get");

        CompletionRequest loadedAgain = historyManager.getChat("chat-1").block();
        assertNotNull(loadedAgain);
        assertEquals(2, loadedAgain.getMessages().size());
    }

    @Test
    void addMessageAppendsCloneAndReturnsUpdatedChat() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.loadChat("chat-1", new CompletionRequest()
                .withModel("gpt-5.4")
                .addDeveloperMessage("Be concise")).block();

        CompletionUserMessage message = new CompletionUserMessage("Hello");
        CompletionRequest updated = historyManager.addMessage("chat-1", message).block();
        message.setContent("Changed after add");

        assertNotNull(updated);
        assertMessages(updated, List.of("Be concise", "Hello"));

        CompletionRequest loaded = historyManager.getChat("chat-1").block();
        assertNotNull(loaded);
        assertMessages(loaded, List.of("Be concise", "Hello"));
    }

    @Test
    void addMessageForMissingChatFails() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Exception exception = assertThrows(
                NoSuchElementException.class,
                () -> historyManager.addMessage("missing", new CompletionUserMessage("Hello")).block()
        );

        assertEquals("chat not found: missing", exception.getMessage());
    }

    @Test
    void evictRemovesChat() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.loadChat("chat-1", new CompletionRequest()
                .withModel("gpt-5.4")
                .addUserMessage("Hello")).block();

        historyManager.evict("chat-1").block();

        Exception exception = assertThrows(
                NoSuchElementException.class,
                () -> historyManager.getChat("chat-1").block()
        );

        assertEquals("chat not found: chat-1", exception.getMessage());
    }

    @Test
    void concurrentAppendsDoNotLoseMessages() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.loadChat("chat-1", new CompletionRequest()
                .withModel("gpt-5.4")).block();

        Flux.range(0, 200)
                .flatMap(index -> historyManager.addMessage(
                        "chat-1",
                        new CompletionUserMessage("message-" + index)
                ), 32)
                .then()
                .block();

        CompletionRequest loaded = historyManager.getChat("chat-1").block();
        assertNotNull(loaded);
        assertEquals(200, loaded.getMessages().size());
    }

    private void assertMessages(CompletionRequest request, List<String> contents) {
        assertNotNull(request.getMessages());
        assertEquals(contents.size(), request.getMessages().size());
        for (int i = 0; i < contents.size(); i++) {
            CompletionMessage message = request.getMessages().get(i);
            assertInstanceOf(CompletionMessage.class, message);
            assertEquals(contents.get(i), message.getContent());
        }
    }
}
