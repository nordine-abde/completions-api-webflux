package com.anordine.completions.api.webflux.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebClientConfigurationTest {

    @Test
    void shouldRegisterOpenAiClientWhenEnabled() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of(
                "anordine.completions-api-webflux.open-ai.autoconfigure", "true",
                "anordine.completions-api-webflux.open-ai.secret-key", "test-secret"
        ))) {
            assertTrue(context.containsBean("openAiWebClient"));
            assertInstanceOf(WebClient.class, context.getBean("openAiWebClient"));
        }
    }

    @Test
    void shouldRegisterCustomClientUsingCamelCaseBeanName() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of(
                "anordine.completions-api-webflux.custom.local-llm.autoconfigure", "true",
                "anordine.completions-api-webflux.custom.local-llm.base-url", "http://localhost:1234/v1",
                "anordine.completions-api-webflux.custom.local-llm.secret-key", "local-secret"
        ))) {
            assertTrue(context.containsBean("localLlm"));
            assertFalse(context.containsBean("local-llm"));
            assertInstanceOf(WebClient.class, context.getBean("localLlm"));
        }
    }

    @Test
    void shouldNotRegisterCustomClientWhenDisabled() {
        try (AnnotationConfigApplicationContext context = createContext(Map.of(
                "anordine.completions-api-webflux.custom.local-llm.autoconfigure", "false",
                "anordine.completions-api-webflux.custom.local-llm.base-url", "http://localhost:1234/v1",
                "anordine.completions-api-webflux.custom.local-llm.secret-key", "local-secret"
        ))) {
            assertFalse(context.containsBean("localLlm"));
        }
    }

    @Test
    void shouldFailWhenCustomClientBeanNameCollides() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "anordine.completions-api-webflux.custom.local-llm.autoconfigure", "true",
                "anordine.completions-api-webflux.custom.local-llm.base-url", "http://localhost:1234/v1",
                "anordine.completions-api-webflux.custom.local-llm.secret-key", "local-secret"
        )));
        context.registerBean("localLlm", String.class, () -> "collision");
        context.registerBean(WebClient.Builder.class, WebClient::builder);
        context.register(WebClientConfiguration.class);

        try {
            assertThrows(BeanCreationException.class, context::refresh);
        } finally {
            context.close();
        }
    }

    private AnnotationConfigApplicationContext createContext(Map<String, Object> properties) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", properties));
        context.registerBean(WebClient.Builder.class, WebClient::builder);
        context.register(WebClientConfiguration.class);
        context.refresh();
        return context;
    }
}
