package com.anordine.simplifier.webflux.ai.model.message;

import com.anordine.simplifier.webflux.ai.model.enums.role.CompletionRole;
import com.anordine.simplifier.webflux.ai.model.message.abs.CompletionMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
