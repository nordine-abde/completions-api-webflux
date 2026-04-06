package com.anordine.completions.api.webflux.message;

import com.anordine.completions.api.webflux.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.message.abs.CompletionMessage;

public class CompletionUserMessage extends CompletionMessage {

    public CompletionUserMessage(String content, String name) {
        super(content, CompletionRole.USER, name);
    }

    public CompletionUserMessage() {
        super(CompletionRole.USER);
    }
}
