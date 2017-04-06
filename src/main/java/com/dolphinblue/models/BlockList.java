package com.dolphinblue.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

/**
 * Created by Devon on 4/5/17.
 * This class is used for retrieving updated blocks from the front end
 */
public class BlockList {
    @JsonProperty(value = "completed")
    private boolean completed;
    @JsonProperty(value = "toolbox")
    private BlockWrapper toolbox;
    @JsonProperty(value = "editor")
    private BlockWrapper editor;

    public BlockList() {

    }

    @JsonCreator
    public BlockList(BlockWrapper toolbox, BlockWrapper editor, boolean completed) {
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

    public BlockWrapper getToolbox() {
        return toolbox;
    }

    public void setToolbox(BlockWrapper toolbox) {
        this.toolbox = toolbox;
    }

    public BlockWrapper getEditor() {
        return editor;
    }

    public void setEditor(BlockWrapper editor) {
        this.editor = editor;
    }
}
