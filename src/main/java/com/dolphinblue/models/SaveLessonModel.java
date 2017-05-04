package com.dolphinblue.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FreddyEstevez on 5/2/17.
 */
public class SaveLessonModel {
    private String title;
    private String description;
    private boolean shared;
    private List<Long> tasks;

    public SaveLessonModel(){
        tasks = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public List<Long> getTasks() {
        return tasks;
    }

    public void setTasks(List<Long> tasks) {
        this.tasks = tasks;
    }
}
