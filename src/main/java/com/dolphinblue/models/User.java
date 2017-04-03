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
    @Id private Long user_id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String avatar; //Gotten through third party site
    private List<String> admin_msg;//list of moderator messages received
    private boolean is_moderator;
    @Load private List<Key<Lesson>> lessons; // list of lessons the user has started or completed
    @Load private Key<Lesson> current_lesson;//current lesson user is on

    public User(Long user_id, String first_name, String last_name, String email, String password, String avatar, List<Key<Lesson>> lessons) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.lessons = lessons;
    }

    /**
     * public no arg constructor
     */
    public User(){
        lessons = new ArrayList<>();
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
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

