package com.dolphinblue.controller;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.SaveTaskModel;
import com.dolphinblue.models.Task;
import com.dolphinblue.service.AuthenticationService;
import com.dolphinblue.service.CodoUserService;
import com.dolphinblue.service.LessonService;
import com.dolphinblue.service.OfyService;
import com.googlecode.objectify.Objectify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by FreddyEstevez on 4/13/17.
 * Handles requests for creating/updating/saving created lessons.
 */
@Controller
@EnableWebMvc
public class CreateLessonController {
    @Autowired
    LessonService lessonService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    CodoUserService userService;

    @RequestMapping(value = "/savecreatedlesson/{lessonId}/task/{taskId}", method = RequestMethod.POST)
    public void saveCreatedLesson(@RequestBody SaveTaskModel blocks){
        System.out.println("toolbox");
        System.out.println(blocks.getToolbox());
        System.out.println("editor");
        System.out.println(blocks.getEditor());
    }
    @RequestMapping(value = "/createlesson", method = RequestMethod.GET)
    public String getCreateLessonPage(Model model){
        Objectify ofy = OfyService.ofy();
        Lesson l = new Lesson();
        Task t  = new Task();
        //add task to lesson and save to datastore
        l.getTasks().add(ofy.save().entity(t).now());
        //save lesson to datastore
        ofy.save().entity(l);
        model.addAttribute("lesson", l);
        model.addAttribute("task", t);
        return "createtask";
    }
}
