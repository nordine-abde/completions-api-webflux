package com.anordine.completions.api.webflux.model.tool;

import com.anordine.completions.api.webflux.model.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.model.tool.abs.CompletionToolCall;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

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
}
