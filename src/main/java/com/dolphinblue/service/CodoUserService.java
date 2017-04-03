package com.dolphinblue.service;

import com.dolphinblue.models.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by Matt on 4/2/17.
 */
@Service
public class CodoUserService {


    public CodoUserService(){

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
//            Long id = new Long(payload.getSubject());

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");

            try {
                System.out.println(payload.toPrettyString());
            } catch (IOException e) {
                e.printStackTrace();
            }
//            User usr = new User(id,)

        } else {
            System.out.println("Invalid ID token.");
        }
        return false;
    }
}
