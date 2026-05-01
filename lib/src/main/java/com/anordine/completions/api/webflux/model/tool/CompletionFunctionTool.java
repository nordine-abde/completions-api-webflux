package com.anordine.completions.api.webflux.model.tool;

import com.anordine.completions.api.webflux.model.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.model.tool.abs.CompletionTool;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @Override
    public CompletionFunctionTool deepClone() {
        return new CompletionFunctionTool(this.function == null ? null : this.function.deepClone());
    }
}
