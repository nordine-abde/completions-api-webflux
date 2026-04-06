package com.anordine.completions.api.webflux.tools;

public class CompletionFunction {

    private String arguments;
    private String name;

    public CompletionFunction() {
    }

    public CompletionFunction(String arguments, String name) {
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
