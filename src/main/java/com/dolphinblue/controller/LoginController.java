package com.dolphinblue.controller;

import com.dolphinblue.service.AuthenticationService;
import com.dolphinblue.service.CodoUserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/login")
public class LoginController{
    @Autowired
    AuthenticationService authService;
    @Autowired
    CodoUserService userService;

    /**
     * Get the long page
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String getLogin(HttpServletRequest req){

        return "login";
    }

    /**
     * Setup the user page after someone has logged in and check to see if they need to be added
     * @param req
     * @param resp
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView postLogin(HttpServletRequest req, HttpServletResponse resp, Model model){
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
            //System.out.println("Invalid ID token.");
            resp.setStatus(303);
            return new ModelAndView("redirect:login");
        }

    }


}
