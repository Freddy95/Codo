package com.dolphinblue.models;

/**
 * Created by Devon on 4/5/17.
 * This class is used for retrieving updated blocks from the front end
 */
public class SaveTaskModel {

    private boolean completed;
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
}
