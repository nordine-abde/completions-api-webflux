package com.anordine.completions.api.webflux.tool;

import com.anordine.completions.api.webflux.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.tool.abs.CompletionToolCall;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionMessageCustomToolCall extends CompletionToolCall {

    private CompletionMessageCustomTool custom;

    public CompletionMessageCustomToolCall(String id, CompletionMessageCustomTool custom) {
        super(id, CompletionToolType.CUSTOM);
        this.custom = custom;
    }

    public CompletionMessageCustomToolCall() {
        super(CompletionToolType.CUSTOM);
    }

    public CompletionMessageCustomTool getCustom() {
        return custom;
    }

    public void setCustom(CompletionMessageCustomTool custom) {
        this.custom = custom;
    }
}
