package com.anordine.simplifier.webflux.ai.model.toolchoice;

import com.anordine.simplifier.webflux.ai.model.enums.toolchoice.CompletionAllowedToolsMode;
import com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool;
import com.anordine.simplifier.webflux.ai.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionAllowedTools implements DeepClonable<CompletionAllowedTools> {

    private CompletionAllowedToolsMode mode;
    private List<CompletionTool> tools;

    public CompletionAllowedToolsMode getMode() {
        return mode;
    }

    public void setMode(CompletionAllowedToolsMode mode) {
        this.mode = mode;
    }

    public List<CompletionTool> getTools() {
        return tools;
    }

    public void setTools(List<CompletionTool> tools) {
        this.tools = tools;
    }

    @Override
    public CompletionAllowedTools deepClone() {
        CompletionAllowedTools clone = new CompletionAllowedTools();
        clone.setMode(this.mode);
        if (this.tools != null) {
            List<CompletionTool> clonedTools = new ArrayList<>(this.tools.size());
            for (CompletionTool tool : this.tools) {
                clonedTools.add(tool == null ? null : tool.deepClone());
            }
            clone.setTools(clonedTools);
        }
        return clone;
    }
}
