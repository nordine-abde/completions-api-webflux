package com.anordine.completions.api.webflux.model.format;

import com.anordine.completions.api.webflux.model.enums.format.CompletionResponseFormatType;
import com.anordine.completions.api.webflux.model.format.abs.CompletionResponseFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionResponseFormatJsonObject extends CompletionResponseFormat {

    protected CompletionResponseFormatJsonObject() {
        super(CompletionResponseFormatType.JSON_OBJECT);
    }

    @Override
    public CompletionResponseFormatJsonObject deepClone() {
        return new CompletionResponseFormatJsonObject();
    }
}
