package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts;

public class filterdata {
    private String indexHeading; // Heading of the topic/index
    private int quizCount;       // Number of quizzes for the topic

    // Constructor
    public filterdata(String indexHeading, int quizCount) {
        this.indexHeading = indexHeading;
        this.quizCount = quizCount;
    }

    // Getters
    public String getIndexHeading() {
        return indexHeading;
    }

    public int getQuizCount() {
        return quizCount;
    }

    // Setters
    public void setIndexHeading(String indexHeading) {
        this.indexHeading = indexHeading;
    }

    public void setQuizCount(int quizCount) {
        this.quizCount = quizCount;
    }

    // toString for easy display (optional)
    @Override
    public String toString() {
        return indexHeading + " (" + quizCount + " quizzes)";
    }
}
