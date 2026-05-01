package com.anordine.completions.api.webflux.model.tool.format.abs;

import com.anordine.completions.api.webflux.model.enums.toolformat.CompletionCustomToolFormatType;
import com.anordine.completions.api.webflux.model.enums.toolformat.CompletionCustomToolFormatTypeValue;
import com.anordine.completions.api.webflux.model.tool.format.CompletionCustomToolGrammarFormat;
import com.anordine.completions.api.webflux.model.tool.format.CompletionCustomToolTextFormat;
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
                value = CompletionCustomToolTextFormat.class,
                name = CompletionCustomToolFormatTypeValue.TEXT
        ),
        @JsonSubTypes.Type(
                value = CompletionCustomToolGrammarFormat.class,
                name = CompletionCustomToolFormatTypeValue.GRAMMAR
        ),
})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class CompletionCustomToolFormat implements DeepClonable<CompletionCustomToolFormat> {

    protected final CompletionCustomToolFormatType type;

    protected CompletionCustomToolFormat(CompletionCustomToolFormatType type) {
        this.type = type;
    }

    public CompletionCustomToolFormatType getType() {
        return type;
    }

    @Override
    public abstract CompletionCustomToolFormat deepClone();
}
