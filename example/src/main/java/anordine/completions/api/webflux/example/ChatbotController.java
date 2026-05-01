package anordine.completions.api.webflux.example;

import com.anordine.completions.api.webflux.helper.CompletionHelper;
import com.anordine.completions.api.webflux.helper.history.IHistoryManager;
import com.anordine.completions.api.webflux.helper.sse.ChatSseManager;
import com.anordine.completions.api.webflux.helper.sse.SseEventMessage;
import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.CompletionResponse;
import com.anordine.completions.api.webflux.model.CompletionStreamChoice;
import com.anordine.completions.api.webflux.model.CompletionStreamDelta;
import com.anordine.completions.api.webflux.model.CompletionStreamOptions;
import com.anordine.completions.api.webflux.model.CompletionStreamResponse;
import com.anordine.completions.api.webflux.model.CompletionStreamToolCall;
import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.model.message.CompletionAssistantMessage;
import com.anordine.completions.api.webflux.model.message.CompletionChoices;
import com.anordine.completions.api.webflux.model.message.CompletionUserMessage;
import com.anordine.completions.api.webflux.model.tool.CompletionMessageCustomTool;
import com.anordine.completions.api.webflux.model.tool.CompletionMessageFunctionTool;
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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private static final String OPEN_AI_PROVIDER = "open-ai";
    private static final String OPEN_ROUTER_PROVIDER = "open-router";
    private static final String DEEPSEEK_PROVIDER = "deepseek";

    private final CompletionHelper openAiHelper;
    private final CompletionHelper openRouterHelper;
    private final CompletionHelper deepSeekHelper;
    private final IHistoryManager historyManager;
    private final ChatSseManager sseManager;
    private final String defaultModel;

    public ChatbotController(
            @Qualifier("openAiWebClient") WebClient openAiWebClient,
            @Qualifier("openRouterWebClient") WebClient openRouterWebClient,
            @Qualifier("deepSeekWebClient") WebClient deepSeekWebClient,
            IHistoryManager historyManager,
            ChatSseManager sseManager,
            @Value("${example.chat.model:gpt-4o-mini}") String defaultModel
    ) {
        this.historyManager = historyManager;
        this.sseManager = sseManager;
        this.defaultModel = defaultModel;
        this.openAiHelper = new CompletionHelper(openAiWebClient, historyManager);
        this.openRouterHelper = new CompletionHelper(openRouterWebClient, historyManager);
        this.deepSeekHelper = new CompletionHelper(deepSeekWebClient, historyManager);
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
        ChatMode mode = normalizeMode(request.mode());
        CompletionUserMessage userMessage = new CompletionUserMessage(message);
        UUID assistantMessageId = UUID.randomUUID();

        sseManager.setPending(chatId, true);
        sseManager.emitMessage(chatId, UUID.randomUUID(), message, CompletionRole.USER);

        Mono<ChatResponse> response = mode == ChatMode.STREAM
                ? sendStreaming(chatId, provider, model, userMessage, assistantMessageId)
                : sendSimple(chatId, provider, model, userMessage, assistantMessageId);

        return ensureChat(chatId.toString(), model)
                .then(response)
                .doOnError(error -> sseManager.emitError(chatId, error.getMessage()))
                .doFinally(signalType -> sseManager.setPending(chatId, false));
    }

    private Mono<ChatResponse> sendStreaming(
            UUID chatId,
            String provider,
            String model,
            CompletionUserMessage userMessage,
            UUID assistantMessageId
    ) {
        StringBuilder assistantContent = new StringBuilder();
        sseManager.emitMessageStart(chatId, assistantMessageId, CompletionRole.ASSISTANT);

        return helper(provider).streamCompletionsApiWithHistory(chatId.toString(), userMessage)
                .doOnNext(response -> emitStreamResponse(chatId, assistantMessageId, response, assistantContent))
                .doOnComplete(() -> sseManager.emitMessageDone(
                        chatId,
                        assistantMessageId,
                        assistantContent.toString(),
                        CompletionRole.ASSISTANT
                ))
                .then(Mono.fromSupplier(() -> new ChatResponse(
                        chatId,
                        provider,
                        model,
                        ChatMode.STREAM.value,
                        assistantContent.toString()
                )))
                .doOnError(e -> {
                    System.out.println(e.getMessage());
                    e.printStackTrace();


                    if (e instanceof WebClientResponseException webClientResponseException){
                        System.out.println(webClientResponseException.getResponseBodyAs(String.class));
                    }
                });
    }

    private Mono<ChatResponse> sendSimple(
            UUID chatId,
            String provider,
            String model,
            CompletionUserMessage userMessage,
            UUID assistantMessageId
    ) {
        return historyManager.addMessage(chatId.toString(), userMessage)
                .map(this::asNonStreamingRequest)
                .flatMap(helper(provider)::callCompletionsApi)
                .flatMap(response -> {
                    CompletionAssistantMessage assistantMessage = assistantMessage(response);
                    String content = assistantMessage == null ? "" : valueOrDefault(assistantMessage.getContent(), "");
                    if (response.getUsage() != null && sseManager.isEmitUsageEvents()) {
                        sseManager.emitUsage(chatId, response.getUsage());
                    }
                    sseManager.emitMessage(chatId, assistantMessageId, content, CompletionRole.ASSISTANT);

                    Mono<Void> addAssistantMessage = assistantMessage == null
                            ? Mono.empty()
                            : historyManager.addMessage(chatId.toString(), assistantMessage).then();

                    return addAssistantMessage.thenReturn(new ChatResponse(
                            chatId,
                            provider,
                            model,
                            ChatMode.SIMPLE.value,
                            content
                    ));
                });
    }

    private CompletionRequest asNonStreamingRequest(CompletionRequest request) {
        CompletionRequest nonStreamingRequest = request.deepClone();
        nonStreamingRequest.setStream(null);
        nonStreamingRequest.setStreamOptions(null);
        return nonStreamingRequest;
    }

    private CompletionAssistantMessage assistantMessage(CompletionResponse response) {
        if (response.getChoices() == null || response.getChoices().isEmpty()) {
            return null;
        }
        CompletionChoices choice = response.getChoices().getFirst();
        return choice == null ? null : choice.getMessage();
    }

    private Mono<Void> ensureChat(String chatId, String model) {
        return historyManager.getChat(chatId)
                .then()
                .onErrorResume(NoSuchElementException.class, exception -> historyManager.loadChat(
                        chatId,
                        new CompletionRequest()
                                .withModel(model)
                                .withStreamOptions(new CompletionStreamOptions().withIncludeUsage(true))
                ));
    }

    private CompletionHelper helper(String provider) {
        return switch (provider) {
            case OPEN_AI_PROVIDER -> openAiHelper;
            case OPEN_ROUTER_PROVIDER -> openRouterHelper;
            case DEEPSEEK_PROVIDER -> deepSeekHelper;
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
        if ("deep-seek".equals(normalized)) {
            return DEEPSEEK_PROVIDER;
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

    private ChatMode normalizeMode(String mode) {
        if (mode == null || mode.isBlank()) {
            return ChatMode.STREAM;
        }
        return switch (mode.trim().toLowerCase()) {
            case "simple", "non-streaming", "non_streaming", "sync" -> ChatMode.SIMPLE;
            case "stream", "streaming" -> ChatMode.STREAM;
            default -> throw new IllegalArgumentException("Unsupported mode: " + mode);
        };
    }

    private void emitStreamResponse(
            UUID chatId,
            UUID messageId,
            CompletionStreamResponse response,
            StringBuilder assistantContent
    ) {
        if (response.getUsage() != null && sseManager.isEmitUsageEvents()) {
            sseManager.emitUsage(chatId, response.getUsage());
        }
        if (response.getChoices() == null) {
            return;
        }
        for (CompletionStreamChoice choice : response.getChoices()) {
            CompletionStreamDelta delta = choice == null ? null : choice.getDelta();
            if (delta == null) {
                continue;
            }
            if (delta.getContent() != null) {
                assistantContent.append(delta.getContent());
                sseManager.emitChunk(chatId, messageId, delta.getContent(), CompletionRole.ASSISTANT);
            }
            if (delta.getToolCalls() != null) {
                for (CompletionStreamToolCall toolCall : delta.getToolCalls()) {
                    String toolCallContent = toolCallContent(toolCall);
                    if (toolCallContent != null && !toolCallContent.isBlank()) {
                        sseManager.emitToolCallChunk(chatId, messageId, toolCallContent, CompletionRole.ASSISTANT);
                    }
                }
            }
        }
    }

    private String toolCallContent(CompletionStreamToolCall toolCall) {
        if (toolCall == null) {
            return null;
        }
        CompletionMessageFunctionTool function = toolCall.getFunction();
        if (function != null) {
            return function.getArguments() != null ? function.getArguments() : function.getName();
        }
        CompletionMessageCustomTool custom = toolCall.getCustom();
        if (custom != null) {
            return custom.getInput() != null ? custom.getInput() : custom.getName();
        }
        return toolCall.getId();
    }

    private enum ChatMode {
        SIMPLE("simple"),
        STREAM("stream");

        private final String value;

        ChatMode(String value) {
            this.value = value;
        }
    }

    public record ChatRequest(UUID chatId, String provider, String model, String mode, String message) {
    }

    public record ChatResponse(UUID chatId, String provider, String model, String mode, String content) {
    }
}
