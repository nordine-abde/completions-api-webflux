package com.anordine.simplifier.webflux.ai.helper.tool;

import com.anordine.simplifier.webflux.ai.model.CompletionRequest;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionFunctionTool;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class CompletionToolRegistry implements SmartInitializingSingleton {

    private final ApplicationContext applicationContext;
    private final CompletionToolSchemaGenerator schemaGenerator;
    private List<com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool> tools = List.of();
    private Map<String, com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool> toolsByName = Map.of();
    private Map<String, RegisteredTool<?, ?>> executableToolsByName = Map.of();

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
        List<com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool> discoveredTools = new ArrayList<>();
        List<RegisteredTool<?, ?>> discoveredExecutableTools = new ArrayList<>();

        providers.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> discoverTools(entry.getValue()).forEach(discoveredTool -> {
                    discoveredTools.add(discoveredTool.tool());
                    discoveredExecutableTools.add(discoveredTool.executableTool());
                }));

        this.tools = List.copyOf(discoveredTools);
        this.toolsByName = indexToolsByName(discoveredTools);
        this.executableToolsByName = indexExecutableToolsByName(discoveredExecutableTools);
    }

    public List<com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool> getTools() {
        return tools.stream()
                .map(tool -> tool == null ? null : tool.deepClone())
                .toList();
    }

    public Map<String, com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool> getToolsByName() {
        Map<String, com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool> clonedTools = new LinkedHashMap<>();
        toolsByName.forEach((name, tool) -> clonedTools.put(name, tool.deepClone()));
        return Collections.unmodifiableMap(clonedTools);
    }

    public Optional<com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool> getTool(String name) {
        Objects.requireNonNull(name, "name must not be null");
        com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool tool = toolsByName.get(name);
        return tool == null ? Optional.empty() : Optional.of(tool.deepClone());
    }

    public List<com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool> getTools(String... names) {
        if (names == null) {
            return List.of();
        }
        List<com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool> selectedTools = new ArrayList<>(names.length);
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

    private List<DiscoveredTool> discoverTools(Object bean) {
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
                .map(method -> discoverTool(bean, method))
                .toList();
    }

    private DiscoveredTool discoverTool(Object bean, Method method) {
        com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool tool = schemaGenerator.generateTool(method);
        return new DiscoveredTool(tool, createExecutableTool(bean, method, toolName(tool)));
    }

    private RegisteredTool<?, ?> createExecutableTool(Object bean, Method method, String name) {
        CompletionToolMethodSupport.validateToolMethod(method);
        ReflectionUtils.makeAccessible(method);

        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            Function<Void, Object> function = ignored -> invokeMethod(name, bean, method);
            return new RegisteredTool<>(name, ToolArgumentBinder.noArguments(), function);
        }
        if (parameters.length == 1 && !CompletionToolMethodSupport.isSimpleType(parameters[0].getType())) {
            Class<?> parameterType = parameters[0].getType();
            Function<Object, Object> function = input -> invokeMethod(name, bean, method, input);
            return new RegisteredTool<>(name, ToolArgumentBinder.singleValue(parameterType), function);
        }

        ToolParameter[] toolParameters = new ToolParameter[parameters.length];
        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            toolParameters[index] = new ToolParameter(
                    CompletionToolMethodSupport.resolveParameterName(parameter),
                    parameter.getType()
            );
        }
        Function<BoundArguments, Object> function = boundArguments -> invokeMethod(name, bean, method, boundArguments.values());
        return new RegisteredTool<>(name, ToolArgumentBinder.boundArguments(toolParameters), function);
    }

    private Map<String, com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool> indexToolsByName(
            List<com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool> tools
    ) {
        Map<String, com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool> indexedTools = new LinkedHashMap<>();
        for (com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool tool : tools) {
            String name = toolName(tool);
            com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool existing = indexedTools.putIfAbsent(name, tool);
            if (existing != null) {
                throw new IllegalStateException("Duplicate @CompletionTool name: " + name);
            }
        }
        return Collections.unmodifiableMap(indexedTools);
    }

    private Map<String, RegisteredTool<?, ?>> indexExecutableToolsByName(List<RegisteredTool<?, ?>> tools) {
        Map<String, RegisteredTool<?, ?>> indexedTools = new LinkedHashMap<>();
        for (RegisteredTool<?, ?> tool : tools) {
            RegisteredTool<?, ?> existing = indexedTools.putIfAbsent(tool.name(), tool);
            if (existing != null) {
                throw new IllegalStateException("Duplicate @CompletionTool name: " + tool.name());
            }
        }
        return Collections.unmodifiableMap(indexedTools);
    }

    private com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool getRequiredTool(String name) {
        Objects.requireNonNull(name, "name must not be null");
        com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool tool = toolsByName.get(name);
        if (tool == null) {
            throw new IllegalArgumentException("Unknown @CompletionTool name: " + name);
        }
        return tool;
    }

    private String toolName(com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool tool) {
        if (tool instanceof CompletionFunctionTool functionTool
                && functionTool.getFunction() != null
                && functionTool.getFunction().getName() != null
                && !functionTool.getFunction().getName().isBlank()) {
            return functionTool.getFunction().getName();
        }
        throw new IllegalStateException("@CompletionTool registry only supports named function tools");
    }

    RegisteredTool<?, ?> getRequiredExecutableTool(String name) {
        Objects.requireNonNull(name, "name must not be null");
        RegisteredTool<?, ?> tool = executableToolsByName.get(name);
        if (tool == null) {
            throw new IllegalArgumentException("Unknown @CompletionTool name: " + name);
        }
        return tool;
    }

    private Object invokeMethod(String name, Object bean, Method method, Object... arguments) {
        try {
            return method.invoke(bean, arguments);
        } catch (ReflectiveOperationException exception) {
            Throwable cause = exception instanceof java.lang.reflect.InvocationTargetException invocationTargetException
                    ? invocationTargetException.getTargetException()
                    : exception;
            throw new ToolExecutionException("Tool execution failed: " + name, cause);
        }
    }

    private record DiscoveredTool(
            com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool tool,
            RegisteredTool<?, ?> executableTool
    ) {
    }

    record RegisteredTool<I, O>(
            String name,
            ToolArgumentBinder<I> argumentBinder,
            Function<I, O> function
    ) {
    }

    record BoundArguments(Object[] values) {
    }

    record ToolParameter(String name, Class<?> type) {
    }
}
