package com.anordine.simplifier.webflux.ai.configuration;

import com.anordine.simplifier.webflux.ai.helper.sse.ChatSseManager;
import com.anordine.simplifier.webflux.ai.properties.SseProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SseProperties.class)
public class SseConfiguration {

    private static final String SSE_PREFIX = "com.anordine.simplifier.webflux.ai.sse";

    @Bean
    @ConditionalOnMissingBean(ChatSseManager.class)
    @ConditionalOnProperty(prefix = SSE_PREFIX, name = "autoconfigure", havingValue = "true")
    public ChatSseManager chatSseManager(SseProperties properties) {
        return new ChatSseManager(
                properties.getHeartbeatEvery(),
                properties.getTypingEvery(),
                properties.getMaxBackPressure(),
                properties.isEmitUsageEvents()
        );
    }
}
