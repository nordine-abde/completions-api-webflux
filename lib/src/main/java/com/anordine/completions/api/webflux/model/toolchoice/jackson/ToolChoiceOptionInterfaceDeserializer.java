package com.anordine.completions.api.webflux.model.toolchoice.jackson;

import com.anordine.completions.api.webflux.model.enums.toolchoice.CompletionToolChoiceMode;
import com.anordine.completions.api.webflux.model.toolchoice.abs.CompletionToolChoiceOption;
import com.anordine.completions.api.webflux.model.toolchoice.abs.ToolChoiceOptionInterface;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolChoiceOptionInterfaceDeserializer extends StdDeserializer<ToolChoiceOptionInterface> {


    public ToolChoiceOptionInterfaceDeserializer() {
        super(ToolChoiceOptionInterface.class);
    }

    @Override
    public ToolChoiceOptionInterface deserialize(JsonParser parser,
                                                 DeserializationContext context) throws JacksonException {
        JsonNode node = parser.readValueAsTree();
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isString()) {
            return CompletionToolChoiceMode.fromValue(node.asString());
        } else {
            return context.readTreeAsValue(node, CompletionToolChoiceOption.class);
        }
    }
}
