package com.dolphinblue.service;

import com.dolphinblue.models.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

import com.googlecode.objectify.Objectify;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by Matt on 4/2/17.
 */
@Service
public class CodoUserService {


    public CodoUserService(){

    }

    /**
     * Method to get the user if from the google authentication service
     * @param token the GoogleIdToken value get get from the authetnication service
     * @return The user id String
     */
    public String getUserId(GoogleIdToken token){
        if(token != null){
            Payload pld = token.getPayload();
            return pld.getSubject();
        }
        return null; // if the token is not valid then return null

    }

    public boolean checkNewUser(GoogleIdToken token) {
        // Get the payload
        Payload payload = token.getPayload();

        // Get the user id
        String id = payload.getSubject();

        // Get the objectify service
        Objectify ofy = OfyService.ofy();
        // Check for the user
        User check = ofy.load().type(User.class).id(id).now();

        // If the user doesn't exist, return true meaning the user is new
        if (check == null) {
            return true;
        } else {
            // If the user does exist, return false as the user is in the database
            return false;
        }
    }

    /**
     * Use the user id to get user info, add it to the database
     * @param userId the id of the user in the Google User service
     * @return true if the user could be added, false if it wasn't able to be added
     */
    public boolean addUser(GoogleIdToken userId){

        if (userId != null) {
            Payload payload = userId.getPayload();

            // Print user identifier
            String id = payload.getSubject();

            // Get profile information from payload
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String first = name.split(" ")[0];
            String last = name.split(" ")[1];
            String pictureUrl = (String) payload.get("picture");
            Objectify ofy = OfyService.ofy();
            User fetched = ofy.load().type(User.class).id(id).now();

            if (fetched!=null) {
                //update the fields from google's service
                fetched.setAvatar(pictureUrl);
                fetched.setEmail(email);
                fetched.setFirst_name(first);
                fetched.setLast_name(last);
                ofy.save().entity(fetched).now();
                System.out.println("You are a returning user");
                return true;
            } else {

                //TODO: get rid of password field, add default lessons
                User usr = new User(id, "", first, last, email, "", pictureUrl, true, true, new ArrayList());

                //now add the user

                ofy.save().entity(usr).now();

                System.out.println("You are a new user");

                return true;
            }
        } else {
            System.out.println("Invalid ID token.");
        }
        return false;
    }
}
