package com.anordine.completions.api.webflux.message;

import com.anordine.completions.api.webflux.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.message.abs.CompletionMessage;

public class CompletionDeveloperMessage extends CompletionMessage {

    public CompletionDeveloperMessage(String content, String name) {
        super(content, CompletionRole.DEVELOPER, name);
    }

    public CompletionDeveloperMessage() {
        super(CompletionRole.DEVELOPER);
    }
}
