package com.dolphinblue.models;
import com.googlecode.objectify.Ref;
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
    @Index private Ref<User> user_id;//user who is working on the lesson
    private Ref<User> creator_id;//user who created the lesson
    private List<Ref<Task>> tasks;//holds lists of tasks ids for this lesson
    @Index private boolean is_public;
    @Index private  boolean site_owned;
    private Ref<Lesson> original_lesson;
    public Lesson(){
        tasks = new ArrayList<>();
    }

    public Lesson(Long lesson_id, String title, User user, User creator, List<Task> ta, boolean is_public, boolean site_owned) {
        this.lesson_id = lesson_id;
        this.title = title;
        this.user_id = Ref.create(user);
        this.creator_id = Ref.create(creator);
        this.creator_id = creator_id;
        tasks = new ArrayList<>();
        setTasks(ta);
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

    public User getUser_id() {
        return user_id.get();
    }

    public void setUser_id(User user) {
        this.user_id = Ref.create(user);
    }

    public User getCreator_id() {
        return creator_id.get();
    }

    public void setCreator_id(User creator) {
        this.creator_id = Ref.create(creator);
    }

    /**
     * get the tasks objects by using the references
     * @return
     */
    public List<Task> getTasks() {
        List<Task> t = new ArrayList<>();
        for(int i = 0; i < tasks.size(); i++){
            t.add(tasks.get(0).get());
        }
        return t;
    }

    /**
     * Turn tasks objects into references and save them
     * @param ta
     */
    public void setTasks(List<Task> ta) {
        tasks.clear();
        for (int i = 0; i < ta.size(); i++){
            tasks.add(Ref.create(ta.get(i)));
        }
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

    public Lesson getOriginal_lesson() {
        return original_lesson.get();
    }

    public void setOriginal_lesson(Lesson original_lesson) {
        this.original_lesson = Ref.create(original_lesson);
    }


}
