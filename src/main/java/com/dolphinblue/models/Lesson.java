package com.dolphinblue.models;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.*;
/**
 * Created by FreddyEstevez on 3/21/17.
 * Represent model for lessons
 */
@Entity
public class Lesson {

    @Id private Long lesson_id;
    private String title;
    @Index private Key<User> user_id; //user who is working on the lesson
    private Key<User> creator_id; //user who created the lesson
    private List<Key<Task>> tasks; //holds lists of tasks ids for this lesson
    private double percent_complete; // Hold the percent of task the user has completed
    @Index private boolean is_public;
    @Index private  boolean site_owned;
    private Key<Lesson> original_lesson;
    public Lesson(){
        this.tasks = new ArrayList<>();
    }

    public Lesson(Long lesson_id, String title, Key user, Key creator, List<Key<Task>> tasks, boolean is_public, boolean site_owned) {
        this.lesson_id = lesson_id;
        this.title = title;
        this.user_id = user;
        this.creator_id = creator;
        this.tasks = tasks;
        this.is_public = is_public;
        this.site_owned = site_owned;
    }

    public Long getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(Long lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Key getUser_id() {
        return user_id;
    }

    public void setUser_id(User user) {
        this.user_id = Key.create(user);
    }

    public Key getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(User creator) {
        this.creator_id = Key.create(creator);
    }

    public List<Key<Task>> getTasks() {
        return tasks;
    }

    public void setTasks(List<Key<Task>> tasks) {
        this.tasks = tasks;
    }

    public double getPercent_complete() {
        return percent_complete;
    }

    public void setPercent_complete(double percent_complete) {
        this.percent_complete = percent_complete;
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

    public Key getOriginal_lesson() {
        return original_lesson;
    }

    public void setOriginal_lesson(Lesson original_lesson) {
        this.original_lesson = Key.create(original_lesson);
    }


}
