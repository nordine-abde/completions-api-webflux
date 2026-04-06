package com.anordine.completions.api.webflux.model.toolchoice;

import com.anordine.completions.api.webflux.model.enums.toolchoice.CompletionAllowedToolsMode;
import com.anordine.completions.api.webflux.model.tool.abs.CompletionTool;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionAllowedTools {

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
}
