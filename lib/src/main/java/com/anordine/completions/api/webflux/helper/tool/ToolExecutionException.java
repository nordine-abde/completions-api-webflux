package com.anordine.completions.api.webflux.helper.tool;

public class ToolExecutionException extends RuntimeException {

    public ToolExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
