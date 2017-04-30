package com.dolphinblue.models;
import java.util.*;
/**
 * Created by Devon on 4/5/17.
 * This class is used for retrieving updated blocks from the front end
 */
public class SaveTaskModel {

    private boolean completed;
    private String hint;
    private String title;
    private String instructions;
    private List<String> test_case;
    private List<String> expected_output;
    private BlockList toolbox;
    private BlockList editor;

    public SaveTaskModel() {

    }

    public SaveTaskModel(BlockList toolbox, BlockList editor, boolean completed) {
        this.toolbox = toolbox;
        this.editor = editor;
        this.completed = completed;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public BlockList getToolbox() {
        return toolbox;
    }

    public void setToolbox(BlockList toolbox) {
        this.toolbox = toolbox;
    }

    public BlockList getEditor() {
        return editor;
    }

    public void setEditor(BlockList editor) {
        this.editor = editor;
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

    public String toString(){
        return "TITLE = " +  this.title +" ,\n"+
                "INSTRUCTIONS = " +  this.instructions+" ,\n" +
                "HINT = " +  this.hint + " ,\n" +
                "TEST CASE = " +  this.test_case.toString() + " ,\n" +
                "EXPECTED OUTPUT = " +  this.expected_output.toString();
    }
}
