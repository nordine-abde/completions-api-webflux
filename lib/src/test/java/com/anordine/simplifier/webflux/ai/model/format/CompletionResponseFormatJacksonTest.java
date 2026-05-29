package com.anordine.simplifier.webflux.ai.model.format;

import com.anordine.simplifier.webflux.ai.model.format.abs.CompletionResponseFormat;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompletionResponseFormatJacksonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesTypeUsingWireValues() throws Exception {
        String json = objectMapper.writeValueAsString(new CompletionResponseFormatText());

        assertTrue(json.contains("\"type\":\"text\""));
    }

    @Test
    void deserializesJsonSchemaSubtypeFromType() throws Exception {
        String json = """
                {
                  "type": "json_schema",
                  "json_schema": {
                    "name": "vehicle_result",
                    "description": "Vehicle result payload",
                    "schema": {
                      "type": "object"
                    },
                    "strict": true
                  }
                }
                """;

        CompletionResponseFormat responseFormat =
                objectMapper.readValue(json, CompletionResponseFormat.class);

        CompletionResponseFormatJsonSchema jsonSchema =
                assertInstanceOf(CompletionResponseFormatJsonSchema.class, responseFormat);
        assertEquals("vehicle_result", jsonSchema.getJsonSchema().getName());
        assertEquals("Vehicle result payload", jsonSchema.getJsonSchema().getDescription());
        assertEquals(Map.of("type", "object"), jsonSchema.getJsonSchema().getSchema());
        assertEquals(Boolean.TRUE, jsonSchema.getJsonSchema().getStrict());
    }

    @Test
    void serializesJsonSchemaUsingNestedWireShape() throws Exception {
        CompletionResponseFormatJsonSchema responseFormat = new CompletionResponseFormatJsonSchema(
                new CompletionResponseFormatJsonSchemaDefinition(
                        "vehicle_result",
                        "Vehicle result payload",
                        Map.of("type", "object"),
                        true
                )
        );

        String json = objectMapper.writeValueAsString(responseFormat);

        assertTrue(json.contains("\"type\":\"json_schema\""));
        assertTrue(json.contains("\"json_schema\":"));
        assertTrue(json.contains("\"name\":\"vehicle_result\""));
    }
}
