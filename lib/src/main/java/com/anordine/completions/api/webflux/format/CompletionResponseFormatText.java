package com.anordine.completions.api.webflux.format;

import com.anordine.completions.api.webflux.enums.format.CompletionResponseFormatType;
import com.anordine.completions.api.webflux.format.abs.CompletionResponseFormat;

public class CompletionResponseFormatText extends CompletionResponseFormat {

    public CompletionResponseFormatText() {
        super(CompletionResponseFormatType.TEXT);
    }

}
