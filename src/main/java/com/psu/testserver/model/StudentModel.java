package com.psu.testserver.model;

import com.psu.testserver.enums.TestPassStatus;

public class StudentModel {
    public String name;
    public TestPassStatus testPassStatus;

    public StudentModel(String name, TestPassStatus testPassStatus) {
        this.name = name;
        this.testPassStatus = testPassStatus;
    }
}
