package com.anordine.simplifier.webflux.ai.helper;

import com.anordine.simplifier.webflux.ai.helper.history.IHistoryManager;
import com.anordine.simplifier.webflux.ai.model.CompletionRequest;
import com.anordine.simplifier.webflux.ai.model.CompletionResponse;
import com.anordine.simplifier.webflux.ai.model.CompletionStreamChoice;
import com.anordine.simplifier.webflux.ai.model.CompletionStreamDelta;
import com.anordine.simplifier.webflux.ai.model.CompletionStreamResponse;
import com.anordine.simplifier.webflux.ai.model.CompletionStreamToolCall;
import com.anordine.simplifier.webflux.ai.model.enums.tool.CompletionToolType;
import com.anordine.simplifier.webflux.ai.model.enums.role.CompletionRole;
import com.anordine.simplifier.webflux.ai.model.message.CompletionAssistantMessage;
import com.anordine.simplifier.webflux.ai.model.message.abs.CompletionMessage;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionMessageCustomTool;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionMessageCustomToolCall;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionMessageFunctionTool;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionMessageFunctionToolCall;
import com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionToolCall;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CompletionHelper {

    private static final String COMPLETION_PATH = "/chat/completions";
    private static final String STREAM_DONE = "[DONE]";

    private final WebClient webClient;
    private final IHistoryManager historyManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public Flux<@NonNull CompletionStreamResponse> streamCompletionsApi(CompletionRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        CompletionRequest streamRequest = request.deepClone();
        streamRequest.setStream(Boolean.TRUE);

        return webClient.post()
                .uri(COMPLETION_PATH)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(streamRequest)
                .retrieve()
                .bodyToFlux(String.class)
                .flatMapIterable(this::extractStreamData)
                .filter(data -> !data.isBlank())
                .filter(data -> !STREAM_DONE.equals(data))
                .map(this::readStreamResponse);
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

    public Flux<@NonNull CompletionStreamResponse> streamCompletionsApiWithHistory(String chatId, CompletionMessage message) {
        Objects.requireNonNull(this.historyManager, "historyManager must not be null");
        StreamAccumulator accumulator = new StreamAccumulator();

        return this.historyManager.addMessage(chatId, message)
                .flatMapMany(this::streamCompletionsApi)
                .doOnNext(accumulator::accept)
                .concatWith(Mono.defer(() -> this.historyManager.addMessage(chatId, accumulator.toAssistantMessage()).then(Mono.empty())));
    }

    private List<String> extractStreamData(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        if (!value.contains("data:")) {
            return List.of(value.trim());
        }

        List<String> events = new ArrayList<>();
        StringBuilder event = new StringBuilder();
        for (String line : value.split("\\R", -1)) {
            if (line.isBlank()) {
                addExtractedEvent(events, event);
                continue;
            }
            if (line.startsWith("data:")) {
                if (!event.isEmpty()) {
                    event.append('\n');
                }
                event.append(line.substring("data:".length()).trim());
            }
        }
        addExtractedEvent(events, event);
        return events;
    }

    private void addExtractedEvent(List<String> events, StringBuilder event) {
        if (!event.isEmpty()) {
            events.add(event.toString());
            event.setLength(0);
        }
    }

    private CompletionStreamResponse readStreamResponse(String data) {
        try {
            return objectMapper.readValue(data, CompletionStreamResponse.class);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to parse completion stream chunk", exception);
        }
    }

    private static final class StreamAccumulator {

        private final StringBuilder content = new StringBuilder();
        private final StringBuilder refusal = new StringBuilder();
        private final Map<Integer, ToolCallAccumulator> toolCalls = new LinkedHashMap<>();

        private void accept(CompletionStreamResponse response) {
            if (response.getChoices() == null) {
                return;
            }
            for (CompletionStreamChoice choice : response.getChoices()) {
                CompletionStreamDelta delta = choice == null ? null : choice.getDelta();
                if (delta == null) {
                    continue;
                }
                if (delta.getContent() != null) {
                    content.append(delta.getContent());
                }
                if (delta.getRefusal() != null) {
                    refusal.append(delta.getRefusal());
                }
                if (delta.getToolCalls() != null) {
                    for (CompletionStreamToolCall toolCall : delta.getToolCalls()) {
                        if (toolCall != null) {
                            toolCalls.computeIfAbsent(toolCallIndex(toolCall), ignored -> new ToolCallAccumulator())
                                    .accept(toolCall);
                        }
                    }
                }
            }
        }

        private CompletionAssistantMessage toAssistantMessage() {
            String finalContent = content.isEmpty() && !toolCalls.isEmpty() ? null : content.toString();
            CompletionAssistantMessage message = new CompletionAssistantMessage(finalContent);
            if (!refusal.isEmpty()) {
                message.setRefusal(refusal.toString());
            }
            List<CompletionToolCall> calls = toolCalls.values().stream()
                    .map(ToolCallAccumulator::toToolCall)
                    .filter(Objects::nonNull)
                    .toList();
            if (!calls.isEmpty()) {
                message.setToolCalls(calls);
            }
            return message;
        }

        private static Integer toolCallIndex(CompletionStreamToolCall toolCall) {
            return toolCall.getIndex() == null ? 0 : toolCall.getIndex();
        }
    }

    private static final class ToolCallAccumulator {

        private String id;
        private CompletionToolType type;
        private String functionName;
        private final StringBuilder functionArguments = new StringBuilder();
        private String customName;
        private final StringBuilder customInput = new StringBuilder();

        private void accept(CompletionStreamToolCall toolCall) {
            if (toolCall.getId() != null) {
                id = toolCall.getId();
            }
            if (toolCall.getType() != null) {
                type = toolCall.getType();
            }
            CompletionMessageFunctionTool function = toolCall.getFunction();
            if (function != null) {
                if (function.getName() != null) {
                    functionName = function.getName();
                }
                if (function.getArguments() != null) {
                    functionArguments.append(function.getArguments());
                }
            }
            CompletionMessageCustomTool custom = toolCall.getCustom();
            if (custom != null) {
                if (custom.getName() != null) {
                    customName = custom.getName();
                }
                if (custom.getInput() != null) {
                    customInput.append(custom.getInput());
                }
            }
        }

        private CompletionToolCall toToolCall() {
            if (type == CompletionToolType.CUSTOM) {
                return new CompletionMessageCustomToolCall(id, new CompletionMessageCustomTool(customInput.toString(), customName));
            }
            if (type == CompletionToolType.FUNCTION || functionName != null || !functionArguments.isEmpty()) {
                return new CompletionMessageFunctionToolCall(
                        id,
                        new CompletionMessageFunctionTool(functionArguments.toString(), functionName)
                );
            }
            return null;
        }
    }
}
