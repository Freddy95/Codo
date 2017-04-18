package com.dolphinblue.models;

/**
 * Created by FreddyEstevez on 4/16/17.
 * Used to retrieve freecode task from front end.
 */
public class SaveFreecodeTaskModel {
    private String freecode;
    private boolean completed;

    public String getFreecode() {
        return freecode;
    }

    public void setFreecode(String freecode) {
        this.freecode = freecode;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
