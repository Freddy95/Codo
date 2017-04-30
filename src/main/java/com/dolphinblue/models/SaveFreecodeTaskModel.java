package com.dolphinblue.models;

import java.util.List;

/**
 * Created by FreddyEstevez on 4/16/17.
 * Used to retrieve freecode task from front end.
 */
public class SaveFreecodeTaskModel {

    private boolean completed;
    private String freecode;
    private String hint;
    private String title;
    private String instructions;
    private List<String> test_case;
    private List<String> expected_output;

    public SaveFreecodeTaskModel() {

    }

    public SaveFreecodeTaskModel(boolean completed, String freecode, String hint, String title, String instructions, List<String> test_case, List<String> expected_output) {
        this.completed = completed;
        this.freecode = freecode;
        this.hint = hint;
        this.title = title;
        this.instructions = instructions;
        this.test_case = test_case;
        this.expected_output = expected_output;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getFreecode() {
        return freecode;
    }

    public void setFreecode(String freecode) {
        this.freecode = freecode;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<String> getTest_case() {
        return test_case;
    }

    public void setTest_case(List<String> test_case) {
        this.test_case = test_case;
    }

    public List<String> getExpected_output() {
        return expected_output;
    }

    public void setExpected_output(List<String> expected_output) {
        this.expected_output = expected_output;
    }
}
