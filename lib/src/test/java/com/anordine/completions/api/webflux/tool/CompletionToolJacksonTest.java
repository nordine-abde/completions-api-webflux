package com.anordine.completions.api.webflux.tool;

import com.anordine.completions.api.webflux.tool.abs.CompletionTool;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompletionToolJacksonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesFunctionToolTypeUsingWireValue() throws Exception {
        CompletionFunctionDefinition functionDefinition = new CompletionFunctionDefinition();
        functionDefinition.setName("lookupVehicle");

        String json = objectMapper.writeValueAsString(new CompletionFunctionTool(functionDefinition));

        assertTrue(json.contains("\"type\":\"function\""));
    }

    @Test
    void deserializesCustomToolSubtypeFromType() throws Exception {
        String json = """
                {
                  "type": "custom",
                  "custom": {
                    "name": "sql_runner",
                    "description": "Execute SQL queries",
                    "format": {
                      "type": "grammar"
                    }
                  }
                }
                """;

        CompletionTool tool = objectMapper.readValue(json, CompletionTool.class);

        CompletionCustomTool customTool = assertInstanceOf(CompletionCustomTool.class, tool);
        assertEquals("sql_runner", customTool.getCustom().getName());
        assertEquals("Execute SQL queries", customTool.getCustom().getDescription());
        assertEquals(Map.of("type", "grammar"), customTool.getCustom().getFormat());
    }
}
