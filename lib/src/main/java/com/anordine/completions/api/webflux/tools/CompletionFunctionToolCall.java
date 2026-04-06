package com.anordine.completions.api.webflux.tools;

import com.anordine.completions.api.webflux.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.tools.abs.CompletionToolCall;

public class CompletionFunctionToolCall extends CompletionToolCall {

    private CompletionFunction function;

    public CompletionFunctionToolCall(String id, CompletionFunction function) {
        super(id, CompletionToolType.FUNCTION);
        this.function = function;
    }

    public CompletionFunctionToolCall() {
        super(CompletionToolType.FUNCTION);
    }

    public CompletionFunction getFunction() {
        return function;
    }

    public void setFunction(CompletionFunction function) {
        this.function = function;
    }
}
