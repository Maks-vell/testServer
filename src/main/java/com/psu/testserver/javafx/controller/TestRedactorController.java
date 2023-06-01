package com.psu.testserver.javafx.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.psu.testserver.javafx.model.QuestionModel;
import com.psu.testserver.javafx.model.TestModel;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class TestRedactorController {

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

    private QuestionModel selectedQuestionModel;

    @FXML
    void initialize() {
        ObservableList<String> items = FXCollections.observableArrayList();
        this.questionListView.setItems(items);

        this.questionList = new ArrayList<>();
        this.questionListView.setCellFactory(ComboBoxListCell.forListView());
        this.questionListView.setEditable(true);


        this.addQuestionButton.setOnAction(this::addQuestionButtonClick);
        this.questionListView.setOnEditStart(this::selectQuestion);
        this.questionField.setOnKeyTyped(this::updateQuestionField);
        this.answerField.setOnKeyTyped(this::updateAnswerField);
        this.readyButton.setOnAction(this::readyButtonClick);
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

    private void selectQuestion(ListView.EditEvent<String> stringEditEvent) {
        formUpdate();
    }

    private void updateAnswerField(KeyEvent keyEvent) {
        updateQuestionModel();
    }

    private void updateQuestionModel() {
        this.selectedQuestionModel.question = this.questionField.getText();
        this.selectedQuestionModel.answer = this.answerField.getText();

        this.questionListView.getItems().set(selectedQuestionModel.number - 1,
                String.format("%d.%s", selectedQuestionModel.number, selectedQuestionModel.question));
    }

    private void updateQuestionField(KeyEvent keyEvent) {
        updateQuestionModel();
    }

    private void readyButtonClick(ActionEvent actionEvent) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        if (this.nameTestField.getText().length() < 1) {
            return;
        }

        File folder = new File("tests");
        if (!folder.exists()) {
            folder.mkdir();
        }

        try {
            FileOutputStream out = new FileOutputStream(String.format("tests\\%s.json", this.nameTestField.getText()));
            out.write(gson.toJson(new TestModel(this.questionList)).getBytes());
            out.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        Stage stage = (Stage) this.readyButton.getScene().getWindow();
        stage.close();
    }

}
