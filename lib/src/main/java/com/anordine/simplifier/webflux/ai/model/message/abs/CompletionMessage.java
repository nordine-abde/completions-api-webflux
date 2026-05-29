package com.anordine.simplifier.webflux.ai.model.message.abs;


import com.anordine.simplifier.webflux.ai.model.enums.role.CompletionRole;
import com.anordine.simplifier.webflux.ai.model.enums.role.CompletionRoleValue;
import com.anordine.simplifier.webflux.ai.model.message.CompletionAssistantMessage;
import com.anordine.simplifier.webflux.ai.model.message.CompletionDeveloperMessage;
import com.anordine.simplifier.webflux.ai.model.message.CompletionFunctionMessage;
import com.anordine.simplifier.webflux.ai.model.message.CompletionSystemMessage;
import com.anordine.simplifier.webflux.ai.model.message.CompletionToolMessage;
import com.anordine.simplifier.webflux.ai.model.message.CompletionUserMessage;
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
        property = "role",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = CompletionDeveloperMessage.class,
                name = CompletionRoleValue.DEVELOPER_ROLE
        ),
        @JsonSubTypes.Type(
                value = CompletionSystemMessage.class,
                name = CompletionRoleValue.SYSTEM_ROLE
        ),
        @JsonSubTypes.Type(
                value = CompletionAssistantMessage.class,
                name = CompletionRoleValue.ASSISTANT_ROLE
        ),
        @JsonSubTypes.Type(
                value = CompletionUserMessage.class,
                name = CompletionRoleValue.USER_ROLE
        ),
        @JsonSubTypes.Type(
                value = CompletionToolMessage.class,
                name = CompletionRoleValue.TOOL_ROLE
        ),
        @JsonSubTypes.Type(
                value = CompletionFunctionMessage.class,
                name = CompletionRoleValue.FUNCTION_ROLE
        ),
})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class CompletionMessage implements DeepClonable<CompletionMessage> {
    protected String content;
    protected final CompletionRole role;
    protected String name;

    protected CompletionMessage(String content, CompletionRole role) {
        this(content, role, null);
    }

    protected CompletionMessage(String content, CompletionRole role, String name) {
        this.content = content;
        this.role = role;
        this.name = name;
    }

    protected CompletionMessage(CompletionRole role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public CompletionRole getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public abstract CompletionMessage deepClone();
}
