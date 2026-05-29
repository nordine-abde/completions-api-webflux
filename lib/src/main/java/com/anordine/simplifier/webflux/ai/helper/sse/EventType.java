package com.anordine.simplifier.webflux.ai.helper.sse;

public enum EventType {
    HEARTBEAT,
    TYPING,
    CHAT_MESSAGE,
    CHAT_MESSAGE_START,
    CHAT_MESSAGE_CHUNK,
    CHAT_MESSAGE_DONE,
    TOOL_CALL,
    TOOL_CALL_CHUNK,
    TITLE_UPDATE,
    USAGE,
    ERROR
}
