package com.anordine.completions.api.webflux.helper.sse;

public enum EventType {
    HEARTBEAT, TYPING, CHAT_MESSAGE, CHAT_MESSAGE_CHUNK, TOOL_CALL, TITLE_UPDATE, ERROR
}
