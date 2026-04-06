package com.anordine.completions.api.webflux.message;

import com.anordine.completions.api.webflux.enums.finish.CompletionFinishReason;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionChoices {

    private CompletionFinishReason finishReason;
    private Integer index;
    private CompletionAssistantMessage message;
    private Object logprobs;

    public CompletionChoices() {
    }

    public CompletionChoices(CompletionFinishReason finishReason,
                             Integer index,
                             CompletionAssistantMessage message,
                             Object logprobs) {
        this.finishReason = finishReason;
        this.index = index;
        this.message = message;
        this.logprobs = logprobs;
    }

    public CompletionFinishReason getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(CompletionFinishReason finishReason) {
        this.finishReason = finishReason;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public CompletionAssistantMessage getMessage() {
        return message;
    }

    public void setMessage(CompletionAssistantMessage message) {
        this.message = message;
    }

    public Object getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(Object logprobs) {
        this.logprobs = logprobs;
    }
}
