package com.anordine.completions.api.webflux;

import com.anordine.completions.api.webflux.message.CompletionDeveloperMessage;
import com.anordine.completions.api.webflux.message.CompletionUserMessage;
import com.anordine.completions.api.webflux.tool.CompletionFunctionTool;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompletionsRequestExamplesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesDefaultExample() throws Exception {
        CompletionsRequest request = readExample("examples/default.json");

        assertEquals("VAR_chat_model_id", request.getModel());
        assertNotNull(request.getMessages());
        assertEquals(2, request.getMessages().size());
        assertInstanceOf(CompletionDeveloperMessage.class, request.getMessages().get(0));
        assertInstanceOf(CompletionUserMessage.class, request.getMessages().get(1));
    }

    @Test
    void deserializesFunctionsExample() throws Exception {
        CompletionsRequest request = readExample("examples/functions.json");

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

    @Test
    void deserializesLogprobsExample() throws Exception {
        CompletionsRequest request = readExample("examples/logprobs.json");

        assertEquals("VAR_chat_model_id", request.getModel());
        assertEquals(Boolean.TRUE, request.getLogprobs());
        assertEquals(2, request.getTopLogprobs());
        assertNotNull(request.getMessages());
        assertEquals(1, request.getMessages().size());
        assertInstanceOf(CompletionUserMessage.class, request.getMessages().get(0));
    }

    private CompletionsRequest readExample(String resourcePath) throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            assertNotNull(inputStream, "Missing test resource: " + resourcePath);
            return objectMapper.readValue(inputStream, CompletionsRequest.class);
        }
    }
}
