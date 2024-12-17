package com.medical.my_medicos.activities.pg.model;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class QuizPGExam {
    private String title,title1,id,index;
    private String type;
    private Timestamp description;
    private String datasize;


    public QuizPGExam(String title, String title1, Timestamp to, String id,String type,String index,String datasize) {
        this.title = title;
        this.datasize=datasize;
        this.id = id;
        this.type=type;
        this.title1=title1;
        this.description = to;
        this.index=index;
    }

    public String getTitle() {
        return title;
    }
    public String getType(){ return type;}
    public String getDatasize(){return datasize;}
    public String getIndex(){ return index;}
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
