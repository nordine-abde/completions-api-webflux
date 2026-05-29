package com.anordine.simplifier.webflux.ai.model.message;

import com.anordine.simplifier.webflux.ai.model.enums.finish.CompletionFinishReason;
import com.anordine.simplifier.webflux.ai.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionChoices implements DeepClonable<CompletionChoices> {

    private CompletionFinishReason finishReason;
    private Integer index;
    private CompletionAssistantMessage message;

    public CompletionChoices() {
    }

    public CompletionChoices(CompletionFinishReason finishReason,
                             Integer index,
                             CompletionAssistantMessage message) {
        this.finishReason = finishReason;
        this.index = index;
        this.message = message;
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

    @Override
    public CompletionChoices deepClone() {
        return new CompletionChoices(
                this.finishReason,
                this.index,
                this.message == null ? null : this.message.deepClone()
        );
    }
}
