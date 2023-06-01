package com.psu.testserver.server;

import com.psu.testserver.server.annotation.PostConstruct;
import com.psu.testserver.server.context.ApplicationContext;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
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
        System.out.printf("Server started on localhost:%d", SERVER_PORT);

        while (this.isLaunch) {
            Socket clientSocket = this.serverSocket.accept();
            System.out.println(" >> " + "Client No:" + this.clientSessions.size() + " started!");

            initializeNewClientSession(context, clientSocket);
        }
    }

    private void initializeNewClientSession(ApplicationContext context, Socket clientSocket) {
        ClientSession clientSession = new ClientSession(context, clientSocket, this.clientSessions.size());
        this.clientSessions.add(clientSession);
        clientSession.start();
    }

    public ClientSession getClientSession(int id) {
        return this.clientSessions.get(id);
    }

    public int getClientCount() {
        return this.clientSessions.size();
    }

    public void stop() throws IOException {
        this.isLaunch = false;
        for (ClientSession clientSession : this.clientSessions) {
            clientSession.stopSession();
            this.clientSessions.remove(clientSession);
        }
        this.serverSocket.close();
    }
}