package com.dolphinblue.controller;

import com.dolphinblue.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Devon on 4/2/17.
 */
@Controller
public class BlockController {
    @Autowired
    LessonService lessonService;

    //public String get_blocks
}
