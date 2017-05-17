package com.dolphinblue.models;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import com.dolphinblue.models.Lesson;

import java.util.*;
/**
 * Created by FreddyEstevez on 3/21/17.
 * Represent model for lessons
 */
public class LessonDetails {

    private Long lesson_id;
    private String title;
    private String user_id; //user who is working on the lesson
    private String creator_id; //user who created the lesson
    private double percent_complete; // Hold the percent of task the user has completed
    private boolean shared;
    private boolean site_owned;
    private String description;
    private String username;
    private Long index;
    private int rating;
    //Date when lesson was last edited or changed.
    private Date last_edited;
    //Date when this lesson was last access by user working on it
    private Date last_accessed;

    public LessonDetails(Long lesson_id, String title, String user, String creator,String username, boolean shared, int rating, boolean site_owned) {
        this.lesson_id = lesson_id;
        this.title = title;
        this.user_id = user;
        this.creator_id = creator;
        this.shared = shared;
        this.rating = rating;
        this.site_owned = site_owned;
        this.username = username;
    }

    public LessonDetails(Lesson l, String username) {
        this.lesson_id = l.getLesson_id();
        this.title = l.getTitle();
        this.user_id = l.getUser_id();
        this.creator_id = l.getCreator_id();
        this.percent_complete = l.getPercent_complete();
        this.shared = l.isShared();
        this.site_owned = l.isSite_owned();
        this.description = l.getDescription();
        this.last_edited = l.getLast_edited();
        this.last_accessed = l.getLast_accessed();
        this.index = l.getIndex();
        this.username = username;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(User user) {
        this.user_id = user.getUser_id();
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator) {
        this.creator_id = creator;
    }

    public double getPercent_complete() {
        return percent_complete;
    }

    public void setPercent_complete(double percent_complete) {
        this.percent_complete = percent_complete;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public boolean isSite_owned() {
        return site_owned;
    }

    public void setSite_owned(boolean site_owned) {
        this.site_owned = site_owned;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setIndex(Long index){
        this.index = index;
    }

    public Long getIndex(){
        return  index;
    }

    public int compareTo(Lesson lesson){
        if(this.getIndex() < lesson.getIndex()){
            return -1;
        }
        return 1;
    }

    public Date getLast_edited() {
        return last_edited;
    }

    public void setLast_edited(Date last_edited) {
        this.last_edited = last_edited;
    }

    public Date getLast_accessed() {
        return last_accessed;
    }

    public void setLast_accessed(Date last_accessed) {
        this.last_accessed = last_accessed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
