package com.dolphinblue.service;

import com.dolphinblue.models.Block;
import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;
import com.dolphinblue.models.User;
import com.google.appengine.api.datastore.ReadPolicy;
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

    /**
     * Create the objectify instance
     * @return
     */
    public static Objectify ofy() {
        return ObjectifyService.ofy().consistency(ReadPolicy.Consistency.STRONG);//prior to v.4.0 use .begin() ,
        //since v.4.0  use ObjectifyService.ofy();
    }

    /**
     * Return the objectify instance
     * @return
     */
    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }

}