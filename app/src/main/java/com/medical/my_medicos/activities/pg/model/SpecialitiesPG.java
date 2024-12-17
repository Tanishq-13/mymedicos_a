package com.medical.my_medicos.activities.pg.model;

public class SpecialitiesPG {
    private String name;
    private int priority;

    public SpecialitiesPG(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    // Method to get formatted priority as a two-digit string
    public String getFormattedPriority() {
        return String.format("%02d", priority);
    }
}
