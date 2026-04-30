package com.anordine.completions.api.webflux.helper;

import com.anordine.completions.api.webflux.model.CompletionResponse;
import com.anordine.completions.api.webflux.model.message.CompletionDeveloperMessage;
import com.anordine.completions.api.webflux.model.message.CompletionUserMessage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
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
}
