package com.anordine.simplifier.webflux.ai.model.tool.format;

import com.anordine.simplifier.webflux.ai.model.enums.toolformat.CompletionGrammarSyntax;
import com.anordine.simplifier.webflux.ai.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionGrammarDefinition implements DeepClonable<CompletionGrammarDefinition> {

    private String definition;
    private CompletionGrammarSyntax syntax;

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public CompletionGrammarSyntax getSyntax() {
        return syntax;
    }

    public void setSyntax(CompletionGrammarSyntax syntax) {
        this.syntax = syntax;
    }

    @Override
    public CompletionGrammarDefinition deepClone() {
        CompletionGrammarDefinition clone = new CompletionGrammarDefinition();
        clone.setDefinition(this.definition);
        clone.setSyntax(this.syntax);
        return clone;
    }
}
