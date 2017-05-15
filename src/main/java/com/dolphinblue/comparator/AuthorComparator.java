package com.dolphinblue.comparator;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.LessonDetails;
import com.dolphinblue.models.User;
import com.dolphinblue.service.OfyService;
import com.googlecode.objectify.Objectify;

import java.util.Comparator;

/**
 * This is used when sorting a list of lessons by the Author or username
 * Created by FreddyEstevez on 5/14/17.
 */
public class AuthorComparator implements Comparator<LessonDetails> {
    public int compare(LessonDetails lesson1, LessonDetails lesson2){
        Objectify ofy = OfyService.ofy();
        if(lesson1.getCreator_id().equals("")){
            return 1;
        }
        if (lesson2.getCreator_id().equals("")){
            return -1;
        }
        User user1 = ofy.load().type(User.class).id(lesson1.getCreator_id()).now();
        User user2 = ofy.load().type(User.class).id(lesson2.getCreator_id()).now();
        return user1.getUsername().compareTo(user2.getUsername());
    }
}
