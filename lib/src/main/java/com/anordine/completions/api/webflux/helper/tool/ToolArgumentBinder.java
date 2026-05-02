package com.anordine.completions.api.webflux.helper.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@FunctionalInterface
interface ToolArgumentBinder<I> {

    I bind(Object arguments, ObjectMapper objectMapper);

    static ToolArgumentBinder<Void> noArguments() {
        return (arguments, objectMapper) -> null;
    }

    static ToolArgumentBinder<Object> singleValue(Class<?> type) {
        return (arguments, objectMapper) -> objectMapper.convertValue(toJsonNode(arguments, objectMapper), type);
    }

    static ToolArgumentBinder<CompletionToolRegistry.BoundArguments> boundArguments(
            CompletionToolRegistry.ToolParameter[] parameters
    ) {
        return (arguments, objectMapper) -> {
            JsonNode node = toJsonNode(arguments, objectMapper);
            if (!node.isObject()) {
                throw new IllegalArgumentException("@CompletionTool arguments must be a JSON object");
            }

            Object[] values = new Object[parameters.length];
            for (int index = 0; index < parameters.length; index++) {
                CompletionToolRegistry.ToolParameter parameter = parameters[index];
                JsonNode value = node.get(parameter.name());
                values[index] = value == null || value.isMissingNode()
                        ? null
                        : objectMapper.convertValue(value, parameter.type());
            }
            return new CompletionToolRegistry.BoundArguments(values);
        };
    }

    private static JsonNode toJsonNode(Object arguments, ObjectMapper objectMapper) {
        if (arguments == null) {
            return objectMapper.createObjectNode();
        }
        if (arguments instanceof JsonNode jsonNode) {
            return jsonNode;
        }
        if (arguments instanceof String json) {
            try {
                return objectMapper.readTree(json.isBlank() ? "{}" : json);
            } catch (JsonProcessingException exception) {
                throw new IllegalArgumentException("@CompletionTool arguments must be valid JSON", exception);
            }
        }
        return objectMapper.valueToTree(arguments);
    }
}
