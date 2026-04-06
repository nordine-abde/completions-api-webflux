package com.anordine.completions.api.webflux.tools;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionCustom {

    private String input;
    private String name;

    public CompletionCustom() {
    }

    public CompletionCustom(String input, String name) {
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
