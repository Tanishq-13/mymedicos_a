package com.medical.my_medicos.activities.neetss.model;

import com.google.firebase.Timestamp;

public class Swgtmodel {

    private String title,title1,id;
    private boolean type;
    private Timestamp description;


    public Swgtmodel(String title, String title1, Timestamp to, String id, boolean type) {
        this.title = title;
        this.id = id;
        this.type=type;
        this.title1=title1;
        this.description = to;
    }

    public String getTitle() {
        return title;
    }
    public boolean getType(){ return type;}
    public String getTitle1() {
        return title1;
    }
    //    public Timestamp getTo() {
//        return description;
//    }
    public Timestamp getTo() {
        return description;
    }

    public void setTo(Timestamp to) {
        this.description= to;
    }

    public String getId() {
        return id;
    }
}
