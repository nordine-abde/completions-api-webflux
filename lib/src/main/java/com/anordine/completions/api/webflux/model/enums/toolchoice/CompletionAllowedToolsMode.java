package com.anordine.completions.api.webflux.model.enums.toolchoice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompletionAllowedToolsMode {

    AUTO(CompletionToolChoiceModeValue.AUTO),
    REQUIRED(CompletionToolChoiceModeValue.REQUIRED);

    private final String value;

    private static final Map<String, CompletionAllowedToolsMode> VALUE_MAP =
            Arrays.stream(CompletionAllowedToolsMode.values())
                    .collect(Collectors.toMap(CompletionAllowedToolsMode::getValue, Function.identity()));

    CompletionAllowedToolsMode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompletionAllowedToolsMode fromValue(String value) {
        CompletionAllowedToolsMode mode = VALUE_MAP.get(value);
        if (mode == null) {
            throw new IllegalArgumentException("Unknown CompletionAllowedToolsMode value: " + value);
        }
        return mode;
    }
}
