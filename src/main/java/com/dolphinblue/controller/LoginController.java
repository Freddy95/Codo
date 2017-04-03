package com.dolphinblue.controller;

import com.dolphinblue.service.AuthenticationService;
import com.dolphinblue.service.CodoUserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Controller
@RequestMapping("/login")
public class LoginController{
    @Autowired
    AuthenticationService authService;
    @Autowired
    CodoUserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String getLogin(HttpServletRequest req){

        return "login";
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView postLogin(HttpServletRequest req, HttpServletResponse resp){
        JacksonFactory jsonFactory = new JacksonFactory();
        NetHttpTransport transport = new NetHttpTransport();
        //get the token from the header sent from the browser
        String token = req.getHeader("Authorization");
        //check if the user in authenticated
        boolean isAuthenticated = authService.isAuthenticated(token,jsonFactory,transport);
        //get the token so we can get the user info
        GoogleIdToken googleToken = authService.getIdToken(token,jsonFactory,transport);
        //now we add the user to the database
        boolean isAdded = userService.addUser(googleToken);

      if(isAuthenticated && isAdded) {
            resp.setStatus(303);
            return new ModelAndView("redirect:user");

        } else {
            System.out.println("Invalid ID token.");
            resp.setStatus(303);
            return new ModelAndView("redirect:login");
        }

    }


}
