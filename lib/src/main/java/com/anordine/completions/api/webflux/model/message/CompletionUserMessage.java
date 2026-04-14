package com.anordine.completions.api.webflux.model.message;

import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.model.message.abs.CompletionMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    @Override
    public CompletionUserMessage deepClone() {
        return new CompletionUserMessage(this.content, this.name);
    }
}
