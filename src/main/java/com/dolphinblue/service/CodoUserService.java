package com.dolphinblue.service;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.Query;
import org.springframework.stereotype.Service;

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
                //System.out.println("You are a returning user");
                return true;
            } else {

                //TODO: get rid of password field, add default lessons
                User usr = new User(id, "", first, last, email, "", pictureUrl, true, true, true, true, new ArrayList());

                //now add the user
                ofy.save().entity(usr).now();

                return true;
            }
        } else {
            //System.out.println("Invalid ID token.");
        }
        return false;
    }

    /**
     * Get a list of badges for a user
     * @param lesson_keys -- the lessons the user has worked on
     * @return -- the list of badges
     */
    public List<String> get_badges(List<Key<Lesson>> lesson_keys) {
        // Create an arraylist for the badges
        List<String> badges = new ArrayList<>();

        // Get the contact for the datastore
        Objectify ofy = OfyService.ofy();

        // Loop through the badges to see what has been completed
        for (int i = 0; i < lesson_keys.size(); i++) {
            Key lesson_key = lesson_keys.get(i);
            List<Long> l = Lesson.MAIN_LESSON_KEYS;
            try {
                Lesson lesson = ofy.load().type(Lesson.class).id(lesson_key.getId()).now();
                Long id = lesson.getOriginal_lesson();
                double percent = lesson.getPercent_complete();
                if(percent == 100.0 && Lesson.MAIN_LESSON_KEYS.contains(lesson.getOriginal_lesson())) {
                    String title = lesson.getTitle();
                    if(title.contains("Logical Operators")) {
                        title.replace(" ","<br>");
                    }
                    badges.add(title + "<br>Badge");
                }
            }catch (Exception e){
                //lesson has been deleted.
                lesson_keys.remove(i);
                i--;
            }

        }

        // Return the badges for the completed lessons
        return badges;
    }

    public boolean check_username_exist(String username) {
        // Get the contact for the datastore
        Objectify ofy = OfyService.ofy();

        // check to see if the username for a particular user already exists or not
        Query<User> q = ofy.load().type(User.class).filter("username", username);

        if(q.list().size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
