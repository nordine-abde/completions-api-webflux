package com.anordine.completions.api.webflux.model.usage;

import com.anordine.completions.api.webflux.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PromptTokenDetails implements DeepClonable<PromptTokenDetails> {

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

    @Override
    public PromptTokenDetails deepClone() {
        return new PromptTokenDetails(this.audioTokens, this.cachedTokens);
    }
}
