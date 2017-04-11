package com.dolphinblue.controller;
import com.dolphinblue.models.User;
import com.dolphinblue.service.AuthenticationService;
import com.dolphinblue.service.CodoUserService;
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
import org.springframework.web.bind.annotation.RequestParam;

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

    /**
     * gets user and adds him to @param model
     * @return
     */
    @RequestMapping(value = "/user//settings", method = RequestMethod.GET)
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

}
