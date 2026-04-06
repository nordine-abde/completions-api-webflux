package com.anordine.completions.api.webflux.model.enums.format;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompletionResponseFormatType {

    TEXT(CompletionResponseFormatTypeValue.TEXT),
    JSON_SCHEMA(CompletionResponseFormatTypeValue.JSON_SCHEMA),
    JSON_OBJECT(CompletionResponseFormatTypeValue.JSON_OBJECT);

    private final String value;

    private static final Map<String, CompletionResponseFormatType> VALUE_MAP =
            Arrays.stream(CompletionResponseFormatType.values())
                    .collect(Collectors.toMap(CompletionResponseFormatType::getValue, Function.identity()));

    CompletionResponseFormatType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompletionResponseFormatType fromValue(String value) {
        CompletionResponseFormatType type = VALUE_MAP.get(value);
        if (type == null) {
            throw new IllegalArgumentException("Unknown CompletionResponseFormatType value: " + value);
        }
        return type;
    }
}
