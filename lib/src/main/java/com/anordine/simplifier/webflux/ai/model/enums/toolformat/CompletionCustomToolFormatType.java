package com.anordine.simplifier.webflux.ai.model.enums.toolformat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompletionCustomToolFormatType {

    TEXT(CompletionCustomToolFormatTypeValue.TEXT),
    GRAMMAR(CompletionCustomToolFormatTypeValue.GRAMMAR);

    private final String value;

    private static final Map<String, CompletionCustomToolFormatType> VALUE_MAP =
            Arrays.stream(CompletionCustomToolFormatType.values())
                    .collect(Collectors.toMap(CompletionCustomToolFormatType::getValue, Function.identity()));

    CompletionCustomToolFormatType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompletionCustomToolFormatType fromValue(String value) {
        CompletionCustomToolFormatType type = VALUE_MAP.get(value);
        if (type == null) {
            throw new IllegalArgumentException("Unknown CompletionCustomToolFormatType value: " + value);
        }
        return type;
    }
}
