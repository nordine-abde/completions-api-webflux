package com.anordine.completions.api.webflux.model.tool.format;

import com.anordine.completions.api.webflux.model.enums.toolformat.CompletionGrammarSyntax;
import com.anordine.completions.api.webflux.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
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
