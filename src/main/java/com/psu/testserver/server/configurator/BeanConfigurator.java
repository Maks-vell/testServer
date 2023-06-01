package com.psu.testserver.server.configurator;

import org.reflections.Reflections;

public interface BeanConfigurator {
    Reflections getScanner();

    <T> Class<? extends T> getImplementationClass(Class<T> interfaceClass);
}
