package com.anordine.completions.api.webflux.message;

public class CompletionAudio {

    private String id;

    public CompletionAudio() {

    }
    public CompletionAudio(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
