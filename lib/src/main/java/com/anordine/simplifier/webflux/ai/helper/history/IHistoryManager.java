package com.anordine.simplifier.webflux.ai.helper.history;

import com.anordine.simplifier.webflux.ai.model.CompletionRequest;
import com.anordine.simplifier.webflux.ai.model.message.abs.CompletionMessage;
import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Mono;

public interface IHistoryManager {

    String CHAT_ID_MUST_NOT_BE_NULL = "chat id must not be null";
    Mono<@NonNull Void> loadChat(String chatId, CompletionRequest completionRequest);
    Mono<@NonNull CompletionRequest> getChat(String id);
    Mono<@NonNull CompletionRequest> addMessage(String id, CompletionMessage message);
    Mono<@NonNull Void> evict(String id);
}
