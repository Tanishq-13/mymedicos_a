package com.medical.my_medicos.activities.pg.model;

public class Subject {
    private String name;
    private String docId;

    public Subject(String name, String docId) {
        this.name = name;
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public String getDocId() {
        return docId;
    }
}
