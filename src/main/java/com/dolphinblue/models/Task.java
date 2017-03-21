package com.dolphinblue.models;

import java.util.List;

/**
 * Created by FreddyEstevez on 3/21/17.
 * Model representing a task.
 */
public class Task {

    private long task_id;
    private String title;
    private String instructions;
    private String hint;
    private String test_case;
    private String expected_output;
    private List<Long> toolbox;//list of block ids in the toolbox
    private List<Long> editor;//list of blocks in the editor
    private String freecode;
    private boolean completed;
    private Type type;

    public Task(){}

    public Task(long task_id, String title, String instructions, String hint, String test_case, String expected_output, List<Long> toolbox, List<Long> editor, String freecode, boolean completed, Type type) {
        this.task_id = task_id;
        this.title = title;
        this.instructions = instructions;
        this.hint = hint;
        this.test_case = test_case;
        this.expected_output = expected_output;
        this.toolbox = toolbox;
        this.editor = editor;
        this.freecode = freecode;
        this.completed = completed;
        this.type = type;
    }

    public enum Type{
        JS,
        HTML,
        CSS;
    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
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

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getTest_case() {
        return test_case;
    }

    public void setTest_case(String test_case) {
        this.test_case = test_case;
    }

    public String getExpected_output() {
        return expected_output;
    }

    public void setExpected_output(String expected_output) {
        this.expected_output = expected_output;
    }

    public List<Long> getToolbox() {
        return toolbox;
    }

    public void setToolbox(List<Long> toolbox) {
        this.toolbox = toolbox;
    }

    public List<Long> getEditor() {
        return editor;
    }

    public void setEditor(List<Long> editor) {
        this.editor = editor;
    }

    public String getFreecode() {
        return freecode;
    }

    public void setFreecode(String freecode) {
        this.freecode = freecode;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
