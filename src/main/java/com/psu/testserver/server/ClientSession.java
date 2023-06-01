package com.psu.testserver.server;

import com.psu.testserver.server.context.ApplicationContext;
import com.psu.testserver.server.transmitter.RequestTransmitter;

import java.io.*;
import java.net.Socket;

public class ClientSession extends Thread {
    private final RequestTransmitter requestTransmitter;
    private final Socket clientSocket;
    private final int id;

    private BufferedReader inputStream;
    private BufferedWriter outputStream;
    private boolean isLaunch;

    ClientSession(ApplicationContext context, Socket clientSocket, int id) {
        this.requestTransmitter = context.getBean(RequestTransmitter.class);
        this.requestTransmitter.setContext(context);

        this.clientSocket = clientSocket;
        this.id = id;
    }

    public void run() {
        try {
            tryRun();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void tryRun() throws IOException {
        initializeIOStream();

        this.isLaunch = true;

        while (this.isLaunch) {
            if (this.inputStream.ready()) {
                this.requestTransmitter.request(this.inputStream.readLine(), id);
            }
        }
    }

    private void initializeIOStream() throws IOException {
        this.inputStream = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        this.outputStream = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
    }

    public void request(String message) throws IOException {
        this.outputStream.write(message);
        this.outputStream.newLine();
        this.outputStream.flush();
    }

    public void stopSession() throws IOException {
        this.isLaunch = false;
        this.inputStream.close();
        this.outputStream.close();
        this.clientSocket.close();
    }
}