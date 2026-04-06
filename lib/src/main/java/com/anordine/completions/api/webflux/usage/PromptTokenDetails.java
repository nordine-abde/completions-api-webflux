package com.anordine.completions.api.webflux.usage;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PromptTokenDetails {

    private Integer audioTokens;
    private Integer cachedTokens;

    public PromptTokenDetails() {
    }

    public PromptTokenDetails(Integer audioTokens, Integer cachedTokens) {
        this.audioTokens = audioTokens;
        this.cachedTokens = cachedTokens;
    }

    public Integer getAudioTokens() {
        return audioTokens;
    }

    public void setAudioTokens(Integer audioTokens) {
        this.audioTokens = audioTokens;
    }

    public Integer getCachedTokens() {
        return cachedTokens;
    }

    public void setCachedTokens(Integer cachedTokens) {
        this.cachedTokens = cachedTokens;
    }
}
