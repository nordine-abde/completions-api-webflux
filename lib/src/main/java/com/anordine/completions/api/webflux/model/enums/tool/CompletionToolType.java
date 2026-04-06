package com.anordine.completions.api.webflux.model.enums.tool;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompletionToolType {

    FUNCTION(CompletionToolTypeValue.FUNCTION),
    CUSTOM(CompletionToolTypeValue.CUSTOM);

    private final String value;

    private static final Map<String, CompletionToolType> VALUE_MAP =
            Arrays.stream(CompletionToolType.values())
                    .collect(Collectors.toMap(CompletionToolType::getValue, Function.identity()));

    CompletionToolType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompletionToolType fromValue(String value) {
        CompletionToolType type = VALUE_MAP.get(value);
        if (type == null) {
            throw new IllegalArgumentException("Unknown CompletionToolType value: " + value);
        }
        return type;
    }
}
