package com.anordine.completions.api.webflux.model.enums.modality;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompletionModality {
    TEXT("text"),
    AUDIO("audio");

    private final String value;

    private static final Map<String, CompletionModality> VALUE_MAP =
            Arrays.stream(CompletionModality.values())
                    .collect(Collectors.toMap(CompletionModality::getValue, Function.identity()));

    CompletionModality(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompletionModality fromValue(String value) {
        CompletionModality modality = VALUE_MAP.get(value);
        if (modality == null) {
            throw new IllegalArgumentException("Unknown CompletionModality value: " + value);
        }
        return modality;
    }
}
