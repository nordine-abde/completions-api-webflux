package com.anordine.completions.api.webflux.helper.tool;

import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.tool.CompletionFunctionTool;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CompletionToolRegistry implements SmartInitializingSingleton {

    private final ApplicationContext applicationContext;
    private final CompletionToolSchemaGenerator schemaGenerator;
    private List<com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> tools = List.of();
    private Map<String, com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> toolsByName = Map.of();

    public CompletionToolRegistry(ApplicationContext applicationContext,
                                  CompletionToolSchemaGenerator schemaGenerator) {
        this.applicationContext = Objects.requireNonNull(applicationContext, "applicationContext must not be null");
        this.schemaGenerator = Objects.requireNonNull(schemaGenerator, "schemaGenerator must not be null");
    }

    @Override
    public void afterSingletonsInstantiated() {
        refresh();
    }

    public void refresh() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(CompletionProvider.class);
        List<com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> discoveredTools = new ArrayList<>();

        providers.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> discoveredTools.addAll(discoverTools(entry.getValue())));

        this.tools = List.copyOf(discoveredTools);
        this.toolsByName = indexToolsByName(discoveredTools);
    }

    public List<com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> getTools() {
        return tools.stream()
                .map(tool -> tool == null ? null : tool.deepClone())
                .toList();
    }

    public Map<String, com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> getToolsByName() {
        Map<String, com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> clonedTools = new LinkedHashMap<>();
        toolsByName.forEach((name, tool) -> clonedTools.put(name, tool.deepClone()));
        return Collections.unmodifiableMap(clonedTools);
    }

    public Optional<com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> getTool(String name) {
        Objects.requireNonNull(name, "name must not be null");
        com.anordine.completions.api.webflux.model.tool.abs.CompletionTool tool = toolsByName.get(name);
        return tool == null ? Optional.empty() : Optional.of(tool.deepClone());
    }

    public List<com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> getTools(String... names) {
        if (names == null) {
            return List.of();
        }
        List<com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> selectedTools = new ArrayList<>(names.length);
        for (String name : names) {
            selectedTools.add(getRequiredTool(name).deepClone());
        }
        return selectedTools;
    }

    public CompletionRequest addToolsTo(CompletionRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        getTools().forEach(request::addTool);
        return request;
    }

    public CompletionRequest addToolsTo(CompletionRequest request, String... names) {
        Objects.requireNonNull(request, "request must not be null");
        getTools(names).forEach(request::addTool);
        return request;
    }

    private List<com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> discoverTools(Object bean) {
        Class<?> targetClass = ClassUtils.getUserClass(bean);
        Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(targetClass);
        List<Method> toolMethods = new ArrayList<>();
        for (Method method : methods) {
            if (AnnotatedElementUtils.hasAnnotation(method, CompletionTool.class)) {
                toolMethods.add(method);
            }
        }
        toolMethods.sort(Comparator.comparing(Method::getName));
        return toolMethods.stream()
                .map(schemaGenerator::generateTool)
                .map(tool -> (com.anordine.completions.api.webflux.model.tool.abs.CompletionTool) tool)
                .toList();
    }

    private Map<String, com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> indexToolsByName(
            List<com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> tools
    ) {
        Map<String, com.anordine.completions.api.webflux.model.tool.abs.CompletionTool> indexedTools = new LinkedHashMap<>();
        for (com.anordine.completions.api.webflux.model.tool.abs.CompletionTool tool : tools) {
            String name = toolName(tool);
            com.anordine.completions.api.webflux.model.tool.abs.CompletionTool existing = indexedTools.putIfAbsent(name, tool);
            if (existing != null) {
                throw new IllegalStateException("Duplicate @CompletionTool name: " + name);
            }
        }
        return Collections.unmodifiableMap(indexedTools);
    }

    private com.anordine.completions.api.webflux.model.tool.abs.CompletionTool getRequiredTool(String name) {
        Objects.requireNonNull(name, "name must not be null");
        com.anordine.completions.api.webflux.model.tool.abs.CompletionTool tool = toolsByName.get(name);
        if (tool == null) {
            throw new IllegalArgumentException("Unknown @CompletionTool name: " + name);
        }
        return tool;
    }

    private String toolName(com.anordine.completions.api.webflux.model.tool.abs.CompletionTool tool) {
        if (tool instanceof CompletionFunctionTool functionTool
                && functionTool.getFunction() != null
                && functionTool.getFunction().getName() != null
                && !functionTool.getFunction().getName().isBlank()) {
            return functionTool.getFunction().getName();
        }
        throw new IllegalStateException("@CompletionTool registry only supports named function tools");
    }
}
