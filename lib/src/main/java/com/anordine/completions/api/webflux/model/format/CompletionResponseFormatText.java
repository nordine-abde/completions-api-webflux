package com.anordine.completions.api.webflux.model.format;

import com.anordine.completions.api.webflux.model.enums.format.CompletionResponseFormatType;
import com.anordine.completions.api.webflux.model.format.abs.CompletionResponseFormat;
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
