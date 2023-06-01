package com.psu.testserver.server;

import com.psu.testserver.server.context.ApplicationContext;
import com.psu.testserver.server.factory.BeanFactory;

import java.io.IOException;

public class ServerApplication {
    public static void main(String[] args) {
        ServerApplication serverApplication = new ServerApplication();

        serverApplication.run();
    }

    public void run() {
        ApplicationContext applicationContext = new ApplicationContext();
        BeanFactory beanFactory = new BeanFactory(applicationContext);
        applicationContext.setBeanFactory(beanFactory);

        serverInitialize(applicationContext);
    }

    private static void serverInitialize(ApplicationContext context) {
        try {
            Server server = context.getBean(Server.class);
            server.start(context);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
