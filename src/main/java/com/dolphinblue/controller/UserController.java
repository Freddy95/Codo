package com.dolphinblue.controller;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.User;
import com.dolphinblue.service.AuthenticationService;
import com.dolphinblue.service.CodoUserService;
import com.dolphinblue.service.LessonService;
import com.dolphinblue.service.OfyService;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.googlecode.objectify.Objectify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by FreddyEstevez on 3/29/17.
 *
 * This controller handles all requests for the user page.
 */
@Controller
public class UserController {
    // Use Autowired to get an instance of the different Services
    @Autowired
    LessonService lessonService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    CodoUserService userService;

    /**
     * This method is for loading the user page, or home page of the site where the user can view
     *  lessons, badges, and percentages that they've completed.
     * @param token -- the login token for the user
     * @param model -- the thymeleaf model that we use to pass data to the front end
     * @return -- the HTML page to be rendered
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String get_user_page(@CookieValue(value="token",defaultValue = "") String token, Model model) {
        // Check to see if the user is authenticated by google
        boolean isAuthenticate = authenticationService.isAuthenticated(token, new JacksonFactory(), new NetHttpTransport());
        if (isAuthenticate) {
            // Create an instance of the objectify object for requests to the datastore
            Objectify ofy = OfyService.ofy();

            // Get the google id token from the authentication token from the browser cookie
            GoogleIdToken googletoken = authenticationService.getIdToken(token, new JacksonFactory(), new NetHttpTransport());

            // Use google's token to contact google app engine's user api and get the user info
            String id = userService.getUserId(googletoken);

            // Load the user's information from the datastore and store it in a user object
            User user = ofy.load().type(User.class).id(id).now();
            if(user == null){
                return "redirect:login";
            }

            model.addAttribute("username",user.getUsername());

            // Get the badges for the user
            List<String> badges = userService.get_badges(user.getLessons());
            model.addAttribute("badges", badges);

            // Create user's own main lesson objects and save them in the datastore
            lessonService.create_main_lessons_for_user(user);

            // Add the user information to the thymeleaf model
            model.addAttribute("user_info", user);
            model.addAttribute("messages",user.getAdmin_msg());

            //clear the admin messages after render
            user.setAdmin_msg(null);

            // Get the main site lessons for the user and add them to the thymeleaf model
            List<Lesson> main_lessons = lessonService.get_main_lessons_by_user(user);
            Collections.sort(main_lessons);
            for (int i = 0; i < main_lessons.size(); i++) {
                Lesson lesson = main_lessons.get(i);
                int roundedPercent = lessonService.get_percent_complete(lesson);
                lesson.setPercent_complete(roundedPercent);
            }
            model.addAttribute("main_lessons", main_lessons);
            // Get the owned lessons for the user
            List<Lesson> own_lessons = lessonService.get_own_lessons(user);
            model.addAttribute("own_lessons", own_lessons);
            // Get the shared lessons for the user
            List<Lesson> shared_lessons = lessonService.create_shared_lessons_for_user(user);
            for (int i = 0; i < shared_lessons.size(); i++) {
                Lesson lesson = shared_lessons.get(i);
                int roundedPercent = lessonService.get_percent_complete(lesson);
                lesson.setPercent_complete(roundedPercent);
            }
            model.addAttribute("shared_lessons", shared_lessons);

            // Fixes a bug with user login
            Lesson l;
            //should only be null if user is a new user
            if (user.getCurrent_lesson() == null) {
                try {
                    l = main_lessons.get(0);
                    user.setCurrent_lesson(l);
                } catch (Exception e) {
                    return "redirect:user";
                }
            } else {
                l = (Lesson) ofy.load().key(user.getCurrent_lesson()).now();
            }

            model.addAttribute("lesson", l);

            // Check to see if the user is a new user
            if (user.isUser_tutorial()) {
                if(user.getEmail().contains("cse308")) {
                    user.setIs_moderator(true);
                } else {
                    user.setIs_moderator(false);
                }
            }

            System.out.println("Messages: " + user.getAdmin_msg());
            // Remove the admin_msgs since the user has been notified
            List<String> new_admin_msg = new ArrayList<>();
            user.setAdmin_msg(new_admin_msg);

            // Made change to user object must save to datastore
            ofy.save().entity(user).now();

            // Return the string representing the HTML user page
            return "user";

        } else {
            return "redirect:login";
        }
    }

    @RequestMapping(value = "/user/toggletutorial", method = RequestMethod.POST)
    public void toggle_tutorial(@CookieValue("token") String token,HttpServletResponse resp){
        // Check if the user is still authenticated by google
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            resp.setStatus(500);
        }

        String userId = userService.getUserId(authenticationService.getIdToken(token,new JacksonFactory(),new NetHttpTransport()));
        // Create the objectify object to store stuff from the datastore
        Objectify ofy = OfyService.ofy();

        // Get the user object from the datastore
        User user = ofy.load().type(User.class).id(userId).now();

        // Reset the tutorial booleans and save them to the datastore
        user.setUser_tutorial(false);

        // Save the user to the datastore
        ofy.save().entity(user).now();

        //give the ok response
        resp.setStatus(200);
    }

    @RequestMapping(value = "/editusername", method = RequestMethod.POST)
    public void create_username(@RequestBody String newUsername, @CookieValue("token") String token,HttpServletResponse resp){
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            resp.setStatus(500);
        }

        String userId = userService.getUserId(authenticationService.getIdToken(token,new JacksonFactory(),new NetHttpTransport()));
        // Create the objectify object to store stuff from the datastore
        Objectify ofy = OfyService.ofy();

        // Get the user object from the datastore
        User user = ofy.load().type(User.class).id(userId).now();

        // Check to see if the username already exists
        boolean exists = userService.check_username_exist(newUsername);

        // if(exists) {
        //     // respond with a bad response
        //     resp.setStatus(400);
        // } else {
            // set the new username
            user.setUsername(newUsername);

            // save the user to the datastore
            ofy.save().entity(user).now();

            // send a good response
            resp.setStatus(200);
        // }
    }

}
