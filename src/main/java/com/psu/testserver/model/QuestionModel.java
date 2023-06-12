package com.psu.testserver.model;

public class QuestionModel {
    public int number;
    public String question;
    public String answer;

    public QuestionModel(String question, String answer, int number) {
        this.question = question;
        this.answer = answer;
        this.number = number;
    }

}
