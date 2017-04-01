package com.dolphinblue.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Matt on 3/19/17.
 */
@Controller
public class TestController {

    @RequestMapping(value="test/lesson-card",method = RequestMethod.GET)
    public String lessonCard(){
        return "lessoncard";
    }

    @RequestMapping(value="test/draggable",method = RequestMethod.GET)
    public String draggable(){
        return "draggable";
    }
}
