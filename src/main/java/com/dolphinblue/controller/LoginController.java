package com.dolphinblue.controller;

import com.dolphinblue.service.AuthenticationService;
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

    @RequestMapping(method = RequestMethod.GET)
    public String getLogin(HttpServletRequest req){
//        UserService us = UserServiceFactory.getUserService();
//        String url = req.getRequestURL().toString();
        //return the login controller
//        Principal usr = req.getUserPrincipal();
        return "login";
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView postLogin(HttpServletRequest req, HttpServletResponse resp){
        JacksonFactory jsonFactory = new JacksonFactory();
        NetHttpTransport transport = new NetHttpTransport();
        boolean isAuthenicated = authService.isAuthenticated(req.getHeader("Authorization"),jsonFactory,transport);
      if(isAuthenicated) {
            resp.setStatus(303);
            return new ModelAndView("redirect:user");

        } else {
            System.out.println("Invalid ID token.");
            resp.setStatus(303);
            return new ModelAndView("redirect:login");
        }

    }


}
