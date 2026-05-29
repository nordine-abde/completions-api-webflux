package com.anordine.simplifier.webflux.ai.model.enums.resoning;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompletionReasoningEffort {

    NONE("none"),
    MINIMAL("minimal"),
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    X_HIGH("xhigh");

    private final String value;

    private static final Map<String, CompletionReasoningEffort> VALUE_MAP =
            Arrays.stream(CompletionReasoningEffort.values())
                    .collect(Collectors.toMap(CompletionReasoningEffort::getValue, Function.identity()));

    CompletionReasoningEffort(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompletionReasoningEffort fromValue(String value) {
        CompletionReasoningEffort reasoningEffort = VALUE_MAP.get(value);
        if (reasoningEffort == null) {
            throw new IllegalArgumentException("Unknown CompletionReasoningEffort value: " + value);
        }
        return reasoningEffort;
    }
}
