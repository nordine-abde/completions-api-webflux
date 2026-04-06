package com.anordine.completions.api.webflux.model.toolchoice.abs;

import com.anordine.completions.api.webflux.model.enums.toolchoice.CompletionToolChoiceType;
import com.anordine.completions.api.webflux.model.enums.toolchoice.CompletionToolChoiceTypeValue;
import com.anordine.completions.api.webflux.model.toolchoice.CompletionNamedCustomToolChoice;
import com.anordine.completions.api.webflux.model.toolchoice.CompletionNamedFunctionToolChoice;
import com.anordine.completions.api.webflux.model.toolchoice.CompletionToolChoiceAllowedTools;
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
                value = CompletionNamedFunctionToolChoice.class,
                name = CompletionToolChoiceTypeValue.FUNCTION
        ),
        @JsonSubTypes.Type(
                value = CompletionNamedCustomToolChoice.class,
                name = CompletionToolChoiceTypeValue.CUSTOM
        ),
        @JsonSubTypes.Type(
                value = CompletionToolChoiceAllowedTools.class,
                name = CompletionToolChoiceTypeValue.ALLOWED_TOOLS
        ),
})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class CompletionToolChoiceOption implements ToolChoiceOptionInterface {

    protected final CompletionToolChoiceType type;

    protected CompletionToolChoiceOption(CompletionToolChoiceType type) {
        this.type = type;
    }

    public CompletionToolChoiceType getType() {
        return type;
    }
}
