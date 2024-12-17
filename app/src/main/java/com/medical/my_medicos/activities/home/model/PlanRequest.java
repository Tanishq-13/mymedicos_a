package com.medical.my_medicos.activities.home.model;

public class PlanRequest {
    private String section;
    private String planID;
    private int duration;
    private String userDocID;
    private String coupon;

    // Constructor
    public PlanRequest(String section, String planID, int duration, String userDocID, String coupon) {
        this.section = section;
        this.planID = planID;
        this.duration = duration;
        this.userDocID = userDocID;
        this.coupon = coupon;
    }

    // Getters and Setters
    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUserDocID() {
        return userDocID;
    }

    public void setUserDocID(String userDocID) {
        this.userDocID = userDocID;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }
}
