package com.dolphinblue.models;

import com.googlecode.objectify.Ref;
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
    @Load private List<Ref<Block>> toolbox;//list of block ids in the toolbox. Must be fetched when task is fetched.
    @Load private List<Ref<Block>> editor;//list of blocks in the editor. Must be fetched when task is fetched
    private String freecode;
    private boolean completed;
    private Type type;

    public Task(){
        editor = new ArrayList<>();
        toolbox = new ArrayList<>();
    }

    public Task(Long task_id, String title, String instructions, String hint, String test_case, String expected_output, List<Block> tool, List<Block> edit, String freecode, boolean completed, Type type) {
        this.task_id = task_id;
        this.title = title;
        this.instructions = instructions;
        this.hint = hint;
        this.test_case = test_case;
        this.expected_output = expected_output;
        editor = new ArrayList<>();
        toolbox = new ArrayList<>();
        setEditor(edit);
        setToolbox(tool);
        this.freecode = freecode;
        this.completed = completed;
        this.type = type;
    }

    public enum Type{
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

    public List<Block> getToolbox() {
        List<Block> ret = new ArrayList<>();
        for(int i = 0; i < toolbox.size(); i++){
            ret.add(toolbox.get(i).get());
        }
        return ret;
    }

    public void setToolbox(List<Block> tool) {
        toolbox.clear();
        for(int i = 0; i < tool.size(); i++){
            toolbox.add(Ref.create(tool.get(i)));
        }
    }

    public List<Block> getEditor() {
        List<Block> ret = new ArrayList<>();
        for(int i = 0; i < editor.size(); i++){
            ret.add(editor.get(i).get());
        }
        return ret;
    }

    public void setEditor(List<Block> edit) {
        editor.clear();
        for(int i = 0; i < edit.size(); i++){
            editor.add(Ref.create(edit.get(i)));
        }
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
