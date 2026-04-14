package com.anordine.completions.api.webflux.model.toolchoice;

import com.anordine.completions.api.webflux.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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
