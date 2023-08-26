package com.example.my_medicos;

public class jobitem {

    String position;
    String hospital;
    String location;

    public jobitem() {
    }

    public jobitem(String position, String hospital, String location) {
        this.position = position;
        this.hospital = hospital;
        this.location = location;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getHospital() {
        return hospital;
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
