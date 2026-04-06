package com.anordine.completions.api.webflux.model.message;

import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.model.message.abs.CompletionMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
