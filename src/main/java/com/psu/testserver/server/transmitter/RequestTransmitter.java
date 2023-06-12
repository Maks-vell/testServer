package com.psu.testserver.server.transmitter;

import com.psu.testserver.server.annotation.Inject;
import com.psu.testserver.server.annotation.Request;
import com.psu.testserver.server.context.ApplicationContext;
import com.psu.testserver.lib.RESTParser;
import com.psu.testserver.server.service.Service;
import com.psu.testserver.server.service.ServiceMap;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class RequestTransmitter {
    private static final Logger log = Logger.getLogger(RequestTransmitter.class);
    @Inject
    private ServiceMap serviceMap;
    @Setter
    private ApplicationContext context;

    public void request(String request, int id) {
        log.info("Request from " + -id + ": " + request);

        try {
            tryRequest(request, id);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private void tryRequest(String request, int id) throws Exception {
        Class serviceClass = this.serviceMap.getService(RESTParser.getServicePath(request));
        Service service = (Service) this.context.getBean(serviceClass);

        for (Method method : Arrays.stream(serviceClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Request.class)).toList()) {

            method.setAccessible(true);
            method.canAccess(service);

            Request requestAnnotation = method.getAnnotation(Request.class);

            if (Objects.equals(requestAnnotation.path(), RESTParser.getMethodPath(request))) {
                method.canAccess(service);
                method.invoke(service, request, id);
            }
        }
    }
}
