package com.anordine.completions.api.webflux.model.tool;

import com.anordine.completions.api.webflux.model.tool.format.abs.CompletionCustomToolFormat;
import com.anordine.completions.api.webflux.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionCustomDefinition implements DeepClonable<CompletionCustomDefinition> {

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

    @Override
    public CompletionCustomDefinition deepClone() {
        CompletionCustomDefinition clone = new CompletionCustomDefinition();
        clone.setName(this.name);
        clone.setDescription(this.description);
        clone.setFormat(this.format == null ? null : this.format.deepClone());
        return clone;
    }
}
