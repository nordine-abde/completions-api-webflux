package com.anordine.completions.api.webflux;

import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.message.CompletionDeveloperMessage;
import com.anordine.completions.api.webflux.model.message.CompletionUserMessage;
import com.anordine.completions.api.webflux.model.tool.CompletionFunctionTool;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompletionRequestExamplesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesDefaultExample() throws Exception {
        CompletionRequest request = readExample("examples/request/default.json");

        assertEquals("VAR_chat_model_id", request.getModel());
        assertNotNull(request.getMessages());
        assertEquals(2, request.getMessages().size());
        assertInstanceOf(CompletionDeveloperMessage.class, request.getMessages().get(0));
        assertInstanceOf(CompletionUserMessage.class, request.getMessages().get(1));
    }

    @Test
    void deserializesFunctionsExample() throws Exception {
        CompletionRequest request = readExample("examples/request/functions.json");

        assertEquals("gpt-5.4", request.getModel());
        assertEquals("auto", request.getToolChoice());
        assertNotNull(request.getTools());
        assertEquals(1, request.getTools().size());
        CompletionFunctionTool tool = assertInstanceOf(CompletionFunctionTool.class, request.getTools().get(0));
        assertEquals("get_current_weather", tool.getFunction().getName());
        assertEquals("Get the current weather in a given location", tool.getFunction().getDescription());
        assertEquals("object", tool.getFunction().getParameters().get("type"));
        assertTrue(tool.getFunction().getParameters().containsKey("properties"));
        assertEquals(1, ((java.util.List<?>) tool.getFunction().getParameters().get("required")).size());
    }


    private CompletionRequest readExample(String resourcePath) throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            assertNotNull(inputStream, "Missing test resource: " + resourcePath);
            return objectMapper.readValue(inputStream, CompletionRequest.class);
        }
    }
}
