package com.anordine.completions.api.webflux.model.format;

import com.anordine.completions.api.webflux.model.enums.format.CompletionResponseFormatType;
import com.anordine.completions.api.webflux.model.format.abs.CompletionResponseFormat;

public class CompletionResponseFormatText extends CompletionResponseFormat {

    public CompletionResponseFormatText() {
        super(CompletionResponseFormatType.TEXT);
    }

}
