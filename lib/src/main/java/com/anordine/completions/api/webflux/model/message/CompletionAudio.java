package com.anordine.completions.api.webflux.model.message;


import com.anordine.completions.api.webflux.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionAudio implements DeepClonable<CompletionAudio> {

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

    @Override
    public CompletionAudio deepClone() {
        return new CompletionAudio(this.id);
    }
}
