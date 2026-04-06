package com.anordine.completions.api.webflux;

import com.anordine.completions.api.webflux.model.CompletionResponse;
import com.anordine.completions.api.webflux.model.enums.finish.CompletionFinishReason;
import com.anordine.completions.api.webflux.model.message.CompletionAssistantMessage;
import com.anordine.completions.api.webflux.model.tool.CompletionMessageFunctionToolCall;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CompletionResponseExamplesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesDefaultExample() throws Exception {
        CompletionResponse response = readExample("examples/response/default.json");

        assertEquals("chatcmpl-B9MBs8CjcvOU2jLn4n570S5qMJKcT", response.getId());
        assertEquals("chat.completion", response.getObject());
        assertEquals(1741569952L, response.getCreated());
        assertEquals("gpt-5.4", response.getModel());
        assertEquals("default", response.getServiceTier());
        assertNotNull(response.getChoices());
        assertEquals(1, response.getChoices().size());
        assertEquals(0, response.getChoices().get(0).getIndex());
        assertEquals(CompletionFinishReason.STOP, response.getChoices().get(0).getFinishReason());

        CompletionAssistantMessage message = response.getChoices().get(0).getMessage();
        assertEquals("Hello! How can I assist you today?", message.getContent());

        assertNotNull(response.getUsage());
        assertEquals(19, response.getUsage().getPromptTokens());
        assertEquals(10, response.getUsage().getCompletionTokens());
        assertEquals(29, response.getUsage().getTotalTokens());
        assertEquals(0, response.getUsage().getPromptTokensDetails().getCachedTokens());
        assertEquals(0, response.getUsage().getCompletionTokensDetails().getAcceptedPredictionTokens());
    }

    @Test
    void deserializesFunctionsExample() throws Exception {
        CompletionResponse response = readExample("examples/response/functions.json");

        assertEquals("chatcmpl-abc123", response.getId());
        assertEquals("gpt-4o-mini", response.getModel());
        assertNotNull(response.getChoices());
        assertEquals(1, response.getChoices().size());
        assertEquals(CompletionFinishReason.TOOL_CALLS, response.getChoices().get(0).getFinishReason());

        CompletionAssistantMessage message = response.getChoices().get(0).getMessage();
        assertNull(message.getContent());
        assertNotNull(message.getToolCalls());
        assertEquals(1, message.getToolCalls().size());

        CompletionMessageFunctionToolCall toolCall =
                assertInstanceOf(CompletionMessageFunctionToolCall.class, message.getToolCalls().get(0));
        assertEquals("call_abc123", toolCall.getId());
        assertEquals("get_current_weather", toolCall.getFunction().getName());
        assertEquals("{\n\"location\": \"Boston, MA\"\n}", toolCall.getFunction().getArguments());

        assertNotNull(response.getUsage());
        assertEquals(82, response.getUsage().getPromptTokens());
        assertEquals(17, response.getUsage().getCompletionTokens());
        assertEquals(99, response.getUsage().getTotalTokens());
        assertEquals(0, response.getUsage().getCompletionTokensDetails().getReasoningTokens());
    }

    private CompletionResponse readExample(String resourcePath) throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            assertNotNull(inputStream, "Missing test resource: " + resourcePath);
            return objectMapper.readValue(inputStream, CompletionResponse.class);
        }
    }
}
