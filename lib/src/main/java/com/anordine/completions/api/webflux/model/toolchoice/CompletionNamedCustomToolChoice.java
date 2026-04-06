package com.anordine.completions.api.webflux.model.toolchoice;

import com.anordine.completions.api.webflux.model.enums.toolchoice.CompletionToolChoiceType;
import com.anordine.completions.api.webflux.model.toolchoice.abs.CompletionToolChoiceOption;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionNamedCustomToolChoice extends CompletionToolChoiceOption {

    private CompletionToolChoiceName custom;

    public CompletionNamedCustomToolChoice() {
        super(CompletionToolChoiceType.CUSTOM);
    }

    public CompletionNamedCustomToolChoice(CompletionToolChoiceName custom) {
        super(CompletionToolChoiceType.CUSTOM);
        this.custom = custom;
    }

    public CompletionToolChoiceName getCustom() {
        return custom;
    }

    public void setCustom(CompletionToolChoiceName custom) {
        this.custom = custom;
    }
}
