package com.medical.my_medicos.activities.pg.model;


import java.util.List;

public class Plan {
    private String PlanName;
    private String PlanTagline;
    private String PlanPrice;
    private String PlanThumbnail,discount;
    private String planid;
    private List<String> PlansFeature;  // List to store plan features

    // Constructor
    public Plan(String PlanName, String PlanTagline, String PlanPrice, String PlanThumbnail, List<String> PlansFeature,String discount,String planid) {
        this.PlanName = PlanName;
        this.discount=discount;
        this.PlanTagline = PlanTagline;
        this.PlanPrice = PlanPrice;
        this.PlanThumbnail = PlanThumbnail;
        this.PlansFeature = PlansFeature;
        this.planid=planid;
    }

    // Getters and setters
    public String getPlanName() {
        return PlanName;
    }
    public String getPlanid(){return planid;}

    public void setPlanName(String planName) {
        this.PlanName = planName;
    }

    public String getPlanTagline() {
        return PlanTagline;
    }

    public void setPlanTagline(String planTagline) {
        this.PlanTagline = planTagline;
    }

    public String getPlanPrice() {
        return PlanPrice;
    }

    public void setPlanPrice(String planPrice) {
        this.PlanPrice = planPrice;
    }

    public String getPlanThumbnail() {
        return PlanThumbnail;
    }
    public String getdiscounted() {
        return discount;
    }

    public void setPlanThumbnail(String planThumbnail) {
        this.PlanThumbnail = planThumbnail;
    }


    public List<String> getPlansFeature() {
        return PlansFeature;
    }

    public void setPlansFeature(List<String> plansFeature) {
        this.PlansFeature = plansFeature;
    }
}
