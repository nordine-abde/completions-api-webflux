package com.anordine.completions.api.webflux.tools.abs;

import com.anordine.completions.api.webflux.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.enums.tool.CompletionToolTypeValue;
import com.anordine.completions.api.webflux.tools.CompletionCustomToolCall;
import com.anordine.completions.api.webflux.tools.CompletionFunctionToolCall;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = CompletionFunctionToolCall.class,
                name = CompletionToolTypeValue.FUNCTION
        ),
        @JsonSubTypes.Type(
                value = CompletionCustomToolCall.class,
                name = CompletionToolTypeValue.CUSTOM
        ),
})
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
