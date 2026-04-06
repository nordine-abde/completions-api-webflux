package com.anordine.completions.api.webflux.model.tool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionMessageCustomTool {

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
}
