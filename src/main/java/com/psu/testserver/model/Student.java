package com.psu.testserver.model;

import com.psu.testserver.enums.TestPassStatus;

public class Student {
    public String name;
    public TestPassStatus testPassStatus;

    public Student(String name, TestPassStatus testPassStatus) {
        this.name = name;
        this.testPassStatus = testPassStatus;
    }
}
