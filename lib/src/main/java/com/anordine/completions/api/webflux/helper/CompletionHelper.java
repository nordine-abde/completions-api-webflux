package com.anordine.completions.api.webflux.helper;

import com.anordine.completions.api.webflux.helper.history.IHistoryManager;
import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.CompletionResponse;
import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.model.message.abs.CompletionMessage;
import org.jspecify.annotations.NonNull;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class CompletionHelper {

    private static final String COMPLETION_PATH = "/chat/completions";

    private final WebClient webClient;
    private final IHistoryManager historyManager;

    public CompletionHelper(WebClient webClient) {
        this(webClient, null);
    }

    public CompletionHelper(WebClient webClient, IHistoryManager historyManager) {
        this.webClient = Objects.requireNonNull(webClient, "webClient must not be null");
        this.historyManager = historyManager;
    }

    public Mono<@NonNull CompletionResponse> callCompletionsApi(CompletionRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return webClient.post()
                .uri(COMPLETION_PATH)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CompletionResponse.class);
    }

    public Mono<@NonNull CompletionResponse> callCompletionsApi(String message) {
        return this.callCompletionsApi(CompletionRequest.create(message));
    }

    public Mono<@NonNull CompletionResponse> callCompletionsApi(String message, CompletionRole role) {
        return this.callCompletionsApi(CompletionRequest.create(message, role));
    }

    public Mono<@NonNull CompletionResponse> callCompletionsApi(String model, String message) {
        return this.callCompletionsApi(CompletionRequest.create(model, message));
    }

    public Mono<@NonNull CompletionResponse> callCompletionsApi(String model, String message, CompletionRole role) {
        return this.callCompletionsApi(CompletionRequest.create(model, message, role));
    }

    public Mono<@NonNull CompletionResponse> callCompletionsApi(String model, CompletionMessage... messages) {
        return this.callCompletionsApi(new CompletionRequest()
                .withModel(model)
                .addMessages(messages));
    }

    public Mono<@NonNull CompletionResponse> callCompletionsApiWithHistory(String chatId, CompletionMessage message) {
        return this.historyManager.addMessage(chatId, message)
                .flatMap(this::callCompletionsApi)
                .flatMap(completionResponse -> this.historyManager.addMessage(chatId, completionResponse.getChoices().getFirst().getMessage())
                        .thenReturn(completionResponse));
    }
}
