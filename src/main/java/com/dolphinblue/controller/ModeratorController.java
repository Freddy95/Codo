package com.dolphinblue.controller;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.User;
import com.dolphinblue.service.AuthenticationService;
import com.dolphinblue.service.CodoUserService;
import com.dolphinblue.service.LessonService;
import com.dolphinblue.service.OfyService;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.googlecode.objectify.Objectify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devon on 5/12/17.
 * This handles all of the requests for the moderator actions, including ratings and reports.
 */
@Controller
@EnableWebMvc
public class ModeratorController {

    // Use Autowired to get an instance of the different Services
    @Autowired
    LessonService lessonService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    CodoUserService userService;

    @RequestMapping(value = "/lesson/{lesson_id}/report", method = RequestMethod.POST)
    public void remove_from_public_domain(@CookieValue("token") String token, HttpServletResponse resp, @PathVariable(value = "lesson_id") Long lessonId, @RequestBody String message){
        // Check if the user is still authenticated by google
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated) {
            // If the user isn't properly authenticated send them back to the login page
            resp.setStatus(500);
        }

        String userId = userService.getUserId(authenticationService.getIdToken(token,new JacksonFactory(),new NetHttpTransport()));
        // Create the objectify object to store stuff from the datastore
        Objectify ofy = OfyService.ofy();

        // Get the lesson object from the datastore
        Lesson lesson = ofy.load().type(Lesson.class).id(lessonId).now();
        // Get the original lesson object from the datastore
        Lesson original_lesson = ofy.load().type(Lesson.class).id(lesson.getOriginal_lesson().getId()).now();
        // Toggle the lesson to private
        original_lesson.setShared(false);
        // Remove all of the lessons with the original lesson as the parent
        //lessonService.remove_children_lessons(original_lesson.getLesson_id());

        // Get the creator id from the lesson
        String creator_id = lesson.getCreator_id();

        // Get the user from the datastore
        User creator = ofy.load().type(User.class).id(creator_id).now();

        // Add the message to the creator admin_msg list
        List<String> admin_msgs = creator.getAdmin_msg();
        if(admin_msgs == null){
            admin_msgs = new ArrayList<String>();
        }
        admin_msgs.add("ALERT! The lesson " + lesson.getTitle() + " has been removed for the following reason: " + message);
        creator.setAdmin_msg(admin_msgs);

        // Save the creator user object and lesson object to the datastore
        ofy.save().entity(creator).now();
        ofy.save().entity(original_lesson).now();

        //give the ok response
        resp.setStatus(200);
    }
}
