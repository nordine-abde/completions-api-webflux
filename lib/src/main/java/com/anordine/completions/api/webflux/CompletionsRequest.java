package com.anordine.completions.api.webflux;

import com.anordine.completions.api.webflux.enums.modality.CompletionModality;
import com.anordine.completions.api.webflux.enums.resoning.CompletionReasoningEffort;
import com.anordine.completions.api.webflux.enums.verbosity.CompletionVerbosity;
import com.anordine.completions.api.webflux.format.abs.CompletionResponseFormat;
import com.anordine.completions.api.webflux.message.abs.CompletionMessage;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionsRequest {

    private List<CompletionMessage> messages;
    private String model;
    private Double frequencyPenalty;
    private Boolean logprobs;
    private Integer maxCompletionTokens;
    private List<CompletionModality> modalities;
    private Integer n;
    private Boolean parallelToolCalls;
    private Double presencePenality;
    private String promptCacheKey;
    private CompletionReasoningEffort reasoningEffort;
    private CompletionResponseFormat responseFormat;
    private String safetyIdentifier;
    private Boolean store;
    private Double temperature;
    //private CompletionTool tools;
    private Integer topLogProbs;
    private Double topP;
    private CompletionVerbosity verbosity;

}
