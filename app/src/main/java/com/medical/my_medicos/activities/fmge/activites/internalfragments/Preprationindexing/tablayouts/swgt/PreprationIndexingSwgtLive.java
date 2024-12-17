package com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.tablayouts.swgt;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.model.QuizFmgeExam;
import com.medical.my_medicos.activities.fmge.adapters.WeeklyQuizAdapterSwgt;
import com.medical.my_medicos.activities.fmge.model.Swgtmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class PreprationIndexingSwgtLive extends Fragment {
    private WeeklyQuizAdapterSwgt quizAdapter;
    private ArrayList<QuizFmgeExam> quizpg = new ArrayList<>();
    private String speciality;
    LinearLayout nocardpg;
    ProgressBar progressBar;

    private static final String ARG_SPECIALITY = "speciality";

    public PreprationIndexingSwgtLive() {
        // Required empty public constructor
    }

    public static PreprationIndexingSwgtLive newInstance(String speciality) {
        PreprationIndexingSwgtLive fragment = new PreprationIndexingSwgtLive();
        Bundle args = new Bundle();
        args.putString(ARG_SPECIALITY, speciality);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            speciality = getArguments().getString(ARG_SPECIALITY);
            Log.d(TAG, "Speciality passed: " + speciality); // Log the speciality passed
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prepration_indexing_swgt_live, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        nocardpg = view.findViewById(R.id.nocardpg);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        quizAdapter = new WeeklyQuizAdapterSwgt(getContext(), quizpg);
        recyclerView.setAdapter(quizAdapter);

        getQuestions(speciality);
        return view;
    }

    private void getQuestions(String title) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getPhoneNumber();
            Log.d(TAG, "Fetching attempted quizzes for user: " + userId); // Log the user ID
            fetchAttemptedQuizzes(userId, attemptedQuizIds -> fetchLiveQuizzes(FirebaseFirestore.getInstance(), title, Timestamp.now(), attemptedQuizIds));
        } else {
            Log.e(TAG, "User is not logged in.");
        }
    }

    private void fetchAttemptedQuizzes(String phoneNumber, Consumer<Set<String>> onCompleted) {
        FirebaseFirestore.getInstance().collection("QuizResults").document(phoneNumber).collection("Exam")
                .get()
                .addOnCompleteListener(task -> {
                    Set<String> attemptedQuizIds = new HashSet<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            attemptedQuizIds.add(document.getId());
                            Log.d(TAG, "Attempted quiz ID: " + document.getId()); // Log each attempted quiz ID
                        }
                    } else {
                        Log.e(TAG, "Error fetching attempted quizzes: ", task.getException());
                    }
                    onCompleted.accept(attemptedQuizIds);
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchLiveQuizzes(FirebaseFirestore db, String title, Timestamp now, Set<String> attemptedQuizIds) {
        Log.d(TAG, "Fetching live quizzes for speciality: " + title); // Log the speciality being fetched
        CollectionReference quizzCollection = db.collection("Fmge").document("CWT").collection("Quiz");
        Query query = quizzCollection.whereLessThanOrEqualTo("from", now)
                .whereGreaterThanOrEqualTo("to", now);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                quizpg.clear(); // Clearing the list to ensure it only contains current live quizzes
                Log.d(TAG, "Live quizzes fetched successfully."); // Log successful fetch
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String quizId = document.getId();
                    Log.d(TAG, "Processing quiz ID: " + quizId); // Log each quiz ID
                    if (!attemptedQuizIds.contains(quizId)) {
                        handleEachQuiz(document, title, now);
                    } else {
                        Log.d(TAG, "Quiz ID " + quizId + " has already been attempted."); // Log if quiz has been attempted
                    }
                }
                Collections.sort(quizpg, Comparator.comparing(QuizFmgeExam::getTo));

                if (quizpg.isEmpty()) {
                    Log.d(TAG, "No live quizzes found.");
                    nocardpg.setVisibility(View.VISIBLE); // Show the 'nocardpg' layout
                } else {
                    nocardpg.setVisibility(View.GONE); // Hide the 'nocardpg' layout
                }
                quizAdapter.notifyDataSetChanged(); // Update the adapter after changes
            } else {
                Log.e(TAG, "Error getting live quizzes: ", task.getException());
                nocardpg.setVisibility(View.VISIBLE); // Show the 'nocardpg' layout in case of error
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void handleEachQuiz(QueryDocumentSnapshot document, String title, Timestamp now) {
        String quizTitle = document.getString("title");
        String speciality = document.getString("speciality");
        Timestamp to = document.getTimestamp("to");
        Timestamp from = document.getTimestamp("from");

        Log.d(TAG, "Quiz Title: " + quizTitle); // Log the quiz title
        Log.d(TAG, "Speciality: " + speciality); // Log the speciality
        Log.d(TAG, "From: " + from + " | To: " + to); // Log the date range

        String id = document.getString("qid");
        boolean index1 = document.contains("index");
        String index;
        if (index1) {
            index = document.getString("index");
        } else {
            index = "Loading";
        }
        Log.d(TAG, "Index: " + index); // Log the index

        boolean paid1 = document.contains("type");
        if (paid1) {
            Object field = document.get("type");
            if (field instanceof String) {
                String type1 = document.getString("type");
                if (type1.equals("Paid")) {
                    type1 = document.getString("hyOption");
                    if (title.isEmpty() || speciality.equals(title)) {
                        if (now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                            QuizFmgeExam quiz = new QuizFmgeExam(quizTitle, speciality, to, id, type1, index);
                            quizpg.add(quiz);
                            Log.d(TAG, "Added paid quiz: " + quizTitle); // Log when a quiz is added
                        }
                    }
                } else {
                    if (title.isEmpty() || speciality.equals(title)) {
                        if (now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                            QuizFmgeExam quiz = new QuizFmgeExam(quizTitle, speciality, to, id, type1, index);
                            quizpg.add(quiz);
                            Log.d(TAG, "Added free quiz: " + quizTitle); // Log when a quiz is added
                        }
                    }
                }
            } else if (field instanceof Boolean) {
                if (title.isEmpty() || speciality.equals(title)) {
                    if (now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                        QuizFmgeExam quiz = new QuizFmgeExam(quizTitle, speciality, to, id, "Basic", index);
                        quizpg.add(quiz);
                        Log.d(TAG, "Added basic quiz: " + quizTitle); // Log when a basic quiz is added
                    }
                }
            }
        }
    }
}
