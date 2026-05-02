package com.anordine.completions.api.webflux.configuration;

import com.anordine.completions.api.webflux.helper.tool.CompletionToolRegistry;
import com.anordine.completions.api.webflux.helper.tool.CompletionToolSchemaGenerator;
import com.anordine.completions.api.webflux.helper.tool.ToolExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CompletionToolConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CompletionToolSchemaGenerator completionToolSchemaGenerator() {
        return new CompletionToolSchemaGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public CompletionToolRegistry completionToolRegistry(ApplicationContext applicationContext,
                                                         CompletionToolSchemaGenerator schemaGenerator) {
        return new CompletionToolRegistry(applicationContext, schemaGenerator);
    }

    @Bean
    @ConditionalOnMissingBean
    public ToolExecutor toolExecutor(CompletionToolRegistry toolRegistry) {
        return new ToolExecutor(toolRegistry);
    }
}
