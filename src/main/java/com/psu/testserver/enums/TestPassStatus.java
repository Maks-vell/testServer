package com.psu.testserver.enums;

public enum TestPassStatus {
    NOT_RECEIVED("неизвестно"),
    RECEIVED("получен"),
    PASS_OF("сдан");

    private final String str;

    TestPassStatus(String str){
        this.str = str;
    }

    public String toString(){
        return this.str;
    }
}