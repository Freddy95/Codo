package com.dolphinblue.controller;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.User;
import com.dolphinblue.service.LessonService;
import com.dolphinblue.service.OfyService;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by FreddyEstevez on 3/29/17.
 * This controller should handle all requests for the user page.
 *
 * User Request
 * Main Lessons Request
 * Current Lesson Request
 */
@Controller
public class UserController {
    @Autowired
    LessonService lessonService;

    /**
     * gets user and adds him to @param model
     * @return
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public String get_user_page(@RequestParam(value = "id") Long id, Model model){
        Objectify ofy = OfyService.ofy();

        User user = ofy.load().type(User.class).id(id).now();
        model.addAttribute("user_info", user);

        List<Key<Lesson>> lesson_keys = user.getLessons();
        List<Lesson> main_lessons = lessonService.get_main_lessons_by_id(lesson_keys);
        model.addAttribute("main_lessons", main_lessons);

        return "user";
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String get_user_page(Model model, @CookieValue("token") String token){
        // Objectify ofy = OfyService.ofy();

        // User user = ofy.load().type(User.class).id(id).now();
        // model.addAttribute("user", user);
        return "user";
    }

}
