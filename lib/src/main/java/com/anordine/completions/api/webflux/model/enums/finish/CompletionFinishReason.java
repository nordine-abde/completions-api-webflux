package com.anordine.completions.api.webflux.model.enums.finish;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompletionFinishReason {
    STOP(CompletionFinishReasonValue.STOP),
    LENGTH(CompletionFinishReasonValue.LENGTH),
    TOOL_CALLS(CompletionFinishReasonValue.TOOL_CALLS),
    CONTENT_FILTER(CompletionFinishReasonValue.CONTENT_FILTER),
    FUNCTION_CALL(CompletionFinishReasonValue.FUNCTION_CALL);

    private final String value;

    private static final Map<String, CompletionFinishReason> VALUE_MAP =
            Arrays.stream(CompletionFinishReason.values())
                    .collect(Collectors.toMap(CompletionFinishReason::getValue, Function.identity()));

    CompletionFinishReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompletionFinishReason fromValue(String value) {
        CompletionFinishReason finishReason = VALUE_MAP.get(value);
        if (finishReason == null) {
            throw new IllegalArgumentException("Unknown CompletionFinishReason value: " + value);
        }
        return finishReason;
    }
}
