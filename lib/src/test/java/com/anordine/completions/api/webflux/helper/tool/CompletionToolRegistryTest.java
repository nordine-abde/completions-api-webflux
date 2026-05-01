package com.anordine.completions.api.webflux.helper.tool;

import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.tool.CompletionFunctionTool;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompletionToolRegistryTest {

    @Test
    void scansCompletionProviderBeansAfterStartup() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(CompletionToolSchemaGenerator.class);
            context.registerBean(ToolProvider.class);
            context.registerBean(CompletionToolRegistry.class, () -> new CompletionToolRegistry(
                    context,
                    context.getBean(CompletionToolSchemaGenerator.class)
            ));

            context.refresh();

            CompletionToolRegistry registry = context.getBean(CompletionToolRegistry.class);
            List<com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> tools = registry.getTools();

            assertEquals(2, tools.size());
            CompletionFunctionTool first = assertInstanceOf(CompletionFunctionTool.class, tools.get(0));
            CompletionFunctionTool second = assertInstanceOf(CompletionFunctionTool.class, tools.get(1));
            assertEquals("first", first.getFunction().getName());
            assertEquals("second", second.getFunction().getName());
        }
    }

    @Test
    void canAddRegisteredToolsToRequest() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(CompletionToolSchemaGenerator.class);
            context.registerBean(ToolProvider.class);
            context.registerBean(CompletionToolRegistry.class, () -> new CompletionToolRegistry(
                    context,
                    context.getBean(CompletionToolSchemaGenerator.class)
            ));

            context.refresh();

            CompletionRequest request = new CompletionRequest();
            context.getBean(CompletionToolRegistry.class).addToolsTo(request);

            assertEquals(2, request.getTools().size());
        }
    }

    @Test
    void exposesToolsByName() {
        try (AnnotationConfigApplicationContext context = registryContext(ToolProvider.class)) {
            CompletionToolRegistry registry = context.getBean(CompletionToolRegistry.class);

            Map<String, com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> toolsByName =
                    registry.getToolsByName();

            assertEquals(2, toolsByName.size());
            assertTrue(toolsByName.containsKey("first"));
            assertTrue(toolsByName.containsKey("second"));
            assertTrue(registry.getTool("first").isPresent());
        }
    }

    @Test
    void canSelectToolsByName() {
        try (AnnotationConfigApplicationContext context = registryContext(ToolProvider.class)) {
            CompletionToolRegistry registry = context.getBean(CompletionToolRegistry.class);

            List<com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> tools = registry.getTools("second");

            assertEquals(1, tools.size());
            CompletionFunctionTool selected = assertInstanceOf(CompletionFunctionTool.class, tools.getFirst());
            assertEquals("second", selected.getFunction().getName());
        }
    }

    @Test
    void canAddSelectedToolsToRequest() {
        try (AnnotationConfigApplicationContext context = registryContext(ToolProvider.class)) {
            CompletionRequest request = new CompletionRequest();

            context.getBean(CompletionToolRegistry.class).addToolsTo(request, "first");

            assertEquals(1, request.getTools().size());
            CompletionFunctionTool selected = assertInstanceOf(CompletionFunctionTool.class, request.getTools().getFirst());
            assertEquals("first", selected.getFunction().getName());
        }
    }

    @Test
    void rejectsUnknownToolNames() {
        try (AnnotationConfigApplicationContext context = registryContext(ToolProvider.class)) {
            CompletionToolRegistry registry = context.getBean(CompletionToolRegistry.class);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> registry.getTools("missing")
            );

            assertEquals("Unknown @CompletionTool name: missing", exception.getMessage());
        }
    }

    @Test
    void rejectsDuplicateToolNamesAtStartup() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(CompletionToolSchemaGenerator.class);
            context.registerBean(DuplicateToolProvider.class);
            context.registerBean(CompletionToolRegistry.class, () -> new CompletionToolRegistry(
                    context,
                    context.getBean(CompletionToolSchemaGenerator.class)
            ));

            assertThrows(Exception.class, context::refresh);
        }
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
        public String second() {
            return "second";
        }

        @CompletionTool
        public String first() {
            return "first";
        }
    }

    @CompletionProvider
    static class DuplicateToolProvider {

        @CompletionTool(name = "same")
        public String first() {
            return "first";
        }

        @CompletionTool(name = "same")
        public String second() {
            return "second";
        }
    }
}
