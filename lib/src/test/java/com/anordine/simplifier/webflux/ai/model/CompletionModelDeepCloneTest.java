package com.anordine.simplifier.webflux.ai.model;

import com.anordine.simplifier.webflux.ai.model.enums.finish.CompletionFinishReason;
import com.anordine.simplifier.webflux.ai.model.enums.modality.CompletionModality;
import com.anordine.simplifier.webflux.ai.model.enums.prompt.CompletionPromptCacheRetention;
import com.anordine.simplifier.webflux.ai.model.enums.resoning.CompletionReasoningEffort;
import com.anordine.simplifier.webflux.ai.model.enums.toolchoice.CompletionAllowedToolsMode;
import com.anordine.simplifier.webflux.ai.model.enums.toolchoice.CompletionToolChoiceMode;
import com.anordine.simplifier.webflux.ai.model.enums.toolformat.CompletionGrammarSyntax;
import com.anordine.simplifier.webflux.ai.model.enums.verbosity.CompletionVerbosity;
import com.anordine.simplifier.webflux.ai.model.format.CompletionResponseFormatJsonObject;
import com.anordine.simplifier.webflux.ai.model.format.CompletionResponseFormatJsonSchema;
import com.anordine.simplifier.webflux.ai.model.format.CompletionResponseFormatJsonSchemaDefinition;
import com.anordine.simplifier.webflux.ai.model.format.CompletionResponseFormatText;
import com.anordine.simplifier.webflux.ai.model.format.abs.CompletionResponseFormat;
import com.anordine.simplifier.webflux.ai.model.message.CompletionAssistantMessage;
import com.anordine.simplifier.webflux.ai.model.message.CompletionAudio;
import com.anordine.simplifier.webflux.ai.model.message.CompletionChoices;
import com.anordine.simplifier.webflux.ai.model.message.CompletionDeveloperMessage;
import com.anordine.simplifier.webflux.ai.model.message.CompletionFunctionMessage;
import com.anordine.simplifier.webflux.ai.model.message.CompletionSystemMessage;
import com.anordine.simplifier.webflux.ai.model.message.CompletionToolMessage;
import com.anordine.simplifier.webflux.ai.model.message.CompletionUserMessage;
import com.anordine.simplifier.webflux.ai.model.message.abs.CompletionMessage;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionCustomDefinition;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionCustomTool;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionFunctionDefinition;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionFunctionTool;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionMessageCustomTool;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionMessageCustomToolCall;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionMessageFunctionTool;
import com.anordine.simplifier.webflux.ai.model.tool.CompletionMessageFunctionToolCall;
import com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionTool;
import com.anordine.simplifier.webflux.ai.model.tool.abs.CompletionToolCall;
import com.anordine.simplifier.webflux.ai.model.tool.format.CompletionCustomToolGrammarFormat;
import com.anordine.simplifier.webflux.ai.model.tool.format.CompletionCustomToolTextFormat;
import com.anordine.simplifier.webflux.ai.model.tool.format.CompletionGrammarDefinition;
import com.anordine.simplifier.webflux.ai.model.tool.format.abs.CompletionCustomToolFormat;
import com.anordine.simplifier.webflux.ai.model.toolchoice.CompletionAllowedTools;
import com.anordine.simplifier.webflux.ai.model.toolchoice.CompletionNamedCustomToolChoice;
import com.anordine.simplifier.webflux.ai.model.toolchoice.CompletionNamedFunctionToolChoice;
import com.anordine.simplifier.webflux.ai.model.toolchoice.CompletionToolChoiceAllowedTools;
import com.anordine.simplifier.webflux.ai.model.toolchoice.CompletionToolChoiceName;
import com.anordine.simplifier.webflux.ai.model.toolchoice.abs.ToolChoiceOptionInterface;
import com.anordine.simplifier.webflux.ai.model.usage.CompletionTokensDetails;
import com.anordine.simplifier.webflux.ai.model.usage.CompletionUsage;
import com.anordine.simplifier.webflux.ai.model.usage.PromptTokenDetails;
import com.anordine.simplifier.webflux.ai.util.DeepCloneUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class CompletionModelDeepCloneTest {

    @Test
    void completionRequestDeepCloneCopiesEveryFieldAndMutableNestedValue() {
        CompletionRequest original = new CompletionRequest();
        original.setMessages(new ArrayList<>(List.of(
                new CompletionDeveloperMessage("developer", "dev"),
                new CompletionSystemMessage("system", "sys"),
                new CompletionUserMessage("user", "alice"),
                assistantMessage()
        )));
        original.setModel("gpt-5.4");
        original.setFrequencyPenalty(0.1);
        original.setMaxCompletionTokens(2048);
        original.setModalities(new ArrayList<>(List.of(CompletionModality.TEXT, CompletionModality.AUDIO)));
        original.setN(2);
        original.setParallelToolCalls(true);
        original.setPresencePenalty(0.2);
        original.setPromptCacheKey("cache-key");
        original.setPromptCacheRetention(CompletionPromptCacheRetention.TWENTY_FOUR_HOURS);
        original.setReasoningEffort(CompletionReasoningEffort.HIGH);
        original.setResponseFormat(jsonSchemaFormat());
        original.setSafetyIdentifier("safety");
        original.setStore(true);
        original.setTemperature(0.3);
        original.setToolChoice(namedFunctionChoice());
        original.setTools(new ArrayList<>(List.of(functionTool(), customGrammarTool(), customTextTool())));
        original.setLogprobs(true);
        original.setTopLogprobs(4);
        original.setTopP(0.9);
        original.setVerbosity(CompletionVerbosity.HIGH);
        original.setExtraBody(jsonObjectMap());

        CompletionRequest clone = original.deepClone();

        assertSimpleValues(original, clone);

        assertDeepCopiedList(original.getMessages(), clone.getMessages());
        assertNotSame(original.getModalities(), clone.getModalities());
        assertEquals(original.getModalities(), clone.getModalities());
        assertDeepCopiedList(original.getTools(), clone.getTools());
        assertNotSame(original.getResponseFormat(), clone.getResponseFormat());
        assertNotSame(original.getToolChoice(), clone.getToolChoice());

        original.getMessages().clear();
        original.getModalities().add(CompletionModality.TEXT);
        original.getTools().clear();
        namedFunctionChoice(original.getToolChoice()).getFunction().setName("mutated");
        jsonSchema(original.getResponseFormat()).getJsonSchema().getSchema().put("mutated", true);
        original.getExtraBody().put("type", "mutated");

        assertList(clone);
    }

    private void assertList(CompletionRequest clone) {
        assertAll(
                () -> assertEquals(4, clone.getMessages().size()),
                () -> assertEquals(2, clone.getModalities().size()),
                () -> assertEquals(3, clone.getTools().size()),
                () -> assertEquals("lookup", namedFunctionChoice(clone.getToolChoice()).getFunction().getName()),
                () -> assertFalse(jsonSchema(clone.getResponseFormat()).getJsonSchema().getSchema().containsKey("mutated"))
        );
    }

    private static void assertSimpleValues(CompletionRequest original, CompletionRequest clone) {
        assertAll(
                () -> assertNotSame(original, clone),
                () -> assertEquals("gpt-5.4", clone.getModel()),
                () -> assertEquals(0.1, clone.getFrequencyPenalty()),
                () -> assertEquals(2048, clone.getMaxCompletionTokens()),
                () -> assertEquals(List.of(CompletionModality.TEXT, CompletionModality.AUDIO), clone.getModalities()),
                () -> assertEquals(2, clone.getN()),
                () -> assertEquals(Boolean.TRUE, clone.getParallelToolCalls()),
                () -> assertEquals(0.2, clone.getPresencePenalty()),
                () -> assertEquals("cache-key", clone.getPromptCacheKey()),
                () -> assertEquals(CompletionPromptCacheRetention.TWENTY_FOUR_HOURS, clone.getPromptCacheRetention()),
                () -> assertEquals(CompletionReasoningEffort.HIGH, clone.getReasoningEffort()),
                () -> assertEquals("safety", clone.getSafetyIdentifier()),
                () -> assertEquals(Boolean.TRUE, clone.getStore()),
                () -> assertEquals(0.3, clone.getTemperature()),
                () -> assertEquals(Boolean.TRUE, clone.getLogprobs()),
                () -> assertEquals(4, clone.getTopLogprobs()),
                () -> assertEquals(0.9, clone.getTopP()),
                () -> assertEquals(CompletionVerbosity.HIGH, clone.getVerbosity()),
                () -> assertEquals("object", clone.getExtraBody().get("type"))
        );
    }

    @Test
    void completionResponseDeepCloneCopiesChoicesMessagesAndUsage() {
        CompletionResponse original = new CompletionResponse(
                "response-id",
                123L,
                "gpt-5.4",
                "chat.completion",
                "default",
                new ArrayList<>(List.of(new CompletionChoices(
                        CompletionFinishReason.TOOL_CALLS,
                        0,
                        assistantMessage()
                ))),
                usage()
        );

        CompletionResponse clone = original.deepClone();

        assertAll(
                () -> assertNotSame(original, clone),
                () -> assertEquals("response-id", clone.getId()),
                () -> assertEquals(123L, clone.getCreated()),
                () -> assertEquals("gpt-5.4", clone.getModel()),
                () -> assertEquals("chat.completion", clone.getObject()),
                () -> assertEquals("default", clone.getServiceTier()),
                () -> assertDeepCopiedList(original.getChoices(), clone.getChoices()),
                () -> assertNotSame(original.getChoices().getFirst().getMessage(), clone.getChoices().getFirst().getMessage()),
                () -> assertNotSame(original.getUsage(), clone.getUsage()),
                () -> assertNotSame(
                        original.getUsage().getCompletionTokensDetails(),
                        clone.getUsage().getCompletionTokensDetails()
                ),
                () -> assertNotSame(
                        original.getUsage().getPromptTokensDetails(),
                        clone.getUsage().getPromptTokensDetails()
                )
        );

        original.getChoices().getFirst().getMessage().setContent("mutated");
        original.getUsage().getCompletionTokensDetails().setReasoningTokens(999);

        assertAll(
                () -> assertEquals("assistant", clone.getChoices().getFirst().getMessage().getContent()),
                () -> assertEquals(3, clone.getUsage().getCompletionTokensDetails().getReasoningTokens())
        );
    }

    @Test
    void everyCompletionMessageSubtypeDeepClonesItsOwnFields() {
        CompletionDeveloperMessage developer = new CompletionDeveloperMessage("developer", "dev");
        CompletionSystemMessage system = new CompletionSystemMessage("system", "sys");
        CompletionUserMessage user = new CompletionUserMessage("user", "alice");
        CompletionFunctionMessage function = new CompletionFunctionMessage("function", "fn");
        CompletionToolMessage tool = new CompletionToolMessage("tool", "tool-name");
        tool.setToolCallId("call-1");
        CompletionAssistantMessage assistant = assistantMessage();

        assertPlainMessageClone(developer.deepClone(), developer, CompletionDeveloperMessage.class);
        assertPlainMessageClone(system.deepClone(), system, CompletionSystemMessage.class);
        assertPlainMessageClone(user.deepClone(), user, CompletionUserMessage.class);
        assertPlainMessageClone(function.deepClone(), function, CompletionFunctionMessage.class);

        CompletionToolMessage toolClone = tool.deepClone();
        assertPlainMessageClone(toolClone, tool, CompletionToolMessage.class);
        assertEquals("call-1", toolClone.getToolCallId());

        CompletionAssistantMessage assistantClone = assistant.deepClone();
        assertPlainMessageClone(assistantClone, assistant, CompletionAssistantMessage.class);
        assertDeepCopiedList(assistant.getToolCalls(), assistantClone.getToolCalls());
        assertNotSame(assistant.getAudio(), assistantClone.getAudio());

        assistant.getToolCalls().clear();
        assistant.getAudio().setId("mutated-audio");

        assertAll(
                () -> assertEquals(2, assistantClone.getToolCalls().size()),
                () -> assertEquals("audio-id", assistantClone.getAudio().getId())
        );
    }

    @Test
    void everyToolFormatToolCallAndToolChoiceSubtypeDeepClonesItsOwnFields() throws Exception {
        CompletionMessageFunctionToolCall functionToolCall =
                new CompletionMessageFunctionToolCall("call-fn", new CompletionMessageFunctionTool("{}", "lookup"));
        CompletionMessageCustomToolCall customToolCall =
                new CompletionMessageCustomToolCall("call-custom", new CompletionMessageCustomTool("input", "runner"));
        CompletionFunctionTool functionTool = functionTool();
        CompletionCustomTool grammarTool = customGrammarTool();
        CompletionCustomTool textTool = customTextTool();
        CompletionCustomToolGrammarFormat grammarFormat = grammarFormat();
        CompletionCustomToolTextFormat textFormat = new CompletionCustomToolTextFormat();
        CompletionResponseFormatText textResponseFormat = new CompletionResponseFormatText();
        CompletionResponseFormatJsonSchema schemaFormat = jsonSchemaFormat();
        CompletionResponseFormatJsonObject objectResponseFormat = jsonObjectResponseFormat();
        CompletionNamedFunctionToolChoice namedFunction = namedFunctionChoice();
        CompletionNamedCustomToolChoice namedCustom =
                new CompletionNamedCustomToolChoice(new CompletionToolChoiceName("custom-runner"));
        CompletionToolChoiceAllowedTools allowedTools = allowedToolsChoice();

        CompletionMessageFunctionToolCall functionToolCallClone = functionToolCall.deepClone();
        CompletionMessageCustomToolCall customToolCallClone = customToolCall.deepClone();
        CompletionFunctionTool functionToolClone = functionTool.deepClone();
        CompletionCustomTool grammarToolClone = grammarTool.deepClone();
        CompletionCustomTool textToolClone = textTool.deepClone();
        CompletionCustomToolGrammarFormat grammarFormatClone = grammarFormat.deepClone();
        CompletionCustomToolTextFormat textFormatClone = textFormat.deepClone();
        CompletionResponseFormatText textResponseFormatClone = textResponseFormat.deepClone();
        CompletionResponseFormatJsonSchema schemaFormatClone = schemaFormat.deepClone();
        CompletionResponseFormatJsonObject objectResponseFormatClone = objectResponseFormat.deepClone();
        CompletionNamedFunctionToolChoice namedFunctionClone = namedFunction.deepClone();
        CompletionNamedCustomToolChoice namedCustomClone = namedCustom.deepClone();
        CompletionToolChoiceAllowedTools allowedToolsClone = allowedTools.deepClone();

        assertToolCallClone(functionToolCallClone, functionToolCall);
        assertToolCallClone(customToolCallClone, customToolCall);
        assertToolClone(functionToolClone, functionTool);
        assertToolClone(grammarToolClone, grammarTool);
        assertToolClone(textToolClone, textTool);
        assertCustomToolFormatClone(grammarFormatClone, grammarFormat);
        assertCustomToolFormatClone(textFormatClone, textFormat);
        assertResponseFormatClone(textResponseFormatClone, textResponseFormat);
        assertResponseFormatClone(schemaFormatClone, schemaFormat);
        assertResponseFormatClone(objectResponseFormatClone, objectResponseFormat);
        assertToolChoiceClone(namedFunctionClone, namedFunction);
        assertToolChoiceClone(namedCustomClone, namedCustom);
        assertToolChoiceClone(allowedToolsClone, allowedTools);
        assertSame(CompletionToolChoiceMode.AUTO, CompletionToolChoiceMode.AUTO.deepClone());

        functionToolCall.getFunction().setName("mutated");
        customToolCall.getCustom().setName("mutated");
        functionTool.getFunction().getParameters().put("mutated", true);
        grammarTool.getCustom().setName("mutated");
        textTool.getCustom().setName("mutated");
        grammarFormat.getGrammar().setDefinition("mutated");
        schemaFormat.getJsonSchema().getSchema().put("mutated", true);
        namedFunction.getFunction().setName("mutated");
        namedCustom.getCustom().setName("mutated");
        allowedTools.getAllowedTools().getTools().clear();

        assertAll(
                () -> assertEquals("lookup", functionToolCallClone.getFunction().getName()),
                () -> assertEquals("runner", customToolCallClone.getCustom().getName()),
                () -> assertFalse(functionToolClone.getFunction().getParameters().containsKey("mutated")),
                () -> assertEquals("grammar-runner", grammarToolClone.getCustom().getName()),
                () -> assertEquals("text-runner", textToolClone.getCustom().getName()),
                () -> assertEquals("start: /.+/", grammarFormatClone.getGrammar().getDefinition()),
                () -> assertFalse(schemaFormatClone.getJsonSchema().getSchema().containsKey("mutated")),
                () -> assertEquals("lookup", namedFunctionClone.getFunction().getName()),
                () -> assertEquals("custom-runner", namedCustomClone.getCustom().getName()),
                () -> assertEquals(2, allowedToolsClone.getAllowedTools().getTools().size())
        );
    }

    @Test
    void leafObjectsDeepCloneNullsAndValuesPrecisely() {
        CompletionRequest emptyRequestClone = new CompletionRequest().deepClone();
        CompletionResponse emptyResponseClone = new CompletionResponse().deepClone();
        CompletionAssistantMessage emptyAssistantClone = new CompletionAssistantMessage().deepClone();
        CompletionUsage emptyUsageClone = new CompletionUsage().deepClone();

        assertAll(
                () -> assertNull(emptyRequestClone.getMessages()),
                () -> assertNull(emptyRequestClone.getModalities()),
                () -> assertNull(emptyRequestClone.getResponseFormat()),
                () -> assertNull(emptyRequestClone.getToolChoice()),
                () -> assertNull(emptyRequestClone.getTools()),
                () -> assertNull(emptyResponseClone.getChoices()),
                () -> assertNull(emptyResponseClone.getUsage()),
                () -> assertNull(emptyAssistantClone.getToolCalls()),
                () -> assertNull(emptyAssistantClone.getAudio()),
                () -> assertNull(emptyUsageClone.getCompletionTokensDetails()),
                () -> assertNull(emptyUsageClone.getPromptTokensDetails())
        );

        assertAll(
                () -> assertEquals("audio-id", new CompletionAudio("audio-id").deepClone().getId()),
                () -> assertEquals(1, new CompletionTokensDetails(1, 2, 3, 4).deepClone().getAcceptedPredictionTokens()),
                () -> assertEquals(2, new PromptTokenDetails(1, 2).deepClone().getCachedTokens()),
                () -> assertEquals("tool-name", new CompletionToolChoiceName("tool-name").deepClone().getName())
        );
    }

    @Test
    void jsonObjectMapsAndListsAreRecursivelyCopiedWithoutSharingMutableContainers() {
        Map<String, Object> parameters = new LinkedHashMap<>();
        Map<String, Object> nestedMap = new LinkedHashMap<>();
        List<Object> nestedList = new ArrayList<>();
        Map<String, Object> mapInsideList = new LinkedHashMap<>();
        CompletionToolChoiceName modelObjectInsideList = new CompletionToolChoiceName("listed-tool");
        mapInsideList.put("kind", "inner");
        nestedList.add(mapInsideList);
        nestedList.add(modelObjectInsideList);
        nestedMap.put("items", nestedList);
        parameters.put("nested", nestedMap);
        parameters.put("modelObject", new CompletionAudio("audio-id"));

        CompletionFunctionDefinition original = new CompletionFunctionDefinition();
        original.setName("lookup");
        original.setParameters(parameters);

        CompletionFunctionDefinition clone = original.deepClone();

        assertNotSame(original.getParameters(), clone.getParameters());
        Map<?, ?> clonedNestedMap = assertInstanceOf(Map.class, clone.getParameters().get("nested"));
        List<?> clonedNestedList = assertInstanceOf(List.class, clonedNestedMap.get("items"));
        Map<?, ?> clonedMapInsideList = assertInstanceOf(Map.class, clonedNestedList.getFirst());
        CompletionToolChoiceName clonedModelObjectInsideList =
                assertInstanceOf(CompletionToolChoiceName.class, clonedNestedList.get(1));
        CompletionAudio clonedModelObjectInsideMap =
                assertInstanceOf(CompletionAudio.class, clone.getParameters().get("modelObject"));

        assertAll(
                () -> assertNotSame(nestedMap, clonedNestedMap),
                () -> assertNotSame(nestedList, clonedNestedList),
                () -> assertNotSame(mapInsideList, clonedMapInsideList),
                () -> assertNotSame(modelObjectInsideList, clonedModelObjectInsideList),
                () -> assertNotSame(parameters.get("modelObject"), clonedModelObjectInsideMap),
                () -> assertEquals("inner", clonedMapInsideList.get("kind")),
                () -> assertEquals("listed-tool", clonedModelObjectInsideList.getName()),
                () -> assertEquals("audio-id", clonedModelObjectInsideMap.getId())
        );

        mapInsideList.put("kind", "mutated");
        modelObjectInsideList.setName("mutated");
        ((CompletionAudio) parameters.get("modelObject")).setId("mutated");

        assertAll(
                () -> assertEquals("inner", clonedMapInsideList.get("kind")),
                () -> assertEquals("listed-tool", clonedModelObjectInsideList.getName()),
                () -> assertEquals("audio-id", clonedModelObjectInsideMap.getId())
        );
    }

    @Test
    void deepCloneUtilCallsDeepCloneForModelObjectsInsideObjectLists() {
        CompletionToolChoiceName toolChoiceName = new CompletionToolChoiceName("listed-tool");
        CompletionAudio audio = new CompletionAudio("audio-id");
        List<Object> original = new ArrayList<>(List.of(toolChoiceName, audio));

        List<Object> clone = DeepCloneUtil.deepCloneList(original);

        CompletionToolChoiceName clonedToolChoiceName =
                assertInstanceOf(CompletionToolChoiceName.class, clone.getFirst());
        CompletionAudio clonedAudio = assertInstanceOf(CompletionAudio.class, clone.get(1));

        assertAll(
                () -> assertNotSame(original, clone),
                () -> assertNotSame(toolChoiceName, clonedToolChoiceName),
                () -> assertNotSame(audio, clonedAudio),
                () -> assertEquals("listed-tool", clonedToolChoiceName.getName()),
                () -> assertEquals("audio-id", clonedAudio.getId())
        );

        toolChoiceName.setName("mutated");
        audio.setId("mutated");

        assertAll(
                () -> assertEquals("listed-tool", clonedToolChoiceName.getName()),
                () -> assertEquals("audio-id", clonedAudio.getId())
        );
    }

    private CompletionAssistantMessage assistantMessage() {
        CompletionAssistantMessage assistant = new CompletionAssistantMessage("assistant", "assistant-name");
        assistant.setRefusal("no");
        assistant.setAudio(new CompletionAudio("audio-id"));
        assistant.setToolCalls(new ArrayList<>(List.of(
                new CompletionMessageFunctionToolCall("call-fn", new CompletionMessageFunctionTool("{}", "lookup")),
                new CompletionMessageCustomToolCall("call-custom", new CompletionMessageCustomTool("input", "runner"))
        )));
        return assistant;
    }

    private CompletionFunctionTool functionTool() {
        CompletionFunctionDefinition definition = new CompletionFunctionDefinition();
        definition.setName("lookup");
        definition.setDescription("Lookup data");
        definition.setStrict(true);
        definition.setParameters(jsonObjectMap());
        return new CompletionFunctionTool(definition);
    }

    private CompletionCustomTool customGrammarTool() {
        CompletionCustomDefinition definition = new CompletionCustomDefinition();
        definition.setName("grammar-runner");
        definition.setDescription("Run grammar");
        definition.setFormat(grammarFormat());
        return new CompletionCustomTool(definition);
    }

    private CompletionCustomTool customTextTool() {
        CompletionCustomDefinition definition = new CompletionCustomDefinition();
        definition.setName("text-runner");
        definition.setDescription("Run text");
        definition.setFormat(new CompletionCustomToolTextFormat());
        return new CompletionCustomTool(definition);
    }

    private CompletionCustomToolGrammarFormat grammarFormat() {
        CompletionGrammarDefinition grammar = new CompletionGrammarDefinition();
        grammar.setDefinition("start: /.+/");
        grammar.setSyntax(CompletionGrammarSyntax.REGEX);
        return new CompletionCustomToolGrammarFormat(grammar);
    }

    private CompletionResponseFormatJsonSchema jsonSchemaFormat() {
        return new CompletionResponseFormatJsonSchema(
                new CompletionResponseFormatJsonSchemaDefinition(
                        "vehicle",
                        "Vehicle schema",
                        jsonObjectMap(),
                        true
                )
        );
    }

    private CompletionResponseFormatJsonObject jsonObjectResponseFormat() throws Exception {
        Constructor<CompletionResponseFormatJsonObject> constructor =
                CompletionResponseFormatJsonObject.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private Map<String, Object> jsonObjectMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> nested = new LinkedHashMap<>();
        nested.put("type", "string");
        map.put("type", "object");
        map.put("required", new ArrayList<>(List.of("id", "name")));
        map.put("properties", nested);
        return map;
    }

    private CompletionNamedFunctionToolChoice namedFunctionChoice() {
        return new CompletionNamedFunctionToolChoice(new CompletionToolChoiceName("lookup"));
    }

    private CompletionNamedFunctionToolChoice namedFunctionChoice(ToolChoiceOptionInterface toolChoice) {
        return assertInstanceOf(CompletionNamedFunctionToolChoice.class, toolChoice);
    }

    private CompletionToolChoiceAllowedTools allowedToolsChoice() {
        CompletionAllowedTools allowedTools = new CompletionAllowedTools();
        allowedTools.setMode(CompletionAllowedToolsMode.REQUIRED);
        allowedTools.setTools(new ArrayList<>(List.of(functionTool(), customGrammarTool())));
        return new CompletionToolChoiceAllowedTools(allowedTools);
    }

    private CompletionResponseFormatJsonSchema jsonSchema(CompletionResponseFormat responseFormat) {
        return assertInstanceOf(CompletionResponseFormatJsonSchema.class, responseFormat);
    }

    private CompletionUsage usage() {
        return new CompletionUsage(
                10,
                20,
                30,
                new CompletionTokensDetails(1, 2, 3, 4),
                new PromptTokenDetails(5, 6)
        );
    }

    private <T extends CompletionMessage> void assertPlainMessageClone(
            CompletionMessage clone,
            T original,
            Class<T> expectedType
    ) {
        T typedClone = assertInstanceOf(expectedType, clone);
        assertNotSame(original, typedClone);
        assertEquals(original.getContent(), typedClone.getContent());
        assertEquals(original.getRole(), typedClone.getRole());
        assertEquals(original.getName(), typedClone.getName());
    }

    private void assertToolCallClone(CompletionToolCall clone, CompletionToolCall original) {
        assertNotSame(original, clone);
        assertEquals(original.getId(), clone.getId());
        assertEquals(original.getType(), clone.getType());
        if (original instanceof CompletionMessageFunctionToolCall originalFunctionCall) {
            CompletionMessageFunctionToolCall cloneFunctionCall =
                    assertInstanceOf(CompletionMessageFunctionToolCall.class, clone);
            assertNotSame(originalFunctionCall.getFunction(), cloneFunctionCall.getFunction());
            assertEquals(originalFunctionCall.getFunction().getName(), cloneFunctionCall.getFunction().getName());
        }
        if (original instanceof CompletionMessageCustomToolCall originalCustomCall) {
            CompletionMessageCustomToolCall cloneCustomCall =
                    assertInstanceOf(CompletionMessageCustomToolCall.class, clone);
            assertNotSame(originalCustomCall.getCustom(), cloneCustomCall.getCustom());
            assertEquals(originalCustomCall.getCustom().getName(), cloneCustomCall.getCustom().getName());
        }
    }

    private void assertToolClone(CompletionTool clone, CompletionTool original) {
        assertNotSame(original, clone);
        assertEquals(original.getType(), clone.getType());
        if (original instanceof CompletionFunctionTool originalFunctionTool) {
            CompletionFunctionTool cloneFunctionTool = assertInstanceOf(CompletionFunctionTool.class, clone);
            assertNotSame(originalFunctionTool.getFunction(), cloneFunctionTool.getFunction());
            assertNotSame(
                    originalFunctionTool.getFunction().getParameters(),
                    cloneFunctionTool.getFunction().getParameters()
            );
            assertEquals(originalFunctionTool.getFunction().getName(), cloneFunctionTool.getFunction().getName());
        }
        if (original instanceof CompletionCustomTool originalCustomTool) {
            CompletionCustomTool cloneCustomTool = assertInstanceOf(CompletionCustomTool.class, clone);
            assertNotSame(originalCustomTool.getCustom(), cloneCustomTool.getCustom());
            assertEquals(originalCustomTool.getCustom().getName(), cloneCustomTool.getCustom().getName());
            assertNotSame(originalCustomTool.getCustom().getFormat(), cloneCustomTool.getCustom().getFormat());
        }
    }

    private void assertCustomToolFormatClone(
            CompletionCustomToolFormat clone,
            CompletionCustomToolFormat original
    ) {
        assertNotSame(original, clone);
        assertEquals(original.getType(), clone.getType());
        if (original instanceof CompletionCustomToolGrammarFormat originalGrammarFormat) {
            CompletionCustomToolGrammarFormat cloneGrammarFormat =
                    assertInstanceOf(CompletionCustomToolGrammarFormat.class, clone);
            assertNotSame(originalGrammarFormat.getGrammar(), cloneGrammarFormat.getGrammar());
            assertEquals(originalGrammarFormat.getGrammar().getDefinition(), cloneGrammarFormat.getGrammar().getDefinition());
        }
    }

    private void assertResponseFormatClone(CompletionResponseFormat clone, CompletionResponseFormat original) {
        assertNotSame(original, clone);
        assertEquals(original.getType(), clone.getType());
        if (original instanceof CompletionResponseFormatJsonSchema originalJsonSchema) {
            CompletionResponseFormatJsonSchema cloneJsonSchema =
                    assertInstanceOf(CompletionResponseFormatJsonSchema.class, clone);
            assertNotSame(originalJsonSchema.getJsonSchema(), cloneJsonSchema.getJsonSchema());
            assertNotSame(originalJsonSchema.getJsonSchema().getSchema(), cloneJsonSchema.getJsonSchema().getSchema());
            assertEquals(originalJsonSchema.getJsonSchema().getName(), cloneJsonSchema.getJsonSchema().getName());
        }
    }

    private void assertToolChoiceClone(
            ToolChoiceOptionInterface clone,
            ToolChoiceOptionInterface original
    ) {
        assertNotSame(original, clone);
        if (original instanceof CompletionNamedFunctionToolChoice originalNamedFunction) {
            CompletionNamedFunctionToolChoice cloneNamedFunction =
                    assertInstanceOf(CompletionNamedFunctionToolChoice.class, clone);
            assertNotSame(originalNamedFunction.getFunction(), cloneNamedFunction.getFunction());
            assertEquals(originalNamedFunction.getFunction().getName(), cloneNamedFunction.getFunction().getName());
        }
        if (original instanceof CompletionNamedCustomToolChoice originalNamedCustom) {
            CompletionNamedCustomToolChoice cloneNamedCustom =
                    assertInstanceOf(CompletionNamedCustomToolChoice.class, clone);
            assertNotSame(originalNamedCustom.getCustom(), cloneNamedCustom.getCustom());
            assertEquals(originalNamedCustom.getCustom().getName(), cloneNamedCustom.getCustom().getName());
        }
        if (original instanceof CompletionToolChoiceAllowedTools originalAllowedTools) {
            CompletionToolChoiceAllowedTools cloneAllowedTools =
                    assertInstanceOf(CompletionToolChoiceAllowedTools.class, clone);
            assertNotSame(originalAllowedTools.getAllowedTools(), cloneAllowedTools.getAllowedTools());
            assertDeepCopiedList(
                    originalAllowedTools.getAllowedTools().getTools(),
                    cloneAllowedTools.getAllowedTools().getTools()
            );
        }
    }

    private <T> void assertDeepCopiedList(List<T> original, List<T> clone) {
        assertNotSame(original, clone);
        assertEquals(original.size(), clone.size());
        for (int i = 0; i < original.size(); i++) {
            if (original.get(i) != null) {
                assertNotSame(original.get(i), clone.get(i));
            }
        }
    }
}
