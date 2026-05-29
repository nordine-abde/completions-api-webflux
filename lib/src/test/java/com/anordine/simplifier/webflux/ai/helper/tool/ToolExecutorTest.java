package com.anordine.simplifier.webflux.ai.helper.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ToolExecutorTest {

    @Test
    void executesNoArgumentTool() {
        try (AnnotationConfigApplicationContext context = registryContext(ToolProvider.class)) {
            ToolExecutor executor = toolExecutor(context);

            assertEquals("pong", executor.execute("ping").block());
        }
    }

    @Test
    void executesSingleDtoArgumentTool() {
        try (AnnotationConfigApplicationContext context = registryContext(ToolProvider.class)) {
            ToolExecutor executor = toolExecutor(context);

            Object result = executor.executeJson("save_profile", """
                    {"name":"Ada","age":37}
                    """).block();

            assertEquals(new UserResult("Ada", 37), result);
        }
    }

    @Test
    void executesNamedArgumentTool() {
        try (AnnotationConfigApplicationContext context = registryContext(ToolProvider.class)) {
            ToolExecutor executor = toolExecutor(context);

            Object result = executor.executeJson("findProfile", """
                    {"user_id":"u-1","include_roles":true}
                    """).block();

            assertEquals("u-1:true", result);
        }
    }

    @Test
    void executesMonoReturningTool() {
        try (AnnotationConfigApplicationContext context = registryContext(ToolProvider.class)) {
            ToolExecutor executor = toolExecutor(context);

            assertEquals("async", executor.execute("asyncValue").block());
        }
    }

    @Test
    void collectsPublisherReturningToolToList() {
        try (AnnotationConfigApplicationContext context = registryContext(ToolProvider.class)) {
            ToolExecutor executor = toolExecutor(context);

            Object result = executor.execute("streamValues").block();

            assertEquals(List.of("a", "b"), result);
        }
    }

    @Test
    void rejectsUnknownToolName() {
        try (AnnotationConfigApplicationContext context = registryContext(ToolProvider.class)) {
            ToolExecutor executor = toolExecutor(context);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> executor.execute("missing").block()
            );

            assertEquals("Unknown @CompletionTool name: missing", exception.getMessage());
        }
    }

    @Test
    void unwrapsInvocationFailures() {
        try (AnnotationConfigApplicationContext context = registryContext(ToolProvider.class)) {
            ToolExecutor executor = toolExecutor(context);

            ToolExecutionException exception = assertThrows(
                    ToolExecutionException.class,
                    () -> executor.execute("fails").block()
            );

            assertInstanceOf(IllegalStateException.class, exception.getCause());
            assertEquals("boom", exception.getCause().getMessage());
        }
    }

    private ToolExecutor toolExecutor(AnnotationConfigApplicationContext context) {
        return new ToolExecutor(
                context.getBean(CompletionToolRegistry.class),
                new ObjectMapper(),
                Schedulers.immediate()
        );
    }

    private AnnotationConfigApplicationContext registryContext(Class<?> providerType) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean(CompletionToolSchemaGenerator.class);
        context.registerBean(providerType);
        context.registerBean(CompletionToolRegistry.class, () -> new CompletionToolRegistry(
                context,
                context.getBean(CompletionToolSchemaGenerator.class)
        ));
        context.refresh();
        return context;
    }

    @CompletionProvider
    static class ToolProvider {

        @CompletionTool
        public String ping() {
            return "pong";
        }

        @CompletionTool(name = "save_profile")
        public UserResult saveProfile(UserProfile profile) {
            return new UserResult(profile.name(), profile.age());
        }

        @CompletionTool(strict = false)
        public String findProfile(@JsonProperty(value = "user_id", required = true) String userId,
                                  @JsonProperty("include_roles") boolean includeRoles) {
            return userId + ":" + includeRoles;
        }

        @CompletionTool
        public Mono<String> asyncValue() {
            return Mono.just("async");
        }

        @CompletionTool
        public Flux<String> streamValues() {
            return Flux.just("a", "b");
        }

        @CompletionTool
        public String fails() {
            throw new IllegalStateException("boom");
        }
    }

    record UserProfile(
            @JsonProperty(required = true) String name,
            @JsonProperty(required = true) int age
    ) {
    }

    record UserResult(String name, int age) {
    }
}
