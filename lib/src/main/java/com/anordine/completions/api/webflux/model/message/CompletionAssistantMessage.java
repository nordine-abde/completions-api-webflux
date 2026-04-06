package com.anordine.completions.api.webflux.model.message;

import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.model.message.abs.CompletionMessage;
import com.anordine.completions.api.webflux.model.tool.abs.CompletionToolCall;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
    private List<Object> annotations;
    private List<CompletionToolCall> toolCalls;
    private CompletionAudio audio;

    public String getRefusal() {
        return refusal;
    }

    public List<CompletionToolCall> getToolCalls() {
        return toolCalls;
    }

    public List<Object> getAnnotations() {
        return annotations;
    }

    public void setRefusal(String refusal) {
        this.refusal = refusal;
    }

    public void setAnnotations(List<Object> annotations) {
        this.annotations = annotations;
    }

    public void setToolCalls(List<CompletionToolCall> toolCalls) {
        this.toolCalls = toolCalls;
    }

    public CompletionAudio getAudio() {
        return audio;
    }

    public void setAudio(CompletionAudio audio) {
        this.audio = audio;
    }


}
