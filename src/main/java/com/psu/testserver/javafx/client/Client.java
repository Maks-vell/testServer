package com.psu.testserver.javafx.client;

import com.psu.testserver.lib.RESTParser;

import java.net.*;
import java.io.*;
import java.util.Objects;

public class Client {
    private final long RESPONSE_WAITING_TIME = 8 * 1000;
    private Socket socket;
    private BufferedReader inputStream;
    private BufferedWriter outputStream;

    public Client() {
    }

    public void initConnection(String address, int port) throws IOException {
        this.socket = new Socket(address, port);
        System.out.println("Connected to server");

        this.inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void request(String request) {
        try {
            tryRequest(request);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void tryRequest(String request) throws IOException {
        this.outputStream.write(request);
        this.outputStream.newLine();
        this.outputStream.flush();
    }

    public String requestWithResponse(String request) throws IOException {
        request(request);

        return getResponse();
    }

    private String getResponse() throws IOException {
        long startWaitingResponseTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startWaitingResponseTime < RESPONSE_WAITING_TIME) {
            if (this.inputStream.ready()) {
                String response = this.inputStream.readLine();
                if (Objects.equals(RESTParser.getCode(response), "ERROR")) {
                    throw new RuntimeException(RESTParser.ErrorMessage(response));
                }
                return response;
            }
        }

        throw new IOException("waiting response time is out");
    }

    public void closeConnection() throws IOException {
        this.inputStream.close();
        this.outputStream.close();
        this.socket.close();
    }
}
