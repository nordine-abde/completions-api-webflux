package com.anordine.completions.api.webflux.model.enums.toolchoice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompletionToolChoiceType {

    FUNCTION(CompletionToolChoiceTypeValue.FUNCTION),
    CUSTOM(CompletionToolChoiceTypeValue.CUSTOM),
    ALLOWED_TOOLS(CompletionToolChoiceTypeValue.ALLOWED_TOOLS);

    private final String value;

    private static final Map<String, CompletionToolChoiceType> VALUE_MAP =
            Arrays.stream(CompletionToolChoiceType.values())
                    .collect(Collectors.toMap(CompletionToolChoiceType::getValue, Function.identity()));

    CompletionToolChoiceType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompletionToolChoiceType fromValue(String value) {
        CompletionToolChoiceType type = VALUE_MAP.get(value);
        if (type == null) {
            throw new IllegalArgumentException("Unknown CompletionToolChoiceType value: " + value);
        }
        return type;
    }
}
