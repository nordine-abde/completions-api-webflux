package com.anordine.completions.api.webflux.format;

import com.anordine.completions.api.webflux.format.abs.CompletionResponseFormat;
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
                  "name": "vehicle_result",
                  "description": "Vehicle result payload",
                  "schema": {
                    "type": "object"
                  },
                  "strict": true
                }
                """;

        CompletionResponseFormat responseFormat =
                objectMapper.readValue(json, CompletionResponseFormat.class);

        CompletionResponseFormatJsonSchema jsonSchema =
                assertInstanceOf(CompletionResponseFormatJsonSchema.class, responseFormat);
        assertEquals("vehicle_result", jsonSchema.getName());
        assertEquals("Vehicle result payload", jsonSchema.getDescription());
        assertEquals(Map.of("type", "object"), jsonSchema.getSchema());
        assertEquals(Boolean.TRUE, jsonSchema.getStrict());
    }
}
