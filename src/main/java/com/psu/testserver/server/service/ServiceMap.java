package com.psu.testserver.server.service;

import java.util.Map;

public class ServiceMap {
    private final Map<String, Class> serviceMap;

    public ServiceMap() {
        this.serviceMap = Map.of(
                "testService", TestService.class
        );
    }

    public Class getService(String path) throws Exception {
        if (!serviceMap.containsKey(path)) {
            throw new Exception("BAD GATEWAY");
        }
        return serviceMap.get(path);
    }
}
