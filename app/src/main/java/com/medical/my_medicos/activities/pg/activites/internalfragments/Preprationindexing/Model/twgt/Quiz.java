package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.Model.twgt;

import com.google.firebase.Timestamp;

public class Quiz {
    private String title;
    private String index,Id,title1;
    Timestamp dueDate;
    String type;
    // Add other fields as needed

    // Default constructor required for calls to DataSnapshot.getValue(Quiz.class)
    public Quiz() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public Timestamp getDueDate() {
        return  dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }
    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    // Add getters and setters for other fields
}
