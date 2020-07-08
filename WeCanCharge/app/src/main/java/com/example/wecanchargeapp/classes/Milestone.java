package com.example.wecanchargeapp.classes;

public class Milestone {

    public String uid, color, conditional, text, type;

    public Milestone(String uid, String color, String conditional, String text, String type) {
        this.uid = uid;
        this.color = color;
        this.conditional = conditional;
        this.text = text;
        this.type = type;
    }

    public Milestone() {}

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setConditional(String conditional) {
        this.conditional = conditional;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public String getColor() {
        return color;
    }

    public String getConditional() {
        return conditional;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

}
