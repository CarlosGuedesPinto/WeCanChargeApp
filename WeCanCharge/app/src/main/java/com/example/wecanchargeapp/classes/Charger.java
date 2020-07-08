package com.example.wecanchargeapp.classes;


import java.io.Serializable;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;

public class Charger implements Serializable {

    public String userUID, chargerUID, name, lastKnowedState, chargerMqttId;
    public Boolean isCharging;

    public Charger() {

    }


    public Charger(String userUID, String name, String chargerUID, String lastKnowedState, String chargerMqttId, Boolean isCharging) {
        this.userUID = userUID;
        this.name = name;
        this.chargerMqttId = chargerMqttId;
        this.chargerUID = chargerUID;
        this.lastKnowedState = lastKnowedState;
        this.isCharging = isCharging;
    }


    public void setUserUID(String UserUID) {
        this.userUID = userUID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChargerUID(String chargerUID) {
        this.chargerUID = chargerUID;
    }

    public void setChargerMqttId(String chargerMqttId) {
        this.chargerMqttId = chargerMqttId;
    }

    public void setLastKnowedState(String lastKnowedState) {
        this.lastKnowedState = lastKnowedState;
    }

    public void setState(Boolean state) { this.isCharging = state; }


    public String getUserUID() {
        return userUID;
    }

    public String getChargerUID() {
        return chargerUID;
    }

    public String getChargerMqttId() {
        return chargerMqttId;
    }

    public String getLastKnowedState() {
        return lastKnowedState;
    }

    public String getName() {
        return name;
    }

    public Boolean getState() { return isCharging; }

}
