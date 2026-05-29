package com.anordine.simplifier.webflux.ai.helper.history;

import com.anordine.simplifier.webflux.ai.model.CompletionRequest;
import com.anordine.simplifier.webflux.ai.model.message.abs.CompletionMessage;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class InRedisPostgresHistoryManager<E> implements IHistoryManager {

    private final IHistoryManager redisHistoryManager;
    private final BiFunction<String, CompletionRequest, E> completionRequestToEntity;
    private final Function<E, CompletionRequest> entityToCompletionRequest;
    private final Function<String, Mono<E>> retrieveEntity;
    private final Function<E, Mono<?>> saveEntity;

    public InRedisPostgresHistoryManager(
            String keyPrefix,
            ReactiveRedisTemplate<@NonNull String, @NonNull CompletionRequest> requestRedis,
            ReactiveRedisTemplate<@NonNull String, @NonNull CompletionMessage> messageRedis,
            BiFunction<String, CompletionRequest, E> completionRequestToEntity,
            Function<E, CompletionRequest> entityToCompletionRequest,
            Function<String, Mono<E>> retrieveEntity,
            Function<E, Mono<?>> saveEntity
    ) {
        this(
                new InRedisHistoryManager(keyPrefix, requestRedis, messageRedis),
                completionRequestToEntity,
                entityToCompletionRequest,
                retrieveEntity,
                saveEntity
        );
    }

    public InRedisPostgresHistoryManager(
            IHistoryManager redisHistoryManager,
            BiFunction<String, CompletionRequest, E> completionRequestToEntity,
            Function<E, CompletionRequest> entityToCompletionRequest,
            Function<String, Mono<E>> retrieveEntity,
            Function<E, Mono<?>> saveEntity
    ) {
        this.redisHistoryManager = Objects.requireNonNull(redisHistoryManager, "redis history manager must not be null");
        this.completionRequestToEntity = Objects.requireNonNull(
                completionRequestToEntity,
                "completion request to entity mapper must not be null"
        );
        this.entityToCompletionRequest = Objects.requireNonNull(
                entityToCompletionRequest,
                "entity to completion request mapper must not be null"
        );
        this.retrieveEntity = Objects.requireNonNull(retrieveEntity, "retrieve entity function must not be null");
        this.saveEntity = Objects.requireNonNull(saveEntity, "save entity function must not be null");
    }

    @Override
    public Mono<@NonNull Void> loadChat(String chatId, CompletionRequest completionRequest) {
        return Mono.defer(() -> {
            String id = Objects.requireNonNull(chatId, CHAT_ID_MUST_NOT_BE_NULL);
            CompletionRequest request = Objects.requireNonNull(
                    completionRequest,
                    "completion request must not be null"
            ).deepClone();

            return saveToPostgres(id, request)
                    .then(loadRedisCache(id, request));
        });
    }

    @Override
    public Mono<@NonNull CompletionRequest> getChat(String id) {
        return Mono.defer(() -> {
            String chatId = Objects.requireNonNull(id, CHAT_ID_MUST_NOT_BE_NULL);

            return getFromRedisCache(chatId)
                    .switchIfEmpty(Mono.defer(() -> getFromPostgres(chatId)
                            .flatMap(request -> loadRedisCache(chatId, request)
                                    .thenReturn(request.deepClone()))));
        });
    }

    @Override
    public Mono<@NonNull CompletionRequest> addMessage(String id, CompletionMessage message) {
        return Mono.defer(() -> {
            String chatId = Objects.requireNonNull(id, CHAT_ID_MUST_NOT_BE_NULL);
            CompletionMessage safeMessage = Objects.requireNonNull(
                    message,
                    "message must not be null"
            ).deepClone();

            return getFromPostgres(chatId)
                    .map(request -> request.addMessage(safeMessage))
                    .flatMap(updated -> saveToPostgres(chatId, updated)
                            .then(loadRedisCache(chatId, updated))
                            .thenReturn(updated.deepClone()));
        });
    }

    @Override
    public Mono<@NonNull Void> evict(String id) {
        return Mono.defer(() -> {
            String chatId = Objects.requireNonNull(id, CHAT_ID_MUST_NOT_BE_NULL);
            return redisHistoryManager.evict(chatId)
                    .onErrorResume(throwable -> Mono.empty());
        });
    }

    private Mono<CompletionRequest> getFromRedisCache(String chatId) {
        return Mono.defer(() -> redisHistoryManager.getChat(chatId)
                .map(CompletionRequest::deepClone)
                .onErrorResume(throwable -> Mono.empty()));
    }

    private Mono<CompletionRequest> getFromPostgres(String chatId) {
        return Mono.defer(() -> Objects.requireNonNull(
                        retrieveEntity.apply(chatId),
                        "retrieve entity function must not return null"
                )
                .switchIfEmpty(Mono.error(new NoSuchElementException("chat not found: " + chatId)))
                .map(entity -> Objects.requireNonNull(
                        entityToCompletionRequest.apply(entity),
                        "entity to completion request mapper must not return null"
                ).deepClone()));
    }

    private Mono<@NonNull Void> saveToPostgres(String chatId, CompletionRequest request) {
        return Mono.defer(() -> {
            E entity = Objects.requireNonNull(
                    completionRequestToEntity.apply(chatId, request.deepClone()),
                    "completion request to entity mapper must not return null"
            );

            return Objects.requireNonNull(
                    saveEntity.apply(entity),
                    "save entity function must not return null"
            ).then();
        });
    }

    private Mono<@NonNull Void> loadRedisCache(String chatId, CompletionRequest request) {
        return Mono.defer(() -> redisHistoryManager.loadChat(chatId, request.deepClone())
                .onErrorResume(throwable -> Mono.empty()));
    }
}
