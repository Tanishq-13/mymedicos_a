package com.medical.my_medicos.activities.neetss.model;
import com.hishd.tinycart.model.Item;

import java.io.Serializable;
import java.math.BigDecimal;

public class PerDaySS implements Item, Serializable {

    private String question, optiona, optionb,optionc,optiond, checkbtn,idquestion, description;

    public PerDaySS(String DailyQuestion, String optionA, String optionB, String optionC, String optionD, String SubmitAnswer, String idQuestion, String description) {
        this.question = DailyQuestion;
        this.optiona = optionA;
        this.optionb = optionB;
        this.optionc = optionC;
        this.optiond = optionD;
        this.idquestion = idQuestion;
        this.checkbtn = SubmitAnswer;
        this.description = description;
    }

    public String getDailyQuestion() {
        return question;
    }

    public void setDailyQuestion(String DailyQuestion) {
        this.question = DailyQuestion;
    }

    public String getDailyQuestionA() {
        return optiona;
    }

    public void setDailyQuestionA(String optionA) {
        this.optiona = optionA;
    }

    public String getDailyQuestionB() {
        return optionb;
    }

    public void setDailyQuestionB(String optionB) {
        this.optionb = optionB;
    }

    public String getDailyQuestionC() {
        return optionc;
    }

    public void setDailyQuestionC(String optionC) {
        this.optionc = optionC;
    }

    public String getDailyQuestionD() {
        return optiond;
    }

    public void setDailyQuestionD(String optionD) {
        this.optiond = optionD;
    }

    public String getSubmitDailyQuestion() {
        return checkbtn;
    }

    public void setSubmitDailyQuestion(String SubmitAnswer) {
        this.checkbtn = SubmitAnswer;
    }

    public String getidQuestion() {
        return idquestion;
    }

    public void setidQuestion(String id) {
        this.idquestion = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public BigDecimal getItemPrice() {
        return null;
    }

    @Override
    public String getItemName() {
        return question;
    }
}
