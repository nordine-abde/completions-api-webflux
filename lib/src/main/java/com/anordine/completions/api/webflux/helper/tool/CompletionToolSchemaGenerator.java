package com.anordine.completions.api.webflux.helper.tool;

import com.anordine.completions.api.webflux.model.tool.CompletionFunctionDefinition;
import com.anordine.completions.api.webflux.model.tool.CompletionFunctionTool;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class CompletionToolSchemaGenerator {

    private static final TypeReference<Map<String, Object>> STRING_OBJECT_MAP = new TypeReference<>() {
    };
    private static final Pattern OPEN_AI_FUNCTION_NAME = Pattern.compile("^[A-Za-z0-9_-]{1,64}$");

    private final ObjectMapper objectMapper;
    private final SchemaGenerator schemaGenerator;

    public CompletionToolSchemaGenerator() {
        this(new ObjectMapper());
    }

    CompletionToolSchemaGenerator(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        SchemaGeneratorConfig config = new SchemaGeneratorConfigBuilder(
                SchemaVersion.DRAFT_2020_12,
                OptionPreset.PLAIN_JSON
        )
                .with(new JacksonModule())
                .with(Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT)
                .build();
        this.schemaGenerator = new SchemaGenerator(config);
    }

    public CompletionFunctionTool generateTool(Method method) {
        Objects.requireNonNull(method, "method must not be null");
        CompletionTool annotation = AnnotatedElementUtils.findMergedAnnotation(method, CompletionTool.class);
        if (annotation == null) {
            throw new IllegalArgumentException("method must be annotated with @CompletionTool");
        }
        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("@CompletionTool method must not be static: " + method);
        }

        ObjectNode parameters = generateParametersSchema(method);
        if (annotation.strict()) {
            applyOpenAiStrictSchemaRules(parameters);
        }

        CompletionFunctionDefinition function = new CompletionFunctionDefinition();
        function.setName(resolveName(annotation, method));
        if (!annotation.description().isBlank()) {
            function.setDescription(annotation.description());
        }
        function.setStrict(annotation.strict());
        function.setParameters(objectMapper.convertValue(parameters, STRING_OBJECT_MAP));
        return new CompletionFunctionTool(function);
    }

    private ObjectNode generateParametersSchema(Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return emptyObjectSchema();
        }
        if (parameters.length == 1 && !isSimpleType(parameters[0].getType())) {
            ObjectNode schema = schemaGenerator.generateSchema(parameters[0].getType());
            schema.remove("$schema");
            return schema;
        }

        ObjectNode schema = emptyObjectSchema();
        ObjectNode properties = objectMapper.createObjectNode();
        ArrayNode required = objectMapper.createArrayNode();

        for (Parameter parameter : parameters) {
            String name = resolveParameterName(parameter);
            ObjectNode parameterSchema = schemaGenerator.generateSchema(parameter.getType());
            parameterSchema.remove("$schema");
            properties.set(name, parameterSchema);
            if (isRequired(parameter)) {
                required.add(name);
            }
        }

        schema.set("properties", properties);
        schema.set("required", required);
        return schema;
    }

    private ObjectNode emptyObjectSchema() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        schema.set("properties", objectMapper.createObjectNode());
        schema.set("required", objectMapper.createArrayNode());
        schema.put("additionalProperties", false);
        return schema;
    }

    private void applyOpenAiStrictSchemaRules(ObjectNode schema) {
        if (schema.has("properties") && schema.get("properties").isObject()) {
            ObjectNode properties = (ObjectNode) schema.get("properties");
            Set<String> existingRequired = requiredPropertyNames(schema);
            ArrayNode required = objectMapper.createArrayNode();

            for (Iterator<Entry<String, com.fasterxml.jackson.databind.JsonNode>> iterator = properties.fields(); iterator.hasNext(); ) {
                Entry<String, com.fasterxml.jackson.databind.JsonNode> entry = iterator.next();
                String propertyName = entry.getKey();
                required.add(propertyName);
                if (!existingRequired.contains(propertyName) && entry.getValue().isObject()) {
                    allowNullType((ObjectNode) entry.getValue());
                }
            }
            schema.set("required", required);
        }

        if (isObjectSchema(schema)) {
            schema.put("additionalProperties", false);
        }

        for (Iterator<Entry<String, com.fasterxml.jackson.databind.JsonNode>> iterator = schema.fields(); iterator.hasNext(); ) {
            Entry<String, com.fasterxml.jackson.databind.JsonNode> entry = iterator.next();
            if (entry.getValue().isObject()) {
                applyOpenAiStrictSchemaRules((ObjectNode) entry.getValue());
            } else if (entry.getValue().isArray()) {
                entry.getValue().forEach(item -> {
                    if (item.isObject()) {
                        applyOpenAiStrictSchemaRules((ObjectNode) item);
                    }
                });
            }
        }
    }

    private Set<String> requiredPropertyNames(ObjectNode schema) {
        Set<String> required = new LinkedHashSet<>();
        if (schema.has("required") && schema.get("required").isArray()) {
            schema.get("required").forEach(item -> {
                if (item.isTextual()) {
                    required.add(item.asText());
                }
            });
        }
        return required;
    }

    private void allowNullType(ObjectNode schema) {
        if (!schema.has("type")) {
            return;
        }
        if (schema.get("type").isTextual()) {
            String type = schema.get("type").asText();
            if (!"null".equals(type)) {
                ArrayNode nullableType = objectMapper.createArrayNode();
                nullableType.add(type);
                nullableType.add("null");
                schema.set("type", nullableType);
            }
            return;
        }
        if (schema.get("type").isArray()) {
            boolean hasNull = false;
            for (int index = 0; index < schema.get("type").size(); index++) {
                if ("null".equals(schema.get("type").get(index).asText())) {
                    hasNull = true;
                    break;
                }
            }
            if (!hasNull) {
                ((ArrayNode) schema.get("type")).add("null");
            }
        }
    }

    private boolean isObjectSchema(ObjectNode schema) {
        if (!schema.has("type")) {
            return schema.has("properties");
        }
        if (schema.get("type").isTextual()) {
            return "object".equals(schema.get("type").asText());
        }
        if (schema.get("type").isArray()) {
            for (int index = 0; index < schema.get("type").size(); index++) {
                if ("object".equals(schema.get("type").get(index).asText())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String resolveName(CompletionTool annotation, Method method) {
        String name = annotation.name().isBlank() ? method.getName() : annotation.name();
        if (!OPEN_AI_FUNCTION_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException(
                    "@CompletionTool name must contain only letters, digits, underscores, or dashes and be at most 64 characters: "
                            + method
            );
        }
        return name;
    }

    private String resolveParameterName(Parameter parameter) {
        JsonProperty jsonProperty = parameter.getAnnotation(JsonProperty.class);
        if (jsonProperty != null && !jsonProperty.value().isBlank()) {
            return jsonProperty.value();
        }
        if (parameter.isNamePresent()) {
            return parameter.getName();
        }
        throw new IllegalArgumentException(
                "@CompletionTool method parameters must be compiled with -parameters or annotated with @JsonProperty"
        );
    }

    private boolean isRequired(Parameter parameter) {
        JsonProperty jsonProperty = parameter.getAnnotation(JsonProperty.class);
        return parameter.getType().isPrimitive() || (jsonProperty != null && jsonProperty.required());
    }

    private boolean isSimpleType(Class<?> type) {
        return ClassUtils.isPrimitiveOrWrapper(type)
                || CharSequence.class.isAssignableFrom(type)
                || Number.class.isAssignableFrom(type)
                || Boolean.class == type
                || Character.class == type
                || type.isEnum();
    }
}
