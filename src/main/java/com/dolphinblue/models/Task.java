package com.dolphinblue.models;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FreddyEstevez on 3/21/17.
 * Model representing a task.
 */
@Entity
public class Task {

    @Id private Long task_id;
    private String title;
    private String instructions;
    private String hint;
    private String test_case;
    private String expected_output;
    @Load private List<Key<Block>> toolbox;//list of block ids in the toolbox. Must be fetched when task is fetched.
    @Load private List<Key<Block>> editor;//list of blocks in the editor. Must be fetched when task is fetched
    private String freecode;
    private boolean completed;
    private boolean block_task;
    private Type type;
    @Load private Key<Task> original_task;//reference to original task

    public Task(){
        editor = new ArrayList<>();
        toolbox = new ArrayList<>();
    }

    public Task(Long task_id, String title, String instructions, String hint, String test_case, String expected_output, List<Key<Block>> toolbox, List<Key<Block>> editor, String freecode, boolean completed, Type type) {
        this.task_id = task_id;
        this.title = title;
        this.instructions = instructions;
        this.hint = hint;
        this.test_case = test_case;
        this.expected_output = expected_output;
        this.editor = editor;
        this.toolbox = toolbox;
        this.freecode = freecode;
        this.completed = completed;
        this.type = type;
    }

    public enum Type {
        JS,
        HTML,
        CSS;
    }

    public Long getTask_id() {
        return task_id;
    }

    public void setTask_id(Long task_id) {
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

    public List<Key<Block>> getToolbox() {
        return toolbox;
    }

    public void setToolbox(List<Key<Block>> toolbox) {
        this.toolbox = toolbox;
    }

    public List<Key<Block>> getEditor() {
        return editor;
    }

    public void setEditor(List<Key<Block>> editor) {
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

    public void setOriginal_task(Task t){
        original_task = Key.create(t);
    }

    public Key getOriginal_task(){
        return original_task;
    }

    public boolean isBlock_task() {
        return block_task;
    }

    public void setBlock_task(boolean block_task) {
        this.block_task = block_task;
    }
}
