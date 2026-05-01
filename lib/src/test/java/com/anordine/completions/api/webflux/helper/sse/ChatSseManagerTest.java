package com.anordine.completions.api.webflux.helper.sse;

import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.model.usage.CompletionUsage;
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
    void emitChunkAndDonePublishStreamingEvents() {
        ChatSseManager manager = new ChatSseManager(Duration.ofHours(1), 8);
        UUID chatId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        List<ServerSentEvent<SseEventMessage>> events = new ArrayList<>();

        Disposable subscription = manager.createSseStream(chatId).subscribe(events::add);

        try {
            assertEquals(Sinks.EmitResult.OK, manager.emitMessageStart(chatId, messageId, CompletionRole.ASSISTANT));
            assertEquals(Sinks.EmitResult.OK, manager.emitChunk(chatId, messageId, "Hel", CompletionRole.ASSISTANT));
            assertEquals(Sinks.EmitResult.OK, manager.emitMessageDone(chatId, messageId, "Hello", CompletionRole.ASSISTANT));

            assertEquals(3, events.size());
            assertEquals(EventType.CHAT_MESSAGE_START.name(), events.get(0).event());
            assertEquals(EventType.CHAT_MESSAGE_CHUNK.name(), events.get(1).event());
            assertEquals("Hel", events.get(1).data().getContent());
            assertEquals(EventType.CHAT_MESSAGE_DONE.name(), events.get(2).event());
            assertEquals("Hello", events.get(2).data().getContent());
        } finally {
            subscription.dispose();
            manager.shutdown();
        }
    }

    @Test
    void emitUsagePublishesUsageEvent() {
        ChatSseManager manager = new ChatSseManager(Duration.ofHours(1), Duration.ofSeconds(1), 8, true);
        UUID chatId = UUID.randomUUID();
        List<ServerSentEvent<SseEventMessage>> events = new ArrayList<>();

        Disposable subscription = manager.createSseStream(chatId).subscribe(events::add);

        try {
            assertTrue(manager.isEmitUsageEvents());
            CompletionUsage usage = new CompletionUsage();
            usage.setPromptTokens(3);
            usage.setCompletionTokens(2);
            usage.setTotalTokens(5);

            assertEquals(Sinks.EmitResult.OK, manager.emitUsage(chatId, usage));

            assertEquals(1, events.size());
            assertEquals(EventType.USAGE.name(), events.getFirst().event());
            assertEquals(chatId, events.getFirst().data().getChatId());
            assertEquals(5, events.getFirst().data().getUsage().getTotalTokens());
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
