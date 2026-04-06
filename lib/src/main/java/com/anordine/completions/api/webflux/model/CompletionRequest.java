package com.anordine.completions.api.webflux.model;

import com.anordine.completions.api.webflux.model.enums.modality.CompletionModality;
import com.anordine.completions.api.webflux.model.enums.prompt.CompletionPromptCacheRetention;
import com.anordine.completions.api.webflux.model.enums.resoning.CompletionReasoningEffort;
import com.anordine.completions.api.webflux.model.enums.verbosity.CompletionVerbosity;
import com.anordine.completions.api.webflux.model.format.abs.CompletionResponseFormat;
import com.anordine.completions.api.webflux.model.message.abs.CompletionMessage;
import com.anordine.completions.api.webflux.model.tool.abs.CompletionTool;
import com.anordine.completions.api.webflux.model.toolchoice.abs.ToolChoiceOptionInterface;
import com.anordine.completions.api.webflux.model.toolchoice.jackson.ToolChoiceOptionInterfaceDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionRequest {

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
    private Double temperature;
    @JsonDeserialize(using = ToolChoiceOptionInterfaceDeserializer.class)
    private ToolChoiceOptionInterface toolChoice;
    private List<CompletionTool> tools;
    private Boolean logprobs;
    private Integer topLogprobs;
    private Double topP;
    private CompletionVerbosity verbosity;

    public CompletionVerbosity getVerbosity() {
        return verbosity;
    }

    public void setVerbosity(CompletionVerbosity verbosity) {
        this.verbosity = verbosity;
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
}
