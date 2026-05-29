package com.anordine.simplifier.webflux.ai.helper.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

public class ToolExecutor {

    private final CompletionToolRegistry toolRegistry;
    private final ObjectMapper objectMapper;
    private final Scheduler scheduler;

    public ToolExecutor(CompletionToolRegistry toolRegistry) {
        this(toolRegistry, new ObjectMapper(), Schedulers.boundedElastic());
    }

    public ToolExecutor(CompletionToolRegistry toolRegistry, ObjectMapper objectMapper) {
        this(toolRegistry, objectMapper, Schedulers.boundedElastic());
    }

    ToolExecutor(CompletionToolRegistry toolRegistry, ObjectMapper objectMapper, Scheduler scheduler) {
        this.toolRegistry = Objects.requireNonNull(toolRegistry, "toolRegistry must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler must not be null");
    }

    public Mono<Object> execute(String name) {
        return execute(name, (Object) null);
    }

    public Mono<Object> executeJson(String name, String arguments) {
        return execute(name, arguments);
    }

    public Mono<Object> execute(String name, Object arguments) {
        Objects.requireNonNull(name, "name must not be null");
        return Mono.defer(() -> {
                    CompletionToolRegistry.RegisteredTool<?, ?> tool = toolRegistry.getRequiredExecutableTool(name);
                    return executeRegisteredTool(tool, arguments);
                })
                .subscribeOn(scheduler);
    }

    private <I, O> Mono<Object> executeRegisteredTool(CompletionToolRegistry.RegisteredTool<I, O> tool,
                                                      Object arguments) {
        I input = tool.argumentBinder().bind(arguments, objectMapper);
        O result = tool.function().apply(input);
        return toMono(result);
    }

    private Mono<Object> toMono(Object result) {
        if (result == null) {
            return Mono.empty();
        }
        if (result instanceof Mono<?> mono) {
            return mono.cast(Object.class);
        }
        if (result instanceof Publisher<?> publisher) {
            return Flux.from(publisher)
                    .collectList()
                    .cast(Object.class);
        }
        return Mono.just(result);
    }
}
