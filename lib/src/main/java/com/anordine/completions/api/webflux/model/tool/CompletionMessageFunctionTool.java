package com.anordine.completions.api.webflux.model.tool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionMessageFunctionTool {

    private String arguments;
    private String name;

    public CompletionMessageFunctionTool() {
    }

    public CompletionMessageFunctionTool(String arguments, String name) {
        this.arguments = arguments;
        this.name = name;
    }

    public String getArguments() {
        return arguments;
    }

    public String getName() {
        return name;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public void setName(String name) {
        this.name = name;
    }
}
