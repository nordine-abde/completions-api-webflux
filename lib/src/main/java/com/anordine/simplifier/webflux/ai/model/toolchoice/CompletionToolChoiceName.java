package com.anordine.simplifier.webflux.ai.model.toolchoice;

import com.anordine.simplifier.webflux.ai.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompletionToolChoiceName implements DeepClonable<CompletionToolChoiceName> {

    private String name;

    public CompletionToolChoiceName() {
    }

    public CompletionToolChoiceName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public CompletionToolChoiceName deepClone() {
        return new CompletionToolChoiceName(this.name);
    }
}
