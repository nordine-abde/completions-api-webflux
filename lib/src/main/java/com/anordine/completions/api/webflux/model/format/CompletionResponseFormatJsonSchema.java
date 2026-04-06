package com.anordine.completions.api.webflux.model.format;

import com.anordine.completions.api.webflux.model.enums.format.CompletionResponseFormatType;
import com.anordine.completions.api.webflux.model.format.abs.CompletionResponseFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionResponseFormatJsonSchema extends CompletionResponseFormat {

    protected CompletionResponseFormatJsonSchema() {
        super(CompletionResponseFormatType.JSON_SCHEMA);
    }

    private CompletionResponseFormatJsonSchemaDefinition jsonSchema;

    public CompletionResponseFormatJsonSchema(CompletionResponseFormatJsonSchemaDefinition jsonSchema) {
        super(CompletionResponseFormatType.JSON_SCHEMA);
        this.jsonSchema = jsonSchema;
    }

    public CompletionResponseFormatJsonSchemaDefinition getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(CompletionResponseFormatJsonSchemaDefinition jsonSchema) {
        this.jsonSchema = jsonSchema;
    }
}
