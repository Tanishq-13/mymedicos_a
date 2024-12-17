package com.medical.my_medicos.activities.pg.activites.internalfragments;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.adapters.PerDayPGAdapter;
import com.medical.my_medicos.activities.pg.model.PerDayPG;

import java.util.ArrayList;

public class DailyQuestionActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayoutPreparation;
    private LottieAnimationView timer;
    private String quiztiddaya;
    private PerDayPGAdapter perDayPGAdapter;
    private ArrayList<PerDayPG> dailyquestionspg;
    private FirebaseUser currentUser;
    private LinearLayout nocardp;
    private RecyclerView perDayQuestionsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tr);

        // Initialize currentUser
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Fetch the Document ID from Intent
        Intent intent = getIntent();
        String documentId = intent.getStringExtra("QUESTION_ID"); // Passed document ID

        swipeRefreshLayoutPreparation = findViewById(R.id.swipeRefreshLayoutPreparation);
        swipeRefreshLayoutPreparation.setOnRefreshListener(this::refreshContent);

        perDayQuestionsRecyclerView = findViewById(R.id.perdayquestions);
        nocardp = findViewById(R.id.nocardpg);
        timer = findViewById(R.id.timer);

        // Initialize Firestore and load the data
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        initPerDayQuestions(documentId);

        if (currentUser != null && documentId != null) {
            // Fetch data from Firestore
            db.collection("PGupload")
                    .document("Daily")
                    .collection("Quiz")
                    .document(documentId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                quiztiddaya = document.getString("QuizToday");
                                Log.d("TAG", "Document data: " + document.getData());

                                // Fetch quiz data after getting quiztiddaya
                                fetchData();
                            } else {
                                Log.e("TAG", "No such document!");
                            }
                        } else {
                            Log.e(ContentValues.TAG, "Error getting document: ", task.getException());
                        }
                    });
        } else {
            Log.e("Activity", "CurrentUser or DocumentId is null");
        }
    }

    // Initialize the list and adapter for displaying quiz questions
    void initPerDayQuestions(String quizToday) {
        dailyquestionspg = new ArrayList<>();
        perDayPGAdapter = new PerDayPGAdapter(this, dailyquestionspg);

        // Fetch the questions using quizToday ID
        getPerDayQuestions(quizToday);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        perDayQuestionsRecyclerView.setLayoutManager(layoutManager);
        perDayQuestionsRecyclerView.setAdapter(perDayPGAdapter);
    }

    // Updated method to fetch data from Firestore directly
    void getPerDayQuestions(String quiz) {
        Log.d("DEBUG", "getPerDayQuestions: Fetching data from Firestore");

        if (quiz != null && !quiz.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("PGupload")
                    .document("Daily")
                    .collection("Quiz")
                    .document(quiz)  // Document reference for the quiz ID
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Fetch all fields directly from the document
                                String question = (String) document.get("Question");
                                String optionA = (String) document.get("A");
                                String optionB = (String) document.get("B");
                                String optionC = (String) document.get("C");
                                String optionD = (String) document.get("D");
                                String correctAnswer = (String) document.get("Correct");
                                String questionId = (String) document.get("id");
                                String description = (String) document.get("Description");

                                // Create a PerDayPG object with the fetched data
                                PerDayPG perday = new PerDayPG(
                                        question,
                                        optionA,
                                        optionB,
                                        optionC,
                                        optionD,
                                        correctAnswer,
                                        questionId,
                                        description
                                );

                                // Add the question to the list if not already present
                                if (!containsQuestionId(dailyquestionspg, questionId)) {
                                    dailyquestionspg.add(perday);
                                    nocardp.setVisibility(View.GONE); // Hide the "no card" message if a question is found
                                }

                                // Update the adapter if the list is not empty
                                if (!dailyquestionspg.isEmpty()) {
                                    perDayPGAdapter.notifyDataSetChanged();
                                }

                            } else {
                                Log.e("FirestoreError", "Document does not exist.");
                            }
                        } else {
                            Log.e("FirestoreError", "Error getting document: ", task.getException());
                        }
                    });
        } else {
            Log.e("FirestoreError", "Quiz ID is null or empty");
        }
    }

    private boolean containsQuestionId(ArrayList<PerDayPG> list, String questionId) {
        for (PerDayPG question : list) {
            if (question.getidQuestion().equals(questionId)) {
                return true;
            }
        }
        return false;
    }

    private void refreshContent() {
        clearData();
        fetchData();
        swipeRefreshLayoutPreparation.setRefreshing(false);
    }

    private void clearData() {
        dailyquestionspg.clear();
    }

    void fetchData() {
        getPerDayQuestions(quiztiddaya);
    }
}
