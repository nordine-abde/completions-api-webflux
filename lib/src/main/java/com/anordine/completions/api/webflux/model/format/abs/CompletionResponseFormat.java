package com.anordine.completions.api.webflux.model.format.abs;

import com.anordine.completions.api.webflux.model.enums.format.CompletionResponseFormatType;
import com.anordine.completions.api.webflux.model.enums.format.CompletionResponseFormatTypeValue;
import com.anordine.completions.api.webflux.model.format.CompletionResponseFormatJsonObject;
import com.anordine.completions.api.webflux.model.format.CompletionResponseFormatJsonSchema;
import com.anordine.completions.api.webflux.model.format.CompletionResponseFormatText;
import com.anordine.completions.api.webflux.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
public abstract class CompletionResponseFormat implements DeepClonable<CompletionResponseFormat> {

    protected final CompletionResponseFormatType type;

    protected CompletionResponseFormat(CompletionResponseFormatType type) {
        this.type = type;
    }

    public CompletionResponseFormatType getType() {
        return type;
    }

    @Override
    public abstract CompletionResponseFormat deepClone();
}
