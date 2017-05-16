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
public class Lesson implements Comparable<Lesson>{

    @Id private Long lesson_id;
    private Long index;
    private String title;
    @Index private String user_id; //user who is working on the lesson
    @Index private String creator_id; //user who created the lesson
    private List<Key<Task>> tasks; //holds lists of tasks ids for this lesson
    private int percent_complete; // Hold the percent of task the user has completed
    @Index private boolean shared;

    @Index private boolean site_owned;
    private Key<Lesson> original_lesson;
    private String description;
    private int rating;
    private int number_of_ratings;
    //Date when lesson was last edited or changed.
    private Date last_edited;
    //Date when this lesson was last access by user working on it
    private Date last_accessed;

    public static List<Key<Lesson>> MAIN_LESSON_KEYS = new ArrayList<>();


    public Lesson(){
        this.tasks = new ArrayList<>();
    }

    public Lesson(Long lesson_id, String title, String user, String creator, List<Key<Task>> tasks, boolean shared, int rating, boolean site_owned) {
        this.lesson_id = lesson_id;
        this.title = title;
        this.user_id = user;
        this.creator_id = creator;
        this.tasks = tasks;
        this.shared = shared;
        this.rating = rating;
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

    public List<Key<Task>> getTasks() {
        return tasks;
    }

    public void setTasks(List<Key<Task>> tasks) {
        this.tasks = tasks;
    }

    public int getPercent_complete() {
        return percent_complete;
    }

    public void setPercent_complete(int percent_complete) {
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


    public Key<Lesson> getOriginal_lesson() {
        return original_lesson;
    }

    public void setOriginal_lesson(Lesson original_lesson) {
        this.original_lesson = Key.create(Lesson.class, original_lesson.getLesson_id());
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



    public int getNumber_of_ratings() {
        return number_of_ratings;
    }

    public void setNumber_of_ratings(int number_of_ratings) {
        this.number_of_ratings = number_of_ratings;
    }

}
