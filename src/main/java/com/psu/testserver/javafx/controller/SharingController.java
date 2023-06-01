package com.psu.testserver.javafx.controller;

import com.psu.testserver.javafx.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;

public class SharingController {
    @Setter
    private String sharingTestName;

    @FXML
    private Button closeSharingButton;

    @FXML
    private Text deliveredTestCount;

    private Client client;

    @FXML
    void initialize() {
        this.client = new Client();
        try{
            client.initConnection("127.0.0.1", 3384);
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }

        client.request(String.format("POST/testService/setTest/%s", this.sharingTestName));

        closeSharingButton.setOnAction(this::tryCloseSharing);
    }

    private void tryCloseSharing(ActionEvent event){

        try {
            closeSharing();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void closeSharing() throws IOException {
        client.closeConnection();
        Stage stage = (Stage) this.closeSharingButton.getScene().getWindow();
        stage.close();
    }
}
