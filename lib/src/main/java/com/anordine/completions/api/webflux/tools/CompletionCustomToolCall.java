package com.anordine.completions.api.webflux.tools;

import com.anordine.completions.api.webflux.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.tools.abs.CompletionToolCall;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionCustomToolCall extends CompletionToolCall {

    private CompletionCustom custom;

    public CompletionCustomToolCall(String id, CompletionCustom custom) {
        super(id, CompletionToolType.CUSTOM);
        this.custom = custom;
    }

    public CompletionCustomToolCall() {
        super(CompletionToolType.CUSTOM);
    }

    public CompletionCustom getCustom() {
        return custom;
    }

    public void setCustom(CompletionCustom custom) {
        this.custom = custom;
    }
}
