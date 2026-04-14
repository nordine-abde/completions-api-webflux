package com.anordine.completions.api.webflux.model.tool;

import com.anordine.completions.api.webflux.model.enums.tool.CompletionToolType;
import com.anordine.completions.api.webflux.model.tool.abs.CompletionTool;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    @Override
    public CompletionCustomTool deepClone() {
        return new CompletionCustomTool(this.custom == null ? null : this.custom.deepClone());
    }
}
