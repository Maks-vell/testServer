package com.psu.testserver.server.config;

public class JavaConfiguration implements Configuration {
    @Override
    public String getPackageToScan() {
        return "com.psu.testserver.server";
    }
}
