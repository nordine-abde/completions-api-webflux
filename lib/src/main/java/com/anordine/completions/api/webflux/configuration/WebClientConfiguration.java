package com.anordine.completions.api.webflux.configuration;

import com.anordine.completions.api.webflux.client.ClientProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
public class WebClientConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public WebClient.Builder builder() {
        return WebClient.builder();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "anordine.completions-api-webflux.open-ai",
            name = "autoconfigure",
            havingValue = "true"
    )
    public WebClient openAiWebClient(WebClient.Builder builder,
                                     @Value("${anordine.completions-api-webflux.open-ai.secret-key}") String secretKey) {
        return ClientProvider.buildOpenAiWebClient(builder, secretKey);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "anordine.completions-api-webflux.groq",
            name = "autoconfigure",
            havingValue = "true"
    )
    public WebClient groqWebClient(WebClient.Builder builder,
                                   @Value("${anordine.completions-api-webflux.groq.secret-key}") String secretKey) {
        return ClientProvider.buildGroqWebClient(builder, secretKey);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "anordine.completions-api-webflux.gemini",
            name = "autoconfigure",
            havingValue = "true"
    )
    public WebClient geminiWebClient(WebClient.Builder builder,
                                     @Value("${anordine.completions-api-webflux.gemini.secret-key}") String secretKey) {
        return ClientProvider.buildGeminiWebClient(builder, secretKey);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "anordine.completions-api-webflux.open-router",
            name = "autoconfigure",
            havingValue = "true"
    )
    public WebClient openRouterWebClient(WebClient.Builder builder,
                                         @Value("${anordine.completions-api-webflux.open-router.secret-key}") String secretKey) {
        return ClientProvider.buildOpenRouterWebClient(builder, secretKey);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "anordine.completions-api-webflux.claude",
            name = "autoconfigure",
            havingValue = "true"
    )
    public WebClient claudeWebClient(WebClient.Builder builder,
                                     @Value("${anordine.completions-api-webflux.claude.secret-key}") String secretKey) {
        return ClientProvider.buildClaudeWebClient(builder, secretKey);
    }

    @Bean
    public static BeanDefinitionRegistryPostProcessor customWebClientBeans() {
        return new CustomWebClientBeanFactoryPostProcessor();
    }
}
