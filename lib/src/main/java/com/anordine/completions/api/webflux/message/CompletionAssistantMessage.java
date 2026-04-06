package com.anordine.completions.api.webflux.message;

import com.anordine.completions.api.webflux.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.message.abs.CompletionMessage;

public class CompletionAssistantMessage extends CompletionMessage {

    public CompletionAssistantMessage(String content) {
        super(content, CompletionRole.ASSISTANT);
    }

    public CompletionAssistantMessage(String content, String name) {
        super(content, CompletionRole.ASSISTANT, name);
    }

    public CompletionAssistantMessage() {
        super(CompletionRole.ASSISTANT);
    }
}
