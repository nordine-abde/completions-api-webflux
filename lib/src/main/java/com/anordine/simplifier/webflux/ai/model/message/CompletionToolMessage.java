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
public class CompletionToolMessage extends CompletionMessage {

    public CompletionToolMessage(String content) {
        super(content, CompletionRole.TOOL);
    }

    public CompletionToolMessage(String content, String name) {
        super(content, CompletionRole.TOOL, name);
    }

    public CompletionToolMessage() {
        super(CompletionRole.TOOL);
    }

    private String toolCallId;

    public String getToolCallId() {
        return toolCallId;
    }

    public void setToolCallId(String toolCallId) {
        this.toolCallId = toolCallId;
    }

    @Override
    public CompletionToolMessage deepClone() {
        CompletionToolMessage clone = new CompletionToolMessage(this.content, this.name);
        clone.setToolCallId(this.toolCallId);
        return clone;
    }
}
