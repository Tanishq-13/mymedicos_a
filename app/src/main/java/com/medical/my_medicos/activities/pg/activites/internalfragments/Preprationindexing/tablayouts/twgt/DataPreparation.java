package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.twgt;

// DataPreparation.java


import com.medical.my_medicos.activities.pg.model.ListItem;
import com.medical.my_medicos.activities.pg.model.QuizPG;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataPreparation {

    public static List<ListItem> prepareListItems(List<QuizPG> quizList) {
        List<ListItem> listItems = new ArrayList<>();
        Map<String, List<QuizPG>> groupedMap = new LinkedHashMap<>();

        // Group quizzes by index
        for (QuizPG quiz : quizList) {
            String index = quiz.getIndex();
            if (!groupedMap.containsKey(index)) {
                groupedMap.put(index, new ArrayList<>());
            }
            groupedMap.get(index).add(quiz);
        }

        // Create ListItems with headings and content
        for (Map.Entry<String, List<QuizPG>> entry : groupedMap.entrySet()) {
            // Add heading
            listItems.add(new ListItem(entry.getKey()));
            // Add all quizzes under this heading
            for (QuizPG quiz : entry.getValue()) {
                listItems.add(new ListItem(quiz));
            }
        }

        return listItems;
    }
}
