package com.anordine.completions.api.webflux.message;

import com.anordine.completions.api.webflux.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.message.abs.CompletionMessage;

public class CompletionSystemMessage extends CompletionMessage {
    public CompletionSystemMessage(String content) {
        super(content, CompletionRole.SYSTEM);
    }

    public CompletionSystemMessage(String content, String name) {
        super(content, CompletionRole.SYSTEM, name);
    }

    public CompletionSystemMessage() {
        super(CompletionRole.SYSTEM);
    }
}
