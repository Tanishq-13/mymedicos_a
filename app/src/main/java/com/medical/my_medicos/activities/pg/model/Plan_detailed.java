package com.medical.my_medicos.activities.pg.model;

public class Plan_detailed {
    private String months;
    private String originalPrice;
    private String discountedPrice,selectedcol,planid;
    private boolean isRecom;
    private int month;

    public Plan_detailed(String months, String originalPrice, String discountedPrice,Boolean isRecom,String selectedcol,int month,String planid) {
        this.months = months;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.isRecom=isRecom;
        this.planid=planid;
        this.month=month;
        this.selectedcol=selectedcol;
    }

    public String getMonths() {
        return months;
    }
    public boolean getisRecom(){
        return isRecom;
    }
    public String getSelectedcol(){return selectedcol;}
    public String getPlanid(){return planid;}
    public int getMonth(){return month;}

    public String getOriginalPrice() {
        return originalPrice;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }
}

