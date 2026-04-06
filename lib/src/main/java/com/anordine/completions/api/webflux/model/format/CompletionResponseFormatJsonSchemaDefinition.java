package com.anordine.completions.api.webflux.model.format;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionResponseFormatJsonSchemaDefinition {

    private String name;
    private String description;
    private Map<String, Object> schema;
    private Boolean strict;

    public CompletionResponseFormatJsonSchemaDefinition() {
    }

    public CompletionResponseFormatJsonSchemaDefinition(String name,
                                                        String description,
                                                        Map<String, Object> schema,
                                                        Boolean strict) {
        this.name = name;
        this.description = description;
        this.schema = schema;
        this.strict = strict;
    }

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

    public Map<String, Object> getSchema() {
        return schema;
    }

    public void setSchema(Map<String, Object> schema) {
        this.schema = schema;
    }

    public Boolean getStrict() {
        return strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }
}
