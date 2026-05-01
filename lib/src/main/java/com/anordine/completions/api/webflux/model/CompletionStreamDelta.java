package com.anordine.completions.api.webflux.model;

import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.util.DeepClonable;
import com.anordine.completions.api.webflux.util.DeepCloneUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionStreamDelta implements DeepClonable<CompletionStreamDelta> {

    private String content;
    private CompletionRole role;
    private String refusal;
    private List<CompletionStreamToolCall> toolCalls;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CompletionRole getRole() {
        return role;
    }

    public void setRole(CompletionRole role) {
        this.role = role;
    }

    public String getRefusal() {
        return refusal;
    }

    public void setRefusal(String refusal) {
        this.refusal = refusal;
    }

    public List<CompletionStreamToolCall> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<CompletionStreamToolCall> toolCalls) {
        this.toolCalls = toolCalls;
    }

    @Override
    public CompletionStreamDelta deepClone() {
        CompletionStreamDelta clone = new CompletionStreamDelta();
        clone.setContent(this.content);
        clone.setRole(this.role);
        clone.setRefusal(this.refusal);
        clone.setToolCalls(DeepCloneUtil.deepCloneList(this.toolCalls));
        return clone;
    }
}
