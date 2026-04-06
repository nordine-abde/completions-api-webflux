package com.anordine.completions.api.webflux.message;

import com.anordine.completions.api.webflux.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.message.abs.CompletionMessage;
import com.anordine.completions.api.webflux.tools.abs.CompletionToolCall;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CompletionAssistantMessage extends CompletionMessage {

    public CompletionAssistantMessage(String content) {
        super(content, CompletionRole.ASSISTANT);
    }

    public CompletionAssistantMessage(String content, String name) {
        super(content, CompletionRole.ASSISTANT, name);
    }

    public CompletionAssistantMessage() {
        super(CompletionRole.ASSISTANT);
    }

    private String refusal;
    @JsonProperty("tool_calls")
    private List<CompletionToolCall> toolCalls;

    public String getRefusal() {
        return refusal;
    }

    @JsonProperty("tool_calls")
    public List<CompletionToolCall> getToolCalls() {
        return toolCalls;
    }

    public void setRefusal(String refusal) {
        this.refusal = refusal;
    }

    @JsonProperty("tool_calls")
    public void setToolCalls(List<CompletionToolCall> toolCalls) {
        this.toolCalls = toolCalls;
    }


}
