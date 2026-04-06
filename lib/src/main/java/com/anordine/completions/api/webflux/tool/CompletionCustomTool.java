package com.anordine.completions.api.webflux.tool;

import com.anordine.completions.api.webflux.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.tool.abs.CompletionTool;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionCustomTool extends CompletionTool {

    public CompletionCustomTool() {
        super(CompletionToolType.CUSTOM);
    }

    private CompletionCustomDefinition custom;

    public CompletionCustomTool(CompletionCustomDefinition custom) {
        super(CompletionToolType.CUSTOM);
        this.custom = custom;
    }

    public CompletionCustomDefinition getCustom() {
        return custom;
    }

    public void setCustom(CompletionCustomDefinition custom) {
        this.custom = custom;
    }
}
