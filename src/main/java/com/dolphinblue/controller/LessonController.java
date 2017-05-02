package com.dolphinblue.controller;

import com.dolphinblue.models.Lesson;
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
}
