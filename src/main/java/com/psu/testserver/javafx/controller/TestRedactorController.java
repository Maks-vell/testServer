package com.psu.testserver.javafx.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.psu.testserver.model.QuestionModel;
import com.psu.testserver.model.TestModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class TestRedactorController implements Controller {
    private static final Logger log = Logger.getLogger(TestRedactorController.class);

    @FXML
    private Button readyButton;

    @FXML
    private ListView<String> questionListView;

    private List<QuestionModel> questionList;

    @FXML
    private TextField answerField;

    @FXML
    private TextField questionField;

    @FXML
    private Button addQuestionButton;

    @FXML
    private Label questionNumber;

    @FXML
    private TextField nameTestField;

    @FXML
    private Label errorNameLabel;

    @FXML
    private Button deleteQuestionButton;

    @Setter
    private boolean isEdit;
    @Setter
    private String editableTestName;
    @Setter
    private MainController mainController;

    private QuestionModel selectedQuestionModel;

    @FXML
    void initialize() {
        ObservableList<String> items = FXCollections.observableArrayList();
        this.questionListView.setItems(items);
        LoadEditableTest();

        this.questionListView.setCellFactory(ComboBoxListCell.forListView());
        this.questionListView.setEditable(true);

        this.addQuestionButton.setOnAction(this::addQuestionButtonClick);
        this.questionListView.setOnEditStart(this::selectNewQuestionModel);
        this.questionField.setOnKeyTyped(this::updateQuestionField);
        this.answerField.setOnKeyTyped(this::updateAnswerField);
        this.readyButton.setOnAction(this::readyButtonClick);
        this.deleteQuestionButton.setOnAction(this::deleteQuestionButtonClick);
    }

    private void LoadEditableTest() {
        try {
            tryLoadEditableTest();
        } catch (IOException ex) {
            log.error(ex.getMessage());
            Stage stage = (Stage) this.readyButton.getScene().getWindow();
            stage.close();
        }
    }

    private void tryLoadEditableTest() throws IOException {
        if (!this.isEdit) {
            this.questionList = new ArrayList<>();
            return;
        }
        this.nameTestField.setText(getEditableTestNameNoExtension());

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        FileReader fileReader = new FileReader(String.format("tests\\%s", this.editableTestName));
        TestModel testModel = gson.fromJson(fileReader, TestModel.class);
        fileReader.close();

        this.questionList = testModel.questions;

        updateListView();
    }

    private String getEditableTestNameNoExtension() {
        return this.editableTestName.split("\\.")[0];
    }

    private void updateListView() {
        this.questionListView.getItems().clear();
        for (QuestionModel newQuestion : this.questionList) {
            this.questionListView.getItems().add(String.format("%d.%s", newQuestion.number, newQuestion.question));
        }
    }

    private void addQuestionButtonClick(ActionEvent event) {
        QuestionModel newQuestion = new QuestionModel("", "", this.questionList.size() + 1);
        this.questionList.add(newQuestion);

        this.questionListView.getItems().add(String.format("%d.%s", newQuestion.number, newQuestion.question));
        this.questionListView.edit(newQuestion.number - 1);

        formUpdate();
    }

    private void formUpdate() {
        this.selectedQuestionModel = this.questionList.get(this.questionListView.getEditingIndex());
        this.questionNumber.setText(String.format("№ %d", selectedQuestionModel.number));
        this.questionField.setText(selectedQuestionModel.question);
        this.answerField.setText(selectedQuestionModel.answer);
    }

    private void deleteQuestionButtonClick(ActionEvent actionEvent) {
        if (this.selectedQuestionModel == null) {
            return;
        }

        for (QuestionModel questionModel : this.questionList) {
            if (questionModel.number > this.selectedQuestionModel.number) {
                questionModel.number--;
            }
        }

        this.questionList.remove(this.selectedQuestionModel.number - 1);

        updateListView();
    }

    private void selectNewQuestionModel(ListView.EditEvent<String> stringEditEvent) {
        formUpdate();
    }

    private void updateQuestionField(KeyEvent keyEvent) {
        updateQuestionModel();
    }

    private void updateQuestionModel() {
        this.selectedQuestionModel.question = this.questionField.getText();
        this.selectedQuestionModel.answer = this.answerField.getText();

        this.questionListView.getItems().set(selectedQuestionModel.number - 1,
                String.format("%d.%s", selectedQuestionModel.number, selectedQuestionModel.question));
    }

    private void updateAnswerField(KeyEvent keyEvent) {
        updateQuestionModel();
    }

    private void readyButtonClick(ActionEvent actionEvent) {
        if (this.nameTestField.getText().length() < 1) {
            this.errorNameLabel.setVisible(true);
            return;
        }

        try {
            trySafeTestInJson();
        } catch (IOException ex) {
            log.error(ex.getMessage());
            return;
        }

        this.mainController.updateTestListView();
        Stage stage = (Stage) this.readyButton.getScene().getWindow();
        stage.close();
    }

    private void trySafeTestInJson() throws IOException {
        checkFolder();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        FileOutputStream fileOutputStream = new FileOutputStream(String.format("tests\\%s.json", this.nameTestField.getText()));

        fileOutputStream.write(gson.toJson(new TestModel(this.questionList)).getBytes());
        fileOutputStream.close();
    }

    private void checkFolder() throws IOException {
        File folder = new File("tests");
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                throw new IOException("Can't create directory");
            }
        }
    }
}
