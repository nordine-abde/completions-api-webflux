package com.anordine.simplifier.webflux.ai.example;

import com.anordine.simplifier.webflux.ai.helper.tool.CompletionToolRegistry;
import com.anordine.simplifier.webflux.ai.model.CompletionRequest;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionFunctionTool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CompletionToolUsageExampleTest {

    @Autowired
    private CompletionToolRegistry toolRegistry;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void addsAllScannedToolsToCompletionRequest() throws Exception {
        CompletionRequest request = CompletionRequest.create("What is the weather in Rome?");

        toolRegistry.addToolsTo(request);

        assertEquals(List.of("current_weather", "support_hours"), toolNames(request));

        CompletionFunctionTool weatherTool = assertInstanceOf(CompletionFunctionTool.class, request.getTools().getFirst());
        assertEquals("Get the current weather for a city.", weatherTool.getFunction().getDescription());
        assertEquals(true, weatherTool.getFunction().getStrict());

        Map<String, Object> parameters = weatherTool.getFunction().getParameters();
        assertEquals("object", parameters.get("type"));
        assertEquals(false, parameters.get("additionalProperties"));
        assertTrue(parameters.containsKey("properties"));

        String json = objectMapper.writeValueAsString(request);
        assertTrue(json.contains("\"tools\""));
        assertTrue(json.contains("\"name\":\"current_weather\""));
        assertTrue(json.contains("\"strict\":true"));
    }

    @Test
    void addsOnlySelectedToolsByNameToCompletionRequest() {
        CompletionRequest request = CompletionRequest.create("When is support available?");

        toolRegistry.addToolsTo(request, "support_hours");

        assertEquals(List.of("support_hours"), toolNames(request));
    }

    private List<String> toolNames(CompletionRequest request) {
        return request.getTools().stream()
                .map(tool -> assertInstanceOf(CompletionFunctionTool.class, tool).getFunction().getName())
                .toList();
    }

}
