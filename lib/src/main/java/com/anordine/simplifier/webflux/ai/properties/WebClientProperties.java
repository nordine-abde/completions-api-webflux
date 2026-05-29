package com.anordine.simplifier.webflux.ai.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "com.anordine.simplifier.webflux.ai")
public class WebClientProperties {

    private Map<String, CustomClient> custom = new LinkedHashMap<>();

    public Map<String, CustomClient> getCustom() {
        return custom;
    }

    public void setCustom(Map<String, CustomClient> custom) {
        this.custom = custom != null ? new LinkedHashMap<>(custom) : new LinkedHashMap<>();
    }

    public static class CustomClient {

        private boolean autoconfigure;
        private String baseUrl;
        private String secretKey;

        public boolean isAutoconfigure() {
            return autoconfigure;
        }

        public void setAutoconfigure(boolean autoconfigure) {
            this.autoconfigure = autoconfigure;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }
}
