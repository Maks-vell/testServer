package com.psu.testserver.javafx.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.psu.testserver.javafx.client.Client;
import com.psu.testserver.model.StudentListModel;
import com.psu.testserver.model.StudentModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.ListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SharingController implements Controller {

    private final int TIME_BETWEEN_GET_STUDENTS = 2 * 1000;
    @Setter
    private String sharingTestName;

    @FXML
    private ListView<String> studentListView;

    @FXML
    private Text testNameLabel;

    @FXML
    private Button closeSharingButton;

    private Client client;

    public List<StudentModel> studentModelList;

    private Timer timerGetStudents;

    @FXML
    void initialize() {
        try {
            tryInitializeClient();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            closeSharing(new ActionEvent());
        }
        this.testNameLabel.setText(this.sharingTestName);

        this.studentModelList = new ArrayList<>();

        startGetStudents();

        this.closeSharingButton.setOnAction(this::closeSharing);
    }

    private void startGetStudents() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), this::getStudents));
        timeline.setCycleCount(1000);
        timeline.play();
    }

    private void getStudents(ActionEvent actionEvent) {
        String studentListModelJSON = null;

        try {
            studentListModelJSON = client.requestWithResponse("GET/testService/getStudents");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        StudentListModel studentListModel = gson.fromJson(studentListModelJSON, StudentListModel.class);
        this.studentModelList = studentListModel.studentModels;
        studentListViewUpdate();
    }

    public void studentListViewUpdate() {
        this.studentListView.getItems().clear();
        for (StudentModel studentModel : this.studentModelList) {
            this.studentListView.getItems().add(String.format("%s тест %s", studentModel.name, studentModel.testPassStatus.toString()));
        }
    }

    private void tryInitializeClient() throws IOException {
        this.client = new Client();
        this.client.initConnection("127.0.0.1", 3384);

        this.client.request(String.format("POST/testService/startSharingTest/%s", this.sharingTestName));
    }

    private void closeSharing(ActionEvent event) {
        try {
            tryCloseSharing();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void tryCloseSharing() throws IOException {
        this.timerGetStudents.cancel();

        this.client.closeConnection();
        this.client.request("GET/testService/stopSharingTest");

        Stage stage = (Stage) this.closeSharingButton.getScene().getWindow();
        stage.close();
    }
}