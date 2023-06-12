package com.psu.testserver.server.context;

import com.psu.testserver.server.factory.BeanFactory;
import com.psu.testserver.server.postprocessor.PostProcessor;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    private static final Logger log = Logger.getLogger(ApplicationContext.class);
    @Setter
    private BeanFactory beanFactory;
    private final Map<Class, Object> beanMap = new ConcurrentHashMap<>();

    public ApplicationContext() {
    }

    public <T> T getBean(Class<T> tClass) {
        if (beanMap.containsKey(tClass)) {
            return (T) beanMap.get(tClass);
        }

        T bean = beanFactory.getBean(tClass);
        beanMap.put(tClass, bean);

        callPostProcessors(bean);

        return bean;
    }

    private void callPostProcessors(Object bean) {
        try {
            tryCallPostProcessors(bean);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private void tryCallPostProcessors(Object bean)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        for (Class processor : beanFactory.getBeanConfigurator().getScanner().getSubTypesOf(PostProcessor.class)) {
            PostProcessor postprocessor = (PostProcessor) processor.getDeclaredConstructor().newInstance();
            postprocessor.process(bean);
        }
    }
}
