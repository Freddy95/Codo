package com.dolphinblue.controller;

import com.dolphinblue.models.Block;
import com.dolphinblue.models.Block.Type;
import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;
import com.dolphinblue.service.LessonJSONService;
import com.dolphinblue.service.LessonService;
import com.dolphinblue.service.OfyService;
import java.util.ArrayList;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by FreddyEstevez on 3/29/17.
 * This controller should handle all requests for the task page.
 *
 * Request for a Task
 * - Request for Editor Blocks
 * - Request for Toolbox Blocks
 * Update Task
 * - Update Block Order
 * - Update Block Location for if it moves from Toolbox to Editor and the other way around
 * Restart Task
 * Restart Lesson
 * Update Lesson
 */

@Controller
public class TaskController {
    @Autowired
    LessonService lessonService;

    /**
     * For debugging the block task pages
     * @param model
     * @return
     */
    @RequestMapping(value = "/debug-block-task", method = RequestMethod.GET)
    public String get_task(Model model){
        Lesson l = new Lesson();
        l.setTitle("First Lesson");
        Task t = new Task();
        t.setTitle("First Block Task");
        t.setInstructions("These are the instructions.");
        t.setHint("This is the hint.");
        t.setTest_case("x = 1;");
        t.setExpected_output("3");

        ArrayList<Block> toolbox = new ArrayList<Block>();
        ArrayList<Block> editor = new ArrayList<Block>();

        toolbox.add(new Block(new Long(1), "x = 2;", Type.ASSIGN, true));
        toolbox.add(new Block(new Long(2), "x += 5;", Type.ASSIGN, true));

        editor.add(new Block(new Long(2), "x += 1;", Type.ASSIGN, true));
        editor.add(new Block(new Long(2), "console.log(x);", Type.ASSIGN, true));

        model.addAttribute("lesson", l);
        model.addAttribute("toolbox", toolbox);
        model.addAttribute("editor", editor);
        model.addAttribute("task", t);

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
    @RequestMapping(value = "/task", method = RequestMethod.GET)
    public String get_task(@RequestParam(value = "id") Long id, Model model){
        Objectify ofy = OfyService.ofy();
        Task task = ofy.load().type(Task.class).id(id).now();
        model.addAttribute("task", task);

        List<Key<Block>> e_block_keys = task.getEditor();
        List<Block> editor_blocks = lessonService.get_blocks_by_id(e_block_keys);
        model.addAttribute("editor_blocks", editor_blocks);

        List<Key<Block>> t_block_keys = task.getToolbox();
        List<Block> toolbox_blocks = lessonService.get_blocks_by_id(t_block_keys);
        model.addAttribute("toolbox_blocks", toolbox_blocks);

        return "block-task";
    }

    /**
     * Updates a task
     * @param task
     */
    @RequestMapping(value = "/updatetask", method = RequestMethod.POST)
    public void update_task(@RequestBody Task task){ // Figure out later getting blocks
        List<Key<Block>> editor_blocks = task.getEditor();
        List<Key<Block>> toolbox_blocks = task.getToolbox();

        lessonService.update_blocks(editor_blocks);

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
     * Updates a lesson
     * @param model
     * @return
     */
    @RequestMapping(value = "/updatelesson", method = RequestMethod.POST)
    public String update_lesson(@RequestBody Lesson lesson, Model model){

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
    @RequestMapping(value = "/jsonlesson")
    public String lessons_from_json(@RequestParam(value = "path") String path){
        Lesson l = LessonJSONService.create_lesson_from_JSON(path);
        OfyService.ofy().save().entity(l).now();
        return "index";
    }

}
