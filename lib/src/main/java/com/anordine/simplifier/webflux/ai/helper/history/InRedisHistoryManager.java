package com.anordine.simplifier.webflux.ai.helper.history;

import com.anordine.simplifier.webflux.ai.model.CompletionRequest;
import com.anordine.simplifier.webflux.ai.model.message.abs.CompletionMessage;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class InRedisHistoryManager implements IHistoryManager {

    private final String keyPrefix;

    private final ReactiveRedisTemplate<@NonNull String, @NonNull CompletionRequest> requestRedis;
    private final ReactiveRedisTemplate<@NonNull String, @NonNull CompletionMessage> messageRedis;

    public InRedisHistoryManager(
            String keyPrefix,
            ReactiveRedisTemplate<@NonNull String, @NonNull CompletionRequest> requestRedis,
            ReactiveRedisTemplate<@NonNull String, @NonNull CompletionMessage> messageRedis
    ) {
        this.keyPrefix = Objects.requireNonNull(keyPrefix, "key prefix must not be null");
        this.requestRedis = Objects.requireNonNull(requestRedis, "request redis must not be null");
        this.messageRedis = Objects.requireNonNull(messageRedis, "message redis must not be null");
    }

    @Override
    public Mono<@NonNull Void> loadChat(
            String chatId,
            CompletionRequest completionRequest
    ) {
        return Mono.defer(() -> {
            String id = Objects.requireNonNull(chatId, CHAT_ID_MUST_NOT_BE_NULL);

            CompletionRequest request = Objects.requireNonNull(
                    completionRequest,
                    "completion request must not be null"
            ).deepClone();
            List<CompletionMessage> messages = cloneMessages(request);
            request.setMessages(null);

            return requestRedis.opsForValue()
                    .set(requestKey(id), request)
                    .then(messageRedis.delete(messageKey(id)))
                    .thenMany(Flux.fromIterable(messages)
                            .concatMap(message -> messageRedis.opsForList()
                                    .rightPush(messageKey(id), message)))
                    .then();
        });
    }

    @Override
    public Mono<@NonNull CompletionRequest> getChat(String id) {
        return Mono.defer(() -> {
            String chatId = Objects.requireNonNull(id, CHAT_ID_MUST_NOT_BE_NULL);

            return requestRedis.opsForValue()
                    .get(requestKey(chatId))
                    .switchIfEmpty(Mono.error(
                            new NoSuchElementException("chat not found: " + chatId)
                    ))
                    .flatMap(request -> messageRedis.opsForList()
                            .range(messageKey(chatId), 0, -1)
                            .collectList()
                            .map(messages -> {
                                CompletionRequest clonedRequest = request.deepClone();
                                clonedRequest.setMessages(null);
                                messages.forEach(clonedRequest::addMessage);
                                return clonedRequest;
                            }));
        });
    }

    @Override
    public Mono<@NonNull CompletionRequest> addMessage(
            String id,
            CompletionMessage message
    ) {
        return Mono.defer(() -> {
            String chatId = Objects.requireNonNull(id, CHAT_ID_MUST_NOT_BE_NULL);

            CompletionMessage safeMessage = Objects.requireNonNull(
                    message,
                    "message must not be null"
            ).deepClone();

            return ensureChatExists(chatId)
                    .then(messageRedis.opsForList()
                            .rightPush(messageKey(chatId), safeMessage))
                    .then(getChat(chatId));
        });
    }

    @Override
    public Mono<@NonNull Void> evict(String id) {
        return Mono.defer(() -> {
            String chatId = Objects.requireNonNull(id, CHAT_ID_MUST_NOT_BE_NULL);

            return requestRedis.delete(requestKey(chatId), messageKey(chatId))
                    .then();
        });
    }

    private Mono<@NonNull Void> ensureChatExists(String chatId) {
        return requestRedis.hasKey(requestKey(chatId))
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new NoSuchElementException("chat not found: " + chatId)))
                .then();
    }

    private List<CompletionMessage> cloneMessages(CompletionRequest request) {
        if (request.getMessages() == null) {
            return List.of();
        }
        return request.getMessages()
                .stream()
                .map(message -> Objects.requireNonNull(message, "message must not be null").deepClone())
                .toList();
    }

    private String requestKey(String chatId) {
        return keyPrefix + chatId + ":request";
    }

    private String messageKey(String chatId) {
        return keyPrefix + chatId + ":messages";
    }
}
