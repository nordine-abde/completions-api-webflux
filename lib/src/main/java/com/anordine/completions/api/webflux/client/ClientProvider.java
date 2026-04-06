package com.anordine.completions.api.webflux.client;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

public class ClientProvider {

    private ClientProvider() {
    }

    private static final String OPEN_AI_BASE_URL = "https://api.openai.com/v1";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/openai";
    private static final String GROQ_BASE_URL = "https://api.groq.com/openai/v1";
    private static final String OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1";
    private static final String CLAUDE_BASE_URL = "https://api.anthropic.com/v1";


    public static WebClient buildOpenAiWebClient(WebClient.Builder builder, String secretKey) {
        return buildCompletionsWebClient(builder, OPEN_AI_BASE_URL, secretKey);
    }

    public static WebClient buildGeminiWebClient(WebClient.Builder builder, String secretKey) {
        return buildCompletionsWebClient(builder, GEMINI_BASE_URL, secretKey);
    }

    public static WebClient buildGroqWebClient(WebClient.Builder builder, String secretKey) {
        return buildCompletionsWebClient(builder, GROQ_BASE_URL, secretKey);
    }

    public static WebClient buildOpenRouterWebClient(WebClient.Builder builder, String secretKey) {
        return buildCompletionsWebClient(builder, OPENROUTER_BASE_URL, secretKey);
    }

    public static WebClient buildClaudeWebClient(WebClient.Builder builder, String secretKey) {
        return buildCompletionsWebClient(builder, CLAUDE_BASE_URL, secretKey);
    }

    public static WebClient buildCompletionsWebClient(WebClient.Builder builder,
                                               String baseUrl,
                                               String secretKey) {

        if(Strings.isBlank(baseUrl)) {
            throw new IllegalArgumentException("null or blank baseUrl");
        }
        if(Strings.isBlank(secretKey)) {
            throw new IllegalArgumentException("null or blank secret key");
        }
        return Objects.requireNonNull(builder).clone()
                .baseUrl(baseUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setBearerAuth(secretKey);
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .build();
    }

}
