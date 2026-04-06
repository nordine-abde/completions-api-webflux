package com.anordine.completions.api.webflux.model.enums.toolchoice;

import com.anordine.completions.api.webflux.model.toolchoice.abs.ToolChoiceOptionInterface;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompletionToolChoiceMode implements ToolChoiceOptionInterface {

    NONE(CompletionToolChoiceModeValue.NONE),
    AUTO(CompletionToolChoiceModeValue.AUTO),
    REQUIRED(CompletionToolChoiceModeValue.REQUIRED);

    private final String value;

    private static final Map<String, CompletionToolChoiceMode> VALUE_MAP =
            Arrays.stream(CompletionToolChoiceMode.values())
                    .collect(Collectors.toMap(CompletionToolChoiceMode::getValue, Function.identity()));

    CompletionToolChoiceMode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompletionToolChoiceMode fromValue(String value) {
        CompletionToolChoiceMode mode = VALUE_MAP.get(value);
        if (mode == null) {
            throw new IllegalArgumentException("Unknown CompletionToolChoiceMode value: " + value);
        }
        return mode;
    }
}
