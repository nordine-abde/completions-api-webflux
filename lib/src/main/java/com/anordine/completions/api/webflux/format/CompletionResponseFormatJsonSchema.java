package com.anordine.completions.api.webflux.format;

import com.anordine.completions.api.webflux.enums.format.CompletionResponseFormatType;
import com.anordine.completions.api.webflux.format.abs.CompletionResponseFormat;

import java.util.Map;

public class CompletionResponseFormatJsonSchema extends CompletionResponseFormat {

    protected CompletionResponseFormatJsonSchema() {
        super(CompletionResponseFormatType.JSON_SCHEMA);
    }

    private String name;
    private String description;
    private Map<String, Object> schema;
    private Boolean strict;

    public CompletionResponseFormatJsonSchema(String name,
                                              String description,
                                              Map<String, Object> schema,
                                              Boolean strict) {
        super(CompletionResponseFormatType.JSON_SCHEMA);
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
