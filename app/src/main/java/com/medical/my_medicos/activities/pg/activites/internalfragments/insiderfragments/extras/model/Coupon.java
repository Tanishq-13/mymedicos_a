package com.medical.my_medicos.activities.pg.activites.internalfragments.insiderfragments.extras.model;

import com.google.firebase.Timestamp;

public class Coupon {
    private String about;
    private String code;
    private String couponId;
    private int discount;
    private Boolean isActive;

    public Coupon(String about, String code, String couponId, int discount, boolean isActive) {
        this.about = about;
        this.code = code;
        this.couponId = couponId;
        this.discount = discount;
        this.isActive = isActive;
    }

    public String getAbout() {
        return about;
    }

    public String getCode() {
        return code;
    }

    public String getCouponId() {
        return couponId;
    }

    public int getDiscount() {
        return discount;
    }

    public boolean getStatus(){
        return isActive;
    }
}
