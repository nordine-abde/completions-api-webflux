package com.anordine.simplifier.webflux.ai.configuration;

import com.anordine.simplifier.webflux.ai.helper.history.IHistoryManager;
import com.anordine.simplifier.webflux.ai.helper.history.InMemoryHistoryManager;
import com.anordine.simplifier.webflux.ai.helper.history.InRedisHistoryManager;
import com.anordine.simplifier.webflux.ai.model.CompletionRequest;
import com.anordine.simplifier.webflux.ai.model.message.CompletionUserMessage;
import com.anordine.simplifier.webflux.ai.model.message.abs.CompletionMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryConfigurationTest {

    @Test
    void shouldNotRegisterHistoryManagerWhenDisabledByDefault() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of())) {
            assertFalse(context.containsBean("inMemoryHistoryManager"));
            assertFalse(context.containsBean("inRedisHistoryManager"));
        }
    }

    @Test
    void shouldRegisterMemoryHistoryManagerWhenEnabledWithoutMode() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of(
                "com.anordine.simplifier.webflux.ai.history.autoconfigure", "true"
        ))) {
            IHistoryManager historyManager = context.getBean(IHistoryManager.class);

            assertInstanceOf(InMemoryHistoryManager.class, historyManager);
        }
    }

    @Test
    void shouldRegisterRedisHistoryManagerAndDefaultSerializerTemplates() {
        try (AnnotationConfigApplicationContext context = createContextWithRedisConnectionFactory(Map.of(
                "com.anordine.simplifier.webflux.ai.history.autoconfigure", "true",
                "com.anordine.simplifier.webflux.ai.history.mode", "redis"
        ))) {
            IHistoryManager historyManager = context.getBean(IHistoryManager.class);

            assertInstanceOf(InRedisHistoryManager.class, historyManager);
            assertInstanceOf(ReactiveRedisConnectionFactory.class, context.getBean(ReactiveRedisConnectionFactory.class));
            assertTrue(context.containsBean(HistoryConfiguration.COMPLETION_REQUEST_REDIS_TEMPLATE_BEAN_NAME));
            assertTrue(context.containsBean(HistoryConfiguration.COMPLETION_MESSAGE_REDIS_TEMPLATE_BEAN_NAME));
        }
    }

    @Test
    void shouldRequireRedisConnectionFactoryWhenRedisModeIsEnabled() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "com.anordine.simplifier.webflux.ai.history.autoconfigure", "true",
                "com.anordine.simplifier.webflux.ai.history.mode", "redis"
        )));
        context.register(HistoryConfiguration.class);

        try {
            assertThrows(BeanCreationException.class, context::refresh);
        } finally {
            context.close();
        }
    }

    @Test
    void shouldBackOffOnlyMatchingDefaultRedisTemplateByGenericType() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "com.anordine.simplifier.webflux.ai.history.autoconfigure", "true",
                "com.anordine.simplifier.webflux.ai.history.mode", "redis"
        )));
        context.register(CustomRequestRedisTemplateConfiguration.class);
        context.register(HistoryConfiguration.class);

        try {
            context.refresh();

            assertInstanceOf(InRedisHistoryManager.class, context.getBean(IHistoryManager.class));
            assertTrue(context.containsBean("customRequestRedis"));
            assertFalse(context.containsBean(HistoryConfiguration.COMPLETION_REQUEST_REDIS_TEMPLATE_BEAN_NAME));
            assertTrue(context.containsBean(HistoryConfiguration.COMPLETION_MESSAGE_REDIS_TEMPLATE_BEAN_NAME));
        } finally {
            context.close();
        }
    }

    @Test
    void shouldBackOffWhenHistoryManagerAlreadyExists() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "com.anordine.simplifier.webflux.ai.history.autoconfigure", "true",
                "com.anordine.simplifier.webflux.ai.history.mode", "redis"
        )));
        InMemoryHistoryManager customHistoryManager = new InMemoryHistoryManager();
        context.registerBean(IHistoryManager.class, () -> customHistoryManager);
        context.registerBean(ReactiveRedisConnectionFactory.class, () -> new LettuceConnectionFactory("127.0.0.1", 6379));
        context.register(HistoryConfiguration.class);

        try {
            context.refresh();

            assertEquals(customHistoryManager, context.getBean(IHistoryManager.class));
        } finally {
            context.close();
        }
    }

    @Test
    void jacksonRedisSerializerRoundTripsCompletionTypes() {
        JacksonJsonRedisSerializer<CompletionRequest> requestSerializer =
                new JacksonJsonRedisSerializer<>(CompletionRequest.class);
        JacksonJsonRedisSerializer<CompletionMessage> messageSerializer =
                new JacksonJsonRedisSerializer<>(CompletionMessage.class);

        byte[] requestBytes = requestSerializer.serialize(new CompletionRequest()
                .withModel("gpt-5.4")
                .addUserMessage("Hello"));
        CompletionRequest request = requestSerializer.deserialize(requestBytes);
        byte[] messageBytes = messageSerializer.serialize(new CompletionUserMessage("Hello"));
        CompletionMessage message = messageSerializer.deserialize(messageBytes);

        assertNotNull(request);
        assertEquals("gpt-5.4", request.getModel());
        assertEquals("Hello", request.getMessages().getFirst().getContent());
        assertInstanceOf(CompletionUserMessage.class, message);
        assertEquals("Hello", message.getContent());
        assertArrayEquals(new byte[0], requestSerializer.serialize(null));
    }

    private AnnotationConfigApplicationContext createContext(Map<String, Object> properties) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", properties));
        context.register(HistoryConfiguration.class);
        context.refresh();
        return context;
    }

    private AnnotationConfigApplicationContext createContextWithRedisConnectionFactory(Map<String, Object> properties) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", properties));
        context.registerBean(ReactiveRedisConnectionFactory.class, () -> new LettuceConnectionFactory("127.0.0.1", 6379));
        context.register(HistoryConfiguration.class);
        context.refresh();
        return context;
    }

    private static <T> ReactiveRedisTemplate<String, T> redisTemplate(
            ReactiveRedisConnectionFactory connectionFactory,
            Class<T> valueType
    ) {
        RedisSerializationContext<String, T> context = RedisSerializationContext
                .<String, T>newSerializationContext(RedisSerializer.string())
                .value(new JacksonJsonRedisSerializer<>(valueType))
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomRequestRedisTemplateConfiguration {

        @Bean
        ReactiveRedisConnectionFactory redisConnectionFactory() {
            return new LettuceConnectionFactory("127.0.0.1", 6379);
        }

        @Bean
        ReactiveRedisTemplate<String, CompletionRequest> customRequestRedis(ReactiveRedisConnectionFactory connectionFactory) {
            return redisTemplate(connectionFactory, CompletionRequest.class);
        }
    }
}
