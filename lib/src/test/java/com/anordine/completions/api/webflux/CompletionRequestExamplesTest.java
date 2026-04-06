package com.anordine.completions.api.webflux;

import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.enums.prompt.CompletionPromptCacheRetention;
import com.anordine.completions.api.webflux.model.enums.toolchoice.CompletionAllowedToolsMode;
import com.anordine.completions.api.webflux.model.enums.toolchoice.CompletionToolChoiceMode;
import com.anordine.completions.api.webflux.model.message.CompletionDeveloperMessage;
import com.anordine.completions.api.webflux.model.message.CompletionFunctionMessage;
import com.anordine.completions.api.webflux.model.message.CompletionToolMessage;
import com.anordine.completions.api.webflux.model.message.CompletionUserMessage;
import com.anordine.completions.api.webflux.model.tool.CompletionFunctionTool;
import com.anordine.completions.api.webflux.model.toolchoice.CompletionNamedCustomToolChoice;
import com.anordine.completions.api.webflux.model.toolchoice.CompletionNamedFunctionToolChoice;
import com.anordine.completions.api.webflux.model.toolchoice.CompletionToolChoiceAllowedTools;
import com.anordine.completions.api.webflux.model.toolchoice.CompletionToolChoiceName;
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
        CompletionToolChoiceMode toolChoice = assertInstanceOf(CompletionToolChoiceMode.class, request.getToolChoice());
        assertEquals(CompletionToolChoiceMode.AUTO, toolChoice);
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
    void deserializesAdditionalRequestFieldsAndNamedFunctionToolChoice() throws Exception {
        CompletionRequest request = objectMapper.readValue("""
                {
                  "model": "gpt-5.4",
                  "messages": [
                    {
                      "role": "user",
                      "content": "Hello"
                    }
                  ],
                  "logprobs": true,
                  "top_logprobs": 3,
                  "prompt_cache_retention": "24h",
                  "tool_choice": {
                    "type": "function",
                    "function": {
                      "name": "get_current_weather"
                    }
                  }
                }
                """, CompletionRequest.class);

        assertEquals(Boolean.TRUE, request.getLogprobs());
        assertEquals(3, request.getTopLogprobs());
        assertEquals(CompletionPromptCacheRetention.TWENTY_FOUR_HOURS, request.getPromptCacheRetention());
        CompletionNamedFunctionToolChoice toolChoice =
                assertInstanceOf(CompletionNamedFunctionToolChoice.class, request.getToolChoice());
        assertEquals("get_current_weather", toolChoice.getFunction().getName());
    }

    @Test
    void deserializesAllowedToolsToolChoiceVariant() throws Exception {
        CompletionRequest request = objectMapper.readValue("""
                {
                  "model": "gpt-5.4",
                  "messages": [
                    {
                      "role": "user",
                      "content": "Hello"
                    }
                  ],
                  "tool_choice": {
                    "type": "allowed_tools",
                    "allowed_tools": {
                      "mode": "required",
                      "tools": [
                        {
                          "type": "function",
                          "function": {
                            "name": "get_current_weather"
                          }
                        }
                      ]
                    }
                  }
                }
                """, CompletionRequest.class);

        CompletionToolChoiceAllowedTools toolChoice =
                assertInstanceOf(CompletionToolChoiceAllowedTools.class, request.getToolChoice());
        assertEquals(CompletionAllowedToolsMode.REQUIRED, toolChoice.getAllowedTools().getMode());
        assertEquals(1, toolChoice.getAllowedTools().getTools().size());
        CompletionFunctionTool tool =
                assertInstanceOf(CompletionFunctionTool.class, toolChoice.getAllowedTools().getTools().get(0));
        assertEquals("get_current_weather", tool.getFunction().getName());
    }

    @Test
    void deserializesNamedCustomToolChoiceVariant() throws Exception {
        CompletionRequest request = objectMapper.readValue("""
                {
                  "model": "gpt-5.4",
                  "messages": [
                    {
                      "role": "user",
                      "content": "Hello"
                    }
                  ],
                  "tool_choice": {
                    "type": "custom",
                    "custom": {
                      "name": "sql_runner"
                    }
                  }
                }
                """, CompletionRequest.class);

        CompletionNamedCustomToolChoice toolChoice =
                assertInstanceOf(CompletionNamedCustomToolChoice.class, request.getToolChoice());
        assertEquals("sql_runner", toolChoice.getCustom().getName());
    }

    @Test
    void serializesStringModeToolChoiceUsingEnumWireValue() throws Exception {
        CompletionRequest request = new CompletionRequest();
        request.setToolChoice(CompletionToolChoiceMode.REQUIRED);

        String json = objectMapper.writeValueAsString(request);

        assertTrue(json.contains("\"tool_choice\":\"required\""));
    }

    @Test
    void serializesObjectToolChoiceUsingSubtypeFields() throws Exception {
        CompletionRequest request = new CompletionRequest();
        request.setToolChoice(new CompletionNamedFunctionToolChoice(new CompletionToolChoiceName("get_current_weather")));

        String json = objectMapper.writeValueAsString(request);

        assertTrue(json.contains("\"tool_choice\":{"));
        assertTrue(json.contains("\"type\":\"function\""));
        assertTrue(json.contains("\"function\":{\"name\":\"get_current_weather\"}"));
    }

    @Test
    void mapsDocumentedRequiredRequestMessageFieldsWithoutValidation() throws Exception {
        CompletionRequest request = objectMapper.readValue("""
                {
                  "model": "gpt-5.4",
                  "messages": [
                    {
                      "role": "function",
                      "name": "lookup_vehicle",
                      "content": "lookup result"
                    },
                    {
                      "role": "tool",
                      "tool_call_id": "call_123",
                      "content": "tool result"
                    }
                  ]
                }
                """, CompletionRequest.class);

        CompletionFunctionMessage functionMessage =
                assertInstanceOf(CompletionFunctionMessage.class, request.getMessages().get(0));
        assertEquals("lookup_vehicle", functionMessage.getName());
        CompletionToolMessage toolMessage =
                assertInstanceOf(CompletionToolMessage.class, request.getMessages().get(1));
        assertEquals("call_123", toolMessage.getToolCallId());
    }


    private CompletionRequest readExample(String resourcePath) throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            assertNotNull(inputStream, "Missing test resource: " + resourcePath);
            return objectMapper.readValue(inputStream, CompletionRequest.class);
        }
    }
}
