package com.anordine.completions.api.webflux.model;

import com.anordine.completions.api.webflux.model.usage.CompletionUsage;
import com.anordine.completions.api.webflux.util.DeepClonable;
import com.anordine.completions.api.webflux.util.DeepCloneUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionStreamResponse implements DeepClonable<CompletionStreamResponse> {

    private String id;
    private Long created;
    private String model;
    private String object;
    private String serviceTier;
    private String systemFingerprint;
    private List<CompletionStreamChoice> choices;
    private CompletionUsage usage;
    private String obfuscation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getServiceTier() {
        return serviceTier;
    }

    public void setServiceTier(String serviceTier) {
        this.serviceTier = serviceTier;
    }

    public String getSystemFingerprint() {
        return systemFingerprint;
    }

    public void setSystemFingerprint(String systemFingerprint) {
        this.systemFingerprint = systemFingerprint;
    }

    public List<CompletionStreamChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<CompletionStreamChoice> choices) {
        this.choices = choices;
    }

    public CompletionUsage getUsage() {
        return usage;
    }

    public void setUsage(CompletionUsage usage) {
        this.usage = usage;
    }

    public String getObfuscation() {
        return obfuscation;
    }

    public void setObfuscation(String obfuscation) {
        this.obfuscation = obfuscation;
    }

    @Override
    public CompletionStreamResponse deepClone() {
        CompletionStreamResponse clone = new CompletionStreamResponse();
        clone.setId(this.id);
        clone.setCreated(this.created);
        clone.setModel(this.model);
        clone.setObject(this.object);
        clone.setServiceTier(this.serviceTier);
        clone.setSystemFingerprint(this.systemFingerprint);
        clone.setChoices(DeepCloneUtil.deepCloneList(this.choices));
        clone.setUsage(this.usage == null ? null : this.usage.deepClone());
        clone.setObfuscation(this.obfuscation);
        return clone;
    }
}
