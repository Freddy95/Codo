package com.dolphinblue.service;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;
import com.dolphinblue.models.Block;
import com.dolphinblue.models.Block.Type;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by FreddyEstevez on 4/18/17.
 */
@Service
public class TaskService {

    /**
     * Returns a default block catalog.
     */
    public List<Block> get_catalog() {
        List<Block> catalog = new ArrayList<Block>();
        catalog.add(new Block(-1, "// Click to edit", Type.STATIC, false));
        catalog.add(new Block(-1, "butts;", Type.LOG, true));
        catalog.add(new Block(-1, "blah;", Type.IF, true));
        catalog.add(new Block(-1, "butts;", Type.WHILE, true));
        catalog.add(new Block(-1, "butts;", Type.FOR, true));
        catalog.add(new Block(-1, "{", Type.CURL, false));
        catalog.add(new Block(-1, "}", Type.CURL, false));
        return catalog;
    }

    /**
     * Gets previous task in lesson. Adds to model
     * @param task -- current task in lesson
     * @param lesson -- current lesson.
     * @param model -- model to send data to.
     */
    public void get_previous_task(Task task, Lesson lesson, Model model){
        // Get the index for the navigation bar in the lesson
        int index = lesson.getTasks().indexOf(Key.create(Task.class, task.getTask_id()));
        // Set the previous tasks for the lesson
        if(index != 0) {
            // This task is not the first one so there is a prev task
            model.addAttribute("prev_task", lesson.getTasks().get(index-1).getId());
        } else {
            // This task is the first task
            model.addAttribute("prev_task", -1);
        }


    }

    /**
     * Gets next task in lesson. Adds to model
     * @param task -- current task in lesson
     * @param lesson -- current lesson.
     * @param model -- model to send data to.
     */
    public void get_next_task(Task task, Lesson lesson, Model model){
        // Get the index for the navigation bar in the lesson
        int index = lesson.getTasks().indexOf(Key.create(Task.class, task.getTask_id()));
        // Set the next tasks for the lesson
        if (index < lesson.getTasks().size()-1){
            // Not last task so we have a next task
            model.addAttribute("next_task", lesson.getTasks().get(index+1).getId());
        }else{
            // The task is the last task, no next task
            model.addAttribute("next_task", -1);
        }
    }

    /**
     * Gets the task titles and status from each task in @param lesson.
     * Adds the values to the model
     * @param lesson
     * @param model
     */
    public void get_task_navigation(Lesson lesson, Model model){
        //load all tasks to check which are completed
        Objectify ofy = OfyService.ofy();
        Map<Key<Task>, Task> mapOfTasks = ofy.load().keys(lesson.getTasks());
        //list of tasks in lesson
        List<Task> t = new ArrayList<>(mapOfTasks.values());
        //list of booleans checking if task is completed
        List<Boolean> taskStatusList = new ArrayList<>();
        //list of task title
        List<String> taskTitleList = new ArrayList<>();

        //iterate through tasks
        for (int i = 0; i < t.size(); i++){
            taskStatusList.add(t.get(i).isIn_progress());
            taskTitleList.add(t.get(i).getTitle());
        }
        //load booleans and titles into thymeleaf model
        model.addAttribute("task_statuses", taskStatusList);
        model.addAttribute("task_titles", taskTitleList);
    }

    /**
     *
     * Gets the next task in lesson to edit based on index of last deleted task.
     * @param lesson -- lesson
     * @param index -- index of last task deleted
     * @return
     */
    public Task get_edit_task(Lesson lesson, int index){
        Objectify ofy = OfyService.ofy();
        if (index == -1){
            index = 0;
        }
        //set current task to edit
        if(lesson.getTasks().size() == 0){
            Task task = new Task();
            lesson.getTasks().add(ofy.save().entity(task).now());
            return task;

        }else{
            if(index >= lesson.getTasks().size()){
                index = lesson.getTasks().size() - 1;
            }
            Key k = lesson.getTasks().get(index);
            Task task = (Task) ofy.load().key(k).now();
            return task;
        }
    }

    /**
     * Deletes a task from a lesson, returns index of that deleted task.
     * @param lesson -- lesson
     * @param task_key -- key of task to deleted
     * @return -- index of deleted task. Returns -1 if task is not in lesson.
     */
    public int delete_task(Lesson lesson, Key task_key){
        int index = -1;
        if(lesson.getTasks().contains(task_key)){
            index = lesson.getTasks().indexOf(task_key);
            lesson.getTasks().remove(task_key);
        }
        return index;
    }

}
