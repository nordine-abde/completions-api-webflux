package com.anordine.completions.api.webflux.format.abs;

import com.anordine.completions.api.webflux.enums.format.CompletionResponseFormatType;
import com.anordine.completions.api.webflux.enums.format.CompletionResponseFormatTypeValue;
import com.anordine.completions.api.webflux.format.CompletionResponseFormatJsonObject;
import com.anordine.completions.api.webflux.format.CompletionResponseFormatJsonSchema;
import com.anordine.completions.api.webflux.format.CompletionResponseFormatText;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = CompletionResponseFormatText.class,
                name = CompletionResponseFormatTypeValue.TEXT
        ),
        @JsonSubTypes.Type(
                value = CompletionResponseFormatJsonSchema.class,
                name = CompletionResponseFormatTypeValue.JSON_SCHEMA
        ),
        @JsonSubTypes.Type(
                value = CompletionResponseFormatJsonObject.class,
                name = CompletionResponseFormatTypeValue.JSON_OBJECT
        ),
})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class CompletionResponseFormat {

    protected final CompletionResponseFormatType type;

    protected CompletionResponseFormat(CompletionResponseFormatType type) {
        this.type = type;
    }

    public CompletionResponseFormatType getType() {
        return type;
    }
}
