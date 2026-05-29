package com.anordine.simplifier.webflux.ai.helper.tool;

import com.anordine.simplifier.webflux.ai.model.tool.CompletionFunctionTool;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompletionToolSchemaGeneratorTest {

    private final CompletionToolSchemaGenerator generator = new CompletionToolSchemaGenerator();

    @Test
    void generatesStrictFunctionToolFromSingleDtoParameter() throws Exception {
        CompletionFunctionTool tool = generator.generateTool(method("saveProfile", UserProfile.class));

        assertEquals("save_profile", tool.getFunction().getName());
        assertEquals("Save a user profile", tool.getFunction().getDescription());
        assertEquals(Boolean.TRUE, tool.getFunction().getStrict());

        Map<String, Object> parameters = tool.getFunction().getParameters();
        assertEquals("object", parameters.get("type"));
        assertEquals(Boolean.FALSE, parameters.get("additionalProperties"));

        List<?> required = assertInstanceOf(List.class, parameters.get("required"));
        assertTrue(required.containsAll(List.of("name", "age", "email", "roles")));

        Map<?, ?> properties = assertInstanceOf(Map.class, parameters.get("properties"));
        Map<?, ?> email = assertInstanceOf(Map.class, properties.get("email"));
        assertIterableEquals(List.of("string", "null"), assertInstanceOf(List.class, email.get("type")));
    }

    @Test
    void honorsStrictFalseAnnotation() throws Exception {
        CompletionFunctionTool tool = generator.generateTool(method("findProfile", String.class, boolean.class));

        assertEquals("findProfile", tool.getFunction().getName());
        assertEquals(Boolean.FALSE, tool.getFunction().getStrict());

        Map<String, Object> parameters = tool.getFunction().getParameters();
        List<?> required = assertInstanceOf(List.class, parameters.get("required"));
        assertEquals(List.of("user_id", "include_roles"), required);
        assertEquals(Boolean.FALSE, parameters.get("additionalProperties"));
    }

    @Test
    void generatesEmptyObjectSchemaForNoArgumentTools() throws Exception {
        CompletionFunctionTool tool = generator.generateTool(method("ping"));

        Map<String, Object> parameters = tool.getFunction().getParameters();
        assertEquals("object", parameters.get("type"));
        assertEquals(Boolean.FALSE, parameters.get("additionalProperties"));
        assertNotNull(parameters.get("properties"));
        assertEquals(List.of(), parameters.get("required"));
    }

    private Method method(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        return ToolProvider.class.getDeclaredMethod(name, parameterTypes);
    }

    @CompletionProvider
    static class ToolProvider {

        @CompletionTool(name = "save_profile", description = "Save a user profile")
        public String saveProfile(UserProfile profile) {
            return "saved";
        }

        @CompletionTool(strict = false)
        public String findProfile(@JsonProperty(value = "user_id", required = true) String userId,
                                  @JsonProperty("include_roles") boolean includeRoles) {
            return "found";
        }

        @CompletionTool
        public String ping() {
            return "pong";
        }
    }

    static class UserProfile {

        @JsonProperty(required = true)
        private String name;

        @JsonProperty(required = true)
        private int age;

        private String email;

        private List<String> roles;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
