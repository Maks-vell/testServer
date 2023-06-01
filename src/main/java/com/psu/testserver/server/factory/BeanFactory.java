package com.psu.testserver.server.factory;

import com.psu.testserver.server.config.Configuration;
import com.psu.testserver.server.config.JavaConfiguration;
import com.psu.testserver.server.configurator.BeanConfigurator;
import com.psu.testserver.server.configurator.JavaBeanConfigurator;
import com.psu.testserver.server.context.ApplicationContext;
import com.psu.testserver.server.annotation.Inject;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class BeanFactory {
    private final Configuration configuration;
    private final ApplicationContext applicationContext;

    @Getter
    private final BeanConfigurator beanConfigurator;

    public BeanFactory(ApplicationContext applicationContext) {
        this.configuration = new JavaConfiguration();
        this.beanConfigurator = new JavaBeanConfigurator(this.configuration.getPackageToScan());
        this.applicationContext = applicationContext;
    }

    public <T> T getBean(Class<T> tClass) {
        Class<? extends T> implementationClass = tClass;

        if (implementationClass.isInterface()) {
            implementationClass = this.beanConfigurator.getImplementationClass(implementationClass);
        }

        T bean = null;
        try {
            bean = tryBeanInitialize(implementationClass);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return bean;
    }

    private <T> T tryBeanInitialize(Class<? extends T> implementationClass)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        T bean = implementationClass.getDeclaredConstructor().newInstance();

        for (Field field : Arrays.stream(implementationClass.getDeclaredFields()).
                filter(field -> field.isAnnotationPresent(Inject.class)).toList()) {
            field.setAccessible(true);
            field.canAccess(bean);
            field.set(bean, applicationContext.getBean(field.getType()));
        }
        return bean;
    }
}
