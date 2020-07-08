package com.example.wecanchargeapp.classes;


import java.io.Serializable;

public class BusinessCharger implements Serializable {

    public String ownerUid, uid, name, mqttId;
    public Integer lat, lng;

    public BusinessCharger() {

    }


    public BusinessCharger(String ownerUid, String name, String uid, String mqttId, Integer lat, Integer lng) {
        this.ownerUid = ownerUid;
        this.name = name;
        this.mqttId = mqttId;
        this.uid = uid;
        this.lat = lat;
        this.lng = lng;
    }


    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setMqttId(String mqttId) {
        this.mqttId = mqttId;
    }

    public void setLat(Integer lat) {
        this.lat = lat;
    }

    public void setLng(Integer lng) {
        this.lng = lng;
    }


    public String getOwnerUid() {
        return ownerUid;
    }

    public String getMqttId() {
        return mqttId;
    }

    public String getUid() {
        return uid;
    }

    public Integer getLat() {
        return lat;
    }

    public Integer getLng() {
        return lng;
    }

    public String getName() {
        return name;
    }


}
