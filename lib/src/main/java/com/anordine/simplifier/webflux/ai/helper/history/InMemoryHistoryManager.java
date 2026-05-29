package com.anordine.simplifier.webflux.ai.helper.history;

import com.anordine.simplifier.webflux.ai.model.CompletionRequest;
import com.anordine.simplifier.webflux.ai.model.message.abs.CompletionMessage;
import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryHistoryManager implements IHistoryManager {

    private final Map<String, CompletionRequest> chats = new ConcurrentHashMap<>();

    @Override
    public Mono<@NonNull Void> loadChat(String chatId, CompletionRequest completionRequest) {
        return Mono.fromRunnable(() -> {
                    String id = Objects.requireNonNull(chatId, CHAT_ID_MUST_NOT_BE_NULL);

                    CompletionRequest request = Objects.requireNonNull(
                            completionRequest,
                            "completion request must not be null"
                    );

                    chats.put(id, request.deepClone());
                })
                .subscribeOn(Schedulers.parallel())
                .then();
    }

    @Override
    public Mono<@NonNull CompletionRequest> getChat(String id) {
        return Mono.fromCallable(() -> {
                    String chatId = Objects.requireNonNull(id, CHAT_ID_MUST_NOT_BE_NULL);

                    CompletionRequest request = chats.get(chatId);
                    if (request == null) {
                        throw new NoSuchElementException("chat not found: " + chatId);
                    }

                    return request.deepClone();
                })
                .subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<@NonNull CompletionRequest> addMessage(String id, CompletionMessage message) {
        return Mono.fromCallable(() -> {
                    String chatId = Objects.requireNonNull(id, CHAT_ID_MUST_NOT_BE_NULL);

                    CompletionMessage clonedMessage = Objects.requireNonNull(
                            message,
                            "message must not be null"
                    ).deepClone();

                    CompletionRequest updated = chats.compute(chatId, (key, existing) -> {
                        if (existing == null) {
                            throw new NoSuchElementException("chat not found: " + key);
                        }

                        CompletionRequest clonedRequest = existing.deepClone();
                        clonedRequest.addMessage(clonedMessage);
                        return clonedRequest;
                    });

                    return updated.deepClone();
                })
                .subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<@NonNull Void> evict(String id) {
        return Mono.fromRunnable(() -> chats.remove(id));
    }
}