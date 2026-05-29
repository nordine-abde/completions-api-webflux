package com.anordine.simplifier.webflux.ai.model.tool;

import com.anordine.simplifier.webflux.ai.util.DeepClonable;
import com.anordine.simplifier.webflux.ai.util.DeepCloneUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionFunctionDefinition implements DeepClonable<CompletionFunctionDefinition> {

    private Map<String, Object> parameters;
    private Boolean strict;
    private String description;
    private String name;

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Boolean getStrict() {
        return strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public CompletionFunctionDefinition deepClone() {
        CompletionFunctionDefinition clone = new CompletionFunctionDefinition();
        clone.setParameters(DeepCloneUtil.deepCloneStringObjectMap(this.parameters));
        clone.setStrict(this.strict);
        clone.setDescription(this.description);
        clone.setName(this.name);
        return clone;
    }
}
