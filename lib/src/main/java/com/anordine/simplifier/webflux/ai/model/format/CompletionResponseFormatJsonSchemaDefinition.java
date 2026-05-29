package com.anordine.simplifier.webflux.ai.model.format;

import com.anordine.simplifier.webflux.ai.util.DeepClonable;
import com.anordine.simplifier.webflux.ai.util.DeepCloneUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionResponseFormatJsonSchemaDefinition
        implements DeepClonable<CompletionResponseFormatJsonSchemaDefinition> {

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

    @Override
    public CompletionResponseFormatJsonSchemaDefinition deepClone() {
        return new CompletionResponseFormatJsonSchemaDefinition(
                this.name,
                this.description,
                DeepCloneUtil.deepCloneStringObjectMap(this.schema),
                this.strict
        );
    }
}
