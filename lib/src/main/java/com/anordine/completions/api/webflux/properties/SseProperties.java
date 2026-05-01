package com.anordine.completions.api.webflux.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "anordine.completions-api-webflux.sse")
public class SseProperties {

    private boolean autoconfigure;
    private Duration heartbeatEvery = Duration.ofSeconds(30);
    private Duration typingEvery = Duration.ofSeconds(3);
    private int maxBackPressure = 256;
    private boolean emitUsageEvents = true;

    public boolean isAutoconfigure() {
        return autoconfigure;
    }

    public void setAutoconfigure(boolean autoconfigure) {
        this.autoconfigure = autoconfigure;
    }

    public Duration getHeartbeatEvery() {
        return heartbeatEvery;
    }

    public void setHeartbeatEvery(Duration heartbeatEvery) {
        this.heartbeatEvery = heartbeatEvery != null ? heartbeatEvery : Duration.ofSeconds(30);
    }

    public Duration getTypingEvery() {
        return typingEvery;
    }

    public void setTypingEvery(Duration typingEvery) {
        this.typingEvery = typingEvery != null ? typingEvery : Duration.ofSeconds(3);
    }

    public int getMaxBackPressure() {
        return maxBackPressure;
    }

    public void setMaxBackPressure(int maxBackPressure) {
        this.maxBackPressure = maxBackPressure;
    }

    public boolean isEmitUsageEvents() {
        return emitUsageEvents;
    }

    public void setEmitUsageEvents(boolean emitUsageEvents) {
        this.emitUsageEvents = emitUsageEvents;
    }
}
