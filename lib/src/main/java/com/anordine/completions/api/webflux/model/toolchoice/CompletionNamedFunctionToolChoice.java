package com.anordine.completions.api.webflux.model.toolchoice;

import com.anordine.completions.api.webflux.model.enums.toolchoice.CompletionToolChoiceType;
import com.anordine.completions.api.webflux.model.toolchoice.abs.CompletionToolChoiceOption;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionNamedFunctionToolChoice extends CompletionToolChoiceOption {

    private CompletionToolChoiceName function;

    public CompletionNamedFunctionToolChoice() {
        super(CompletionToolChoiceType.FUNCTION);
    }

    public CompletionNamedFunctionToolChoice(CompletionToolChoiceName function) {
        super(CompletionToolChoiceType.FUNCTION);
        this.function = function;
    }

    public CompletionToolChoiceName getFunction() {
        return function;
    }

    public void setFunction(CompletionToolChoiceName function) {
        this.function = function;
    }

    @Override
    public CompletionNamedFunctionToolChoice deepClone() {
        return new CompletionNamedFunctionToolChoice(
                this.function == null ? null : this.function.deepClone()
        );
    }
}
