package anordine.completions.api.webflux.example;

import com.anordine.completions.api.webflux.helper.CompletionHelper;
import com.anordine.completions.api.webflux.helper.history.IHistoryManager;
import com.anordine.completions.api.webflux.helper.sse.ChatSseManager;
import com.anordine.completions.api.webflux.helper.sse.SseEventMessage;
import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.CompletionResponse;
import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.model.message.CompletionAssistantMessage;
import com.anordine.completions.api.webflux.model.message.CompletionUserMessage;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private static final String OPEN_AI_PROVIDER = "open-ai";
    private static final String OPEN_ROUTER_PROVIDER = "open-router";

    private final CompletionHelper openAiHelper;
    private final CompletionHelper openRouterHelper;
    private final IHistoryManager historyManager;
    private final ChatSseManager sseManager;
    private final String defaultModel;

    public ChatbotController(
            @Qualifier("openAiWebClient") WebClient openAiWebClient,
            @Qualifier("openRouterWebClient") WebClient openRouterWebClient,
            IHistoryManager historyManager,
            ChatSseManager sseManager,
            @Value("${example.chat.model:gpt-4o-mini}") String defaultModel
    ) {
        this.historyManager = historyManager;
        this.sseManager = sseManager;
        this.defaultModel = defaultModel;
        this.openAiHelper = new CompletionHelper(openAiWebClient, historyManager);
        this.openRouterHelper = new CompletionHelper(openRouterWebClient, historyManager);
    }

    @GetMapping(value = "/{chatId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<@NonNull ServerSentEvent<@NonNull SseEventMessage>> events(@PathVariable UUID chatId) {
        return sseManager.createSseStream(chatId);
    }

    @PostMapping
    public Mono<ChatResponse> send(@RequestBody Mono<ChatRequest> request) {
        return request.flatMap(this::send);
    }

    private Mono<ChatResponse> send(ChatRequest request) {
        UUID chatId = request.chatId() == null ? UUID.randomUUID() : request.chatId();
        String provider = normalizeProvider(request.provider());
        String model = valueOrDefault(request.model(), defaultModel);
        String message = requireMessage(request.message());
        CompletionUserMessage userMessage = new CompletionUserMessage(message);

        sseManager.setPending(chatId, true);
        sseManager.emitMessage(chatId, UUID.randomUUID(), message, CompletionRole.USER);

        return ensureChat(chatId.toString(), model)
                .then(helper(provider).callCompletionsApiWithHistory(chatId.toString(), userMessage))
                .map(response -> toChatResponse(chatId, provider, model, response))
                .doOnNext(response -> sseManager.emitMessage(
                        chatId,
                        UUID.randomUUID(),
                        response.content(),
                        CompletionRole.ASSISTANT
                ))
                .doOnError(error -> sseManager.emitError(chatId, error.getMessage()))
                .doFinally(signalType -> sseManager.setPending(chatId, false));
    }

    private Mono<Void> ensureChat(String chatId, String model) {
        return historyManager.getChat(chatId)
                .then()
                .onErrorResume(NoSuchElementException.class, exception -> historyManager.loadChat(
                        chatId,
                        new CompletionRequest().withModel(model)
                ));
    }

    private CompletionHelper helper(String provider) {
        return switch (provider) {
            case OPEN_AI_PROVIDER -> openAiHelper;
            case OPEN_ROUTER_PROVIDER -> openRouterHelper;
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }

    private String normalizeProvider(String provider) {
        if (provider == null || provider.isBlank()) {
            return OPEN_AI_PROVIDER;
        }
        String normalized = provider.trim().toLowerCase().replace("_", "-");
        if ("openai".equals(normalized)) {
            return OPEN_AI_PROVIDER;
        }
        if ("openrouter".equals(normalized)) {
            return OPEN_ROUTER_PROVIDER;
        }
        return normalized;
    }

    private String requireMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        return message.trim();
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private ChatResponse toChatResponse(
            UUID chatId,
            String provider,
            String model,
            CompletionResponse response
    ) {
        return new ChatResponse(chatId, provider, model, assistantContent(response));
    }

    private String assistantContent(CompletionResponse response) {
        if (response.getChoices() == null || response.getChoices().isEmpty()) {
            return "";
        }
        CompletionAssistantMessage message = response.getChoices().getFirst().getMessage();
        return message == null || message.getContent() == null ? "" : message.getContent();
    }

    public record ChatRequest(UUID chatId, String provider, String model, String message) {
    }

    public record ChatResponse(UUID chatId, String provider, String model, String content) {
    }
}
