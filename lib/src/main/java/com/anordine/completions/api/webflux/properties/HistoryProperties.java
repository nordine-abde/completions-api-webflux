package com.anordine.completions.api.webflux.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "anordine.completions-api-webflux.history")
public class HistoryProperties {

    private boolean autoconfigure;
    private Mode mode = Mode.MEMORY;
    private Redis redis = new Redis();

    public boolean isAutoconfigure() {
        return autoconfigure;
    }

    public void setAutoconfigure(boolean autoconfigure) {
        this.autoconfigure = autoconfigure;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode != null ? mode : Mode.MEMORY;
    }

    public Redis getRedis() {
        return redis;
    }

    public void setRedis(Redis redis) {
        this.redis = redis != null ? redis : new Redis();
    }

    public enum Mode {
        MEMORY,
        REDIS
    }

    public static class Redis {

        private String host = "127.0.0.1";
        private int port = 6379;
        private String prefix = "completions-api-webflux:history";
        private boolean autoconfigureSerializers = true;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host != null ? host : "127.0.0.1";
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix != null ? prefix : "completions-api-webflux:history";
        }

        public String getKeyPrefix() {
            return prefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            setPrefix(keyPrefix);
        }

        public boolean isAutoconfigureSerializers() {
            return autoconfigureSerializers;
        }

        public void setAutoconfigureSerializers(boolean autoconfigureSerializers) {
            this.autoconfigureSerializers = autoconfigureSerializers;
        }
    }
}
