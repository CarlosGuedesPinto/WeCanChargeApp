package com.example.wecanchargeapp.classes;


import java.io.Serializable;
import java.util.HashMap;

public class Notification implements Serializable {

    public String uid, date, text, type;

    public Notification() {

    }


    public Notification(String date, String text, String type) {
        this.date = date;
        this.text = text;
        this.type = type;
    }


    public void setUserUID(String UserUID) {
        this.date = date;
    }

    public void setName(String name) {
        this.text = text;
    }

    public void setToken(String token) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

}
