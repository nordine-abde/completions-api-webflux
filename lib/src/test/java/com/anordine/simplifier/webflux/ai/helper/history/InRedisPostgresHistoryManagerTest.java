package com.anordine.simplifier.webflux.ai.helper.history;

import com.anordine.simplifier.webflux.ai.model.CompletionRequest;
import com.anordine.simplifier.webflux.ai.model.message.CompletionDeveloperMessage;
import com.anordine.simplifier.webflux.ai.model.message.CompletionUserMessage;
import com.anordine.simplifier.webflux.ai.model.message.abs.CompletionMessage;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InRedisPostgresHistoryManagerTest {

    @Test
    void getChatFallsBackToPostgresAndWarmsRedisCache() {
        InMemoryHistoryManager redisCache = new InMemoryHistoryManager();
        Map<String, StoredChat> postgres = new HashMap<>();
        postgres.put("chat-1", new StoredChat("chat-1", new CompletionRequest()
                .withModel("gpt-5.4")
                .addUserMessage("Hello")));

        InRedisPostgresHistoryManager<StoredChat> historyManager = historyManager(redisCache, postgres);

        CompletionRequest loaded = historyManager.getChat("chat-1").block();

        assertNotNull(loaded);
        assertEquals("gpt-5.4", loaded.getModel());
        assertMessages(loaded, List.of("Hello"));

        CompletionRequest cached = redisCache.getChat("chat-1").block();
        assertNotNull(cached);
        assertEquals("gpt-5.4", cached.getModel());
        assertMessages(cached, List.of("Hello"));
    }

    @Test
    void loadGetAndAddUsePostgresWhenRedisFails() {
        Map<String, StoredChat> postgres = new HashMap<>();
        InRedisPostgresHistoryManager<StoredChat> historyManager = historyManager(
                new FailingHistoryManager(),
                postgres
        );

        historyManager.loadChat("chat-1", new CompletionRequest()
                .withModel("gpt-5.4")
                .addDeveloperMessage("Be concise")).block();

        CompletionRequest loaded = historyManager.getChat("chat-1").block();
        assertNotNull(loaded);
        assertMessages(loaded, List.of("Be concise"));

        CompletionRequest updated = historyManager.addMessage("chat-1", new CompletionUserMessage("Hello")).block();

        assertNotNull(updated);
        assertMessages(updated, List.of("Be concise", "Hello"));
        assertMessages(postgres.get("chat-1").requestCopy(), List.of("Be concise", "Hello"));
    }

    @Test
    void addMessageUsesPostgresAsAuthoritativeSourceAndRefreshesRedisCache() {
        InMemoryHistoryManager redisCache = new InMemoryHistoryManager();
        redisCache.loadChat("chat-1", new CompletionRequest()
                .withModel("gpt-5.4")
                .addUserMessage("stale-cache")).block();

        Map<String, StoredChat> postgres = new HashMap<>();
        postgres.put("chat-1", new StoredChat("chat-1", new CompletionRequest()
                .withModel("gpt-5.4")
                .addUserMessage("persistent")));

        InRedisPostgresHistoryManager<StoredChat> historyManager = historyManager(redisCache, postgres);

        CompletionRequest updated = historyManager.addMessage("chat-1", new CompletionUserMessage("new")).block();

        assertNotNull(updated);
        assertMessages(updated, List.of("persistent", "new"));
        assertMessages(postgres.get("chat-1").requestCopy(), List.of("persistent", "new"));
        assertMessages(redisCache.getChat("chat-1").block(), List.of("persistent", "new"));
    }

    @Test
    void loadChatFailsWhenPostgresSaveFailsAndDoesNotWarmRedis() {
        AtomicBoolean redisLoaded = new AtomicBoolean(false);
        IHistoryManager redisCache = new ObservedHistoryManager(redisLoaded);
        InRedisPostgresHistoryManager<StoredChat> historyManager = new InRedisPostgresHistoryManager<>(
                redisCache,
                StoredChat::new,
                StoredChat::requestCopy,
                chatId -> Mono.empty(),
                entity -> Mono.error(new IllegalStateException("postgres down"))
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> historyManager.loadChat("chat-1", new CompletionRequest().withModel("gpt-5.4")).block()
        );

        assertEquals("postgres down", exception.getMessage());
        assertFalse(redisLoaded.get());
    }

    @Test
    void addMessageFailsWhenPostgresSaveFailsAndDoesNotRefreshRedis() {
        AtomicBoolean redisLoaded = new AtomicBoolean(false);
        IHistoryManager redisCache = new ObservedHistoryManager(redisLoaded);
        StoredChat existing = new StoredChat("chat-1", new CompletionRequest()
                .withModel("gpt-5.4")
                .addDeveloperMessage("Be concise"));

        InRedisPostgresHistoryManager<StoredChat> historyManager = new InRedisPostgresHistoryManager<>(
                redisCache,
                StoredChat::new,
                StoredChat::requestCopy,
                chatId -> Mono.just(existing),
                entity -> Mono.error(new IllegalStateException("postgres down"))
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> historyManager.addMessage("chat-1", new CompletionUserMessage("Hello")).block()
        );

        assertEquals("postgres down", exception.getMessage());
        assertFalse(redisLoaded.get());
        assertMessages(existing.requestCopy(), List.of("Be concise"));
    }

    @Test
    void missingChatFailsWhenRedisAndPostgresMiss() {
        InRedisPostgresHistoryManager<StoredChat> historyManager = historyManager(
                new InMemoryHistoryManager(),
                new HashMap<>()
        );

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> historyManager.getChat("missing").block()
        );

        assertEquals("chat not found: missing", exception.getMessage());
    }

    private InRedisPostgresHistoryManager<StoredChat> historyManager(
            IHistoryManager redisCache,
            Map<String, StoredChat> postgres
    ) {
        return new InRedisPostgresHistoryManager<>(
                redisCache,
                StoredChat::new,
                StoredChat::requestCopy,
                chatId -> Mono.justOrEmpty(postgres.get(chatId)),
                entity -> {
                    postgres.put(entity.chatId(), entity);
                    return Mono.just(entity);
                }
        );
    }

    private void assertMessages(CompletionRequest request, List<String> contents) {
        assertNotNull(request);
        assertNotNull(request.getMessages());
        assertEquals(contents.size(), request.getMessages().size());
        for (int i = 0; i < contents.size(); i++) {
            CompletionMessage message = request.getMessages().get(i);
            assertInstanceOf(CompletionMessage.class, message);
            assertEquals(contents.get(i), message.getContent());
        }
    }

    private record StoredChat(String chatId, CompletionRequest request) {

        private StoredChat {
            request = request == null ? null : request.deepClone();
        }

        private CompletionRequest requestCopy() {
            return request.deepClone();
        }
    }

    private static class FailingHistoryManager implements IHistoryManager {

        @Override
        public Mono<Void> loadChat(String chatId, CompletionRequest completionRequest) {
            return Mono.error(new IllegalStateException("redis down"));
        }

        @Override
        public Mono<CompletionRequest> getChat(String id) {
            return Mono.error(new IllegalStateException("redis down"));
        }

        @Override
        public Mono<CompletionRequest> addMessage(String id, CompletionMessage message) {
            return Mono.error(new IllegalStateException("redis down"));
        }

        @Override
        public Mono<Void> evict(String id) {
            return Mono.error(new IllegalStateException("redis down"));
        }
    }

    private static class ObservedHistoryManager extends InMemoryHistoryManager {

        private final AtomicBoolean loadCalled;

        private ObservedHistoryManager(AtomicBoolean loadCalled) {
            this.loadCalled = loadCalled;
        }

        @Override
        public Mono<Void> loadChat(String chatId, CompletionRequest completionRequest) {
            loadCalled.set(true);
            return super.loadChat(chatId, completionRequest);
        }
    }
}
