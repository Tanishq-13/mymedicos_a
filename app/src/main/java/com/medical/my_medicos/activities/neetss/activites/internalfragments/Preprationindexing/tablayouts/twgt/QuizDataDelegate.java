package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.twgt;

import com.medical.my_medicos.activities.neetss.model.QuizSS;
import com.medical.my_medicos.activities.pg.model.QuizPG;

import java.util.ArrayList;

public interface QuizDataDelegate {
    void onQuizDataFetched(ArrayList<QuizSS> quizList);
}
