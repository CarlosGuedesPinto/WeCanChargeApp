package com.example.wecanchargeapp.classes;

import java.util.ArrayList;
import java.util.List;

public class Businesses {

    public String uid, name, location, address, closingTime, phoneNumber, photo, conditionsText, conditionsTitle;
    public ArrayList<String> chargers;
    public List<Object> reviews;

    public Businesses(String uid, String address, String phoneNumber, List<Object> reviews, String conditionsText, String closingTime, String name, String photo,  String conditionsTitle, String location, ArrayList<String> chargers) {
        this.uid = uid;
        this.location = location;
        this.chargers = chargers;
        this.name = name;
        this.address = address;
        this.closingTime = closingTime;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
        this.reviews = reviews;
        this.conditionsText = conditionsText;
        this.conditionsTitle = conditionsTitle;

    }

    public Businesses() {}

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setConditionsText(String conditionsText) {
        this.conditionsText = conditionsText;
    }

    public void setConditionsTitle(String conditionsTitle) {
        this.conditionsTitle = conditionsTitle;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChargers(ArrayList<String> chargers) {
        this.chargers = chargers;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setReviews(List<Object> reviews) {
        this.reviews = reviews;
    }

    public String getUid() {
        return uid;
    }

    public String getLocation() {
        return location;
    }

    public String getConditionsText() {
        return conditionsText;
    }

    public String getConditionsTitle() {
        return conditionsTitle;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getChargers() {
        return chargers;
    }

    public String getAddress() {
        return address;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public List<Object> getReviews() {
        return reviews;
    }

}
