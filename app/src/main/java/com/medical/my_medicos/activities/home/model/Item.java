package com.medical.my_medicos.activities.home.model;

public class Item {
    private String title;
    private int imageResId;

    public Item(String title, int imageResId) {
        this.title = title;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }
}