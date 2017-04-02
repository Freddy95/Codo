package com.dolphinblue.controller;
import com.dolphinblue.models.User;
import com.dolphinblue.service.OfyService;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Henry Chin on 4-1-17.
 * This controller should handle all requests for the settings page.
 */
@Controller
public class SettingsController {

    /**
     * gets user and adds him to @param model
     * @return
     */
    @RequestMapping(value = "/user/{id}/settings", method = RequestMethod.GET)
    public String get_settings_page(@RequestParam(value = "id") Long id, Model model){
        Objectify ofy = OfyService.ofy();

        User user = ofy.load().type(User.class).id(id).now();
        model.addAttribute("user", user);
        return "settings";
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String get_settings_page(Model model){
        // Objectify ofy = OfyService.ofy();

        // User user = ofy.load().type(User.class).id(id).now();
        // model.addAttribute("user", user);
        return "settings";
    }

}
