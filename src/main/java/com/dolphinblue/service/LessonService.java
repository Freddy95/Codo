package com.dolphinblue.service;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import java.util.List;

/**
 * Created by Devon on 4/2/17.
 *
 * This class provides calculation services for the controller classes
 */
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

}
