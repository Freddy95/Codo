package com.dolphinblue.controller;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;
import com.dolphinblue.models.User;
import com.dolphinblue.service.AuthenticationService;
import com.dolphinblue.service.CodoUserService;
import com.dolphinblue.service.OfyService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.googlecode.objectify.Objectify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Matt on 4/27/17.
 */
@Controller
public class LessonController {

    @Autowired
    CodoUserService userService;

    @Autowired
    AuthenticationService authenticationService;
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

    public String get_freecode_task(@CookieValue("token") String token, Long task_id, Model model) {
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return "redirect:login";
        }

        // Create an objectify object to make requests to the datastore
        Objectify ofy = OfyService.ofy();

        // Pull the task information from the datastore
        Task task = ofy.load().type(Task.class).id(task_id).now();
        model.addAttribute("freecodeTask", task);

        // Populate the HTML lesson page with the correct task
        //String requestUrl = "/lesson/" + lessonId + "/task/" + task.getTask_id();
        //return "redirect:" + requestUrl;

        return "edit-freecode-task";
    }

    public Task save_freecode_task(@CookieValue("token") String token, Task freecodeTask) {
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return null;
        }

        // Create the objectify object to get stuff from the datastore
        Objectify ofy = OfyService.ofy();

        // Save the changes to the datastore
        ofy.save().entity(freecodeTask);

        // TODO: add save freecode task
        return freecodeTask;
    }

}