package com.anordine.simplifier.webflux.ai.model.toolchoice.abs;

import com.anordine.simplifier.webflux.ai.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ToolChoiceOptionInterface extends DeepClonable<ToolChoiceOptionInterface> {
}
