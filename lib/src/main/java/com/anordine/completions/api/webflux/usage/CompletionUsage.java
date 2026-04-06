package com.anordine.completions.api.webflux.usage;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionUsage {

    private Integer completionTokens;
    private Integer promptTokens;
    private Integer totalTokens;

    private CompletionTokensDetails completionTokenDetails;
    private PromptTokenDetails promptTokenDetails;

    public CompletionUsage() {
    }

    public CompletionUsage(Integer completionTokens,
                           Integer promptTokens,
                           Integer totalTokens,
                           CompletionTokensDetails completionTokenDetails,
                           PromptTokenDetails promptTokenDetails) {
        this.completionTokens = completionTokens;
        this.promptTokens = promptTokens;
        this.totalTokens = totalTokens;
        this.completionTokenDetails = completionTokenDetails;
        this.promptTokenDetails = promptTokenDetails;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens;
    }

    public Integer getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
    }

    public CompletionTokensDetails getCompletionTokenDetails() {
        return completionTokenDetails;
    }

    public void setCompletionTokenDetails(CompletionTokensDetails completionTokenDetails) {
        this.completionTokenDetails = completionTokenDetails;
    }

    public PromptTokenDetails getPromptTokenDetails() {
        return promptTokenDetails;
    }

    public void setPromptTokenDetails(PromptTokenDetails promptTokenDetails) {
        this.promptTokenDetails = promptTokenDetails;
    }
}
