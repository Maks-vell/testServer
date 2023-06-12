package com.psu.testserver;

import com.psu.testserver.javafx.JavaFxApplication;
import com.psu.testserver.server.ServerApplication;

import org.apache.log4j.Logger;

public class Launcher {
    private static final Logger log = Logger.getLogger(Launcher.class);

    public static void main(String[] args) {
        Thread serverThread = new Thread(() -> ServerApplication.main(args));
        serverThread.start();
        log.info("Server started");

        Thread javaFxThread = new Thread(() -> JavaFxApplication.main(args));
        javaFxThread.start();
        log.info("JavaFX started");
    }
}
