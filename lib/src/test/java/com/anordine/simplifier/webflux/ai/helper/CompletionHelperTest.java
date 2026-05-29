package com.anordine.simplifier.webflux.ai.helper;

import com.anordine.simplifier.webflux.ai.helper.history.InMemoryHistoryManager;
import com.anordine.simplifier.webflux.ai.model.CompletionRequest;
import com.anordine.simplifier.webflux.ai.model.CompletionResponse;
import com.anordine.simplifier.webflux.ai.model.CompletionStreamResponse;
import com.anordine.simplifier.webflux.ai.model.message.CompletionDeveloperMessage;
import com.anordine.simplifier.webflux.ai.model.message.CompletionUserMessage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompletionHelperTest {

    @Test
    void postsRequestToChatCompletionsEndpointAndMapsResponse() throws IOException {
        AtomicReference<String> method = new AtomicReference<>();
        AtomicReference<String> path = new AtomicReference<>();
        AtomicReference<String> requestBody = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/chat/completions", exchange -> handleCompletionRequest(exchange, method, path, requestBody));
        server.start();

        try {
            CompletionHelper completionHelper = new CompletionHelper(WebClient.builder()
                    .baseUrl("http://127.0.0.1:" + server.getAddress().getPort())
                    .build());

            CompletionResponse response = completionHelper.callCompletionsApi(
                    "gpt-5.4",
                    new CompletionDeveloperMessage("You are terse"),
                    new CompletionUserMessage("Hello from test")
            ).block();

            assertNotNull(response);
            assertEquals("chatcmpl-test", response.getId());
            assertEquals("gpt-5.4", response.getModel());
            assertEquals("Hello back", response.getChoices().get(0).getMessage().getContent());
            assertEquals("POST", method.get());
            assertEquals("/chat/completions", path.get());
            assertTrue(requestBody.get().contains("\"model\":\"gpt-5.4\""));
            assertTrue(requestBody.get().contains("\"role\":\"developer\""));
            assertTrue(requestBody.get().contains("\"role\":\"user\""));
            assertTrue(requestBody.get().contains("\"content\":\"Hello from test\""));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void singleCompletionMessageUsesModelBasedVarargsOverload() throws IOException {
        AtomicReference<String> method = new AtomicReference<>();
        AtomicReference<String> path = new AtomicReference<>();
        AtomicReference<String> requestBody = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/chat/completions", exchange -> handleCompletionRequest(exchange, method, path, requestBody));
        server.start();

        try {
            CompletionHelper completionHelper = new CompletionHelper(WebClient.builder()
                    .baseUrl("http://127.0.0.1:" + server.getAddress().getPort())
                    .build());

            CompletionResponse response = completionHelper.callCompletionsApi(
                    "gpt-5.4",
                    new CompletionUserMessage("Hello from single message test")
            ).block();

            assertNotNull(response);
            assertEquals("POST", method.get());
            assertEquals("/chat/completions", path.get());
            assertTrue(requestBody.get().contains("\"model\":\"gpt-5.4\""));
            assertTrue(requestBody.get().contains("\"role\":\"user\""));
            assertTrue(requestBody.get().contains("\"content\":\"Hello from single message test\""));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void streamsCompletionChunksAndForcesStreamRequestField() throws IOException {
        AtomicReference<String> method = new AtomicReference<>();
        AtomicReference<String> path = new AtomicReference<>();
        AtomicReference<String> requestBody = new AtomicReference<>();
        AtomicReference<String> accept = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/chat/completions", exchange -> handleStreamingCompletionRequest(
                exchange,
                method,
                path,
                requestBody,
                accept
        ));
        server.start();

        try {
            CompletionHelper completionHelper = new CompletionHelper(WebClient.builder()
                    .baseUrl("http://127.0.0.1:" + server.getAddress().getPort())
                    .build());

            List<CompletionStreamResponse> chunks = completionHelper.streamCompletionsApi(new CompletionRequest()
                    .withModel("gpt-5.4")
                    .addUserMessage("Hello from stream")
            ).collectList().block();

            assertNotNull(chunks);
            assertEquals(3, chunks.size());
            assertEquals("Hel", chunks.get(0).getChoices().getFirst().getDelta().getContent());
            assertEquals("lo", chunks.get(1).getChoices().getFirst().getDelta().getContent());
            assertEquals(5, chunks.get(2).getUsage().getTotalTokens());
            assertEquals("POST", method.get());
            assertEquals("/chat/completions", path.get());
            assertTrue(accept.get().contains("text/event-stream"));
            assertTrue(requestBody.get().contains("\"stream\":true"));
            assertTrue(requestBody.get().contains("\"content\":\"Hello from stream\""));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void streamWithHistoryStoresFinalAssistantMessage() throws IOException {
        AtomicReference<String> method = new AtomicReference<>();
        AtomicReference<String> path = new AtomicReference<>();
        AtomicReference<String> requestBody = new AtomicReference<>();
        AtomicReference<String> accept = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/chat/completions", exchange -> handleStreamingCompletionRequest(
                exchange,
                method,
                path,
                requestBody,
                accept
        ));
        server.start();

        try {
            InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
            historyManager.loadChat("chat-1", new CompletionRequest().withModel("gpt-5.4")).block();
            CompletionHelper completionHelper = new CompletionHelper(WebClient.builder()
                    .baseUrl("http://127.0.0.1:" + server.getAddress().getPort())
                    .build(), historyManager);

            List<CompletionStreamResponse> chunks = completionHelper.streamCompletionsApiWithHistory(
                    "chat-1",
                    new CompletionUserMessage("Hello history")
            ).collectList().block();
            CompletionRequest history = historyManager.getChat("chat-1").block();

            assertNotNull(chunks);
            assertNotNull(history);
            assertEquals(2, history.getMessages().size());
            assertEquals("Hello history", history.getMessages().get(0).getContent());
            assertEquals("Hello", history.getMessages().get(1).getContent());
            assertTrue(requestBody.get().contains("\"stream\":true"));
        } finally {
            server.stop(0);
        }
    }

    private void handleCompletionRequest(HttpExchange exchange,
                                         AtomicReference<String> method,
                                         AtomicReference<String> path,
                                         AtomicReference<String> requestBody) throws IOException {
        method.set(exchange.getRequestMethod());
        path.set(exchange.getRequestURI().getPath());
        requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));

        byte[] responseBody = """
                {
                  "id": "chatcmpl-test",
                  "created": 1741569952,
                  "model": "gpt-5.4",
                  "object": "chat.completion",
                  "service_tier": "default",
                  "choices": [
                    {
                      "index": 0,
                      "finish_reason": "stop",
                      "message": {
                        "role": "assistant",
                        "content": "Hello back"
                      }
                    }
                  ]
                }
                """.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBody.length);
        exchange.getResponseBody().write(responseBody);
        exchange.close();
    }

    private void handleStreamingCompletionRequest(HttpExchange exchange,
                                                  AtomicReference<String> method,
                                                  AtomicReference<String> path,
                                                  AtomicReference<String> requestBody,
                                                  AtomicReference<String> accept) throws IOException {
        method.set(exchange.getRequestMethod());
        path.set(exchange.getRequestURI().getPath());
        accept.set(exchange.getRequestHeaders().getFirst("Accept"));
        requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));

        byte[] responseBody = """
                data: {"id":"chatcmpl-stream","created":1741569952,"model":"gpt-5.4","object":"chat.completion.chunk","choices":[{"index":0,"delta":{"role":"assistant","content":"Hel"},"finish_reason":null}],"usage":null}

                data: {"id":"chatcmpl-stream","created":1741569952,"model":"gpt-5.4","object":"chat.completion.chunk","choices":[{"index":0,"delta":{"content":"lo"},"finish_reason":null}],"usage":null}

                data: {"id":"chatcmpl-stream","created":1741569952,"model":"gpt-5.4","object":"chat.completion.chunk","choices":[],"usage":{"prompt_tokens":3,"completion_tokens":2,"total_tokens":5}}

                data: [DONE]

                """.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
        exchange.sendResponseHeaders(200, responseBody.length);
        exchange.getResponseBody().write(responseBody);
        exchange.close();
    }
}
