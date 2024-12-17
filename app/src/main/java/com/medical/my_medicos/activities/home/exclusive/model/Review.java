package com.medical.my_medicos.activities.home.exclusive.model;

import java.util.Date;

public class Review {

    private String docId;
    private String studentName;
    private String studentReview;
    private int rating;
    private Date date;

    // Default constructor (required for Firestore)
    public Review() {
    }

    // Parameterized constructor
    public Review(String docId, String studentName, String studentReview, int rating, Date date) {
        this.docId = docId;
        this.studentName = studentName;
        this.studentReview = studentReview;
        this.rating = rating;
        this.date = date;
    }

    // Getters and setters
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentReview() {
        return studentReview;
    }

    public void setStudentReview(String studentReview) {
        this.studentReview = studentReview;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // toString() for debugging purposes
    @Override
    public String toString() {
        return "Review{" +
                "docId='" + docId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", studentReview='" + studentReview + '\'' +
                ", rating=" + rating +
                ", date=" + date +
                '}';
    }
}
