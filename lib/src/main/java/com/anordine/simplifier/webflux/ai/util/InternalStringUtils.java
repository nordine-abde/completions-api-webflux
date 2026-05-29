package com.anordine.simplifier.webflux.ai.util;

public class InternalStringUtils {

    private InternalStringUtils() {

    }

    public static String toCamelCase(String value) {
        StringBuilder camelCaseValue = new StringBuilder();
        boolean uppercaseNext = false;

        for (char current : value.toCharArray()) {
            if (!Character.isLetterOrDigit(current)) {
                uppercaseNext = !camelCaseValue.isEmpty();
            } else {
                if (camelCaseValue.isEmpty()) {
                    camelCaseValue.append(Character.toLowerCase(current));
                    uppercaseNext = false;
                } else if (uppercaseNext) {
                    camelCaseValue.append(Character.toUpperCase(current));
                    uppercaseNext = false;
                } else {
                    camelCaseValue.append(current);
                }
            }
        }
        return camelCaseValue.toString();
    }
}
