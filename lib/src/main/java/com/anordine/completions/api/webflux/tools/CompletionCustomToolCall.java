package com.anordine.completions.api.webflux.tools;

import com.anordine.completions.api.webflux.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.tools.abs.CompletionToolCall;

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
