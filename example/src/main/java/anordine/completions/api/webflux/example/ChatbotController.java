package anordine.completions.api.webflux.example;

import com.anordine.completions.api.webflux.helper.CompletionHelper;
import com.anordine.completions.api.webflux.helper.history.IHistoryManager;
import com.anordine.completions.api.webflux.helper.sse.ChatSseManager;
import com.anordine.completions.api.webflux.helper.sse.SseEventMessage;
import com.anordine.completions.api.webflux.helper.tool.CompletionToolRegistry;
import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.CompletionResponse;
import com.anordine.completions.api.webflux.model.CompletionStreamChoice;
import com.anordine.completions.api.webflux.model.CompletionStreamDelta;
import com.anordine.completions.api.webflux.model.CompletionStreamOptions;
import com.anordine.completions.api.webflux.model.CompletionStreamResponse;
import com.anordine.completions.api.webflux.model.CompletionStreamToolCall;
import com.anordine.completions.api.webflux.model.enums.finish.CompletionFinishReason;
import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.model.message.CompletionAssistantMessage;
import com.anordine.completions.api.webflux.model.message.CompletionChoices;
import com.anordine.completions.api.webflux.model.message.CompletionUserMessage;
import com.anordine.completions.api.webflux.model.tool.CompletionMessageCustomTool;
import com.anordine.completions.api.webflux.model.tool.CompletionMessageFunctionTool;
import com.anordine.completions.api.webflux.model.tool.CompletionMessageFunctionToolCall;
import com.anordine.completions.api.webflux.model.tool.abs.CompletionToolCall;
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

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private static final String OPEN_AI_PROVIDER = "open-ai";
    private static final String OPEN_ROUTER_PROVIDER = "open-router";
    private static final String DEEPSEEK_PROVIDER = "deepseek";
    private static final tools.jackson.databind.ObjectMapper OBJECT_MAPPER = new tools.jackson.databind.ObjectMapper();

    private final CompletionHelper openAiHelper;
    private final CompletionHelper openRouterHelper;
    private final CompletionHelper deepSeekHelper;
    private final IHistoryManager historyManager;
    private final ChatSseManager sseManager;
    private final CompletionToolRegistry toolRegistry;
    private final String defaultModel;

    public ChatbotController(
            @Qualifier("openAiWebClient") WebClient openAiWebClient,
            @Qualifier("openRouterWebClient") WebClient openRouterWebClient,
            @Qualifier("deepSeekWebClient") WebClient deepSeekWebClient,
            IHistoryManager historyManager,
            ChatSseManager sseManager,
            CompletionToolRegistry toolRegistry,
            @Value("${example.chat.model:gpt-4o-mini}") String defaultModel
    ) {
        this.historyManager = historyManager;
        this.sseManager = sseManager;
        this.toolRegistry = toolRegistry;
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
        List<String> tools = normalizeTools(request.tools());
        CompletionUserMessage userMessage = new CompletionUserMessage(message);
        UUID assistantMessageId = UUID.randomUUID();

        sseManager.setPending(chatId, true);
        sseManager.emitMessage(chatId, UUID.randomUUID(), message, CompletionRole.USER);

        Mono<ChatResponse> response = mode == ChatMode.STREAM
                ? sendStreaming(chatId, provider, model, userMessage, assistantMessageId)
                : sendSimple(chatId, provider, model, userMessage, assistantMessageId);

        return ensureChat(chatId.toString(), model, tools)
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
        ToolCallLogAccumulator toolCallLogAccumulator = new ToolCallLogAccumulator();
        sseManager.emitMessageStart(chatId, assistantMessageId, CompletionRole.ASSISTANT);

        return helper(provider).streamCompletionsApiWithHistory(chatId.toString(), userMessage)
                .doOnNext(response -> emitStreamResponse(
                        chatId,
                        assistantMessageId,
                        response,
                        assistantContent,
                        toolCallLogAccumulator
                ))
                .doOnComplete(() -> {
                    emitToolCallLogs(chatId, assistantMessageId, toolCallLogAccumulator.drain());
                    sseManager.emitMessageDone(
                            chatId,
                            assistantMessageId,
                            assistantContent.toString(),
                            CompletionRole.ASSISTANT
                    );
                })
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
                    emitToolCallLogs(chatId, assistantMessageId, assistantToolCalls(assistantMessage));
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

    private Mono<Void> ensureChat(String chatId, String model, List<String> tools) {
        return historyManager.getChat(chatId)
                .then()
                .onErrorResume(NoSuchElementException.class, exception -> historyManager.loadChat(
                        chatId,
                        requestWithTools(model, tools)
                ));
    }

    private CompletionRequest requestWithTools(String model, List<String> tools) {
        CompletionRequest completionRequest = new CompletionRequest()
                .withModel(model)
                .withStreamOptions(new CompletionStreamOptions().withIncludeUsage(true));
        if (!tools.isEmpty()) {
            toolRegistry.addToolsTo(completionRequest, tools.toArray(String[]::new));
        }
        return completionRequest;
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

    private List<String> normalizeTools(List<String> tools) {
        if (tools == null) {
            return List.of();
        }
        return tools.stream()
                .filter(tool -> tool != null && !tool.isBlank())
                .map(String::trim)
                .toList();
    }

    private void emitStreamResponse(
            UUID chatId,
            UUID messageId,
            CompletionStreamResponse response,
            StringBuilder assistantContent,
            ToolCallLogAccumulator toolCallLogAccumulator
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
                    toolCallLogAccumulator.accept(toolCall);
                }
            }
            if (isToolCallFinish(choice.getFinishReason())) {
                emitToolCallLogs(chatId, messageId, toolCallLogAccumulator.drain());
            }
        }
    }

    private boolean isToolCallFinish(CompletionFinishReason finishReason) {
        return finishReason == CompletionFinishReason.TOOL_CALLS
                || finishReason == CompletionFinishReason.FUNCTION_CALL;
    }

    private List<ToolCallLog> assistantToolCalls(CompletionAssistantMessage assistantMessage) {
        if (assistantMessage == null || assistantMessage.getToolCalls() == null) {
            return List.of();
        }
        return assistantMessage.getToolCalls().stream()
                .map(this::toolCallLog)
                .filter(log -> log.name() != null && !log.name().isBlank())
                .toList();
    }

    private ToolCallLog toolCallLog(CompletionToolCall toolCall) {
        if (toolCall instanceof CompletionMessageFunctionToolCall functionToolCall
                && functionToolCall.getFunction() != null) {
            CompletionMessageFunctionTool function = functionToolCall.getFunction();
            return new ToolCallLog(toolCall.getId(), "function", function.getName(), function.getArguments());
        }
        return new ToolCallLog(toolCall.getId(), "tool", null, null);
    }

    private void emitToolCallLogs(UUID chatId, UUID messageId, List<ToolCallLog> toolCalls) {
        for (ToolCallLog toolCall : toolCalls) {
            sseManager.emitToolCall(chatId, messageId, toolCallContent(toolCall), CompletionRole.ASSISTANT);
        }
    }

    private String toolCallContent(ToolCallLog toolCall) {
        try {
            return OBJECT_MAPPER.writeValueAsString(toolCall);
        } catch (Exception exception) {
            return "{\"name\":\"" + toolCall.name() + "\",\"arguments\":\"" + toolCall.arguments() + "\"}";
        }
    }

    private enum ChatMode {
        SIMPLE("simple"),
        STREAM("stream");

        private final String value;

        ChatMode(String value) {
            this.value = value;
        }
    }

    public record ChatRequest(UUID chatId, String provider, String model, String mode, String message, List<String> tools) {
    }

    public record ChatResponse(UUID chatId, String provider, String model, String mode, String content) {
    }

    private record ToolCallLog(String id, String type, String name, String arguments) {
    }

    private static final class ToolCallLogAccumulator {

        private final Map<Integer, MutableToolCallLog> toolCalls = new java.util.LinkedHashMap<>();

        private void accept(CompletionStreamToolCall toolCall) {
            if (toolCall == null) {
                return;
            }
            MutableToolCallLog log = toolCalls.computeIfAbsent(toolCallIndex(toolCall), ignored -> new MutableToolCallLog());
            if (toolCall.getId() != null) {
                log.id = toolCall.getId();
            }
            if (toolCall.getType() != null) {
                log.type = toolCall.getType().getValue();
            }
            CompletionMessageFunctionTool function = toolCall.getFunction();
            if (function != null) {
                log.type = "function";
                if (function.getName() != null) {
                    log.name = function.getName();
                }
                if (function.getArguments() != null) {
                    log.arguments.append(function.getArguments());
                }
            }
            CompletionMessageCustomTool custom = toolCall.getCustom();
            if (custom != null) {
                log.type = "custom";
                if (custom.getName() != null) {
                    log.name = custom.getName();
                }
                if (custom.getInput() != null) {
                    log.arguments.append(custom.getInput());
                }
            }
        }

        private List<ToolCallLog> drain() {
            List<ToolCallLog> logs = toolCalls.values().stream()
                    .map(MutableToolCallLog::toLog)
                    .filter(log -> log.name() != null && !log.name().isBlank())
                    .toList();
            toolCalls.clear();
            return logs;
        }

        private static Integer toolCallIndex(CompletionStreamToolCall toolCall) {
            return toolCall.getIndex() == null ? 0 : toolCall.getIndex();
        }
    }

    private static final class MutableToolCallLog {

        private String id;
        private String type = "function";
        private String name;
        private final StringBuilder arguments = new StringBuilder();

        private ToolCallLog toLog() {
            return new ToolCallLog(id, type, name, arguments.toString());
        }
    }
}
