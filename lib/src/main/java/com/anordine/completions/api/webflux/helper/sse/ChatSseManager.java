package com.anordine.completions.api.webflux.helper.sse;

import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.model.usage.CompletionUsage;
import org.jspecify.annotations.NonNull;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatSseManager {

    private final Map<UUID, ChatStream> chatStreams = new ConcurrentHashMap<>();
    private final Duration heartbeatEvery;
    private final Duration typingEvery;
    private final int maxBackPressure;
    private final boolean emitUsageEvents;

    public ChatSseManager(Duration heartbeatEvery, int maxBackPressure) {
        this(heartbeatEvery, Duration.ofSeconds(3), maxBackPressure);
    }

    public ChatSseManager(Duration heartbeatEvery, Duration typingEvery, int maxBackPressure) {
        this(heartbeatEvery, typingEvery, maxBackPressure, true);
    }

    public ChatSseManager(Duration heartbeatEvery, Duration typingEvery, int maxBackPressure, boolean emitUsageEvents) {
        this.heartbeatEvery = heartbeatEvery;
        this.typingEvery = typingEvery;
        this.maxBackPressure = maxBackPressure;
        this.emitUsageEvents = emitUsageEvents;
    }

    private ChatStream getOrCreateStream(UUID chatId) {
        return chatStreams.computeIfAbsent(chatId, id -> new ChatStream(maxBackPressure));
    }

    public ChatStream get(UUID chatId) {
        return chatStreams.get(chatId);
    }

    public Flux<@NonNull ServerSentEvent<@NonNull SseEventMessage>> createSseStream(UUID chatId) {
        ChatStream stream = getOrCreateStream(chatId);
        touch(stream);

        Flux<@NonNull ServerSentEvent<@NonNull SseEventMessage>> heartbeat =
                Flux.interval(heartbeatEvery)
                        .map(i -> ServerSentEvent.<SseEventMessage>builder()
                                .event(EventType.HEARTBEAT.name())
                                .build());

        Flux<@NonNull ServerSentEvent<@NonNull SseEventMessage>> initialTyping =
                Flux.defer(() -> stream.isPending()
                        ? Flux.just(typingEvent())
                        : Flux.empty());

        Flux<@NonNull ServerSentEvent<@NonNull SseEventMessage>> typing =
                Flux.interval(typingEvery)
                        .filter(i -> stream.isPending())
                        .map(i -> typingEvent());

        return Flux.concat(initialTyping, Flux.merge(stream.asFlux(), heartbeat, typing))
                .doOnSubscribe(sub -> stream.incrementSubscribers())
                .doFinally(sig -> {
                    if (stream.decrementSubscribers() == 0 && !stream.isPending()) {
                        stream.complete();
                        chatStreams.remove(chatId, stream);
                    }
                });
    }

    public Sinks.EmitResult emitMessage(UUID chatId, UUID messageId, String content, CompletionRole role) {
        return emit(chatId, EventType.CHAT_MESSAGE, messageId, content, role);
    }

    public Sinks.EmitResult emitChunk(UUID chatId, UUID messageId, String content, CompletionRole role) {
        return emit(chatId, EventType.CHAT_MESSAGE_CHUNK, messageId, content, role);
    }

    public Sinks.EmitResult emitMessageStart(UUID chatId, UUID messageId, CompletionRole role) {
        return emit(chatId, EventType.CHAT_MESSAGE_START, messageId, null, role);
    }

    public Sinks.EmitResult emitMessageDone(UUID chatId, UUID messageId, String content, CompletionRole role) {
        return emit(chatId, EventType.CHAT_MESSAGE_DONE, messageId, content, role);
    }

    public Sinks.EmitResult emitToolCall(UUID chatId, UUID messageId, String content, CompletionRole role) {
        return emit(chatId, EventType.TOOL_CALL, messageId, content, role);
    }

    public Sinks.EmitResult emitToolCallChunk(UUID chatId, UUID messageId, String content, CompletionRole role) {
        return emit(chatId, EventType.TOOL_CALL_CHUNK, messageId, content, role);
    }

    public Sinks.EmitResult emitTitleUpdate(UUID chatId, String title) {
        return emit(chatId, EventType.TITLE_UPDATE, null, title, null);
    }

    public Sinks.EmitResult emitError(UUID chatId, String error) {
        return emit(chatId, EventType.ERROR, null, error, null);
    }

    public Sinks.EmitResult emitUsage(UUID chatId, CompletionUsage usage) {
        ChatStream stream = getOrCreateStream(chatId);
        touch(stream);
        return stream.emit(ServerSentEvent.<SseEventMessage>builder()
                .event(EventType.USAGE.name())
                .data(new SseEventMessage(chatId, usage))
                .build());
    }

    public Sinks.EmitResult emit(UUID chatId, EventType eventType, String content) {
        return emit(chatId, eventType, null, content, null);
    }

    public Sinks.EmitResult emit(
            UUID chatId,
            EventType eventType,
            UUID messageId,
            String content,
            CompletionRole role
    ) {
        ChatStream stream = getOrCreateStream(chatId);
        touch(stream);
        return stream.emit(ServerSentEvent.<SseEventMessage>builder()
                .event(eventType.name())
                .data(new SseEventMessage(messageId, chatId, content, role, eventType))
                .build());
    }

    public void setPending(UUID chatId, boolean pending) {
        ChatStream stream = pending ? getOrCreateStream(chatId) : chatStreams.get(chatId);
        if (stream == null) {
            return;
        }
        stream.setPending(pending);
        touch(stream);
        if (!pending && stream.getSubscribers() == 0) {
            stream.complete();
            chatStreams.remove(chatId, stream);
        }
    }

    public boolean complete(UUID chatId) {
        ChatStream stream = chatStreams.remove(chatId);
        return stream == null || stream.complete().isSuccess();
    }

    public Duration getHeartbeatEvery() {
        return heartbeatEvery;
    }

    public Duration getTypingEvery() {
        return typingEvery;
    }

    public int getMaxBackPressure() {
        return maxBackPressure;
    }

    public boolean isEmitUsageEvents() {
        return emitUsageEvents;
    }

    private void touch(ChatStream stream) {
        stream.touch();
    }

    private static ServerSentEvent<@NonNull SseEventMessage> typingEvent() {
        return ServerSentEvent.<SseEventMessage>builder()
                .event(EventType.TYPING.name())
                .data(new SseEventMessage(EventType.TYPING, EventType.TYPING.name()))
                .build();
    }

    public void shutdown() {
        chatStreams.forEach((id, stream) -> stream.complete());
        chatStreams.clear();
    }
}

