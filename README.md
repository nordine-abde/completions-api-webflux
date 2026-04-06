# completions-api-webflux

Java library for OpenAI Chat Completions DTOs plus Spring WebFlux `WebClient` configuration for compatible providers.

## Current State

This repository currently includes:

- request and response DTOs for Chat Completions payloads
- Jackson enum wire-value mapping using `@JsonValue` and `@JsonCreator`
- discriminator-based subtype mapping for fields such as `role`, `type`, tool choice, and response format
- tests covering example-based serialization and deserialization
- Spring `WebClient` configuration for `open-ai`, `groq`, `gemini`, `open-router`, `claude`, and custom compatible endpoints

## Spring Configuration

`WebClientConfiguration` provides:

- a fallback `WebClient.Builder` bean named `builder` when the application does not already define one
- opt-in provider clients controlled by `anordine.completions-api-webflux.<provider>.autoconfigure=true`
- dynamic registration of custom `WebClient` beans from `anordine.completions-api-webflux.custom.<name>`

Custom client property names are converted to camelCase bean names. For example:

- `custom.local-llm` becomes bean `localLlm`
- `custom.partner-gateway` becomes bean `partnerGateway`

The example properties file is at [lib/src/main/resources/application-example.yaml](lib/src/main/resources/application-example.yaml).

Important: this repository does not currently publish Spring Boot auto-configuration metadata such as `AutoConfiguration.imports`. Consumers must register or import [WebClientConfiguration](lib/src/main/java/com/anordine/completions/api/webflux/configuration/WebClientConfiguration.java) explicitly.

Example:
```java
@Configuration
@Import(WebClientConfiguration.class)
class MyConfiguration {
}
```

## Convenience Usage

For straightforward text chat calls you can build requests fluently and wrap any configured compatible `WebClient` with `CompletionService`.

```java
WebClient openAiWebClient = applicationContext.getBean("openAiWebClient", WebClient.class);

CompletionService completionService = new CompletionService(openAiWebClient);

CompletionResponse response = completionService.callCompletionsApi(
        new CompletionRequest()
                .withModel("gpt-5.4")
                .addDeveloperMessage("Be concise")
                .addUserMessage("Write a haiku about WebFlux")
).block();
```

There are also shorter overloads for common cases such as `callCompletionsApi("gpt-5.4", "Hello")` and helpers like `CompletionRequest.create("gpt-5.4", "Hello")`.

## Chat Completions Coverage

Some documented Chat Completions features are still out of scope for the current DTO model set.

### Not Present In The Current DTO Model

- multimodal request content arrays
- image input content parts
- audio input content parts
- file input content parts
- response `annotations`
- response `logprobs`
- response audio payload details beyond audio `id`
- advanced prediction-related request DTOs
- web search request DTOs
- several optional advanced request fields still planned for a later version

### Streaming Is Not Included

Streaming-related request support is not part of the current version, including:

- `stream`
- `stream_options`

### Deprecated Features Are Intentionally Out Of Scope

Deprecated Chat Completions fields are intentionally out of scope for this project version, including:

- assistant `function_call`
- request `function_call`
- request `functions`
- request `max_tokens`
- request `seed`
- request `user`

## Scope Guidance

If you use this library in its current state, prefer:

- text-only request messages
- `tool_calls` instead of deprecated function-calling fields
- non-streaming chat completions usage
- providers or gateways that accept OpenAI-compatible bearer-auth chat completions requests
- currently modeled request and response features only

## Task Tracking

Implementation and deferral decisions are tracked under [docs/tasks](/home/abdessamad/apps/completions-api-webflux/docs/tasks).
