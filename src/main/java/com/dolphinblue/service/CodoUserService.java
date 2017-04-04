package com.dolphinblue.service;

import com.dolphinblue.models.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

import com.googlecode.objectify.ObjectifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Use the user id to get user info, add it to the database
     * @param userId the id of the user in the Google User service
     * @return true if the user could be added, false if it wasn't able to be added
     */
    public boolean addUser(GoogleIdToken userId){

        if (userId != null) {
            Payload payload = userId.getPayload();

            // Print user identifier
            //TODO: can the Google id be cast to a long?
            String id = payload.getSubject();

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String first = name.split(" ")[0];
            String last = name.split(" ")[1];
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            User fetched = ObjectifyService.ofy().load().type(User.class).id(id).now();

            if(fetched!=null){
                return true;
            }else {

                //TODO: get rid of password field, add default lessons
                User usr = new User(id, first, last, email, "", pictureUrl, new ArrayList());

                //now add the user
                ObjectifyService.ofy().save().entity(usr).now();
                return true;
            }


        } else {
            System.out.println("Invalid ID token.");
        }
        return false;
    }
}
