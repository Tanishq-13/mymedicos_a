package com.medical.my_medicos.activities.news;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class News {
    private String label, thumbnail, description, url, date, type,subject;

    private String documentId; // Add this line
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public News(String documentId,String label, String thumbnail, String description, String subject, String date, String url,String type) {
        this.documentId = documentId; // Add this line
        this.label = label;
        this.thumbnail = thumbnail;
        this.description = description;
        this.date = date;
        this.subject = subject;
        this.url = url;
        this.type = type;
    }

    public String getDocumentId() {
        return documentId;
    }
    public String getLabel() {
        return label;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public String getSubject() {
        return subject;
    }


    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }
    public String getDate(){
        return date;
    }


    public String getFormattedDate() {
        long timestamp = convertStringToTimestamp(date);

        // Convert the timestamp to a Date object
        Date newsDate = new Date(timestamp);

        // Format the Date object as desired (e.g., "11 May 2024")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        Log.d("Date of adapter ", String.valueOf(timestamp));
        Log.d("Date of adapter", String.valueOf(dateFormat));


        return dateFormat.format(newsDate);
    }


    // Helper method to convert the string date to a timestamp
    private long convertStringToTimestamp(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = dateFormat.parse(dateString);

            return date != null ? date.getTime() : 0L;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }
}
