package com.anordine.completions.api.webflux.helper.sse;

import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;

import java.util.UUID;

public class SseEventMessage {

    private UUID id;
    private UUID chatId;
    private String content;
    private CompletionRole role;
    private EventType eventType;

    public SseEventMessage(UUID id, UUID chatId, String content, CompletionRole role, EventType eventType) {
        this.id = id;
        this.chatId = chatId;
        this.content = content;
        this.role = role;
        this.eventType = eventType;
    }

    public SseEventMessage(EventType eventType, String content) {
        this.eventType = eventType;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CompletionRole getRole() {
        return role;
    }

    public void setRole(CompletionRole role) {
        this.role = role;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
