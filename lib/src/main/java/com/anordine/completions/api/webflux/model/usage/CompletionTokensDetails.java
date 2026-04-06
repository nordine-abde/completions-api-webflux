package com.anordine.completions.api.webflux.model.usage;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionTokensDetails {

    private Integer acceptedPredictionTokens;
    private Integer audioTokens;
    private Integer reasoningTokens;
    private Integer rejectedPredictionTokens;

    public CompletionTokensDetails() {
    }

    public CompletionTokensDetails(Integer acceptedPredictionTokens,
                                   Integer audioTokens,
                                   Integer reasoningTokens,
                                   Integer rejectedPredictionTokens) {
        this.acceptedPredictionTokens = acceptedPredictionTokens;
        this.audioTokens = audioTokens;
        this.reasoningTokens = reasoningTokens;
        this.rejectedPredictionTokens = rejectedPredictionTokens;
    }

    public Integer getAcceptedPredictionTokens() {
        return acceptedPredictionTokens;
    }

    public void setAcceptedPredictionTokens(Integer acceptedPredictionTokens) {
        this.acceptedPredictionTokens = acceptedPredictionTokens;
    }

    public Integer getAudioTokens() {
        return audioTokens;
    }

    public void setAudioTokens(Integer audioTokens) {
        this.audioTokens = audioTokens;
    }

    public Integer getReasoningTokens() {
        return reasoningTokens;
    }

    public void setReasoningTokens(Integer reasoningTokens) {
        this.reasoningTokens = reasoningTokens;
    }

    public Integer getRejectedPredictionTokens() {
        return rejectedPredictionTokens;
    }

    public void setRejectedPredictionTokens(Integer rejectedPredictionTokens) {
        this.rejectedPredictionTokens = rejectedPredictionTokens;
    }
}
