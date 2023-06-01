package com.psu.testserver.server.postprocessor;

import com.psu.testserver.server.annotation.PostConstruct;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

public class BeanConstructPostProcessor implements PostProcessor {
    @Override
    @SneakyThrows
    public void process(Object bean) {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.invoke(bean);
            }
        }
    }
}
