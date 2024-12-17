package com.medical.my_medicos.activities.pg.model;

public class VideoPG {
    private String label, thumbnail, url, date;
    public VideoPG(String name, String image,String url,String date) {
        this.label = name;
        this.thumbnail = image;
        this.url = url;
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String name) {
        this.label = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

//    public void setThumbnail(String image) {
//        this.thumbnail = image;
//    }

    public String getUrl() {
        if (url != null && url.length() > 10) {
            return url.substring(0, 10);
        } else {
            return url;
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
