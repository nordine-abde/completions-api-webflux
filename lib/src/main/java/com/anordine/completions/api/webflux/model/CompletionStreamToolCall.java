package com.anordine.completions.api.webflux.model;

import com.anordine.completions.api.webflux.model.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.model.tool.CompletionMessageCustomTool;
import com.anordine.completions.api.webflux.model.tool.CompletionMessageFunctionTool;
import com.anordine.completions.api.webflux.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionStreamToolCall implements DeepClonable<CompletionStreamToolCall> {

    private Integer index;
    private String id;
    private CompletionToolType type;
    private CompletionMessageFunctionTool function;
    private CompletionMessageCustomTool custom;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CompletionToolType getType() {
        return type;
    }

    public void setType(CompletionToolType type) {
        this.type = type;
    }

    public CompletionMessageFunctionTool getFunction() {
        return function;
    }

    public void setFunction(CompletionMessageFunctionTool function) {
        this.function = function;
    }

    public CompletionMessageCustomTool getCustom() {
        return custom;
    }

    public void setCustom(CompletionMessageCustomTool custom) {
        this.custom = custom;
    }

    @Override
    public CompletionStreamToolCall deepClone() {
        CompletionStreamToolCall clone = new CompletionStreamToolCall();
        clone.setIndex(this.index);
        clone.setId(this.id);
        clone.setType(this.type);
        clone.setFunction(this.function == null ? null : this.function.deepClone());
        clone.setCustom(this.custom == null ? null : this.custom.deepClone());
        return clone;
    }
}
