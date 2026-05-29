package com.anordine.simplifier.webflux.ai.helper.tool;

public class ToolExecutionException extends RuntimeException {

    public ToolExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
