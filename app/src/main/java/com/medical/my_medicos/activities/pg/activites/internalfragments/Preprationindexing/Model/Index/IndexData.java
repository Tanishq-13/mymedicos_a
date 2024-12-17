package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.Model.Index;

public class IndexData {
    private String data;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public IndexData() { }

    public IndexData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
