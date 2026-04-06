package com.anordine.completions.api.webflux.model.message;

import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.model.message.abs.CompletionMessage;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionUserMessage extends CompletionMessage {

    public CompletionUserMessage(String content) {
        super(content, CompletionRole.USER);
    }

    public CompletionUserMessage(String content, String name) {
        super(content, CompletionRole.USER, name);
    }

    public CompletionUserMessage() {
        super(CompletionRole.USER);
    }
}
