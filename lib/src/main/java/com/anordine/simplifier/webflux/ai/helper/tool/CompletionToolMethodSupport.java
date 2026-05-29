package com.anordine.simplifier.webflux.ai.helper.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.regex.Pattern;

final class CompletionToolMethodSupport {

    private static final Pattern OPEN_AI_FUNCTION_NAME = Pattern.compile("^[A-Za-z0-9_-]{1,64}$");

    private CompletionToolMethodSupport() {
    }

    static void validateToolMethod(Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("@CompletionTool method must not be static: " + method);
        }
    }

    static String resolveToolName(CompletionTool annotation, Method method) {
        String name = annotation.name().isBlank() ? method.getName() : annotation.name();
        if (!OPEN_AI_FUNCTION_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException(
                    "@CompletionTool name must contain only letters, digits, underscores, or dashes and be at most 64 characters: "
                            + method
            );
        }
        return name;
    }

    static String resolveParameterName(Parameter parameter) {
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

    static boolean isRequired(Parameter parameter) {
        JsonProperty jsonProperty = parameter.getAnnotation(JsonProperty.class);
        return parameter.getType().isPrimitive() || (jsonProperty != null && jsonProperty.required());
    }

    static boolean isSimpleType(Class<?> type) {
        return ClassUtils.isPrimitiveOrWrapper(type)
                || CharSequence.class.isAssignableFrom(type)
                || Number.class.isAssignableFrom(type)
                || Boolean.class == type
                || Character.class == type
                || type.isEnum();
    }
}
