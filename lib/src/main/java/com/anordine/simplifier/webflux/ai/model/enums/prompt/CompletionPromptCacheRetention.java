package com.anordine.simplifier.webflux.ai.model.enums.prompt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompletionPromptCacheRetention {

    IN_MEMORY(CompletionPromptCacheRetentionValue.IN_MEMORY),
    TWENTY_FOUR_HOURS(CompletionPromptCacheRetentionValue.TWENTY_FOUR_HOURS);

    private final String value;

    private static final Map<String, CompletionPromptCacheRetention> VALUE_MAP =
            Arrays.stream(CompletionPromptCacheRetention.values())
                    .collect(Collectors.toMap(CompletionPromptCacheRetention::getValue, Function.identity()));

    CompletionPromptCacheRetention(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompletionPromptCacheRetention fromValue(String value) {
        CompletionPromptCacheRetention retention = VALUE_MAP.get(value);
        if (retention == null) {
            throw new IllegalArgumentException("Unknown CompletionPromptCacheRetention value: " + value);
        }
        return retention;
    }
}
