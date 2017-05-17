package com.dolphinblue.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Created by Matt on 5/3/17.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle a not found exception and return a 404
     * @return the 404 template
     */
    //@ExceptionHandler(value = NullPointerException.class)
    public String fourohfour(){
        return "404";
    }
}
