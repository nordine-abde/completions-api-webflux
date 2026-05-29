package com.anordine.simplifier.webflux.ai.model.tool;

import com.anordine.simplifier.webflux.ai.model.enums.tool.CompletionToolType;
import com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionToolCall;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionMessageFunctionToolCall extends CompletionToolCall {

    private CompletionMessageFunctionTool function;

    public CompletionMessageFunctionToolCall(String id, CompletionMessageFunctionTool function) {
        super(id, CompletionToolType.FUNCTION);
        this.function = function;
    }

    public CompletionMessageFunctionToolCall() {
        super(CompletionToolType.FUNCTION);
    }

    public CompletionMessageFunctionTool getFunction() {
        return function;
    }

    public void setFunction(CompletionMessageFunctionTool function) {
        this.function = function;
    }

    @Override
    public CompletionMessageFunctionToolCall deepClone() {
        return new CompletionMessageFunctionToolCall(
                this.id,
                this.function == null ? null : this.function.deepClone()
        );
    }
}
