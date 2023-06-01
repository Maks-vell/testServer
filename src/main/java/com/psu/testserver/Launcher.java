package com.psu.testserver;

import com.psu.testserver.javafx.JavaFxApplication;
import com.psu.testserver.server.ServerApplication;

public class Launcher {
    public static void main(String[] args) {
        Thread serverThread = new Thread(() -> ServerApplication.main(args));
        serverThread.start();

        Thread javaFxThread = new Thread(() -> JavaFxApplication.main(args));
        javaFxThread.start();
    }
}
