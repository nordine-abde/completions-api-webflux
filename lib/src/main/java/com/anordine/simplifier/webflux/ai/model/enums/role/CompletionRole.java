package com.anordine.simplifier.webflux.ai.model.enums.role;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompletionRole {

    DEVELOPER(CompletionRoleValue.DEVELOPER_ROLE),
    SYSTEM(CompletionRoleValue.SYSTEM_ROLE),
    ASSISTANT(CompletionRoleValue.ASSISTANT_ROLE),
    USER(CompletionRoleValue.USER_ROLE),
    TOOL(CompletionRoleValue.TOOL_ROLE),
    FUNCTION(CompletionRoleValue.FUNCTION_ROLE);

    private final String value;

    private static final Map<String, CompletionRole> VALUE_MAP =
            Arrays.stream(CompletionRole.values())
                    .collect(Collectors.toMap(CompletionRole::getValue, Function.identity()));

    CompletionRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompletionRole fromValue(String value) {
        CompletionRole role = VALUE_MAP.get(value);
        if (role == null) {
            throw new IllegalArgumentException("Unknown CompletionRole value: " + value);
        }
        return role;
    }
}