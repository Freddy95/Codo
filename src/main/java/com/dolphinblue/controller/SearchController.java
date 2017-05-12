package com.dolphinblue.controller;

import com.dolphinblue.models.*;
import com.dolphinblue.models.Block.Type;
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

import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * Created by FreddyEstevez on 3/29/17.
 *
 * This controller handles all requests for the task page.
 */
@Controller
@EnableWebMvc
public class SearchController {
    // Use Autowired to get an instance of the different Services
    @Autowired
    LessonService lessonService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    CodoUserService userService;

    /**
     *  This is for debugging a block task
     * @param model -- the thymeleaf model used to send data to the front end
     * @return -- the HTML page to be loaded
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@CookieValue("token") String token){
        // Check to see if the user is authenticated by google
        boolean isAuthenticate = authenticationService.isAuthenticated(token, new JacksonFactory(), new NetHttpTransport());

        if (!isAuthenticate) {
            return "redirect:/login";
        }
        return "search";
    }

    @RequestMapping(value = "/search/request",  method = RequestMethod.POST)
    public @ResponseBody
    List<Lesson> search_lessons(@CookieValue("token") String token, @RequestBody SearchObject query) {
        try {
            // Check to see if the user is authenticated by google
            boolean isAuthenticate = authenticationService.isAuthenticated(token, new JacksonFactory(), new NetHttpTransport());
            if (isAuthenticate) {
                // Create an instance of the objectify object for requests to the datastore
                Objectify ofy = OfyService.ofy();

                // Get the google id token from the authentication token from the browser cookie
                GoogleIdToken googletoken = authenticationService.getIdToken(token, new JacksonFactory(), new NetHttpTransport());

                // Use google's token to contact google app engine's user api and get the user info
                String id = userService.getUserId(googletoken);

                System.out.println(id);

                // Load the user's information from the datastore and store it in a user object
                User user = ofy.load().type(User.class).id(id).now();
                if(user == null){
                    return null;
                }

                // TODO: Implement search logic.
                List<Lesson> searchedLessons = lessonService.get_main_lessons_by_user(user);
                return searchedLessons;
            } else {
                return null;
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}