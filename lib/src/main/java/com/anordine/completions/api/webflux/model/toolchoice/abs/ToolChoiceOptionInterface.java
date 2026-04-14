package com.anordine.completions.api.webflux.model.toolchoice.abs;

import com.anordine.completions.api.webflux.util.DeepClonable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface ToolChoiceOptionInterface extends DeepClonable<ToolChoiceOptionInterface> {
}
