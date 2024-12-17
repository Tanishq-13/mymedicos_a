package com.medical.my_medicos.activities.notifications.model;

import android.app.Notification;

import java.util.List;

public class PushNotification {
    private NotificationDataJobs data;
    private List<String> registration_ids; // Change from single 'to' string to a list of tokens

    public PushNotification(NotificationDataJobs data, List<String> registration_ids){
        this.data = data;
        this.registration_ids = registration_ids;
    }

    // Getters and setters
    public NotificationDataJobs getData() {
        return data;
    }

    public void setData(NotificationDataJobs data) {
        this.data = data;
    }

    public List<String> getRegistration_ids() {
        return registration_ids;
    }

    public void setRegistration_ids(List<String> registration_ids) {
        this.registration_ids = registration_ids;
    }
}
