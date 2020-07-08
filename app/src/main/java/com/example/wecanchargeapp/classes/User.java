package com.example.wecanchargeapp.classes;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    public String name, email, userUID, profilePic;
    public List<Object> cars, notifications;
    public List<String> historic, milestonesCompleted;
    public Integer points;

    public User() {}

    public User(String name, String email, String userUID, String profilePic, List cars, List notifications, List historic, List milestonesCompleted, Integer points) {
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
        this.userUID = userUID;
        this.cars = cars;
        this.notifications = notifications;
        this.historic = historic;
        this.milestonesCompleted = milestonesCompleted;
        this.points = points;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePic(String pp) {
        this.profilePic = pp;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addNewCar(Object car) {
        this.cars.add(car);
    }

    public void removeCar(String index) {
            if (this.cars.remove(index));
    }

    public void addNewNotification(Object not) {
        this.notifications.add(not);
    }

    public void removeNotification(String index) {
        if (this.notifications.remove(index));
    }

    public void addNewHistoric(String hist) {
        this.historic.add(hist);
    }

    public void addNewMilestoneCompleted(String mc) {
        this.milestonesCompleted.add(mc);
    }

    public void updatePoints(Integer points) {
        this.points += points;
    }

    public String getName() {
        return name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getUserUID() {
        return userUID;
    }

    public String getEmail() {
        return email;
    }

    public List getCars() {
        return cars;
    }

    public Object getFirstCar() { return cars.get(0); }

    public List getNotifications() {
        return notifications;
    }

    public List getHistoric() {
        return historic;
    }

    public List getMilestonesCompleted() {
        return milestonesCompleted;
    }

    public Integer getPoints() {
        return points;
    }

}
