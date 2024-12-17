package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.Model.Index;


public class NotesData {
    private String description;
    private String time;
    private String title;
    private String file;
    private String type;  // Add this field

    // No-argument constructor is needed for Firestore to deserialize the data
    public NotesData() {}

    // Getter and setter methods
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
