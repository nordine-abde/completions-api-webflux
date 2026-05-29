package com.anordine.simplifier.webflux.ai.model.tool;

import com.anordine.simplifier.webflux.ai.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionMessageCustomTool implements DeepClonable<CompletionMessageCustomTool> {

    private String input;
    private String name;

    public CompletionMessageCustomTool() {
    }

    public CompletionMessageCustomTool(String input, String name) {
        this.input = input;
        this.name = name;
    }

    public String getInput() {
        return input;
    }

    public String getName() {
        return name;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public CompletionMessageCustomTool deepClone() {
        return new CompletionMessageCustomTool(this.input, this.name);
    }
}
