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

    @Override
    public CompletionFunctionMessage deepClone() {
        return new CompletionFunctionMessage(this.content, this.name);
    }
}
