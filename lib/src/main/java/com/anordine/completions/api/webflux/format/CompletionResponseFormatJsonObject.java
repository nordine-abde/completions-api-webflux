package com.anordine.completions.api.webflux.format;

import com.anordine.completions.api.webflux.enums.format.CompletionResponseFormatType;
import com.anordine.completions.api.webflux.format.abs.CompletionResponseFormat;

public class CompletionResponseFormatJsonObject extends CompletionResponseFormat {

    protected CompletionResponseFormatJsonObject() {
        super(CompletionResponseFormatType.JSON_OBJECT);
    }
}
