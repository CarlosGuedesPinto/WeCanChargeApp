package com.example.wecanchargeapp.classes;

import android.content.Intent;

public class ChargingHistory {

    public String uid, idCharger, idUser, kw;
    public String startTime, endTime, state, dateStart;

    public ChargingHistory(String uid, String idCharger, String idUser, String kw, String startTime, String endTime, String state, String dateStart) {
        this.uid = uid;
        this.idCharger = idCharger;
        this.idUser = idUser;
        this.kw = kw;
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = state;
        this.dateStart = dateStart;
    }

    public ChargingHistory() {
    }

    public void setUid(String Uid) {
        this.uid = Uid;
    }

    public void setIdCharger(String id) {
        this.idCharger = id;
    }

    public void setIdUser(String id) {
        this.idUser = id;
    }

    public void setKw(String kw) {
        this.kw = kw;
    }

    public void setStartTime(String time) {
        this.startTime = time;
    }

    public void setEndTime(String time) {
        this.endTime = time;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }


    public String getUid() {
        return uid;
    }

    public String getIdCharger() {
        return idCharger;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getKw() {
        return kw;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getState() {
        return state;
    }

    public String getDateStart() {
        return dateStart;
    }

}