package com.anordine.completions.api.webflux.model.message;

import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.model.message.abs.CompletionMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionDeveloperMessage extends CompletionMessage {

    public CompletionDeveloperMessage(String content) {
        super(content, CompletionRole.DEVELOPER);
    }

    public CompletionDeveloperMessage(String content, String name) {
        super(content, CompletionRole.DEVELOPER, name);
    }

    public CompletionDeveloperMessage() {
        super(CompletionRole.DEVELOPER);
    }

    @Override
    public CompletionDeveloperMessage deepClone() {
        return new CompletionDeveloperMessage(this.content, this.name);
    }
}
