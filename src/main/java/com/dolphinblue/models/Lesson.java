package com.dolphinblue.models;
import java.util.*;
/**
 * Created by FreddyEstevez on 3/21/17.
 * Represent model for lessons
 */
public class Lesson {

    private long lesson_id;
    private String title;
    private long user_id;//user who is working on the lesson
    private long creator_id;//user who created the lesson
    private List<Long> tasks;//holds lists of tasks ids for this lesson
    private boolean is_public;
    private  boolean site_owned;

    public Lesson(long lesson_id, String title, long user_id, long creator_id, List<Long> tasks, boolean is_public, boolean site_owned) {
        this.lesson_id = lesson_id;
        this.title = title;
        this.user_id = user_id;
        this.creator_id = creator_id;
        this.tasks = tasks;
        this.is_public = is_public;
        this.site_owned = site_owned;
    }

    public long getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(long lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(long creator_id) {
        this.creator_id = creator_id;
    }

    public List<Long> getTasks() {
        return tasks;
    }

    public void setTasks(List<Long> tasks) {
        this.tasks = tasks;
    }

    public boolean isIs_public() {
        return is_public;
    }

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }

    public boolean isSite_owned() {
        return site_owned;
    }

    public void setSite_owned(boolean site_owned) {
        this.site_owned = site_owned;
    }
}
