package com.anordine.completions.api.webflux.model.tool.format.abs;

import com.anordine.completions.api.webflux.model.enums.toolformat.CompletionCustomToolFormatType;
import com.anordine.completions.api.webflux.model.enums.toolformat.CompletionCustomToolFormatTypeValue;
import com.anordine.completions.api.webflux.model.tool.format.CompletionCustomToolGrammarFormat;
import com.anordine.completions.api.webflux.model.tool.format.CompletionCustomToolTextFormat;
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
                value = CompletionCustomToolTextFormat.class,
                name = CompletionCustomToolFormatTypeValue.TEXT
        ),
        @JsonSubTypes.Type(
                value = CompletionCustomToolGrammarFormat.class,
                name = CompletionCustomToolFormatTypeValue.GRAMMAR
        ),
})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class CompletionCustomToolFormat {

    protected final CompletionCustomToolFormatType type;

    protected CompletionCustomToolFormat(CompletionCustomToolFormatType type) {
        this.type = type;
    }

    public CompletionCustomToolFormatType getType() {
        return type;
    }
}
