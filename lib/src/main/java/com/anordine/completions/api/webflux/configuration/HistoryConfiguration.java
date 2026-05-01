package com.anordine.completions.api.webflux.configuration;

import com.anordine.completions.api.webflux.helper.history.IHistoryManager;
import com.anordine.completions.api.webflux.helper.history.InMemoryHistoryManager;
import com.anordine.completions.api.webflux.helper.history.InRedisHistoryManager;
import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.message.abs.CompletionMessage;
import com.anordine.completions.api.webflux.properties.HistoryProperties;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HistoryProperties.class)
public class HistoryConfiguration {

    public static final String HISTORY_REDIS_CONNECTION_FACTORY_BEAN_NAME = "completionHistoryRedisConnectionFactory";
    public static final String COMPLETION_REQUEST_REDIS_TEMPLATE_BEAN_NAME = "completionRequestReactiveRedisTemplate";
    public static final String COMPLETION_MESSAGE_REDIS_TEMPLATE_BEAN_NAME = "completionMessageReactiveRedisTemplate";

    private static final String HISTORY_PREFIX = "anordine.completions-api-webflux.history";
    private static final String HISTORY_REDIS_PREFIX = HISTORY_PREFIX + ".redis";

    @Bean
    @ConditionalOnMissingBean(IHistoryManager.class)
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "autoconfigure", havingValue = "true")
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "mode", havingValue = "memory", matchIfMissing = true)
    public IHistoryManager inMemoryHistoryManager() {
        return new InMemoryHistoryManager();
    }

    @Bean
    @ConditionalOnMissingBean(IHistoryManager.class)
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "autoconfigure", havingValue = "true")
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "mode", havingValue = "redis")
    public IHistoryManager inRedisHistoryManager(
            HistoryProperties properties,
            @Qualifier(COMPLETION_REQUEST_REDIS_TEMPLATE_BEAN_NAME)
            ReactiveRedisTemplate<@NonNull String, @NonNull CompletionRequest> requestRedis,
            @Qualifier(COMPLETION_MESSAGE_REDIS_TEMPLATE_BEAN_NAME)
            ReactiveRedisTemplate<@NonNull String, @NonNull CompletionMessage> messageRedis
    ) {
        return new InRedisHistoryManager(
                normalizeKeyPrefix(properties.getRedis().getPrefix()),
                requestRedis,
                messageRedis
        );
    }

    @Bean(HISTORY_REDIS_CONNECTION_FACTORY_BEAN_NAME)
    @ConditionalOnMissingBean(name = HISTORY_REDIS_CONNECTION_FACTORY_BEAN_NAME)
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "autoconfigure", havingValue = "true")
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "mode", havingValue = "redis")
    @ConditionalOnProperty(
            prefix = HISTORY_REDIS_PREFIX,
            name = "autoconfigure-serializers",
            havingValue = "true",
            matchIfMissing = true
    )
    public ReactiveRedisConnectionFactory completionHistoryRedisConnectionFactory(HistoryProperties properties) {
        return new LettuceConnectionFactory(
                properties.getRedis().getHost(),
                properties.getRedis().getPort()
        );
    }

    @Bean(COMPLETION_REQUEST_REDIS_TEMPLATE_BEAN_NAME)
    @ConditionalOnMissingBean(name = COMPLETION_REQUEST_REDIS_TEMPLATE_BEAN_NAME)
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "autoconfigure", havingValue = "true")
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "mode", havingValue = "redis")
    @ConditionalOnProperty(
            prefix = HISTORY_REDIS_PREFIX,
            name = "autoconfigure-serializers",
            havingValue = "true",
            matchIfMissing = true
    )
    public ReactiveRedisTemplate<String, CompletionRequest> completionRequestReactiveRedisTemplate(
            @Qualifier(HISTORY_REDIS_CONNECTION_FACTORY_BEAN_NAME)
            ReactiveRedisConnectionFactory connectionFactory
    ) {
        return redisTemplate(connectionFactory, CompletionRequest.class);
    }

    @Bean(COMPLETION_MESSAGE_REDIS_TEMPLATE_BEAN_NAME)
    @ConditionalOnMissingBean(name = COMPLETION_MESSAGE_REDIS_TEMPLATE_BEAN_NAME)
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "autoconfigure", havingValue = "true")
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "mode", havingValue = "redis")
    @ConditionalOnProperty(
            prefix = HISTORY_REDIS_PREFIX,
            name = "autoconfigure-serializers",
            havingValue = "true",
            matchIfMissing = true
    )
    public ReactiveRedisTemplate<String, CompletionMessage> completionMessageReactiveRedisTemplate(
            @Qualifier(HISTORY_REDIS_CONNECTION_FACTORY_BEAN_NAME)
            ReactiveRedisConnectionFactory connectionFactory
    ) {
        return redisTemplate(connectionFactory, CompletionMessage.class);
    }

    private <T> ReactiveRedisTemplate<String, T> redisTemplate(
            ReactiveRedisConnectionFactory connectionFactory,
            Class<T> valueType
    ) {
        RedisSerializationContext<String, T> context = RedisSerializationContext
                .<String, T>newSerializationContext(RedisSerializer.string())
                .value(new JacksonJsonRedisSerializer<>(valueType))
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    private String normalizeKeyPrefix(String keyPrefix) {
        if (keyPrefix == null || keyPrefix.isBlank()) {
            return "";
        }
        return keyPrefix.endsWith(":") ? keyPrefix : keyPrefix + ":";
    }
}
