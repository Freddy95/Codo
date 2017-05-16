package com.dolphinblue.models;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import java.util.*;
/**
 * Created by FreddyEstevez on 3/21/17.
 * Model representing a user/moderator.
 */
@Entity
public class User {
    @Id private String user_id;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String avatar; //Gotten through third party site
    private boolean user_tutorial;
    private boolean lesson_tutorial;
    private boolean cl_tutorial; // create lesson tutorial
    private boolean ct_tutorial; // create task tutorial
    private List<String> admin_msg;//list of moderator messages received
    private boolean is_moderator;
    @Load private List<Key<Lesson>> lessons; // list of lessons the user has started or completed
    @Load private Key<Lesson> current_lesson; //current lesson user is on

    public User(String user_id,String username, String first_name, String last_name, String email, String password, String avatar, boolean user_tutorial, boolean lesson_tutorial, List<Key<Lesson>> lessons) {
        this.user_id = user_id;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.user_tutorial = user_tutorial;
        this.lesson_tutorial = lesson_tutorial;
        this.lessons = lessons;
    }

    /**
     * public no arg constructor
     */
    public User(){
        lessons = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isUser_tutorial() {
        return user_tutorial;
    }

    public void setUser_tutorial(boolean user_tutorial) {
        this.user_tutorial = user_tutorial;
    }

    public boolean isLesson_tutorial() {
        return lesson_tutorial;
    }

    public void setLesson_tutorial(boolean lesson_tutorial) {
        this.lesson_tutorial = lesson_tutorial;
    }

    public boolean isCl_tutorial() {
        return cl_tutorial;
    }

    public void setCl_tutorial(boolean cl_tutorial) {
        this.cl_tutorial = cl_tutorial;
    }

    public boolean isCt_tutorial() {
        return ct_tutorial;
    }

    public void setCt_tutorial(boolean ct_tutorial) {
        this.ct_tutorial = ct_tutorial;
    }

    public List<String> getAdmin_msg() {
        return admin_msg;
    }

    public void setAdmin_msg(List<String> admin_msg) {
        this.admin_msg = admin_msg;
    }

    public boolean isIs_moderator() {
        return is_moderator;
    }

    public void setIs_moderator(boolean is_moderator) {
        this.is_moderator = is_moderator;
    }

    public List<Key<Lesson>> getLessons() {
        return lessons;
    }

    public void setLessons(List<Key<Lesson>> lessons) {
        this.lessons = lessons;
    }

    public Key getCurrent_lesson(){
        return current_lesson;
    }

    public void setCurrent_lesson(Lesson l){
        current_lesson = Key.create(l);
    }
}

