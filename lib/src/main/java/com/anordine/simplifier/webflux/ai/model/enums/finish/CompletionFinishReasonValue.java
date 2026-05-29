package com.anordine.simplifier.webflux.ai.model.enums.finish;

public final class CompletionFinishReasonValue {

    public static final String STOP = "stop";
    public static final String LENGTH = "length";
    public static final String TOOL_CALLS = "tool_calls";
    public static final String CONTENT_FILTER = "content_filter";
    public static final String FUNCTION_CALL = "function_call";

    private CompletionFinishReasonValue() {
    }
}
