package com.anordine.completions.api.webflux.model.usage;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionUsage {

    private Integer completionTokens;
    private Integer promptTokens;
    private Integer totalTokens;

    private CompletionTokensDetails completionTokensDetails;
    private PromptTokenDetails promptTokensDetails;

    public CompletionUsage() {
    }

    public CompletionUsage(Integer completionTokens,
                           Integer promptTokens,
                           Integer totalTokens,
                           CompletionTokensDetails completionTokensDetails,
                           PromptTokenDetails promptTokensDetails) {
        this.completionTokens = completionTokens;
        this.promptTokens = promptTokens;
        this.totalTokens = totalTokens;
        this.completionTokensDetails = completionTokensDetails;
        this.promptTokensDetails = promptTokensDetails;
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

    public CompletionTokensDetails getCompletionTokensDetails() {
        return completionTokensDetails;
    }

    public void setCompletionTokensDetails(CompletionTokensDetails completionTokensDetails) {
        this.completionTokensDetails = completionTokensDetails;
    }

    public PromptTokenDetails getPromptTokensDetails() {
        return promptTokensDetails;
    }

    public void setPromptTokensDetails(PromptTokenDetails promptTokensDetails) {
        this.promptTokensDetails = promptTokensDetails;
    }
}
