package com.dolphinblue.controller;

import com.dolphinblue.service.AuthenticationService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Matt on 3/19/17.
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    AuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(@CookieValue("token") String token){
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(isAuthenticated) {
//            GoogleIdToken googleIdToken = authenticationService.getIdToken(token, new JacksonFactory(), new NetHttpTransport());
            return "redirect:user";
        }else {
            return "redirect:login";
        }
    }
}
