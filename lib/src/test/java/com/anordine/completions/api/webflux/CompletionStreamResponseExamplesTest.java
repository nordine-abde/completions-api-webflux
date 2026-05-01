package com.anordine.completions.api.webflux;

import com.anordine.completions.api.webflux.model.CompletionStreamResponse;
import com.anordine.completions.api.webflux.model.enums.finish.CompletionFinishReason;
import com.anordine.completions.api.webflux.model.enums.role.CompletionRole;
import com.anordine.completions.api.webflux.model.enums.tool.CompletionToolType;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CompletionStreamResponseExamplesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesTextDeltaChunk() throws Exception {
        CompletionStreamResponse response = objectMapper.readValue("""
                {
                  "id": "chatcmpl-123",
                  "object": "chat.completion.chunk",
                  "created": 1694268190,
                  "model": "gpt-4o-mini",
                  "system_fingerprint": "fp_44709d6fcb",
                  "choices": [
                    {
                      "index": 0,
                      "delta": {
                        "role": "assistant",
                        "content": "Hello"
                      },
                      "finish_reason": null
                    }
                  ],
                  "usage": null
                }
                """, CompletionStreamResponse.class);

        assertEquals("chatcmpl-123", response.getId());
        assertEquals("chat.completion.chunk", response.getObject());
        assertEquals("gpt-4o-mini", response.getModel());
        assertEquals("fp_44709d6fcb", response.getSystemFingerprint());
        assertEquals(1, response.getChoices().size());
        assertEquals(0, response.getChoices().getFirst().getIndex());
        assertEquals(CompletionRole.ASSISTANT, response.getChoices().getFirst().getDelta().getRole());
        assertEquals("Hello", response.getChoices().getFirst().getDelta().getContent());
    }

    @Test
    void deserializesToolDeltaAndUsageChunk() throws Exception {
        CompletionStreamResponse toolChunk = objectMapper.readValue("""
                {
                  "id": "chatcmpl-123",
                  "object": "chat.completion.chunk",
                  "created": 1694268190,
                  "model": "gpt-4o-mini",
                  "choices": [
                    {
                      "index": 0,
                      "delta": {
                        "tool_calls": [
                          {
                            "index": 0,
                            "id": "call_abc",
                            "type": "function",
                            "function": {
                              "name": "lookup",
                              "arguments": "{\\"city\\":"
                            }
                          }
                        ]
                      },
                      "finish_reason": null
                    }
                  ]
                }
                """, CompletionStreamResponse.class);

        assertNotNull(toolChunk.getChoices().getFirst().getDelta().getToolCalls());
        assertEquals(CompletionToolType.FUNCTION, toolChunk.getChoices().getFirst().getDelta().getToolCalls().getFirst().getType());
        assertEquals("lookup", toolChunk.getChoices().getFirst().getDelta().getToolCalls().getFirst().getFunction().getName());

        CompletionStreamResponse usageChunk = objectMapper.readValue("""
                {
                  "id": "chatcmpl-123",
                  "object": "chat.completion.chunk",
                  "created": 1694268190,
                  "model": "gpt-4o-mini",
                  "choices": [],
                  "usage": {
                    "prompt_tokens": 3,
                    "completion_tokens": 2,
                    "total_tokens": 5
                  }
                }
                """, CompletionStreamResponse.class);

        assertEquals(0, usageChunk.getChoices().size());
        assertEquals(5, usageChunk.getUsage().getTotalTokens());
    }

    @Test
    void deserializesFinalChunk() throws Exception {
        CompletionStreamResponse response = objectMapper.readValue("""
                {
                  "id": "chatcmpl-123",
                  "object": "chat.completion.chunk",
                  "created": 1694268190,
                  "model": "gpt-4o-mini",
                  "choices": [
                    {
                      "index": 0,
                      "delta": {},
                      "finish_reason": "stop"
                    }
                  ]
                }
                """, CompletionStreamResponse.class);

        assertEquals(CompletionFinishReason.STOP, response.getChoices().getFirst().getFinishReason());
    }
}
