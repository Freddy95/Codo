package com.dolphinblue.controller;

import com.dolphinblue.models.*;
import com.dolphinblue.service.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.*;


/**
 * Created by FreddyEstevez on 4/13/17.
 * Handles requests for creating/updating/saving created lessons.
 */
@Controller
@EnableWebMvc
public class CreateLessonController {
    @Autowired
    LessonService lessonService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    CodoUserService userService;
    @Autowired
    TaskService taskService;

    /**
     * Returns the page to edit a lesson
     * @param model lesson model retrieved from database
     * @return edit-lesson template
     */
    @RequestMapping("/debug-edit-lesson")
    public String debug_edit_lesson(@CookieValue("token") String token, Model model){
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return "redirect:login";
        }

        Lesson l = new Lesson();
        l.setTitle("Default Title");
        l.setDescription("Default Description");
        l.setShared(true);
        model.addAttribute("lesson",l);

        //TODO: add code to fetch lesson from url and load it into the page
        return "edit-lesson";
    }

    /**
     * This route should be called when a user first wants to create a new lesson.
     * Creates a lesson object and saves it in the datastore.
     * @param model -- thymeleaf model
     * @return -- editlesson page.
     */
    @RequestMapping(value = "/createlesson", method = RequestMethod.GET)
    public String get_create_lesson_page(@CookieValue("token") String token, Model model){
        // Check if the user is still authenticated by google
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return null;
        }

        Objectify ofy = OfyService.ofy();

        // Get the google id token from the authentication token from the browser cookie
        GoogleIdToken googletoken = authenticationService.getIdToken(token, new JacksonFactory(), new NetHttpTransport());

        // Use google's token to contact google app engine's user api and get the user info
        String id = userService.getUserId(googletoken);


        Lesson l = new Lesson();
        l.setTitle("Default Title.");
        l.setDescription("Default Description.");
        l.setCreator_id(id);
        //save lesson to datastore
        ofy.save().entity(l).now();

        String requestUrl = "/createlesson/" + l.getLesson_id();
        return "redirect:" + requestUrl;
    }



    /**
     * This route should be called when a user wants to open their own created lesson.
     * Should add lesson_id, lesson, and list of tasks in lesson to model.
     * @param model -- thymeleaf model
     * @param id -- id of lesson to edit
     * @return -- editlesson page.
     */
    @RequestMapping(value = "/createlesson/{lessonId}", method = RequestMethod.GET)
    public String get_created_lesson(@CookieValue("token") String token, @PathVariable(value = "lessonId") long id, Model model){
        // Check if the user is still authenticated by google
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return "redirect:/login";
        }

        Objectify ofy = OfyService.ofy();
        Lesson lesson = ofy.load().type(Lesson.class).id(id).now();


        model.addAttribute("lesson_id", lesson.getLesson_id());
        List<Key<Task>> task_keys = lesson.getTasks();
        List<Task> tasks = lessonService.get_tasks_by_id(task_keys);
        model.addAttribute("tasks", tasks);
        model.addAttribute("lesson", lesson);
        return "edit-lesson";
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
    @ResponseBody
    public long create_task(Model model, @PathVariable(value = "lessonId") long id){
        Objectify ofy = OfyService.ofy();
        Lesson lesson = ofy.load().type(Lesson.class).id(id).now();
        Task task = new Task();
        task.setTitle("New Task");
        lesson.getTasks().add(ofy.save().entity(task).now());
        ofy.save().entity(lesson).now();
        return task.getTask_id();
    }


    /**
     * This route should be called when a user wants edit an already created task.
     * Gets task and adds it to thymeleaf model.
     * @param model -- thymeleaf model
     * @param id -- lesson id
     * @param taskId -- task id
     * @return -- create task page
     * TODO: add query parameter to determine if task created should be block or freecode.
     */
    @RequestMapping(value = "/createlesson/{lessonId}/createtask/{taskId}", method = RequestMethod.GET)
    public String get_create_task_page(Model model, @PathVariable(value = "lessonId") long id, @PathVariable(value = "taskId") long taskId){
        Objectify ofy = OfyService.ofy();
        Task task = ofy.load().type(Task.class).id(taskId).now();
        Lesson lesson = ofy.load().type(Lesson.class).id(id).now();
        model.addAttribute("task", task);
        model.addAttribute("lesson", lesson);
        if(task.getFreecode() == null){
            //blocktask
            return "edit-block-task";
        }
        return "edit-freecode-task";
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
     * @param id -- lesson id
     * @param taskId -- id of task to delete
     * @param model -- thymeleaf model
     * TODO: return create task page?
     * @return --
     */
    @RequestMapping(value = "/createlesson/{lessonId}/createtask/{taskId}/delete", method = RequestMethod.GET)
    public String delete_task(@PathVariable(value = "lessonId") long id,  @PathVariable(value = "taskId") long taskId, Model model){
        Objectify ofy = OfyService.ofy();
        //get key of task
        Key task_key = Key.create(Task.class, taskId);
        //delete task from datastore
        ofy.delete().type(Task.class).id(taskId).now();

        //Get lesson and delete the task key from the list of tasks.
        Lesson lesson = ofy.load().type(Lesson.class).id(id).now();

        taskService.delete_task(lesson, task_key);
        ofy.save().entity(lesson).now();
        String requestUrl = "/createlesson/" + id;
        return "redirect:" + requestUrl;
    }


    /**
     * Saves the current task in the lesson user is creating.
     * @param token -- user access token
     * @param id -- lesson id
     * @param taskId -- id of task to delete
     * @param model -- thymeleaf model
     * TODO: return create task page?
     * @return --
     */
    @RequestMapping(value = "/createlesson/{lessonId}/createtask/{taskId}", method = RequestMethod.POST)
    public @ResponseBody String save_task(@CookieValue("token") String token,  @PathVariable(value = "lessonId") long id,  @PathVariable(value = "taskId") long taskId, @RequestBody SaveTaskModel task_model, Model model){

        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return "redirect:/login";
        }

        Objectify ofy = OfyService.ofy();

        Task task = ofy.load().type(Task.class).id(taskId).now();
        taskService.task_model_to_task(task, task_model);
        ofy.save().entity(task).now();
        model.addAttribute("task", task);
        model.addAttribute("task_id", taskId);
        model.addAttribute("lesson_id", id);
        if(task.getFreecode() == null){
            return "edit-block-task";
        }

        return "edit-freecode-task";
    }


    /**
     * This route should be called when a user wants to save a created lesson.
     * Creates a lesson object and saves it in the datastore.
     */
    @RequestMapping(value = "/createlesson/{lessonId}", method = RequestMethod.POST)
    public @ResponseBody void save_lesson(@PathVariable(value = "lessonId") long id, @RequestBody SaveLessonModel lesson_model){


        Objectify ofy = OfyService.ofy();

        Lesson lesson = ofy.load().type(Lesson.class).id(id).now();
        lessonService.lesson_model_to_lesson(lesson, lesson_model);
        //change date last edited.
        lesson.setLast_edited(new Date());
        ofy.save().entity(lesson).now();
    }


    /**
     * This route should be called when a user wants to delete a lesson
     * Deletes lesson and all its tasks from the datastore.
     * @param id -- id of lesson
     * @return -- user page.
     */
    @RequestMapping(value = "/deletelesson/{lessonId}", method = RequestMethod.GET)
    public String delete_lesson(@CookieValue("token") String token, @PathVariable(value = "lessonId") long id){

        Objectify ofy = OfyService.ofy();
        Lesson lesson = ofy.load().type(Lesson.class).id(id).now();
        for (int i = 0; i < lesson.getTasks().size(); i++){
            taskService.delete_task(lesson, lesson.getTasks().get(i));
        }
        ofy.delete().entity(lesson).now();
        return "redirect:/user";

    }
}
