package com.anordine.completions.api.webflux.model;

import com.anordine.completions.api.webflux.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionStreamOptions implements DeepClonable<CompletionStreamOptions> {

    private Boolean includeObfuscation;
    private Boolean includeUsage;

    public CompletionStreamOptions() {
    }

    public CompletionStreamOptions(Boolean includeUsage, Boolean includeObfuscation) {
        this.includeUsage = includeUsage;
        this.includeObfuscation = includeObfuscation;
    }

    public Boolean getIncludeObfuscation() {
        return includeObfuscation;
    }

    public void setIncludeObfuscation(Boolean includeObfuscation) {
        this.includeObfuscation = includeObfuscation;
    }

    public Boolean getIncludeUsage() {
        return includeUsage;
    }

    public void setIncludeUsage(Boolean includeUsage) {
        this.includeUsage = includeUsage;
    }

    public CompletionStreamOptions withIncludeUsage(Boolean includeUsage) {
        this.includeUsage = includeUsage;
        return this;
    }

    public CompletionStreamOptions withIncludeObfuscation(Boolean includeObfuscation) {
        this.includeObfuscation = includeObfuscation;
        return this;
    }

    @Override
    public CompletionStreamOptions deepClone() {
        return new CompletionStreamOptions(this.includeUsage, this.includeObfuscation);
    }
}
