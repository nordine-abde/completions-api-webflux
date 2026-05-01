package com.anordine.completions.api.webflux.model.tool.format;

import com.anordine.completions.api.webflux.model.enums.toolformat.CompletionCustomToolFormatType;
import com.anordine.completions.api.webflux.model.tool.format.abs.CompletionCustomToolFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionCustomToolGrammarFormat extends CompletionCustomToolFormat {

    private CompletionGrammarDefinition grammar;

    public CompletionCustomToolGrammarFormat() {
        super(CompletionCustomToolFormatType.GRAMMAR);
    }

    public CompletionCustomToolGrammarFormat(CompletionGrammarDefinition grammar) {
        super(CompletionCustomToolFormatType.GRAMMAR);
        this.grammar = grammar;
    }

    public CompletionGrammarDefinition getGrammar() {
        return grammar;
    }

    public void setGrammar(CompletionGrammarDefinition grammar) {
        this.grammar = grammar;
    }

    @Override
    public CompletionCustomToolGrammarFormat deepClone() {
        return new CompletionCustomToolGrammarFormat(this.grammar == null ? null : this.grammar.deepClone());
    }
}
