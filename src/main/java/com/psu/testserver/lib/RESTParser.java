package com.psu.testserver.lib;

public class RESTParser {
    static public String getCode(String input) {
        String[] blocks = input.split("/");

        return blocks[0];
    }

    static public String ErrorMessage(String input) {
        String[] blocks = input.split("/");

        return blocks[1];
    }

    static public String getServicePath(String input) {
        String[] blocks = input.split("/");

        return blocks[1];
    }

    static public String getMethodPath(String input) {
        String[] blocks = input.split("/");

        return blocks[2];
    }

    static public String getParameter(String input, int paramNum) {
        String[] blocks = input.split("/");

        return blocks[paramNum + 2];
    }
}
