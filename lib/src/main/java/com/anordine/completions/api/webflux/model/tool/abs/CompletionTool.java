package com.anordine.completions.api.webflux.model.tool.abs;

import com.anordine.completions.api.webflux.model.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.model.enums.tool.CompletionToolTypeValue;
import com.anordine.completions.api.webflux.model.tool.CompletionCustomTool;
import com.anordine.completions.api.webflux.model.tool.CompletionFunctionTool;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = CompletionFunctionTool.class,
                name = CompletionToolTypeValue.FUNCTION
        ),
        @JsonSubTypes.Type(
                value = CompletionCustomTool.class,
                name = CompletionToolTypeValue.CUSTOM
        ),
})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class CompletionTool {

    protected final CompletionToolType type;

    public CompletionToolType getType() {
        return type;
    }

    protected CompletionTool(CompletionToolType type) {
        this.type = type;
    }
}
