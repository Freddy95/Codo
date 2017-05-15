package com.dolphinblue.comparator;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.User;
import com.dolphinblue.service.OfyService;
import com.googlecode.objectify.Objectify;

import java.util.Comparator;

/**
 * This is used when sorting a list of lessons by the Author or username
 * Created by FreddyEstevez on 5/14/17.
 */
public class AuthorComparator implements Comparator<Lesson> {
    public int compare(Lesson lesson1, Lesson lesson2){
        Objectify ofy = OfyService.ofy();
        User user1 = ofy.load().type(User.class).id(lesson1.getCreator_id()).now();
        User user2 = ofy.load().type(User.class).id(lesson2.getCreator_id()).now();
        return user1.getUsername().compareTo(user2.getUser_id());
    }
}
