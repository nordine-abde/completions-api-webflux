package com.anordine.simplifier.webflux.ai.model.tool.abs;

import com.anordine.simplifier.webflux.ai.model.enums.tool.CompletionToolType;
import com.anordine.simplifier.webflux.ai.model.enums.tool.CompletionToolTypeValue;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionMessageCustomToolCall;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionMessageFunctionToolCall;
import com.anordine.simplifier.webflux.ai.util.DeepClonable;
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
                value = CompletionMessageFunctionToolCall.class,
                name = CompletionToolTypeValue.FUNCTION
        ),
        @JsonSubTypes.Type(
                value = CompletionMessageCustomToolCall.class,
                name = CompletionToolTypeValue.CUSTOM
        ),
})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class CompletionToolCall implements DeepClonable<CompletionToolCall> {

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

    @Override
    public abstract CompletionToolCall deepClone();
}
