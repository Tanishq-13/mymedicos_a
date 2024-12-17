package com.medical.my_medicos.activities.home.model;

public class Doubt {
    private String subject;
    private String message;
    public String ChatId;
    private boolean isClosed;

    public Doubt(String subject, String message, boolean isClosed,String ChatId) {
        this.subject = subject;
        this.message = message;
        this.isClosed = isClosed;
        this.ChatId=ChatId;
    }

    public String getSubject() {
        return subject;
    }

    public String getChatId() {
        return ChatId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isClosed() {
        return isClosed;
    }
}

