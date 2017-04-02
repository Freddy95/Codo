package com.dolphinblue.service;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devon on 4/2/17.
 *
 * This class provides calculation services for the controller classes
 */
@Service
public class LessonService {

    public double get_percent_complete(Lesson lesson) {
        Objectify ofy = OfyService.ofy();

        List<Key<Task>> tasks = lesson.getTasks();

        int total = tasks.size();
        int count = 0;

        for(int i = 0; i < tasks.size(); i++) {
            Key task_key = tasks.get(i);
            Task task = ofy.load().type(Task.class).id(task_key.getId()).now();
            if(task.isCompleted()) {
                count++;
            }
        }
        return (count/total);
    }

    public List<Lesson> get_main_lessons_by_id(List<Key<Lesson>> lesson_keys) {
        Objectify ofy = OfyService.ofy();
        List<Lesson> main_lessons = new ArrayList<>();

        for(int i = 0; i < lesson_keys.size(); i++) {
            Key<Lesson> lesson_key = lesson_keys.get(i);
            Lesson lesson = ofy.load().type(Lesson.class).id(lesson_key.getId()).now();
            if(lesson.isSite_owned()) {
                main_lessons.add(lesson);
            }
        }
        return main_lessons;
    }

}
