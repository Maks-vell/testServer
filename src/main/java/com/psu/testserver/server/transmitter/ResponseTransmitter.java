package com.psu.testserver.server.transmitter;

import com.psu.testserver.server.ClientSession;
import com.psu.testserver.server.Server;
import com.psu.testserver.server.annotation.Inject;
import org.apache.log4j.Logger;

import java.io.IOException;

public class ResponseTransmitter {
    private static final Logger log = Logger.getLogger(ResponseTransmitter.class);
    @Inject
    Server server;

    public void response(String request, int id) {
        try {
            tryResponse(request, id);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    private void tryResponse(String request, int id) throws IOException {
        ClientSession clientSession = server.getClientSession(id);
        clientSession.request(request);
    }
}
