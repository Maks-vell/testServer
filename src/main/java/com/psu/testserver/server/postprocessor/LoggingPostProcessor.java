package com.psu.testserver.server.postprocessor;

public class LoggingPostProcessor implements PostProcessor {
    @Override
    public void process(Object bean) {
        System.out.printf("Bean has been created: %s%n", bean.getClass());
    }
}
