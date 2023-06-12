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
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SharingController implements Controller {
    private static final Logger log = Logger.getLogger(SharingController.class);

    private final int UPDATE_STUDENT_TIME = 3;
    @Setter
    private String sharingTestName = "";
    @Setter
    private boolean isWithAnswers = false;
    @FXML
    private ListView<String> studentListView;

    @FXML
    private Text testNameLabel;

    @FXML
    private Button closeSharingButton;

    private Client client;

    private List<StudentModel> studentModelList;

    private Timeline updateStudentsTimeline;


    @FXML
    void initialize() {
        if (this.sharingTestName.isEmpty()) {
            Stage stage = (Stage) this.closeSharingButton.getScene().getWindow();
            stage.close();
            return;
        }

        try {
            tryInitializeClient();
        } catch (IOException ex) {
            log.error(ex.getMessage());
            closeSharing(new ActionEvent());
        }
        this.client.request(String.format("POST/testService/startSharingTest/%s/%s", this.sharingTestName, this.isWithAnswers));

        this.testNameLabel.setText(this.sharingTestName);
        this.studentModelList = new ArrayList<>();
        this.closeSharingButton.setOnAction(this::closeSharing);

        startGetStudents();
    }

    private void startGetStudents() {
        this.updateStudentsTimeline = new Timeline(new KeyFrame(Duration.seconds(UPDATE_STUDENT_TIME), this::getStudents));
        this.updateStudentsTimeline.play();
    }

    private void getStudents(ActionEvent actionEvent) {
        String studentListModelJSON = null;

        try {
            studentListModelJSON = client.requestWithResponse("GET/testService/getStudents");
        } catch (IOException ex) {
            log.error(ex.getMessage());
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
    }

    private void closeSharing(ActionEvent event) {
        try {
            tryCloseSharing();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    private void tryCloseSharing() throws IOException {
        this.client.closeConnection();
        this.client.request("GET/testService/stopSharingTest");
        this.updateStudentsTimeline.stop();

        Stage stage = (Stage) this.closeSharingButton.getScene().getWindow();
        stage.close();
    }
}