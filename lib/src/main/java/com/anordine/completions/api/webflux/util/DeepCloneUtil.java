package com.anordine.completions.api.webflux.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DeepCloneUtil {

    private DeepCloneUtil() {
    }

    public static <T> T deepClone(T value) {
        return value == null ? null : deepCloneNonNull(value);
    }

    @SuppressWarnings("unchecked")
    private static <T> T deepCloneNonNull(T value) {
        if (value instanceof Map<?, ?> map) {
            return (T) deepCloneMap(map);
        }
        if (value instanceof List<?> list) {
            return (T) deepCloneList(list);
        }
        if (value instanceof DeepClonable<?> deepClonable) {
            return (T) deepClonable.deepClone();
        }
        return value;
    }

    public static <T> List<T> deepCloneList(List<T> values) {
        if (values == null) {
            return null;
        }
        List<T> clone = new ArrayList<>(values.size());
        for (T value : values) {
            clone.add(deepClone(value));
        }
        return clone;
    }

    public static Map<String, Object> deepCloneStringObjectMap(Map<String, Object> values) {
        if (values == null) {
            return null;
        }
        Map<String, Object> clone = LinkedHashMap.newLinkedHashMap(values.size());
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            clone.put(entry.getKey(), deepClone(entry.getValue()));
        }
        return clone;
    }

    private static Map<Object, Object> deepCloneMap(Map<?, ?> values) {
        Map<Object, Object> clone = LinkedHashMap.newLinkedHashMap(values.size());
        for (Map.Entry<?, ?> entry : values.entrySet()) {
            clone.put(entry.getKey(), deepClone(entry.getValue()));
        }
        return clone;
    }
}
