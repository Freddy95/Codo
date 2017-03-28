package com.dolphinblue.controller;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/login")
public class LoginController{

    @RequestMapping(method = RequestMethod.GET)
    public String getLogin(HttpServletRequest req){
//        UserService us = UserServiceFactory.getUserService();
//        String url = req.getRequestURL().toString();
//        //return the login controller
//        Principal usr = req.getUserPrincipal();
        return "login.html";
    }


}