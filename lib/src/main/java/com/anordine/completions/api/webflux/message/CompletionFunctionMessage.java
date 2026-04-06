package com.anordine.completions.api.webflux.message;

import com.anordine.completions.api.webflux.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.message.abs.CompletionMessage;

public class CompletionFunctionMessage extends CompletionMessage {

    public CompletionFunctionMessage(String content) {
        super(content, CompletionRole.FUNCTION);
    }

    public CompletionFunctionMessage(String content, String name) {
        super(content, CompletionRole.FUNCTION, name);
    }

    public CompletionFunctionMessage() {
        super(CompletionRole.FUNCTION);
    }
}
