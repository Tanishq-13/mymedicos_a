package com.medical.my_medicos.activities.home.model;

public class dailyquestion {
    private String questionTitle;
    private String documentId;

    public dailyquestion() {}

    public dailyquestion(String questionTitle, String documentId) {
        this.questionTitle = questionTitle;
        this.documentId = documentId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
