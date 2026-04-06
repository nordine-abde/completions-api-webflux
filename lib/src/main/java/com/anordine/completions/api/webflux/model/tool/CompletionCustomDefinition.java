package com.anordine.completions.api.webflux.model.tool;

import com.anordine.completions.api.webflux.model.tool.format.abs.CompletionCustomToolFormat;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionCustomDefinition {

    private String name;
    private String description;
    private CompletionCustomToolFormat format;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CompletionCustomToolFormat getFormat() {
        return format;
    }

    public void setFormat(CompletionCustomToolFormat format) {
        this.format = format;
    }
}
