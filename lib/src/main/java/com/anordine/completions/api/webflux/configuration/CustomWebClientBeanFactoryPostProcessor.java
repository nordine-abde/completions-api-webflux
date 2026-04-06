package com.anordine.completions.api.webflux.configuration;

import com.anordine.completions.api.webflux.client.ClientProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CustomWebClientBeanFactoryPostProcessor
        implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, BeanFactoryAware {

    private Environment environment;
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory configurableBeanFactory)) {
            throw new IllegalArgumentException("Custom WebClient registration requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = configurableBeanFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        for (Map.Entry<String, WebClientProperties.CustomClient> entry : bindCustomClients().entrySet()) {
            WebClientProperties.CustomClient config = entry.getValue();
            if (config != null && config.isAutoconfigure()) {
                registerCustomWebClient(registry, entry.getKey(), config);
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //No need for it, but required by interface
    }

    private Map<String, WebClientProperties.CustomClient> bindCustomClients() {
        Assert.state(environment != null, "Environment must be set before binding custom WebClient properties");
        return Binder.get(environment)
                .bind(
                        "anordine.completions-api-webflux.custom",
                        Bindable.mapOf(String.class, WebClientProperties.CustomClient.class)
                )
                .orElseGet(LinkedHashMap::new);
    }

    private void registerCustomWebClient(BeanDefinitionRegistry registry,
                                         String propertyName,
                                         WebClientProperties.CustomClient config) {
        String beanName = toCamelCase(propertyName);
        assertBeanNameAvailable(registry, beanName);

        RootBeanDefinition beanDefinition = new RootBeanDefinition(WebClient.class);
        beanDefinition.setInstanceSupplier(() -> createCustomWebClient(propertyName, config));
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private void assertBeanNameAvailable(BeanDefinitionRegistry registry, String beanName) {
        Assert.state(beanFactory != null, "BeanFactory must be set before registering custom WebClient beans");
        if (registry.containsBeanDefinition(beanName) || registry.isAlias(beanName) || beanFactory.containsSingleton(beanName)) {
            throw new BeanCreationException("A bean named '" + beanName + "' already exists");
        }
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

    private String toCamelCase(String value) {
        StringBuilder beanName = new StringBuilder();
        boolean uppercaseNext = false;

        for (char current : value.toCharArray()) {
            if (!Character.isLetterOrDigit(current)) {
                uppercaseNext = !beanName.isEmpty();
            } else {
                if (beanName.isEmpty()) {
                    beanName.append(Character.toLowerCase(current));
                    uppercaseNext = false;
                } else if (uppercaseNext) {
                    beanName.append(Character.toUpperCase(current));
                    uppercaseNext = false;
                } else {
                    beanName.append(current);
                }
            }
        }

        if (beanName.isEmpty()) {
            throw new BeanCreationException("Custom WebClient name '" + value + "' does not produce a valid bean name");
        }

        return beanName.toString();
    }
}
