package com.anordine.completions.api.webflux.model.format;

import com.anordine.completions.api.webflux.model.enums.format.CompletionResponseFormatType;
import com.anordine.completions.api.webflux.model.format.abs.CompletionResponseFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @Override
    public CompletionResponseFormatJsonSchema deepClone() {
        return new CompletionResponseFormatJsonSchema(
                this.jsonSchema == null ? null : this.jsonSchema.deepClone()
        );
    }
}
