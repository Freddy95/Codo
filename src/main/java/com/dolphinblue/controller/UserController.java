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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

            // Create user's own main lesson objects and save them in the datastore
            lessonService.create_main_lessons_for_user(user);

            // Add the user information to the thymeleaf model
            model.addAttribute("user_info", user);

            // Get the main site lessons for the user and add them to the thymeleaf model
            List<Lesson> main_lessons = lessonService.get_main_lessons_by_user(user);
            model.addAttribute("main_lessons", main_lessons);

            // Fixes a bug with user login
            Lesson l;
            if (user.getCurrent_lesson() == null) {
                try {
                    l = main_lessons.get(0);
                } catch (Exception e) {
                    l = new Lesson();
                }
                user.setCurrent_lesson(l);
                // Made change to user object must save to datastore
                ofy.save().entity(user).now();
            } else {
                l = (Lesson) ofy.load().key(user.getCurrent_lesson()).now();
            }

            model.addAttribute("lesson", l);

            // Return the string representing the HTML user page
            return "user";

        } else {
            return "redirect:login";
        }
    }

}
