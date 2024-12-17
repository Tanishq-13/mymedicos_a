package com.medical.my_medicos.activities.home.exclusive.model;
import java.io.Serializable;
public class CourseCard implements Serializable {
    private String cover;
    private String description;
    private String docId;
    private String courseId;
    private boolean featured;
    private String lang;
    private String lastUpdated;
    private String name;
    private boolean premiumStatus;
    private long ratedBy;
    private String ratingAvg;
    private String subject;
    private String title;

    public CourseCard() {}

    public String getCover() {
        return cover;
    }

    public String getDescription() {
        return description;
    }

    public String getDocId() {
        return docId;
    }

    public boolean isFeatured() {
        return featured;
    }

    public String getLang() {
        return lang;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getName() {
        return name;
    }

    public boolean isPremiumStatus() {
        return premiumStatus;
    }

    public long getRatedBy() {
        return ratedBy;
    }

    public String getRatingAvg() {
        return ratingAvg;
    }

    public String getSubject() {
        return subject;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public CourseCard(String cover, String description, String docId, boolean featured, String lang,
                      String lastUpdated, String name, boolean premiumStatus, long ratedBy,
                      String ratingAvg, String subject, String title) {
        this.cover = cover;
        this.description = description;
        this.docId = docId;
        this.featured = featured;
        this.lang = lang;
        this.lastUpdated = lastUpdated;
        this.name = name;
        this.premiumStatus = premiumStatus;
        this.ratedBy = ratedBy;
        this.ratingAvg = ratingAvg;
        this.subject = subject;
        this.title = title;

    }


    // Getters and Setters
    // ...
}


