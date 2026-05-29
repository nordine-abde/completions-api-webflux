package com.anordine.simplifier.webflux.ai.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebClientConfigurationTest {

    @Test
    void shouldRegisterOpenAiClientWhenEnabled() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of(
                "com.anordine.simplifier.webflux.ai.open-ai.autoconfigure", "true",
                "com.anordine.simplifier.webflux.ai.open-ai.secret-key", "test-secret"
        ))) {
            assertTrue(context.containsBean("openAiWebClient"));
            assertInstanceOf(WebClient.class, context.getBean("openAiWebClient"));
        }
    }

    @Test
    void shouldRegisterDeepSeekClientWhenEnabled() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of(
                "com.anordine.simplifier.webflux.ai.deepseek.autoconfigure", "true",
                "com.anordine.simplifier.webflux.ai.deepseek.secret-key", "test-secret"
        ))) {
            assertTrue(context.containsBean("deepSeekWebClient"));
            assertInstanceOf(WebClient.class, context.getBean("deepSeekWebClient"));
        }
    }

    @Test
    void shouldRegisterCustomClientUsingCamelCaseBeanName() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of(
                "com.anordine.simplifier.webflux.ai.custom.local-llm.autoconfigure", "true",
                "com.anordine.simplifier.webflux.ai.custom.local-llm.base-url", "http://localhost:1234/v1",
                "com.anordine.simplifier.webflux.ai.custom.local-llm.secret-key", "local-secret"
        ))) {
            assertTrue(context.containsBean("localLlm"));
            assertFalse(context.containsBean("local-llm"));
            assertInstanceOf(WebClient.class, context.getBean("localLlm"));
        }
    }

    @Test
    void shouldNotRegisterCustomClientWhenDisabled() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of(
                "com.anordine.simplifier.webflux.ai.custom.local-llm.autoconfigure", "false",
                "com.anordine.simplifier.webflux.ai.custom.local-llm.base-url", "http://localhost:1234/v1",
                "com.anordine.simplifier.webflux.ai.custom.local-llm.secret-key", "local-secret"
        ))) {
            assertFalse(context.containsBean("localLlm"));
        }
    }

    @Test
    void shouldFailWhenCustomClientBeanNameCollides() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "com.anordine.simplifier.webflux.ai.custom.local-llm.autoconfigure", "true",
                "com.anordine.simplifier.webflux.ai.custom.local-llm.base-url", "http://localhost:1234/v1",
                "com.anordine.simplifier.webflux.ai.custom.local-llm.secret-key", "local-secret"
        )));
        context.registerBean("localLlm", String.class, () -> "collision");
        context.register(WebClientConfiguration.class);

        try {
            assertThrows(BeanCreationException.class, context::refresh);
        } finally {
            context.close();
        }
    }

    @Test
    void shouldFailWhenTwoCustomNamesNormalizeToSameBeanName() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "com.anordine.simplifier.webflux.ai.custom.local-llm.autoconfigure", "true",
                "com.anordine.simplifier.webflux.ai.custom.local-llm.base-url", "http://localhost:1234/v1",
                "com.anordine.simplifier.webflux.ai.custom.local-llm.secret-key", "local-secret",
                "com.anordine.simplifier.webflux.ai.custom.local_llm.autoconfigure", "true",
                "com.anordine.simplifier.webflux.ai.custom.local_llm.base-url", "http://localhost:4321/v1",
                "com.anordine.simplifier.webflux.ai.custom.local_llm.secret-key", "other-secret"
        )));
        context.register(WebClientConfiguration.class);

        try {
            assertThrows(BeanCreationException.class, context::refresh);
        } finally {
            context.close();
        }
    }

    @Test
    void shouldCreateDefaultBuilderWhenNoneIsProvided() {
        assertDoesNotThrow(() -> {
            try (AnnotationConfigApplicationContext context = createContext(Map.of(
                    "com.anordine.simplifier.webflux.ai.custom.local-llm.autoconfigure", "true",
                    "com.anordine.simplifier.webflux.ai.custom.local-llm.base-url", "http://localhost:1234/v1",
                    "com.anordine.simplifier.webflux.ai.custom.local-llm.secret-key", "local-secret"
            ))) {
                assertTrue(context.containsBean("builder"));
                assertTrue(context.containsBean("localLlm"));
            }
        });
    }

    private AnnotationConfigApplicationContext createContext(Map<String, Object> properties) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", properties));
        context.register(WebClientConfiguration.class);
        context.refresh();
        return context;
    }
}
