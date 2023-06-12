package com.psu.testserver.model;

import java.util.List;

public class TestModel {
    public List<QuestionModel> questions;

    public boolean isWithAnswers = true;

    public TestModel(List<QuestionModel> questions) {
        this.questions = questions;
    }
}
