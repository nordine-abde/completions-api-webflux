package com.anordine.simplifier.webflux.ai.model.tool.format;

import com.anordine.simplifier.webflux.ai.model.enums.toolformat.CompletionCustomToolFormatType;
import com.anordine.simplifier.webflux.ai.model.tool.format.abs.CompletionCustomToolFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompletionCustomToolTextFormat extends CompletionCustomToolFormat {

    public CompletionCustomToolTextFormat() {
        super(CompletionCustomToolFormatType.TEXT);
    }

    @Override
    public CompletionCustomToolTextFormat deepClone() {
        return new CompletionCustomToolTextFormat();
    }
}
