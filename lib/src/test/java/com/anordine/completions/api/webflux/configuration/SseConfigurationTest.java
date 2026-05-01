package com.anordine.completions.api.webflux.configuration;

import com.anordine.completions.api.webflux.helper.sse.ChatSseManager;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SseConfigurationTest {

    @Test
    void shouldNotRegisterSseManagerWhenDisabledByDefault() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of())) {
            assertFalse(context.containsBean("chatSseManager"));
        }
    }

    @Test
    void shouldRegisterSseManagerWhenEnabledWithDefaults() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of(
                "anordine.completions-api-webflux.sse.autoconfigure", "true"
        ))) {
            ChatSseManager manager = context.getBean(ChatSseManager.class);

            assertEquals(Duration.ofSeconds(30), manager.getHeartbeatEvery());
            assertEquals(Duration.ofSeconds(3), manager.getTypingEvery());
            assertEquals(256, manager.getMaxBackPressure());
        }
    }

    @Test
    void shouldBindSseManagerProperties() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of(
                "anordine.completions-api-webflux.sse.autoconfigure", "true",
                "anordine.completions-api-webflux.sse.heartbeat-every", "5s",
                "anordine.completions-api-webflux.sse.typing-every", "1s",
                "anordine.completions-api-webflux.sse.max-back-pressure", "16"
        ))) {
            ChatSseManager manager = context.getBean(ChatSseManager.class);

            assertEquals(Duration.ofSeconds(5), manager.getHeartbeatEvery());
            assertEquals(Duration.ofSeconds(1), manager.getTypingEvery());
            assertEquals(16, manager.getMaxBackPressure());
        }
    }

    @Test
    void shouldBackOffWhenSseManagerAlreadyExists() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "anordine.completions-api-webflux.sse.autoconfigure", "true"
        )));
        ChatSseManager customSseManager = new ChatSseManager(Duration.ofSeconds(10), Duration.ofSeconds(2), 32);
        context.registerBean(ChatSseManager.class, () -> customSseManager);
        context.register(SseConfiguration.class);

        try {
            context.refresh();

            assertEquals(customSseManager, context.getBean(ChatSseManager.class));
        } finally {
            context.close();
        }
    }

    @Test
    void twoArgumentConstructorKeepsDefaultTypingInterval() {
        ChatSseManager manager = new ChatSseManager(Duration.ofSeconds(10), 32);

        assertEquals(Duration.ofSeconds(10), manager.getHeartbeatEvery());
        assertEquals(Duration.ofSeconds(3), manager.getTypingEvery());
        assertEquals(32, manager.getMaxBackPressure());
    }

    private AnnotationConfigApplicationContext createContext(Map<String, Object> properties) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", properties));
        context.register(SseConfiguration.class);
        context.refresh();
        return context;
    }
}
