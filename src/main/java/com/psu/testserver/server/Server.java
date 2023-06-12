package com.psu.testserver.server;

import com.psu.testserver.server.annotation.PostConstruct;
import com.psu.testserver.server.context.ApplicationContext;
import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final Logger log = Logger.getLogger(Server.class);
    private final int SERVER_PORT = 3384;

    private ServerSocket serverSocket;
    private boolean isLaunch;
    private List<ClientSession> clientSessions;

    @PostConstruct
    public void postConstruct() throws IOException {
        this.serverSocket = new ServerSocket(SERVER_PORT);
        this.clientSessions = new ArrayList<>();
        this.isLaunch = false;
    }

    public void start(ApplicationContext context) throws IOException {
        this.isLaunch = true;
        log.info("Server started on localhost:" + SERVER_PORT);

        while (this.isLaunch) {
            Socket clientSocket = this.serverSocket.accept();
            log.info("Client No:" + this.clientSessions.size() + " started!");

            initializeNewClientSession(context, clientSocket);
        }
    }

    private void initializeNewClientSession(ApplicationContext context, Socket clientSocket) {
        ClientSession clientSession = new ClientSession(context, clientSocket, generateId());
        this.clientSessions.add(clientSession);
        clientSession.start();
    }

    private int generateId() {
        int id = (int) (Math.random() * 1000000);

        while (checkIdExistence(id)) {
            id = (int) (Math.random() * 1000000);
        }
        return id;
    }

    private boolean checkIdExistence(int id) {
        for (ClientSession clientSession : this.clientSessions) {
            if (clientSession.getClientId() == id) {
                return true;
            }
        }
        return false;
    }

    public ClientSession getClientSession(int id) throws IOException {
        for (ClientSession clientSession : this.clientSessions) {
            if (clientSession.getClientId() == id) {
                return clientSession;
            }
        }
        throw new IOException("Client with " + id + "is not exists");
    }

    public int getClientCount() {
        return this.clientSessions.size();
    }

    public void tryStop() throws IOException {
        this.isLaunch = false;
        for (ClientSession clientSession : this.clientSessions) {
            clientSession.stopSession();
            this.clientSessions.remove(clientSession);
        }
        this.serverSocket.close();

        log.info("Server was stopped");
    }
}