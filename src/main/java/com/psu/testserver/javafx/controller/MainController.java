package com.psu.testserver.javafx.controller;

import com.psu.testserver.Launcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainController {

    @FXML
    private Button createTestButton;

    @FXML
    private Button deleteTest;

    @FXML
    private Button editTest;

    @FXML
    private CheckBox isAddAnswer;

    @FXML
    private Button shareTestButton;

    @FXML
    private ListView<String> testListView;

    @FXML
    void initialize() {
        File dir = new File("tests");
        File[] arrFiles = dir.listFiles();
        if(arrFiles != null){
            List<File> files = Arrays.asList(arrFiles);
            List<String> tests = new ArrayList<>();
            for(var el: files){
                tests.add(el.getName());
            }
            ObservableList<String> items = FXCollections.observableArrayList(tests);
            this.testListView.setItems(items);
        }

        this.shareTestButton.setOnAction(this::tryStartSharingWindow);
        this.createTestButton.setOnAction(this::tryStartTestRedactorWindow);
    }

    private void tryStartSharingWindow(ActionEvent event) {
        try {
            startSharingWindow();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void tryStartTestRedactorWindow(ActionEvent event) {
        try {
            startTestRedactorWindow();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void startTestRedactorWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("test-redactor-view.fxml"));
        Scene sharingScene = new Scene(fxmlLoader.load(), 1000, 700);

        Stage sharingWindow = new Stage();
        sharingWindow.setTitle("Создание");
        sharingWindow.setScene(sharingScene);

        sharingWindow.show();
    }

    private void startSharingWindow() throws IOException {
        SharingController sharingController = new SharingController();
        sharingController.setSharingTestName(this.testListView.getSelectionModel().getSelectedItem());

        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("sharing-view.fxml"));
        fxmlLoader.setController(sharingController);
        Scene sharingScene = new Scene(fxmlLoader.load(), 600, 400);

        Stage sharingWindow = new Stage();
        sharingWindow.setTitle("Раздача");
        sharingWindow.setScene(sharingScene);

        sharingWindow.show();
    }
}
