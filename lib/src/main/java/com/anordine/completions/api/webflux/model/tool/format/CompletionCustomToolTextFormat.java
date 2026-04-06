package com.anordine.completions.api.webflux.model.tool.format;

import com.anordine.completions.api.webflux.model.enums.toolformat.CompletionCustomToolFormatType;
import com.anordine.completions.api.webflux.model.tool.format.abs.CompletionCustomToolFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionCustomToolTextFormat extends CompletionCustomToolFormat {

    public CompletionCustomToolTextFormat() {
        super(CompletionCustomToolFormatType.TEXT);
    }
}
