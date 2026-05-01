package com.anordine.completions.api.webflux.configuration;

import com.anordine.completions.api.webflux.helper.history.IHistoryManager;
import com.anordine.completions.api.webflux.helper.history.InMemoryHistoryManager;
import com.anordine.completions.api.webflux.helper.history.InRedisHistoryManager;
import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.message.CompletionUserMessage;
import com.anordine.completions.api.webflux.model.message.abs.CompletionMessage;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
                "anordine.completions-api-webflux.history.autoconfigure", "true"
        ))) {
            IHistoryManager historyManager = context.getBean(IHistoryManager.class);

            assertInstanceOf(InMemoryHistoryManager.class, historyManager);
        }
    }

    @Test
    void shouldRegisterRedisHistoryManagerAndDefaultSerializerTemplates() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of(
                "anordine.completions-api-webflux.history.autoconfigure", "true",
                "anordine.completions-api-webflux.history.mode", "redis"
        ))) {
            IHistoryManager historyManager = context.getBean(IHistoryManager.class);

            assertInstanceOf(InRedisHistoryManager.class, historyManager);
            assertTrue(context.containsBean(HistoryConfiguration.HISTORY_REDIS_CONNECTION_FACTORY_BEAN_NAME));
            assertTrue(context.containsBean(HistoryConfiguration.COMPLETION_REQUEST_REDIS_TEMPLATE_BEAN_NAME));
            assertTrue(context.containsBean(HistoryConfiguration.COMPLETION_MESSAGE_REDIS_TEMPLATE_BEAN_NAME));
        }
    }

    @Test
    void shouldBackOffWhenHistoryManagerAlreadyExists() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "anordine.completions-api-webflux.history.autoconfigure", "true",
                "anordine.completions-api-webflux.history.mode", "redis"
        )));
        InMemoryHistoryManager customHistoryManager = new InMemoryHistoryManager();
        context.registerBean(IHistoryManager.class, () -> customHistoryManager);
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
}
