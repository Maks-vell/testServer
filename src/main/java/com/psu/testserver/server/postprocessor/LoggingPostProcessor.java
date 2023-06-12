package com.psu.testserver.server.postprocessor;

import org.apache.log4j.Logger;

public class LoggingPostProcessor implements PostProcessor {
    private static final Logger log = Logger.getLogger(LoggingPostProcessor.class);

    @Override
    public void process(Object bean) {
        log.info("Bean has been created:" + bean.getClass());
    }
}
