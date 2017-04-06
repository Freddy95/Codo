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
import com.googlecode.objectify.Key;
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
 * This controller should handle all requests for the user page.
 *
 * User Request
 * Main Lessons Request
 * Current Lesson Request
 * This class does user stuff
 */
@Controller
public class UserController {
    @Autowired
    LessonService lessonService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    CodoUserService userService;

    /**
     * gets user and adds him to @param model
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String get_user_page(@CookieValue(value="token",defaultValue = "") String token, Model model){
        boolean isAuthenticate = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(isAuthenticate) {
            Objectify ofy = OfyService.ofy();

            //get the google id token from the authentication token from the browser cookie
            GoogleIdToken googletoken = authenticationService.getIdToken(token, new JacksonFactory(), new NetHttpTransport());

            //now we use google's token to contact google app engines user api and get the User info
            String id = userService.getUserId(googletoken);

            User user = ofy.load().type(User.class).id(id).now();
            lessonService.create_main_lessons_for_user(user); //create user's own main lesson objects and save them in datastore
            model.addAttribute("user_info", user);
            List<Lesson> main_lessons = lessonService.get_main_lessons_by_user(user);
            Lesson l;
            if (user.getCurrent_lesson() == null) {
                try {
                    l = main_lessons.get(0);
                }catch(Exception e){
                    l = new Lesson();
                }
                user.setCurrent_lesson(l);
                //made change to user object must save to datastore
                ofy.save().entity(user).now();
            } else {
                l = (Lesson) ofy.load().key(user.getCurrent_lesson()).now();
            }

            model.addAttribute("lesson", l);

            model.addAttribute("main_lessons", main_lessons);
            return "user";
        }else{
            return "redirect:login";
        }
    }

//    @RequestMapping(value = "/user", method = RequestMethod.GET)
//    public String get_user_page(Model model){
//        // Objectify ofy = OfyService.ofy();
//
//        // User user = ofy.load().type(User.class).id(id).now();
//        // model.addAttribute("user", user);
//        return "user";
//    }
//    @RequestMapping(value = "/makeuser")
//    public String make_user(){
//        Objectify ofy = OfyService.ofy();
//        User u = new User();
//        u.setUser_id(1L);
//        u.setFirst_name("Fred");
//        u.setLast_name("estevez");
//        ofy.save().entity(u).now();
//        return "index";
//    }

}
