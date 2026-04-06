package com.anordine.completions.api.webflux.model.tool;

import com.anordine.completions.api.webflux.model.enums.toolformat.CompletionGrammarSyntax;
import com.anordine.completions.api.webflux.model.tool.format.CompletionCustomToolGrammarFormat;
import com.anordine.completions.api.webflux.model.tool.format.CompletionCustomToolTextFormat;
import com.anordine.completions.api.webflux.model.tool.abs.CompletionTool;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

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
                      "type": "grammar",
                      "grammar": {
                        "definition": "start: /.+/",
                        "syntax": "regex"
                      }
                    }
                  }
                }
                """;

        CompletionTool tool = objectMapper.readValue(json, CompletionTool.class);

        CompletionCustomTool customTool = assertInstanceOf(CompletionCustomTool.class, tool);
        assertEquals("sql_runner", customTool.getCustom().getName());
        assertEquals("Execute SQL queries", customTool.getCustom().getDescription());
        CompletionCustomToolGrammarFormat format =
                assertInstanceOf(CompletionCustomToolGrammarFormat.class, customTool.getCustom().getFormat());
        assertEquals("start: /.+/", format.getGrammar().getDefinition());
        assertEquals(CompletionGrammarSyntax.REGEX, format.getGrammar().getSyntax());
    }

    @Test
    void serializesTextCustomToolFormatUsingWireType() throws Exception {
        CompletionCustomDefinition customDefinition = new CompletionCustomDefinition();
        customDefinition.setName("sql_runner");
        customDefinition.setFormat(new CompletionCustomToolTextFormat());

        String json = objectMapper.writeValueAsString(new CompletionCustomTool(customDefinition));

        assertTrue(json.contains("\"type\":\"custom\""));
        assertTrue(json.contains("\"format\":{\"type\":\"text\"}"));
    }
}
