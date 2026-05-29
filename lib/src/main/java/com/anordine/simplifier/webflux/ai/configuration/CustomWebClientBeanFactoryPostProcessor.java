package com.anordine.simplifier.webflux.ai.configuration;

import com.anordine.simplifier.webflux.ai.client.ClientProvider;
import com.anordine.simplifier.webflux.ai.properties.WebClientProperties;
import com.anordine.simplifier.webflux.ai.util.InternalSpringUtils;
import com.anordine.simplifier.webflux.ai.util.InternalStringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * Registers custom WebClient bean definitions early in the Spring startup lifecycle.
 *
 * <p>This class runs as a {@link BeanDefinitionRegistryPostProcessor}, before regular
 * application beans and {@code @ConfigurationProperties} beans are created. Because of
 * that, it cannot rely on normal dependency injection for {@link WebClientProperties}.
 * Instead, it reads the raw {@link Environment} and binds only the custom WebClient
 * properties it needs in order to decide which bean definitions to register.</p>
 */


public final class CustomWebClientBeanFactoryPostProcessor
        implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, BeanFactoryAware {

    private Environment environment;
    private ConfigurableListableBeanFactory beanFactory;

    //called by spring on startup
    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    //called by spring on startup
    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory configurableBeanFactory)) {
            throw new IllegalArgumentException("Custom WebClient registration requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = configurableBeanFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        for (Map.Entry<String, WebClientProperties.CustomClient> entry : bindCustomClientsProperties().entrySet()) {
            WebClientProperties.CustomClient config = entry.getValue();
            if (config != null && config.isAutoconfigure()) {
                registerCustomWebClient(registry, entry.getKey(), config);
            }
        }
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //No need for it, but required by interface
    }

    /**
     * Binds custom WebClient properties directly from the {@link Environment}.
     *
     * <p>This method intentionally does not use an autowired {@link WebClientProperties}
     * bean because this post-processor runs before normal beans are instantiated. Manual
     * binding allows the custom client configuration to be read early enough to register
     * WebClient bean definitions dynamically.</p>
     */

    private Map<String, WebClientProperties.CustomClient> bindCustomClientsProperties() {
        return InternalSpringUtils.bindCustomPropertiesMap(environment, "com.anordine.simplifier.webflux.ai.custom", WebClientProperties.CustomClient.class);
    }

    private void registerCustomWebClient(BeanDefinitionRegistry registry,
                                         String propertyName,
                                         WebClientProperties.CustomClient config) {
        String beanName = toCamelCaseAndCheckValidity(propertyName);
        InternalSpringUtils.assertBeanNameAvailable(beanFactory, registry, beanName);

        RootBeanDefinition beanDefinition = new RootBeanDefinition(WebClient.class);
        beanDefinition.setInstanceSupplier(() -> createCustomWebClient(propertyName, config));
        registry.registerBeanDefinition(beanName, beanDefinition);
    }


    private WebClient createCustomWebClient(String propertyName, WebClientProperties.CustomClient config) {
        Assert.state(beanFactory != null, "BeanFactory must be set before creating custom WebClient beans");
        try {
            return ClientProvider.buildCompletionsWebClient(
                    beanFactory.getBean(WebClient.Builder.class),
                    config.getBaseUrl(),
                    config.getSecretKey()
            );
        } catch (BeansException exception) {
            throw new BeanCreationException("Failed to create custom WebClient '" + propertyName + "'", exception);
        }
    }

    private String toCamelCaseAndCheckValidity(String value) {
        String beanName = InternalStringUtils.toCamelCase(value);
        if (beanName.isEmpty()) {
            throw new BeanCreationException("Custom WebClient name '" + value + "' does not produce a valid bean name");
        }
        return beanName;
    }
}
