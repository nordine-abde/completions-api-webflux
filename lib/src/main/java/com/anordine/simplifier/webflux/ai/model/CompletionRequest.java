package com.anordine.simplifier.webflux.ai.model;

import com.anordine.simplifier.webflux.ai.model.enums.modality.CompletionModality;
import com.anordine.simplifier.webflux.ai.model.enums.prompt.CompletionPromptCacheRetention;
import com.anordine.simplifier.webflux.ai.model.enums.resoning.CompletionReasoningEffort;
import com.anordine.simplifier.webflux.ai.model.enums.role.CompletionRole;
import com.anordine.simplifier.webflux.ai.model.enums.verbosity.CompletionVerbosity;
import com.anordine.simplifier.webflux.ai.model.format.abs.CompletionResponseFormat;
import com.anordine.simplifier.webflux.ai.model.message.*;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionFunctionDefinition;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionFunctionTool;
import com.anordine.simplifier.webflux.ai.model.message.abs.CompletionMessage;
import com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool;
import com.anordine.simplifier.webflux.ai.model.toolchoice.abs.ToolChoiceOptionInterface;
import com.anordine.simplifier.webflux.ai.model.toolchoice.jackson.ToolChoiceOptionInterfaceDeserializer;
import com.anordine.simplifier.webflux.ai.util.DeepClonable;
import com.anordine.simplifier.webflux.ai.util.DeepCloneUtil;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionRequest implements DeepClonable<CompletionRequest> {

    private List<CompletionMessage> messages;
    private String model;
    private Double frequencyPenalty;
    private Integer maxCompletionTokens;
    private List<CompletionModality> modalities;
    private Integer n;
    private Boolean parallelToolCalls;
    private Double presencePenalty;
    private String promptCacheKey;
    private CompletionPromptCacheRetention promptCacheRetention;
    private CompletionReasoningEffort reasoningEffort;
    private CompletionResponseFormat responseFormat;
    private String safetyIdentifier;
    private Boolean store;
    private Boolean stream;
    private CompletionStreamOptions streamOptions;
    private Double temperature;
    @JsonDeserialize(using = ToolChoiceOptionInterfaceDeserializer.class)
    private ToolChoiceOptionInterface toolChoice;
    private List<CompletionTool> tools;
    private Boolean logprobs;
    private Integer topLogprobs;
    private Double topP;
    private CompletionVerbosity verbosity;
    private Map<String, Object> extraBody;

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public CompletionStreamOptions getStreamOptions() {
        return streamOptions;
    }

    public void setStreamOptions(CompletionStreamOptions streamOptions) {
        this.streamOptions = streamOptions;
    }

    public CompletionVerbosity getVerbosity() {
        return verbosity;
    }

    public void setVerbosity(CompletionVerbosity verbosity) {
        this.verbosity = verbosity;
    }

    @JsonIgnore
    public Map<String, Object> getExtraBody() {
        return extraBody;
    }

    public void setExtraBody(Map<String, Object> extraBody) {
        this.extraBody = extraBody == null ? null : new LinkedHashMap<>(extraBody);
    }

    @JsonAnyGetter
    Map<String, Object> getExtraBodyProperties() {
        return extraBody;
    }

    @JsonAnySetter
    public void putExtraBody(String name, Object value) {
        if (extraBody == null) {
            extraBody = new LinkedHashMap<>();
        }
        extraBody.put(name, value);
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public ToolChoiceOptionInterface getToolChoice() {
        return toolChoice;
    }

    public void setToolChoice(ToolChoiceOptionInterface toolChoice) {
        this.toolChoice = toolChoice;
    }

    public List<CompletionTool> getTools() {
        return tools;
    }

    public void setTools(List<CompletionTool> tools) {
        this.tools = tools;
    }

    public Boolean getStore() {
        return store;
    }

    public void setStore(Boolean store) {
        this.store = store;
    }

    public String getSafetyIdentifier() {
        return safetyIdentifier;
    }

    public void setSafetyIdentifier(String safetyIdentifier) {
        this.safetyIdentifier = safetyIdentifier;
    }

    public CompletionResponseFormat getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(CompletionResponseFormat responseFormat) {
        this.responseFormat = responseFormat;
    }

    public CompletionReasoningEffort getReasoningEffort() {
        return reasoningEffort;
    }

    public void setReasoningEffort(CompletionReasoningEffort reasoningEffort) {
        this.reasoningEffort = reasoningEffort;
    }

    public String getPromptCacheKey() {
        return promptCacheKey;
    }

    public void setPromptCacheKey(String promptCacheKey) {
        this.promptCacheKey = promptCacheKey;
    }

    public CompletionPromptCacheRetention getPromptCacheRetention() {
        return promptCacheRetention;
    }

    public void setPromptCacheRetention(CompletionPromptCacheRetention promptCacheRetention) {
        this.promptCacheRetention = promptCacheRetention;
    }

    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Double presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public Boolean getParallelToolCalls() {
        return parallelToolCalls;
    }

    public void setParallelToolCalls(Boolean parallelToolCalls) {
        this.parallelToolCalls = parallelToolCalls;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public List<CompletionModality> getModalities() {
        return modalities;
    }

    public void setModalities(List<CompletionModality> modalities) {
        this.modalities = modalities;
    }

    public Integer getMaxCompletionTokens() {
        return maxCompletionTokens;
    }

    public void setMaxCompletionTokens(Integer maxCompletionTokens) {
        this.maxCompletionTokens = maxCompletionTokens;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(Double frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<CompletionMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<CompletionMessage> messages) {
        this.messages = messages;
    }

    public Boolean getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(Boolean logprobs) {
        this.logprobs = logprobs;
    }

    public Integer getTopLogprobs() {
        return topLogprobs;
    }

    public void setTopLogprobs(Integer topLogprobs) {
        this.topLogprobs = topLogprobs;
    }

    public static CompletionRequest create(String message) {
        return new CompletionRequest().addUserMessage(message);
    }

    public static CompletionRequest create(String message, CompletionRole role) {
        return new CompletionRequest().addMessage(message, role);
    }

    public static CompletionRequest create(String model, String message) {
        return new CompletionRequest()
                .withModel(model)
                .addUserMessage(message);
    }

    public static CompletionRequest create(String model, String message, CompletionRole role) {
        return new CompletionRequest()
                .withModel(model)
                .addMessage(message, role);
    }

    public CompletionRequest withModel(String model) {
        this.model = model;
        return this;
    }

    public CompletionRequest withTemperature(Double temperature) {
        this.temperature = temperature;
        return this;
    }

    public CompletionRequest withReasoningEffort(CompletionReasoningEffort reasoningEffort) {
        this.reasoningEffort = reasoningEffort;
        return this;
    }

    public CompletionRequest withResponseFormat(CompletionResponseFormat responseFormat) {
        this.responseFormat = responseFormat;
        return this;
    }

    public CompletionRequest withToolChoice(ToolChoiceOptionInterface toolChoice) {
        this.toolChoice = toolChoice;
        return this;
    }

    public CompletionRequest withParallelToolCalls(Boolean parallelToolCalls) {
        this.parallelToolCalls = parallelToolCalls;
        return this;
    }

    public CompletionRequest withStore(Boolean store) {
        this.store = store;
        return this;
    }

    public CompletionRequest withStream(Boolean stream) {
        this.stream = stream;
        return this;
    }

    public CompletionRequest withStreamOptions(CompletionStreamOptions streamOptions) {
        this.streamOptions = streamOptions;
        return this;
    }

    public CompletionRequest withExtraBody(Map<String, Object> extraBody) {
        setExtraBody(extraBody);
        return this;
    }

    public CompletionRequest withExtraBodyProperty(String name, Object value) {
        putExtraBody(name, value);
        return this;
    }

    public CompletionRequest addMessage(CompletionMessage completionMessage) {
        if (completionMessage == null) {
            throw new IllegalArgumentException("completionMessage must not be null");
        }
        if (messages == null) {
            this.messages = new ArrayList<>();
        }
        messages.add(completionMessage);
        return this;
    }

    public CompletionRequest addMessages(CompletionMessage... completionMessages) {
        if (completionMessages == null) {
            return this;
        }
        Arrays.stream(completionMessages).forEach(this::addMessage);
        return this;
    }

    public CompletionRequest addMessage(String content, CompletionRole role) {
        if (role == null) {
            role = CompletionRole.USER;
        }

        CompletionMessage completionMessage;

        switch (role) {
            case USER -> completionMessage = new CompletionUserMessage(content);
            case SYSTEM -> completionMessage = new CompletionSystemMessage(content);
            case DEVELOPER -> completionMessage = new CompletionDeveloperMessage(content);
            case TOOL -> completionMessage = new CompletionToolMessage(content);
            case FUNCTION -> completionMessage = new CompletionFunctionMessage(content);
            case ASSISTANT -> completionMessage = new CompletionAssistantMessage(content);
            default -> throw new IllegalArgumentException("invalid role");
        }

        this.addMessage(completionMessage);
        return this;
    }

    public CompletionRequest addDeveloperMessage(String content) {
        return this.addMessage(new CompletionDeveloperMessage(content));
    }

    public CompletionRequest addDeveloperMessage(String content, String name) {
        return this.addMessage(new CompletionDeveloperMessage(content, name));
    }

    public CompletionRequest addSystemMessage(String content) {
        return this.addMessage(new CompletionSystemMessage(content));
    }

    public CompletionRequest addSystemMessage(String content, String name) {
        return this.addMessage(new CompletionSystemMessage(content, name));
    }

    public CompletionRequest addUserMessage(String content) {
        return this.addMessage(new CompletionUserMessage(content));
    }

    public CompletionRequest addUserMessage(String content, String name) {
        return this.addMessage(new CompletionUserMessage(content, name));
    }

    public CompletionRequest addAssistantMessage(String content) {
        return this.addMessage(new CompletionAssistantMessage(content));
    }

    public CompletionRequest addAssistantMessage(String content, String name) {
        return this.addMessage(new CompletionAssistantMessage(content, name));
    }

    public CompletionRequest addFunctionMessage(String name, String content) {
        return this.addMessage(new CompletionFunctionMessage(content, name));
    }

    public CompletionRequest addToolMessage(String toolCallId, String content) {
        CompletionToolMessage toolMessage = new CompletionToolMessage(content);
        toolMessage.setToolCallId(toolCallId);
        return this.addMessage(toolMessage);
    }

    public CompletionRequest addTool(CompletionTool tool) {
        if (tool == null) {
            throw new IllegalArgumentException("tool must not be null");
        }
        if (tools == null) {
            this.tools = new ArrayList<>();
        }
        tools.add(tool);
        return this;
    }

    public CompletionRequest addTools(CompletionTool... completionTools) {
        if (completionTools == null) {
            return this;
        }
        Arrays.stream(completionTools).forEach(this::addTool);
        return this;
    }

    public CompletionRequest addModality(CompletionModality modality) {
        if (modality == null) {
            throw new IllegalArgumentException("modality must not be null");
        }
        if (modalities == null) {
            this.modalities = new ArrayList<>();
        }
        modalities.add(modality);
        return this;
    }

    public CompletionRequest addFunctionTool(CompletionFunctionDefinition functionDefinition) {
        return this.addTool(new CompletionFunctionTool(functionDefinition));
    }

    public CompletionRequest addFunctionTool(String name, String description, java.util.Map<String, Object> parameters) {
        CompletionFunctionDefinition functionDefinition = new CompletionFunctionDefinition();
        functionDefinition.setName(name);
        functionDefinition.setDescription(description);
        functionDefinition.setParameters(parameters);
        return this.addFunctionTool(functionDefinition);
    }

    public CompletionRequest addMessage(String message) {
        return this.addUserMessage(message);
    }

    @Override
    public CompletionRequest deepClone() {
        CompletionRequest clone = new CompletionRequest();
        clone.setMessages(cloneMessages());
        clone.setModel(this.model);
        clone.setFrequencyPenalty(this.frequencyPenalty);
        clone.setMaxCompletionTokens(this.maxCompletionTokens);
        clone.setModalities(DeepCloneUtil.deepCloneList(this.modalities));
        clone.setN(this.n);
        clone.setParallelToolCalls(this.parallelToolCalls);
        clone.setPresencePenalty(this.presencePenalty);
        clone.setPromptCacheKey(this.promptCacheKey);
        clone.setPromptCacheRetention(this.promptCacheRetention);
        clone.setReasoningEffort(this.reasoningEffort);
        clone.setResponseFormat(this.responseFormat == null ? null : this.responseFormat.deepClone());
        clone.setSafetyIdentifier(this.safetyIdentifier);
        clone.setStore(this.store);
        clone.setStream(this.stream);
        clone.setStreamOptions(this.streamOptions == null ? null : this.streamOptions.deepClone());
        clone.setTemperature(this.temperature);
        clone.setToolChoice(this.toolChoice == null ? null : this.toolChoice.deepClone());
        clone.setTools(cloneTools());
        clone.setLogprobs(this.logprobs);
        clone.setTopLogprobs(this.topLogprobs);
        clone.setTopP(this.topP);
        clone.setVerbosity(this.verbosity);
        clone.setExtraBody(DeepCloneUtil.deepCloneStringObjectMap(this.extraBody));
        return clone;
    }

    private List<CompletionMessage> cloneMessages() {
        if (this.messages == null) {
            return null;
        }
        List<CompletionMessage> clonedMessages = new ArrayList<>(this.messages.size());
        for (CompletionMessage message : this.messages) {
            clonedMessages.add(message == null ? null : message.deepClone());
        }
        return clonedMessages;
    }

    private List<CompletionTool> cloneTools() {
        if (this.tools == null) {
            return null;
        }
        List<CompletionTool> clonedTools = new ArrayList<>(this.tools.size());
        for (CompletionTool tool : this.tools) {
            clonedTools.add(tool == null ? null : tool.deepClone());
        }
        return clonedTools;
    }
}
