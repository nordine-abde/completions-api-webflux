package com.anordine.simplifier.webflux.ai.model.enums.toolformat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompletionGrammarSyntax {

    LARK(CompletionGrammarSyntaxValue.LARK),
    REGEX(CompletionGrammarSyntaxValue.REGEX);

    private final String value;

    private static final Map<String, CompletionGrammarSyntax> VALUE_MAP =
            Arrays.stream(CompletionGrammarSyntax.values())
                    .collect(Collectors.toMap(CompletionGrammarSyntax::getValue, Function.identity()));

    CompletionGrammarSyntax(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompletionGrammarSyntax fromValue(String value) {
        CompletionGrammarSyntax syntax = VALUE_MAP.get(value);
        if (syntax == null) {
            throw new IllegalArgumentException("Unknown CompletionGrammarSyntax value: " + value);
        }
        return syntax;
    }
}
