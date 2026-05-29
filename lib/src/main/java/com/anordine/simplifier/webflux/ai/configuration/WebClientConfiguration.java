package com.anordine.simplifier.webflux.ai.configuration;

import com.anordine.simplifier.webflux.ai.client.ClientProvider;
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
            prefix = "com.anordine.simplifier.webflux.ai.open-ai",
            name = "autoconfigure",
            havingValue = "true"
    )
    public WebClient openAiWebClient(WebClient.Builder builder,
                                     @Value("${com.anordine.simplifier.webflux.ai.open-ai.secret-key}") String secretKey) {
        return ClientProvider.buildOpenAiWebClient(builder, secretKey);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "com.anordine.simplifier.webflux.ai.groq",
            name = "autoconfigure",
            havingValue = "true"
    )
    public WebClient groqWebClient(WebClient.Builder builder,
                                   @Value("${com.anordine.simplifier.webflux.ai.groq.secret-key}") String secretKey) {
        return ClientProvider.buildGroqWebClient(builder, secretKey);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "com.anordine.simplifier.webflux.ai.gemini",
            name = "autoconfigure",
            havingValue = "true"
    )
    public WebClient geminiWebClient(WebClient.Builder builder,
                                     @Value("${com.anordine.simplifier.webflux.ai.gemini.secret-key}") String secretKey) {
        return ClientProvider.buildGeminiWebClient(builder, secretKey);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "com.anordine.simplifier.webflux.ai.open-router",
            name = "autoconfigure",
            havingValue = "true"
    )
    public WebClient openRouterWebClient(WebClient.Builder builder,
                                         @Value("${com.anordine.simplifier.webflux.ai.open-router.secret-key}") String secretKey) {
        return ClientProvider.buildOpenRouterWebClient(builder, secretKey);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "com.anordine.simplifier.webflux.ai.claude",
            name = "autoconfigure",
            havingValue = "true"
    )
    public WebClient claudeWebClient(WebClient.Builder builder,
                                     @Value("${com.anordine.simplifier.webflux.ai.claude.secret-key}") String secretKey) {
        return ClientProvider.buildClaudeWebClient(builder, secretKey);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "com.anordine.simplifier.webflux.ai.deepseek",
            name = "autoconfigure",
            havingValue = "true"
    )
    public WebClient deepSeekWebClient(WebClient.Builder builder,
                                       @Value("${com.anordine.simplifier.webflux.ai.deepseek.secret-key}") String secretKey) {
        return ClientProvider.buildDeepSeekWebClient(builder, secretKey);
    }

    @Bean
    public static BeanDefinitionRegistryPostProcessor customWebClientBeans() {
        return new CustomWebClientBeanFactoryPostProcessor();
    }
}
