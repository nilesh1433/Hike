package com.example.nilesh.model;

/**
 * Created by Richa on 3/28/2015.
 */
public class MessageDetails {
    String user;
    String message;
    boolean isLoggedInUserSender;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isLoggedInUserSender() {
        return isLoggedInUserSender;
    }

    public void setLoggedInUserSender(boolean isLoggedInUserSender) {
        this.isLoggedInUserSender = isLoggedInUserSender;
    }
}
