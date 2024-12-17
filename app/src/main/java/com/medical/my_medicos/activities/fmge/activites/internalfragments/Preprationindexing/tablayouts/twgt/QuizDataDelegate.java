package com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.tablayouts.twgt;

import com.medical.my_medicos.activities.fmge.model.QuizFmge;
import com.medical.my_medicos.activities.pg.model.QuizPG;

import java.util.ArrayList;

public interface QuizDataDelegate {
    void onQuizDataFetched(ArrayList<QuizPG> quizList);
}
