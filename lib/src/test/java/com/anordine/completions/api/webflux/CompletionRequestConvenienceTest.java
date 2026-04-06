package com.anordine.completions.api.webflux;

import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.message.CompletionDeveloperMessage;
import com.anordine.completions.api.webflux.model.message.CompletionFunctionMessage;
import com.anordine.completions.api.webflux.model.message.CompletionToolMessage;
import com.anordine.completions.api.webflux.model.message.CompletionUserMessage;
import com.anordine.completions.api.webflux.model.tool.CompletionFunctionTool;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompletionRequestConvenienceTest {

    @Test
    void createsRequestFromModelAndSingleMessage() {
        CompletionRequest request = CompletionRequest.create("gpt-5.4", "Hello there");

        assertEquals("gpt-5.4", request.getModel());
        assertNotNull(request.getMessages());
        assertEquals(1, request.getMessages().size());
        CompletionUserMessage userMessage =
                assertInstanceOf(CompletionUserMessage.class, request.getMessages().get(0));
        assertEquals("Hello there", userMessage.getContent());
    }

    @Test
    void supportsFluentMessageAndToolHelpers() {
        CompletionRequest request = new CompletionRequest()
                .withModel("gpt-5.4")
                .withTemperature(0.2)
                .withStore(true)
                .addDeveloperMessage("You are terse")
                .addUserMessage("Need the weather", "alice")
                .addFunctionMessage("lookup_weather", "{\"temperature\": 21}")
                .addToolMessage("call_123", "{\"temperature\": 21}")
                .addFunctionTool(
                        "get_current_weather",
                        "Get the current weather in a given location",
                        Map.of("type", "object")
                );

        assertEquals("gpt-5.4", request.getModel());
        assertEquals(0.2, request.getTemperature());
        assertEquals(Boolean.TRUE, request.getStore());
        assertNotNull(request.getMessages());
        assertEquals(4, request.getMessages().size());
        assertInstanceOf(CompletionDeveloperMessage.class, request.getMessages().get(0));

        CompletionUserMessage userMessage =
                assertInstanceOf(CompletionUserMessage.class, request.getMessages().get(1));
        assertEquals("alice", userMessage.getName());

        CompletionFunctionMessage functionMessage =
                assertInstanceOf(CompletionFunctionMessage.class, request.getMessages().get(2));
        assertEquals("lookup_weather", functionMessage.getName());

        CompletionToolMessage toolMessage =
                assertInstanceOf(CompletionToolMessage.class, request.getMessages().get(3));
        assertEquals("call_123", toolMessage.getToolCallId());

        assertNotNull(request.getTools());
        assertEquals(1, request.getTools().size());
        CompletionFunctionTool functionTool =
                assertInstanceOf(CompletionFunctionTool.class, request.getTools().get(0));
        assertEquals("get_current_weather", functionTool.getFunction().getName());
        assertEquals("Get the current weather in a given location", functionTool.getFunction().getDescription());
        assertTrue(functionTool.getFunction().getParameters().containsKey("type"));
    }
}
