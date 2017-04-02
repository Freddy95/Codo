package com.dolphinblue.controller;
import com.dolphinblue.models.User;
import com.dolphinblue.service.OfyService;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by FreddyEstevez on 3/29/17.
 * This controller should handle all requests for the user page.
 *
 * Main Lessons Request
 * Name Request
 * Percent Complete (calculated from tasks completed)
 * Current Lesson Request
 */
@Controller
public class UserController {

    /**
     * gets user and adds him to @param model
     * @return
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public String get_user_page(@RequestParam(value = "id") Long id, Model model){
        Objectify ofy = OfyService.ofy();

        User user = ofy.load().type(User.class).id(id).now();
        model.addAttribute("user", user);
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
