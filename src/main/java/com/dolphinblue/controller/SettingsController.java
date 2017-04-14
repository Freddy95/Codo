package com.dolphinblue.controller;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;
import com.dolphinblue.models.User;
import com.dolphinblue.service.AuthenticationService;
import com.dolphinblue.service.CodoUserService;
import com.dolphinblue.service.LessonService;
import com.dolphinblue.service.OfyService;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Henry Chin on 4-1-17.
 * This controller should handle all requests for the settings page.
 */
@Controller
public class SettingsController {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    CodoUserService userService;
    @Autowired
    LessonService lessonService;

    /**
     * gets user and adds him to @param model
     * @return
     */
    @RequestMapping(value = "/user/settings", method = RequestMethod.GET)
    public String get_settings_page(@CookieValue(value = "token",defaultValue = "") String token, Model model){
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(isAuthenticated) {
            GoogleIdToken googleIdToken = authenticationService.getIdToken(token, new JacksonFactory(),new NetHttpTransport());
            String id = userService.getUserId(googleIdToken);
            Objectify ofy = OfyService.ofy();

            User user = ofy.load().type(User.class).id(id).now();
            model.addAttribute("user", user);
            return "settings";
        }else{
            return "redirect:/login";
        }
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String get_settings_page(Model model){
        // Objectify ofy = OfyService.ofy();

        // User user = ofy.load().type(User.class).id(id).now();
        // model.addAttribute("user", user);
        return "settings";
    }

    /**
     * Change the username for a particular user
     * @param token -- the login token of the user
     * @param userId -- the user id of the user to update
     * @param user -- the user object containing the new username
     * @return -- the HTML page to be loaded
     */
    @RequestMapping(value = "/settings/updateusername/{userId}", method = RequestMethod.POST)
    public String update_username(@CookieValue("token") String token, @PathVariable(value = "taskId") Long userId, @RequestBody User user) {
        // Check if the user is still authenticated by google
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return null;
        }

        // Create the objectify object to store stuff from the datastore
        Objectify ofy = OfyService.ofy();

        // Save the changed user to the datastore
        ofy.save().entity(user);

        // Return the string representing the HTML settings page
        return "settings";
    }

    /**
     * Delete a user from the database with the specified user id
     * @param token -- the login token of the user
     * @param userId -- the id of the user to be deleted
     * @return -- the HTML page to be loaded
     */
    @RequestMapping(value = "/settings/deleteuser/{userId}", method = RequestMethod.DELETE)
    public String delete_user(@CookieValue("token") String token, @PathVariable(value = "taskId") Long userId) {
        // Check if the user is still authenticated by google
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return null;
        }

        // Create the objectify object to get stuff from the datastore
        Objectify ofy = OfyService.ofy();

        // Get the user's lessons from the datastore
        User user = ofy.load().type(User.class).id(userId).now();
        List<Key<Lesson>> lesson_keys = user.getLessons();

        // Delete all of the lessons and tasks for that lesson for the user
        for(Key lesson_key : lesson_keys) {
            // Get the lesson to be deleted
            Lesson lesson = ofy.load().type(Lesson.class).id(lesson_key.getId()).now();

            // Get all of the tasks for the lesson and delete them
            List<Key<Task>> task_keys = lesson.getTasks();
            for(Key task_key : task_keys) {
                // Get the task to be deleted
                Task task = ofy.load().type(Task.class).id(task_key.getId()).now();
                // Delete the task
                ofy.delete().entity(task).now();
            }

            // Delete the lesson
            ofy.delete().entity(lesson).now();
        }

        // Once the lesson and tasks have been deleted, delete the user from the datastore
        ofy.delete().entity(user).now();

        // Return the string representing the HTML login page
        return null;
    }

    @RequestMapping(value = "/settings/resetall/{userId}", method = RequestMethod.POST)
    public String reset_all(@CookieValue("token") String token, @PathVariable(value = "taskId") Long userId) {
        // Check if the user is still authenticated by google
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return null;
        }

        // Create the objectify object to store stuff from the datastore
        Objectify ofy = OfyService.ofy();

        // Get the user's lessons from the datastore
        User user = ofy.load().type(User.class).id(userId).now();
        List<Key<Lesson>> lesson_keys = user.getLessons();

        // Reset all of the tasks for each lesson the user has started
        for(Key lesson_key : lesson_keys) {
            // Get the lesson to be reset
            Lesson lesson = ofy.load().type(Lesson.class).id(lesson_key.getId()).now();

            // Get all of the tasks for the lesson and reset them
            List<Key<Task>> task_keys = lesson.getTasks();
            for(Key task_key : task_keys) {
                // Get the task to be reset
                Task task = ofy.load().type(Task.class).id(task_key.getId()).now();
                // Reset the task
                lessonService.reset_task(task);
            }
        }

        // Return the string representing the HTML settings page
        return "settings";
    }

}
