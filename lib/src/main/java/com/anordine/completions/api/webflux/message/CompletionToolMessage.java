package com.anordine.completions.api.webflux.message;

import com.anordine.completions.api.webflux.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.message.abs.CompletionMessage;

public class CompletionToolMessage extends CompletionMessage {

    public CompletionToolMessage(String content) {
        super(content, CompletionRole.TOOL);
    }

    public CompletionToolMessage(String content, String name) {
        super(content, CompletionRole.TOOL, name);
    }

    public CompletionToolMessage() {
        super(CompletionRole.TOOL);
    }

    private String toolCallId;

    public String getToolCallId() {
        return toolCallId;
    }

    public void setToolCallId(String toolCallId) {
        this.toolCallId = toolCallId;
    }
}
