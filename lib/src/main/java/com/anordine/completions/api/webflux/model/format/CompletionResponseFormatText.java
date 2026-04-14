package com.anordine.completions.api.webflux.model.format;

import com.anordine.completions.api.webflux.model.enums.format.CompletionResponseFormatType;
import com.anordine.completions.api.webflux.model.format.abs.CompletionResponseFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionResponseFormatText extends CompletionResponseFormat {

    public CompletionResponseFormatText() {
        super(CompletionResponseFormatType.TEXT);
    }

    @Override
    public CompletionResponseFormatText deepClone() {
        return new CompletionResponseFormatText();
    }
}
