package com.anordine.completions.api.webflux.configuration;

import com.anordine.completions.api.webflux.helper.sse.ChatSseManager;
import com.anordine.completions.api.webflux.properties.SseProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SseProperties.class)
public class SseConfiguration {

    private static final String SSE_PREFIX = "anordine.completions-api-webflux.sse";

    @Bean
    @ConditionalOnMissingBean(ChatSseManager.class)
    @ConditionalOnProperty(prefix = SSE_PREFIX, name = "autoconfigure", havingValue = "true")
    public ChatSseManager chatSseManager(SseProperties properties) {
        return new ChatSseManager(
                properties.getHeartbeatEvery(),
                properties.getTypingEvery(),
                properties.getMaxBackPressure()
        );
    }
}
