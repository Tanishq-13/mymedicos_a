package com.medical.my_medicos.activities.pg.model;

import com.google.firebase.Timestamp;

public class QuizPG {
    private String title, title1, id, index, type,thumbnail;
    String size;
    boolean isSolved,isProgress,isLocked;

    private Timestamp description;

    public QuizPG(String title, String title1, Timestamp to, String id, String type, String index,String thumbnail,String datasize) {
        this.title = title;
        this.id = id;
        this.thumbnail=thumbnail;
        this.type = type;
        this.size=datasize;
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
    public String getThumbnail() {
        return thumbnail;

    }

    public boolean isLocked() {
        return isLocked;
    }

    public String getSize() {
        return size;
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

    public boolean isSolved() {
        return isSolved;
    }

    public boolean isProgress() {
        return isProgress;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }
    public void setLocked(boolean locked){isLocked=locked;}

    public void setProgress(boolean progress) {
        isProgress = progress;
    }

    public void setTo(Timestamp to) {
        this.description = to;
    }

    public String getId() {
        return id;
    }
}
