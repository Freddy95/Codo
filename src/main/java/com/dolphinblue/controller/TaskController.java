package com.dolphinblue.controller;

import com.dolphinblue.models.Block;
import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;
import com.dolphinblue.models.User;
import com.dolphinblue.service.OfyService;
import com.googlecode.objectify.Objectify;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    /**
     * gets tasks for a lesson
     * also gets the blocks for each task
     * @return
     */
    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    public String get_task(@RequestParam(value = "lesson") Lesson lesson, Model model){
        Objectify ofy = OfyService.ofy();

        model.addAttribute("tasks", lesson.getTasks());
        return "lesson";
    }

    /**
     * Updates blocks for program and toolbox
     * @param edit
     * @param prog
     * @param task
     */
    @RequestMapping(value = "/updatelesson", method = RequestMethod.POST)
    public void update_task(@ModelAttribute List<Block> edit, @ModelAttribute List<Block> prog, Task task){
        Objectify ofy = OfyService.ofy();
        ofy.save().entity(task);

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
     * @param id
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
}
