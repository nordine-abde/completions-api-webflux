package com.anordine.completions.api.webflux.configuration;

import com.anordine.completions.api.webflux.helper.history.IHistoryManager;
import com.anordine.completions.api.webflux.helper.history.InMemoryHistoryManager;
import com.anordine.completions.api.webflux.helper.history.InRedisHistoryManager;
import com.anordine.completions.api.webflux.model.CompletionRequest;
import com.anordine.completions.api.webflux.model.message.abs.CompletionMessage;
import com.anordine.completions.api.webflux.properties.HistoryProperties;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HistoryProperties.class)
public class HistoryConfiguration {

    public static final String COMPLETION_REQUEST_REDIS_TEMPLATE_BEAN_NAME = "completionRequestReactiveRedisTemplate";
    public static final String COMPLETION_MESSAGE_REDIS_TEMPLATE_BEAN_NAME = "completionMessageReactiveRedisTemplate";

    private static final String HISTORY_PREFIX = "anordine.completions-api-webflux.history";

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
            ReactiveRedisTemplate<@NonNull String, @NonNull CompletionRequest> requestRedis,
            ReactiveRedisTemplate<@NonNull String, @NonNull CompletionMessage> messageRedis
    ) {
        return new InRedisHistoryManager(
                normalizeKeyPrefix(properties.getRedis().getPrefix()),
                requestRedis,
                messageRedis
        );
    }

    @Bean(COMPLETION_REQUEST_REDIS_TEMPLATE_BEAN_NAME)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "autoconfigure", havingValue = "true")
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "mode", havingValue = "redis")
    public ReactiveRedisTemplate<@NonNull String, @NonNull CompletionRequest> completionRequestReactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory
    ) {
        return redisTemplate(connectionFactory, CompletionRequest.class);
    }

    @Bean(COMPLETION_MESSAGE_REDIS_TEMPLATE_BEAN_NAME)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "autoconfigure", havingValue = "true")
    @ConditionalOnProperty(prefix = HISTORY_PREFIX, name = "mode", havingValue = "redis")
    public ReactiveRedisTemplate<@NonNull String, @NonNull CompletionMessage> completionMessageReactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory
    ) {
        return redisTemplate(connectionFactory, CompletionMessage.class);
    }

    private <T> ReactiveRedisTemplate<@NonNull String, @NonNull T> redisTemplate(
            ReactiveRedisConnectionFactory connectionFactory,
            Class<T> valueType
    ) {
        RedisSerializationContext<@NonNull String, @NonNull T> context = RedisSerializationContext
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
