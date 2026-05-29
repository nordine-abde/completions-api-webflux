package com.anordine.simplifier.webflux.ai.helper.sse;

import org.jspecify.annotations.NonNull;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatStream {

    private final Sinks.Many<@NonNull ServerSentEvent<@NonNull SseEventMessage>> sink;
    private final AtomicInteger subscribers = new AtomicInteger();
    private final AtomicBoolean pending = new AtomicBoolean(false);
    private volatile Instant lastAccess = Instant.now();

    ChatStream(int backPressureLimit) {
        sink = Sinks.many().multicast().onBackpressureBuffer(backPressureLimit);
    }

    Flux<@NonNull ServerSentEvent<@NonNull SseEventMessage>> asFlux() {
        return sink.asFlux();
    }

    Sinks.EmitResult emit(ServerSentEvent<@NonNull SseEventMessage> event) {
        return sink.tryEmitNext(event);
    }

    Sinks.EmitResult complete() {
        return sink.tryEmitComplete();
    }

    int incrementSubscribers() {
        return subscribers.incrementAndGet();
    }

    int decrementSubscribers() {
        return subscribers.decrementAndGet();
    }

    void setPending(boolean pending) {
        this.pending.set(pending);
    }

    void touch() {
        lastAccess = Instant.now();
    }

    public int getSubscribers() {
        return subscribers.get();
    }

    public boolean isPending() {
        return pending.get();
    }

    public Instant getLastAccess() {
        return lastAccess;
    }
}
