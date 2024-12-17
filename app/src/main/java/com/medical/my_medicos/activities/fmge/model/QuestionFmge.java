package com.medical.my_medicos.activities.fmge.model;

public class QuestionFmge {
    private String label, description, url, date;
    public QuestionFmge(String name, String status,String url,String date) {

        this.label = name;
        this.description = status;
        this.url = url;
        this.date = date;
    }
    public String getLabel() {
        return label;
    }

    public void setLabel(String name) {
        this.label = name;
    }

//    public void setThumbnail(String image) {
//        this.thumbnail = image;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String status) {
        this.description = status;
    }

    public String getUrl() {
        // Check if the URL is not null and has at least 10 characters
        if (url != null && url.length() > 10) {
            // Return the first 10 characters of the URL
            return url.substring(0, 10);
        } else {
            // Return the entire URL if it's null or less than 10 characters
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
