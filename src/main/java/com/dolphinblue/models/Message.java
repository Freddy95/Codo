package com.dolphinblue.models;

/**
 * Created by Devon on 5/12/17.
 */
public class Message {
    String message;

    public Message () {
        this.message = "";
    }

    public Message(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
