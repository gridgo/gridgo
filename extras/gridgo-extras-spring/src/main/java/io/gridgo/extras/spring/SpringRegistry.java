package io.gridgo.extras.spring;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.gridgo.framework.support.Registry;

public class SpringRegistry implements Registry {

    private static final String DEFAULT_CONTEXT_FILE = "applicationContext.xml";

    private ConfigurableApplicationContext applicationContext;

    public SpringRegistry() {
        this(DEFAULT_CONTEXT_FILE);
    }

    public SpringRegistry(String... files) {
        this.applicationContext = new ClassPathXmlApplicationContext(files);
    }

    public SpringRegistry(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object lookup(String name) {
        try {
            return applicationContext.getBean(name);
        } catch (NoSuchBeanDefinitionException ex) {
            return null;
        }
    }

    @Override
    public Registry register(String name, Object answer) {
        var factory = applicationContext.getBeanFactory();
        factory.registerSingleton(name, answer);
        return this;
    }

    @Override
    public Object lookupByType(Class<?> type) {
        try {
            return applicationContext.getBean(type);
        } catch (NoSuchBeanDefinitionException ex) {
            return null;
        }
    }
}
