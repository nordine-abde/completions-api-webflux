package com.anordine.completions.api.webflux.model.tool;

import com.anordine.completions.api.webflux.model.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.model.tool.abs.CompletionToolCall;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    @Override
    public CompletionMessageCustomToolCall deepClone() {
        return new CompletionMessageCustomToolCall(
                this.id,
                this.custom == null ? null : this.custom.deepClone()
        );
    }
}
