package com.anordine.completions.api.webflux.helper.history;

import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.message.CompletionAssistantMessage;
import com.anordine.completions.api.webflux.model.message.CompletionDeveloperMessage;
import com.anordine.completions.api.webflux.model.message.CompletionUserMessage;
import com.anordine.completions.api.webflux.model.message.abs.CompletionMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@EnabledIfEnvironmentVariable(named = "REDIS_INTEGRATION_TESTS", matches = "true")
class InRedisHistoryManagerTest {

    private LettuceConnectionFactory connectionFactory;
    private InRedisHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        String host = System.getenv().getOrDefault("REDIS_HOST", "127.0.0.1");
        int port = Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6380"));

        connectionFactory = new LettuceConnectionFactory(host, port);
        connectionFactory.afterPropertiesSet();

        ReactiveRedisTemplate<String, CompletionRequest> requestRedis = redisTemplate(CompletionRequest.class);
        ReactiveRedisTemplate<String, CompletionMessage> messageRedis = redisTemplate(CompletionMessage.class);
        historyManager = new InRedisHistoryManager("test:" + UUID.randomUUID() + ":", requestRedis, messageRedis);
    }

    @AfterEach
    void tearDown() {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }

    @Test
    void loadChatAndGetChatReconstructMessagesInOrder() {
        historyManager.loadChat("chat-1", new CompletionRequest()
                .withModel("gpt-5.4")
                .addDeveloperMessage("Be concise")
                .addUserMessage("Hello")).block();

        CompletionRequest loaded = historyManager.getChat("chat-1").block();

        assertNotNull(loaded);
        assertEquals("gpt-5.4", loaded.getModel());
        assertMessages(loaded, List.of("Be concise", "Hello"));
        assertInstanceOf(CompletionDeveloperMessage.class, loaded.getMessages().get(0));
        assertInstanceOf(CompletionUserMessage.class, loaded.getMessages().get(1));
    }

    @Test
    void addMessageAppendsToRedisList() {
        historyManager.loadChat("chat-1", new CompletionRequest()
                .withModel("gpt-5.4")
                .addDeveloperMessage("Be concise")).block();

        CompletionRequest updated = historyManager.addMessage(
                "chat-1",
                new CompletionUserMessage("Hello")
        ).block();
        historyManager.addMessage("chat-1", new CompletionAssistantMessage("Hi")).block();

        assertNotNull(updated);
        assertMessages(updated, List.of("Be concise", "Hello"));

        CompletionRequest loaded = historyManager.getChat("chat-1").block();
        assertNotNull(loaded);
        assertMessages(loaded, List.of("Be concise", "Hello", "Hi"));
    }

    @Test
    void addMessageForMissingChatFails() {
        Exception exception = assertThrows(
                NoSuchElementException.class,
                () -> historyManager.addMessage("missing", new CompletionUserMessage("Hello")).block()
        );

        assertEquals("chat not found: missing", exception.getMessage());
    }

    @Test
    void evictRemovesRequestAndMessages() {
        historyManager.loadChat("chat-1", new CompletionRequest()
                .withModel("gpt-5.4")
                .addUserMessage("Hello")).block();
        historyManager.addMessage("chat-1", new CompletionAssistantMessage("Hi")).block();

        historyManager.evict("chat-1").block();

        Exception exception = assertThrows(
                NoSuchElementException.class,
                () -> historyManager.getChat("chat-1").block()
        );

        assertEquals("chat not found: chat-1", exception.getMessage());
    }

    @Test
    void concurrentAppendsDoNotLoseMessages() {
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

        Set<String> contents = new HashSet<>();
        loaded.getMessages().forEach(message -> contents.add(message.getContent()));
        assertEquals(200, contents.size());
    }

    private <T> ReactiveRedisTemplate<String, T> redisTemplate(Class<T> valueType) {
        RedisSerializationContext<String, T> context = RedisSerializationContext
                .<String, T>newSerializationContext(RedisSerializer.string())
                .value(new JacksonJsonRedisSerializer<>(valueType))
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
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
