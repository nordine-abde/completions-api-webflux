package com.anordine.completions.api.webflux.tool;

import com.anordine.completions.api.webflux.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.tool.abs.CompletionTool;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionFunctionTool extends CompletionTool {

    public CompletionFunctionTool() {
        super(CompletionToolType.FUNCTION);
    }

    private CompletionFunctionDefinition function;

    public CompletionFunctionTool(CompletionFunctionDefinition function) {
        super(CompletionToolType.FUNCTION);
        this.function = function;
    }

    public CompletionFunctionDefinition getFunction() {
        return function;
    }

    public void setFunction(CompletionFunctionDefinition function) {
        this.function = function;
    }
}
