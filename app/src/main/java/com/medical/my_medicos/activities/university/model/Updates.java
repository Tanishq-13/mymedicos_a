package com.medical.my_medicos.activities.university.model;

import com.hishd.tinycart.model.Item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Updates implements Item, Serializable {

    private String universityName;
    private List<String> universities;
    private int quantity;

    public Updates(String stateName, List<String> universities) {
        this.universityName = stateName;
        this.universities = universities;
    }

    public String getUpdatesName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public List<String> getUniversities() {
        return universities;
    }

    public void setUniversities(List<String> universities) {
        this.universities = universities;
    }

    @Override
    public BigDecimal getItemPrice() {
        // You may want to calculate the total price based on universities or customize as needed
        return BigDecimal.ZERO;
    }

    @Override
    public String getItemName() {
        // Customize the item name based on your needs
        return "State: " + universityName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
