package com.anordine.simplifier.webflux.ai.model.format;

import com.anordine.simplifier.webflux.ai.model.enums.format.CompletionResponseFormatType;
import com.anordine.simplifier.webflux.ai.model.format.abs.CompletionResponseFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompletionResponseFormatText extends CompletionResponseFormat {

    public CompletionResponseFormatText() {
        super(CompletionResponseFormatType.TEXT);
    }

    @Override
    public CompletionResponseFormatText deepClone() {
        return new CompletionResponseFormatText();
    }
}
