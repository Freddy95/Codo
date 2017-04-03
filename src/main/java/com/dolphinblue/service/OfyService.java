package com.dolphinblue.service;

import com.dolphinblue.models.Block;
import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;
import com.dolphinblue.models.User;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
/**
 * Created by FreddyEstevez on 3/28/17.
 */
public class OfyService {
    static {
        ObjectifyService.register(User.class);
        ObjectifyService.register(Lesson.class);
        ObjectifyService.register(Block.class);
        ObjectifyService.register(Task.class);
    }

    public OfyService(){
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();//prior to v.4.0 use .begin() ,
        //since v.4.0  use ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }

}