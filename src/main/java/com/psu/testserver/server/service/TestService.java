package com.psu.testserver.server.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.psu.testserver.model.QuestionModel;
import com.psu.testserver.model.StudentModel;
import com.psu.testserver.enums.TestPassStatus;
import com.psu.testserver.model.StudentListModel;
import com.psu.testserver.model.TestModel;
import com.psu.testserver.server.annotation.Inject;
import com.psu.testserver.server.annotation.PostConstruct;
import com.psu.testserver.server.annotation.Request;
import com.psu.testserver.lib.RESTParser;
import com.psu.testserver.server.transmitter.ResponseTransmitter;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class TestService extends Service {
    private static final Logger log = Logger.getLogger(TestService.class);
    @Inject
    protected ResponseTransmitter responseTransmitter;

    private String sharingTest;

    private boolean isSharingTest;

    private Map<Integer, StudentModel> students;

    @PostConstruct
    public void postConstruct() {
        this.sharingTest = null;
        this.isSharingTest = false;
        this.students = new HashMap<>();
    }

    @Request(path = "getTest", params = {"studentName"})
    private void getTest(String request, int id) {
        log.info("TestService >> getTest");

        String studentName = RESTParser.getParameter(request, 1);
        if (studentName.isEmpty()) {
            return;
        }

        if (!this.isSharingTest) {
            this.responseTransmitter.response("ERROR/Test is not sharing", id);
            return;
        }

        this.responseTransmitter.response(this.sharingTest, id);
        this.students.put(id, new StudentModel(studentName, TestPassStatus.RECEIVED));
    }

    @Request(path = "cancelTest", params = {})
    private void cancelTest(String request, int id) {
        log.info("TestService >> cancelTest");

        if (!this.isSharingTest || !this.students.containsKey(id)) {
            return;
        }

        StudentModel studentModel = this.students.get(id);
        studentModel.testPassStatus = TestPassStatus.PASS_OF;
        this.students.replace(id, studentModel);
    }

    @Request(path = "startSharingTest", params = {"sharingTestName"})
    private void startSharingTest(String request, int id) {
        log.info("TestService >> startSharingTest");

        String sharingTestName = RESTParser.getParameter(request, 1);
        boolean isWithAnswers = Boolean.parseBoolean(RESTParser.getParameter(request, 2));

        if (sharingTestName.isEmpty()) {
            return;
        }

        try {
            String sharingTest = tryGetCurrentTestInStr(sharingTestName);

            if (!isWithAnswers) {
                sharingTest = deleteAnswers(sharingTest);
            }
            this.sharingTest = sharingTest;
        } catch (IOException ex) {
            log.error(ex.getMessage());
            this.responseTransmitter.response("ERROR/Test not found", id);
            return;
        }

        this.students.clear();
        this.isSharingTest = true;
    }

    private String deleteAnswers(String sharingTest) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        TestModel testModel = gson.fromJson(sharingTest, TestModel.class);
        testModel.isWithAnswers = false;
        for (QuestionModel question : testModel.questions) {
            question.answer = "";
        }

        return gson.toJson(testModel);
    }

    private String tryGetCurrentTestInStr(String testName) throws IOException {
        String[] fileLines = ReadTestFile(testName);

        return concatAllLines(fileLines);
    }

    private String[] ReadTestFile(String testName) throws IOException {
        BufferedReader bufReader = new BufferedReader(new FileReader(String.format("tests\\%s", testName)));
        int countLines = countLines(testName);
        String[] fileLines = new String[countLines];

        for (int i = 0; i < countLines; i++) {
            fileLines[i] = bufReader.readLine();
        }

        bufReader.close();
        return fileLines;
    }

    private int countLines(String testName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(String.format("tests\\%s", testName)));
        String currentStr;
        int count = 0;

        while ((currentStr = bufferedReader.readLine()) != null) {
            count++;
        }
        bufferedReader.close();

        return count;
    }

    private String concatAllLines(String[] strLines) {
        StringBuilder fileStr = new StringBuilder();
        for (String line : strLines) {
            fileStr.append(line);
        }

        return fileStr.toString();
    }

    @Request(path = "stopSharingTest", params = {})
    private void stopSharingTest(String request, int id) {
        log.info("TestService >> stopSharingTest");

        this.isSharingTest = false;
        this.sharingTest = "";
        this.students.clear();
    }

    @Request(path = "getStudents", params = {})
    private void getStudents(String request, int id) {
        log.info("TestService >> getStudents");

        StudentListModel studentListModel = new StudentListModel(this.students.values().stream().toList());

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        responseTransmitter.response(gson.toJson(studentListModel), id);
    }
}

