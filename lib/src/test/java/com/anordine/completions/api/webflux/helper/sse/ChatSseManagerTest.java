package com.anordine.completions.api.webflux.helper.sse;

import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatSseManagerTest {

    @Test
    void emitMessagePublishesChatMessageEvent() {
        ChatSseManager manager = new ChatSseManager(Duration.ofHours(1), 8);
        UUID chatId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        List<ServerSentEvent<SseEventMessage>> events = new ArrayList<>();

        Disposable subscription = manager.createSseStream(chatId).subscribe(events::add);

        try {
            assertEquals(Sinks.EmitResult.OK, manager.emitMessage(
                    chatId,
                    messageId,
                    "hello",
                    CompletionRole.ASSISTANT
            ));

            assertEquals(1, events.size());
            ServerSentEvent<SseEventMessage> event = events.getFirst();
            SseEventMessage message = event.data();

            assertEquals(EventType.CHAT_MESSAGE.name(), event.event());
            assertNotNull(message);
            assertEquals(messageId, message.getId());
            assertEquals(chatId, message.getChatId());
            assertEquals("hello", message.getContent());
            assertEquals(CompletionRole.ASSISTANT, message.getRole());
            assertEquals(EventType.CHAT_MESSAGE, message.getEventType());
        } finally {
            subscription.dispose();
            manager.shutdown();
        }
    }

    @Test
    void setPendingEmitsInitialTypingEventForNewSubscribers() {
        ChatSseManager manager = new ChatSseManager(Duration.ofHours(1), 8);
        UUID chatId = UUID.randomUUID();
        List<ServerSentEvent<SseEventMessage>> events = new ArrayList<>();

        manager.setPending(chatId, true);
        Disposable subscription = manager.createSseStream(chatId).subscribe(events::add);

        try {
            assertEquals(1, events.size());
            assertEquals(EventType.TYPING.name(), events.getFirst().event());
            assertTrue(manager.get(chatId).isPending());
        } finally {
            subscription.dispose();
            manager.shutdown();
        }
    }

    @Test
    void disposeLastSubscriberCompletesAndRemovesIdleStream() {
        ChatSseManager manager = new ChatSseManager(Duration.ofHours(1), 8);
        UUID chatId = UUID.randomUUID();

        Disposable subscription = manager.createSseStream(chatId).subscribe();
        assertNotNull(manager.get(chatId));

        subscription.dispose();

        assertNull(manager.get(chatId));
    }

    @Test
    void completeRemovesStream() {
        ChatSseManager manager = new ChatSseManager(Duration.ofHours(1), 8);
        UUID chatId = UUID.randomUUID();

        manager.setPending(chatId, true);

        assertNotNull(manager.get(chatId));
        assertTrue(manager.complete(chatId));
        assertNull(manager.get(chatId));
        assertTrue(manager.complete(chatId));
    }

    @Test
    void chatStreamExposesReadOnlyState() {
        ChatSseManager manager = new ChatSseManager(Duration.ofHours(1), 8);
        UUID chatId = UUID.randomUUID();

        manager.setPending(chatId, true);
        ChatStream stream = manager.get(chatId);

        assertNotNull(stream);
        assertEquals(0, stream.getSubscribers());
        assertTrue(stream.isPending());

        manager.setPending(chatId, false);

        assertFalse(stream.isPending());
    }
}
