package com.anordine.simplifier.webflux.ai.util;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

public class InternalSpringUtils {

    private InternalSpringUtils() {

    }


    public static <T> Map<String, T> bindCustomPropertiesMap(Environment environment, String name,  Class<T> clazz) {
        Assert.state(environment != null, "Environment must be set before binding custom WebClient properties");
        return Binder.get(environment)
                .bind(
                        name,
                        Bindable.mapOf(String.class, clazz)
                )
                .orElseGet(LinkedHashMap::new);
    }

    public static void assertBeanNameAvailable(ConfigurableListableBeanFactory beanFactory, BeanDefinitionRegistry registry, String beanName) {
        Assert.state(beanFactory != null, "BeanFactory must be set before registering custom WebClient beans");
        if (registry.containsBeanDefinition(beanName) || registry.isAlias(beanName) || beanFactory.containsSingleton(beanName)) {
            throw new BeanCreationException("A bean named '" + beanName + "' already exists");
        }
    }

}
