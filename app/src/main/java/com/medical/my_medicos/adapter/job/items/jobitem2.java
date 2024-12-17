package com.medical.my_medicos.adapter.job.items;

public class jobitem2 {

    String position,speciality,Location,Organiser,date;
    String hospital;
    String location,title;


    public jobitem2(String title  ){

        this.title=title;
    }

    public String getPosition() {
        return position;
    }
    public String getTitle() {
        return title;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public String getHospital() {
        return hospital;
    }
    public String getDate() {
        return date;
    }
    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
