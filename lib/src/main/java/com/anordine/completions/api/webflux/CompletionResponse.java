package com.anordine.completions.api.webflux;

import com.anordine.completions.api.webflux.message.CompletionChoices;
import com.anordine.completions.api.webflux.usage.CompletionUsage;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionResponse {

    private String id;
    private Long created;
    private String model;
    private String object;
    private List<CompletionChoices> choices;
    private CompletionUsage usage;

    public CompletionResponse() {
    }

    public CompletionResponse(String id,
                              Long created,
                              String model,
                              String object,
                              List<CompletionChoices> choices,
                              CompletionUsage usage) {
        this.id = id;
        this.created = created;
        this.model = model;
        this.object = object;
        this.choices = choices;
        this.usage = usage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<CompletionChoices> getChoices() {
        return choices;
    }

    public void setChoices(List<CompletionChoices> choices) {
        this.choices = choices;
    }

    public CompletionUsage getUsage() {
        return usage;
    }

    public void setUsage(CompletionUsage usage) {
        this.usage = usage;
    }
}
