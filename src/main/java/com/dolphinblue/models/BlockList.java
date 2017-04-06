package com.dolphinblue.models;

import java.util.List;

/**
 * Created by Devon on 4/5/17.
 * This class is used for retrieving updated blocks from the front end
 */
public class BlockList {
    private boolean isCompleted;
    private List<Block> toolbox;
    private List<Block> editor;

    public BlockList() {

    }

    public BlockList(List<Block> toolbox, List<Block> editor) {
        this.toolbox = toolbox;
        this.editor = editor;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public List<Block> getEditor() {
        return editor;
    }

    public void setEditor(List<Block> editor) {
        this.editor = editor;
    }

    public List<Block> getToolbox() {
        return toolbox;
    }

    public void setToolbox(List<Block> toolbox) {
        this.toolbox = toolbox;
    }
}