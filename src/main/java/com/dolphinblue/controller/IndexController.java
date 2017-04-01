package com.dolphinblue.controller;

import com.google.auth.oauth2.OAuth2Credentials;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Matt on 3/19/17.
 */
@Controller
@RequestMapping("/index")
public class IndexController {

    @RequestMapping(method = RequestMethod.GET)
    public String index(HttpServletRequest req){
        String token = req.getHeader("Authorization");
        //check if it's a valid firebase token
        Boolean valid;
        return "index.html";
    }
}
