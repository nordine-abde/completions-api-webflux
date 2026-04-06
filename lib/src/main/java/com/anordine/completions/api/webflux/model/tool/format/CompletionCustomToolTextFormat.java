package com.anordine.completions.api.webflux.model.tool.format;

import com.anordine.completions.api.webflux.model.enums.toolformat.CompletionCustomToolFormatType;
import com.anordine.completions.api.webflux.model.tool.format.abs.CompletionCustomToolFormat;

public class CompletionCustomToolTextFormat extends CompletionCustomToolFormat {

    public CompletionCustomToolTextFormat() {
        super(CompletionCustomToolFormatType.TEXT);
    }
}
