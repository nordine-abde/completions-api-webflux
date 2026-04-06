# completions-api-webflux

Java DTO models for the OpenAI Chat Completions API using Jackson-based JSON mapping.

## What This Project Covers

This repository provides DTOs for chat completions request and response payloads, with:

- enum wire-value mapping using `@JsonValue` and `@JsonCreator`
- conditional subtype mapping for discriminator-based fields such as `role` and `type`
- example-based serialization and deserialization tests

## Current Version Notes

Some documented Chat Completions features are not present in this version yet.

### Not Present In This Version

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

### Streaming Is Not Included In This Version

Streaming-related request support is not part of the current version, including:

- `stream`
- `stream_options`

### Deprecated Features Will Not Be Implemented

Deprecated Chat Completions fields are intentionally out of scope for this project version, including:

- assistant `function_call`
- request `function_call`
- request `functions`
- request `max_tokens`
- request `seed`
- request `user`

## Scope Guidance

If you use this library in the current version, prefer:

- text-only request messages
- `tool_calls` instead of deprecated function-calling fields
- non-streaming chat completions usage
- currently modeled request and response features only

## Task Tracking

Implementation and deferral decisions are tracked under [docs/tasks](/home/abdessamad/apps/completions-api-webflux/docs/tasks).
