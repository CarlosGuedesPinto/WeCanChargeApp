package com.example.wecanchargeapp.classes;

public class Car {

    public String id, licensePlate, photo, name;
    //public Object status;

    public Car(String id, String licensePlate, String photo, String name) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.photo = photo;
        //this.status = status;
        this.name = name;
    }

    public Car() {}

    public void setId(String Id) {
        this.id = Id;
    }

    public void setLicensePlate(String lp) {
        this.licensePlate = lp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

}
