package com.dolphinblue.controller;

import com.dolphinblue.models.Block;
import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;
import com.dolphinblue.service.LessonJSONService;
import com.dolphinblue.service.LessonService;
import com.dolphinblue.service.OfyService;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by FreddyEstevez on 3/29/17.
 * This controller should handle all requests for the task page.
 *
 * Request for Task
 * Request for Editor Blocks
 * Request for Toolbox Blocks
 * Update Block Order
 * Update Block Location for if it moves from Toolbox to Editor and the other way around
 * Restart Task
 * Restart Lesson
 */
@Controller

public class TaskController {

    @Autowired
    LessonService lessonService;
    @RequestMapping(value = "/debug-block-task", method = RequestMethod.GET)
    public String get_task(Model model){
        return "block-task";
    }

    /**
     * gets tasks for a lesson
     * also gets the blocks for each task
     * 
     * TODO: Add a way to differentiate between which type of task to get.
     *
     * @param id - id of lesson to get tasks from
     * @return
     */
    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    public String get_tasks(@RequestParam(value = "lesson") Long id, Model model){
        Objectify ofy = OfyService.ofy();
        Lesson lesson = ofy.load().type(Lesson.class).id(id).now();
        List<Key<Task>> task_keys = lesson.getTasks();
        List<Task> tasks = lessonService.get_tasks_by_id(task_keys);
        model.addAttribute("tasks", tasks);
        return "lesson";
    }

    /**
     * Updates a task
     * @param task
     */
    @RequestMapping(value = "/updatetask", method = RequestMethod.POST)
    public void update_task(@RequestBody Task task){

        OfyService.ofy().save().entity(task);

    }

    /**
     * Restarts a Lesson to original version
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/restartlesson", method = RequestMethod.GET)
    public String restart_lesson(@RequestParam(value = "id") Long id, Model model){
        Objectify ofy = OfyService.ofy();
        Lesson l = ofy.load().type(Lesson.class).id(id).now();
        model.addAttribute("lesson", l);
        return "lesson";
    }

    /**
     * restart a task
     * @param id the Id of the original task to retrieve from datastore
     * @param model
     * @return
     */
    @RequestMapping(value = "/restarttask", method = RequestMethod.GET)
    public String restart_task(@RequestParam(value = "id") Long id, Model model){
        Objectify ofy = OfyService.ofy();
        Task t = ofy.load().type(Task.class).id(id).now();
        model.addAttribute("task", t);
        return "lesson";
    }

    /**
     * Makes a new lesson and saves it in the datastore
     * Currently all values of that lesson are the null.
     * @param model
     * @return
     */
    @RequestMapping(value = "/makelesson")
    public String make_lesson(Model model){
        Lesson l = new Lesson();
        model.addAttribute("lesson", l);
        OfyService.ofy().save().entity(l);
        return "index";
    }

    /**
     * Gets a lesson based on its id
     * @param id - id of lesson to retrieve
     * @param model
     * @return
     */
    @RequestMapping(value = "/getlesson")
    public String get_lesson(@RequestParam(value = "id") Long id, Model model){
        model.addAttribute("lesson", OfyService.ofy().load().type(Lesson.class).id(id).now());
        return "lessoncard";
    }

    /**
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/updatelesson", method = RequestMethod.POST)
    public String update_lesson(@RequestBody Lesson lesson, Model model){
        // get_percent_complete(lesson) --> returns a double

        lesson.setPercent_complete(lessonService.get_percent_complete(lesson));

        OfyService.ofy().save().entity(lesson);
        return "lessoncard";
    }

    /**
     * Creates a lesson and saves it in the datastore via JSON.
     * NOTE: when supplying the path in url it should be WEB-INF/FILENAME.json
     * dont put quotes ex - "WEB-INF-/FILENAME.json"
     * @param path - path to json file
     * @return
     */
    @RequestMapping(value = "/createlesson")
    public String create_lesson(@RequestParam(value = "path") String path){
        Lesson l = LessonJSONService.create_lesson_from_JSON(path);
        OfyService.ofy().save().entity(l).now();
        return "index";
    }

}
