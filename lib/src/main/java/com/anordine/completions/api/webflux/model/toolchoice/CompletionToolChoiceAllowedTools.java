package com.anordine.completions.api.webflux.model.toolchoice;

import com.anordine.completions.api.webflux.model.enums.toolchoice.CompletionToolChoiceType;
import com.anordine.completions.api.webflux.model.toolchoice.abs.CompletionToolChoiceOption;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionToolChoiceAllowedTools extends CompletionToolChoiceOption {

    private CompletionAllowedTools allowedTools;

    public CompletionToolChoiceAllowedTools() {
        super(CompletionToolChoiceType.ALLOWED_TOOLS);
    }

    public CompletionToolChoiceAllowedTools(CompletionAllowedTools allowedTools) {
        super(CompletionToolChoiceType.ALLOWED_TOOLS);
        this.allowedTools = allowedTools;
    }

    public CompletionAllowedTools getAllowedTools() {
        return allowedTools;
    }

    public void setAllowedTools(CompletionAllowedTools allowedTools) {
        this.allowedTools = allowedTools;
    }

    @Override
    public CompletionToolChoiceAllowedTools deepClone() {
        return new CompletionToolChoiceAllowedTools(
                this.allowedTools == null ? null : this.allowedTools.deepClone()
        );
    }
}
