package com.medical.my_medicos.activities.pg.model;// ListItem.java


import com.medical.my_medicos.activities.pg.model.QuizPG;

public class ListItem {
    public static final int TYPE_HEADING = 0;
    public static final int TYPE_CONTENT = 1;

    private int type;
    private String heading;
    private QuizPG quizItem;

    // Constructor for Heading
    public ListItem(String heading) {
        this.type = TYPE_HEADING;
        this.heading = heading;
    }

    // Constructor for Content Item
    public ListItem(QuizPG quizItem) {
        this.type = TYPE_CONTENT;
        this.quizItem = quizItem;
    }

    public int getType() {
        return type;
    }

    public String getHeading() {
        return heading;
    }

    public QuizPG getQuizItem() {
        return quizItem;
    }
}
