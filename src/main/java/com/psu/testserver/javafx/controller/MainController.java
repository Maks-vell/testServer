package com.psu.testserver.javafx.controller;

import com.psu.testserver.Launcher;
import com.psu.testserver.model.QuestionModel;
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
import java.util.List;

public class MainController implements Controller {
    private final String TEST_DIRECTORY = "tests";

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
        updateTestListView();

        this.shareTestButton.setOnAction(this::shareTestButtonClick);
        this.createTestButton.setOnAction(this::createTestButtonClick);
        this.editTest.setOnAction(this::editTestButtonClick);
        this.deleteTest.setOnAction(this::deleteTestButtonClick);
    }

    public void updateTestListView() {
        List<String> tests = loadTestsFromDirectory(TEST_DIRECTORY);

        ObservableList<String> items = FXCollections.observableArrayList(tests);
        this.testListView.setItems(items);
    }

    private List<String> loadTestsFromDirectory(String directory) {
        File testDirectory = new File(directory);
        File[] testFiles = testDirectory.listFiles();

        List<String> tests = new ArrayList<>();

        if (testFiles == null) {
            return tests;
        }

        for (File testFile : testFiles) {
            tests.add(testFile.getName());
        }
        return tests;
    }

    private void shareTestButtonClick(ActionEvent event) {
        if(!isSelectItem()){
            return;
        }

        try {
            tryStartSharingWindow();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private boolean isSelectItem(){
        return this.testListView.getSelectionModel().getSelectedItem() != null
                && !this.testListView.getSelectionModel().getSelectedItem().isEmpty();
    }

    private void tryStartSharingWindow() throws IOException {
        SharingController sharingController = new SharingController();
        sharingController.setSharingTestName(this.testListView.getSelectionModel().getSelectedItem());

        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("sharing-view.fxml"));
        fxmlLoader.setController(sharingController);

        Scene sharingScene = new Scene(fxmlLoader.load(), 800, 600);
        startWindow(sharingScene, "Раздача");
    }

    private void startWindow(Scene scene, String title) {
        Stage newWindow = new Stage();
        newWindow.setTitle(title);
        newWindow.setResizable(false);
        newWindow.setScene(scene);

        newWindow.show();
    }

    private void createTestButtonClick(ActionEvent event) {
        try {
            tryStartTestRedactorWindow(false);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void tryStartTestRedactorWindow(boolean isEdit) throws IOException {
        TestRedactorController testRedactorController = new TestRedactorController();
        testRedactorController.setMainController(this);

        if (isEdit) {
            String selectedTest = this.testListView.getSelectionModel().getSelectedItem();
            testRedactorController.setEditableTestName(selectedTest);
            testRedactorController.setEdit(true);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("test-redactor-view.fxml"));
        fxmlLoader.setController(testRedactorController);

        Scene sharingScene = new Scene(fxmlLoader.load(), 1000, 700);
        startWindow(sharingScene, "Редактор");
    }

    private void editTestButtonClick(ActionEvent actionEvent) {
        if(!isSelectItem()){
            return;
        }

        try {
            tryStartTestRedactorWindow(true);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void deleteTestButtonClick(ActionEvent actionEvent) {
        if(!isSelectItem()){
            return;
        }

        String selectedTest = this.testListView.getSelectionModel().getSelectedItem();
        File file = new File(String.format("tests\\%s", selectedTest));

        if (!file.delete()) {
            System.out.println("Can't delete file");
            return;
        }

        updateTestListView();
    }
}
