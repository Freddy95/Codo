package com.dolphinblue.controller;

import com.dolphinblue.Comparator.AuthorComparator;
import com.dolphinblue.Comparator.TitleComparator;
import com.dolphinblue.models.*;
import com.dolphinblue.models.Block.Type;
import com.dolphinblue.service.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * Created by FreddyEstevez on 3/29/17.
 *
 * This controller handles all requests for the task page.
 */
@Controller
@EnableWebMvc
public class SearchController {
    // Use Autowired to get an instance of the different Services
    @Autowired
    LessonService lessonService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    CodoUserService userService;

    /**
     *  This is for debugging a block task
     * @param model -- the thymeleaf model used to send data to the front end
     * @return -- the HTML page to be loaded
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@CookieValue("token") String token, Model model){
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return "redirect:login";
        }

        Objectify ofy = OfyService.ofy();

        // Get the google id token from the authentication token from the browser cookie
        GoogleIdToken googletoken = authenticationService.getIdToken(token, new JacksonFactory(), new NetHttpTransport());

        // Use google's token to contact google app engine's user api and get the user info
        String id = userService.getUserId(googletoken);

        // Load the user's information from the datastore and store it in a user object
        User user = ofy.load().type(User.class).id(id).now();
        if(user == null){
            return "redirect:/login";
        }
        model.addAttribute("user_id", user.getUser_id());
        return "search";
    }

    @RequestMapping(value = "/search/request",  method = RequestMethod.POST)
    public @ResponseBody
    List<LessonDetails> search_lessons(@CookieValue("token") String token, @RequestBody SearchObject query) {
        // Check to see if the user is authenticated by google
        boolean isAuthenticate = authenticationService.isAuthenticated(token, new JacksonFactory(), new NetHttpTransport());
        if (isAuthenticate) {
            // Create an instance of the objectify object for requests to the datastore
            Objectify ofy = OfyService.ofy();

            // Get the google id token from the authentication token from the browser cookie
            GoogleIdToken googletoken = authenticationService.getIdToken(token, new JacksonFactory(), new NetHttpTransport());

            // Use google's token to contact google app engine's user api and get the user info
            String id = userService.getUserId(googletoken);

            // Load the user's information from the datastore and store it in a user object
            User user = ofy.load().type(User.class).id(id).now();
            if(user == null){
                return null;
            }
            List<Lesson> lessons;
            if(query.getFilter().equals("all")){
                lessons = lessonService.get_main_lessons_by_user(user);
                lessons.addAll(lessonService.get_own_lessons(user));
                lessons.addAll(lessonService.get_shared_lessons_by_user(user));
            }else if(query.getFilter().equals("main")){
                //get main lessons
                lessons = lessonService.get_main_lessons_by_user(user);
            }else if(query.getFilter().equals("your")){
                //get your created lessons
                lessons = lessonService.get_own_lessons(user);
            }else{
                //get shared lessons
                lessons = lessonService.get_shared_lessons_by_user(user);
            }

            List<String> searchBy = query.getSearchBy();
            //sort the list
            if(query.getSortBy().equals("title")){
                Collections.sort(lessons, new TitleComparator());
            }else if(query.getSortBy().equals("author")){
                Collections.sort(lessons, new AuthorComparator());
            }

            if(query.getSearchTerm().equals("")){
                return lessonService.extract_details(lessons);
            }
            List<Lesson> list = new ArrayList<>();
            //search by author, title, description
            for (int i = 0; i < searchBy.size(); i++){
                if(searchBy.get(i).equals("title")){
                    list.addAll(lessonService.search_by_title(lessons, query.getSearchTerm()));
                }else if(searchBy.get(i).equals("description")) {
                    list.addAll(lessonService.search_by_description(lessons, query.getSearchTerm()));
                }else{
                    list.addAll(lessonService.search_by_author(lessons, query.getSearchTerm()));
                }
            }


            return lessonService.extract_details(list);
        } else {
            return null;
        }
    }








}