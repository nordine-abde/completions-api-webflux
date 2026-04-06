package com.anordine.completions.api.webflux.configuration;

import com.anordine.completions.api.webflux.client.ClientProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.Map;

public class CustomWebClientBeanFactoryPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware {

    private Environment environment;


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Map<String, WebClientProperties.CustomClient> customClients = Binder.get(environment)
                .bind(
                        "anordine.completions-api-webflux.custom",
                        Bindable.mapOf(String.class, WebClientProperties.CustomClient.class)
                )
                .orElseGet(LinkedHashMap::new);

        if (customClients.isEmpty()) {
            return;
        }

        WebClient.Builder builder = beanFactory.getBeanProvider(WebClient.Builder.class).getIfAvailable();
        for (Map.Entry<String, WebClientProperties.CustomClient> entry : customClients.entrySet()) {
            WebClientProperties.CustomClient config = entry.getValue();
            String beanName = toCamelCase(entry.getKey());
            if (config == null || !config.isAutoconfigure()) {
                continue;
            }
            if (builder == null) {
                throw new BeanCreationException("No WebClient.Builder bean available for custom WebClient '" + entry.getKey() + "'");
            }
            if (beanFactory.containsBean(beanName)) {
                throw new BeanCreationException("A bean named '" + beanName + "' already exists");
            }

            beanFactory.registerSingleton(
                    beanName,
                    ClientProvider.buildCompletionsWebClient(builder, config.getBaseUrl(), config.getSecretKey())
            );
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
