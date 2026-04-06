package com.anordine.completions.api.webflux.model.tool.abs;

import com.anordine.completions.api.webflux.model.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.model.enums.tool.CompletionToolTypeValue;
import com.anordine.completions.api.webflux.model.tool.CompletionMessageCustomToolCall;
import com.anordine.completions.api.webflux.model.tool.CompletionMessageFunctionToolCall;
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
                value = CompletionMessageFunctionToolCall.class,
                name = CompletionToolTypeValue.FUNCTION
        ),
        @JsonSubTypes.Type(
                value = CompletionMessageCustomToolCall.class,
                name = CompletionToolTypeValue.CUSTOM
        ),
})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class CompletionToolCall {

    protected String id;
    protected final CompletionToolType type;

    protected CompletionToolCall(String id, CompletionToolType type) {
        this.id = id;
        this.type = type;
    }

    protected CompletionToolCall(CompletionToolType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public CompletionToolType getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }
}
