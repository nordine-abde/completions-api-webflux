package com.anordine.simplifier.webflux.ai.model;

import com.anordine.simplifier.webflux.ai.model.enums.finish.CompletionFinishReason;
import com.anordine.simplifier.webflux.ai.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionStreamChoice implements DeepClonable<CompletionStreamChoice> {

    private CompletionFinishReason finishReason;
    private Integer index;
    private CompletionStreamDelta delta;

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

    public CompletionStreamDelta getDelta() {
        return delta;
    }

    public void setDelta(CompletionStreamDelta delta) {
        this.delta = delta;
    }

    @Override
    public CompletionStreamChoice deepClone() {
        CompletionStreamChoice clone = new CompletionStreamChoice();
        clone.setFinishReason(this.finishReason);
        clone.setIndex(this.index);
        clone.setDelta(this.delta == null ? null : this.delta.deepClone());
        return clone;
    }
}
