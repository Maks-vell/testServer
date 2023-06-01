package com.psu.testserver.javafx.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.psu.testserver.javafx.client.Client;
import com.psu.testserver.model.StudentListModel;
import com.psu.testserver.model.StudentModel;
import javafx.scene.control.ListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
        this.timerGetStudents = new Timer();
        this.timerGetStudents.schedule(new StudentListUpdater(this.client, this), 0, TIME_BETWEEN_GET_STUDENTS);

        this.closeSharingButton.setOnAction(this::closeSharing);
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

class StudentListUpdater extends TimerTask {
    private Client client;

    private SharingController sharingController;

    public StudentListUpdater(Client client, SharingController sharingController) {
        this.client = client;
        this.sharingController = sharingController;
    }

    public void run() {
        try {
            tryGetStudentModelList();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        this.sharingController.studentListViewUpdate();
    }

    private void tryGetStudentModelList() throws IOException {
        String studentListModelJSON = client.requestWithResponse("GET/testService/getStudents");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        StudentListModel studentListModel = gson.fromJson(studentListModelJSON, StudentListModel.class);
        sharingController.studentModelList = studentListModel.studentModels;
    }
}
