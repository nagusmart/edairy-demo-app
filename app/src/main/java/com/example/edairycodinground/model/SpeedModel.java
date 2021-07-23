package com.example.edairycodinground.model;

public class SpeedModel {

    public String phoneNo="";
    public String totalSpeed="";
    public String upSpeed="";
    public String downSpeed="";
    public String timeStamp="";

    public SpeedModel(String phoneNo,String totalSpeed,String upSpeed,String downSpeed,String timeStamp){
        this.phoneNo=phoneNo;
        this.totalSpeed=totalSpeed;
        this.upSpeed=upSpeed;
        this.downSpeed=downSpeed;
        this.timeStamp=timeStamp;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getTotalSpeed() {
        return totalSpeed;
    }

    public void setTotalSpeed(String totalSpeed) {
        this.totalSpeed = totalSpeed;
    }

    public String getUpSpeed() {
        return upSpeed;
    }

    public void setUpSpeed(String upSpeed) {
        this.upSpeed = upSpeed;
    }

    public String getDownSpeed() {
        return downSpeed;
    }

    public void setDownSpeed(String downSpeed) {
        this.downSpeed = downSpeed;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


}
