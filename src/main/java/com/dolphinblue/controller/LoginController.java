package com.dolphinblue.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

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
        String CLIENT_ID = "441622834163-c890l3f1krej8tfgv0sl78b3tqqo10fo.apps.googleusercontent.com";
        String idTokenString = req.getHeader("Authorization");
        JacksonFactory jsonFactory = new JacksonFactory();
        NetHttpTransport transport = new NetHttpTransport();

        System.out.println("idtoken: "+idTokenString);

        GoogleIdTokenVerifier verifier =  new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();


        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (idToken != null) {

            Payload payload = idToken.getPayload();

//            // Print user identifier
//            String userId = payload.getSubject();
//            System.out.println("User ID: " + userId);
//
//            // Get profile information from payload
//            String email = payload.getEmail();
//            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");
//            String locale = (String) payload.get("locale");
//            String familyName = (String) payload.get("family_name");
//            String givenName = (String) payload.get("given_name");

            // Use or store profile information
            // ...
            System.out.println("valid token");
            resp.setStatus(303);
            return new ModelAndView("redirect:user");

        } else {
            System.out.println("Invalid ID token.");
            resp.setStatus(303);
            return new ModelAndView("redirect:login");
        }

    }


}
