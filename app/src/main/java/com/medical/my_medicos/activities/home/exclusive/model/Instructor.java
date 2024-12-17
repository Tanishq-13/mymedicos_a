package com.medical.my_medicos.activities.home.exclusive.model;

public class Instructor {
    private String bio;
    private String docId;
    private String emailId;
    private String interest1;
    private String interest2;
    private String location;
    private String mcn;
    private String name;
    private String phoneNumber;
    private String prefix;
    private String profile;
    private boolean verified;
    private String aadharNumber;
    private boolean exclusive;
    private String msgToStudents;

    // Default constructor required for Firebase or deserialization
    public Instructor() {
    }

    // Parameterized constructor
    public Instructor(String bio, String docId, String emailId, String interest1, String interest2, String location,
                       String mcn, String name, String phoneNumber, String prefix, String profile, boolean verified,
                       String aadharNumber, boolean exclusive, String msgToStudents) {
        this.bio = bio;
        this.docId = docId;
        this.emailId = emailId;
        this.interest1 = interest1;
        this.interest2 = interest2;
        this.location = location;
        this.mcn = mcn;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.prefix = prefix;
        this.profile = profile;
        this.verified = verified;
        this.aadharNumber = aadharNumber;
        this.exclusive = exclusive;
        this.msgToStudents = msgToStudents;
    }

    // Getters and setters
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getInterest1() {
        return interest1;
    }

    public void setInterest1(String interest1) {
        this.interest1 = interest1;
    }

    public String getInterest2() {
        return interest2;
    }

    public void setInterest2(String interest2) {
        this.interest2 = interest2;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMcn() {
        return mcn;
    }

    public void setMcn(String mcn) {
        this.mcn = mcn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public String getMsgToStudents() {
        return msgToStudents;
    }

    public void setMsgToStudents(String msgToStudents) {
        this.msgToStudents = msgToStudents;
    }
}
