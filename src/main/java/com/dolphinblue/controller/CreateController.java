package com.dolphinblue.controller;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.SaveTaskModel;
import com.dolphinblue.models.Task;
import com.dolphinblue.service.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;

/**
 * Created by FreddyEstevez on 4/13/17.
 * Handles requests for creating/updating/saving created lessons.
 */
@Controller
@EnableWebMvc
public class CreateController {
    @Autowired
    LessonService lessonService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    CodoUserService userService;
    @Autowired
    TaskService taskService;

    /**
     * Saves the current block - task in the created lesson
     * @param id -- lesson id
     * @param taskId -- id of task to be saved
     * @param blocks -- list of blocks to be saved
     * @return
     */
    @RequestMapping(value = "/savecreatedlesson/{lessonId}/task/{taskId}", method = RequestMethod.POST)
    public @ResponseBody void save_created_lesson(@PathVariable(value = "lessonId") long id, @PathVariable(value = "taskId") long taskId, @RequestBody SaveTaskModel blocks){
        System.out.println(blocks.toString());
        System.out.println("toolbox");
        System.out.println(blocks.getToolbox());
        System.out.println("editor");
        System.out.println(blocks.getEditor());
        for(int i = 0; i < blocks.getEditor().getBlocks().size(); i++){
            System.out.println(blocks.getEditor().getBlocks().get(i));
        }
        for(int i = 0; i < blocks.getToolbox().getBlocks().size(); i++){
            System.out.println(blocks.getToolbox().getBlocks().get(i));
        }
    }

    /**
     * This route should be called when a user first wants to create a new lesson.
     * Creates a lesson object and saves it in the datastore.
     * @param model -- thymeleaf model
     * @return -- createlessonpage page.
     */
    @RequestMapping(value = "/createlesson", method = RequestMethod.GET)
    public String get_create_lesson_page(Model model){
        Objectify ofy = OfyService.ofy();
        Lesson l = new Lesson();
        Task t  = new Task();
        //add task to lesson and save to datastore
        l.getTasks().add(ofy.save().entity(t).now());
        //save lesson to datastore
        ofy.save().entity(l);
        model.addAttribute("lesson", l.getLesson_id());
        return "createlessonpage";
    }

    /**
     * This route should be called when a user wants to add a new task to a created lesson.
     * Creates task object, saves it in datastore, and save it to list of tasks in lesson object.
     * @param model -- thymeleaf model
     * @param id -- lesson id
     * @return -- create task page
     * TODO: add query parameter to determine if task created should be block or freecode.
     */
    @RequestMapping(value = "/createlesson/{lessonId}/createtask", method = RequestMethod.GET)
    public String get_create_task_page(Model model, @PathVariable(value = "lessonId") long id){
        Objectify ofy = OfyService.ofy();
        Lesson lesson = ofy.load().type(Lesson.class).id(id).now();
        Task task = new Task();
        lesson.getTasks().add(ofy.save().entity(task).now());
        model.addAttribute("lesson", lesson.getLesson_id());
        model.addAttribute("task", task.getTask_id());
        return "createtaskpage";
    }
    /**
     * This route should be called when a user first wants to create a new freecode lesson.
     * Creates a lesson and task object, saves them in datastore.
     * @param model -- thymeleaf model
     * @return -- editfrecodetask page.
     */
    @RequestMapping(value = "/createlesson/freecode", method = RequestMethod.GET)
    public String get_create_freecode(Model model){
        Objectify ofy = OfyService.ofy();
        Lesson l = new Lesson();
        Task t  = new Task();
        ArrayList<String> examples = new ArrayList<>();
        examples.add("hello");
        examples.add("world");

        t.setTest_case(examples);
        t.setExpected_output(examples);
        //add task to lesson and save to datastore
        l.getTasks().add(ofy.save().entity(t).now());
        //save lesson to datastore
        ofy.save().entity(l);
        model.addAttribute("lesson", l);
        model.addAttribute("task", t);
        return "edit-freecode-task";
    }



    /**
     * Deletes the current task in the lesson user is creating.
     * @param token -- user access token
     * @param id -- lesson id
     * @param taskId -- id of task to delete
     * @param model -- thymeleaf model
     * TODO: return create task page?
     * @return --
     */
    @RequestMapping(value = "/savecreatedlesson/{lessonId}/task/{taskId}", method = RequestMethod.DELETE)
    public String delete_task(@CookieValue("token") String token,  @PathVariable(value = "lessonId") long id,  @PathVariable(value = "taskId") long taskId, Model model){

        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return "redirect:/login";
        }

        Objectify ofy = OfyService.ofy();
        //get key of task
        Key task_key = Key.create(Task.class, taskId);
        //delete task from datastore
        ofy.delete().type(Task.class).id(taskId).now();

        //Get lesson and delete the task key from the list of tasks.
        Lesson lesson = ofy.load().type(Lesson.class).id(id).now();

        //delete task and get index of deleted task
        int index = taskService.delete_task(lesson, task_key);

        //find which task to add to model
        Task task = taskService.get_edit_task(lesson, index);
        model.addAttribute("task", task);

        //add lesson to model
        model.addAttribute("lesson", lesson);
        return "";
    }
}
