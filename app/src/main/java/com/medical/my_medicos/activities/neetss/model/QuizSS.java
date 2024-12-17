package com.medical.my_medicos.activities.neetss.model;

import com.google.firebase.Timestamp;

public class QuizSS {
    private String title, title1, id, index, type;
    private Timestamp description;

    public QuizSS(String title, String title1, Timestamp to, String id, String type, String index) {
        this.title = title;
        this.id = id;
        this.type = type;
        this.title1 = title1;
        this.description = to;
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getIndex() {
        return index;
    }

    public String getTitle1() {
        return title1;
    }

    public Timestamp getTo() {
        return description;
    }

    public void setTo(Timestamp to) {
        this.description = to;
    }

    public String getId() {
        return id;
    }
}
