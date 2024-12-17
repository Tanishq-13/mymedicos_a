package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.swgt;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.adapters.WeeklyQuizAdapterSwgt;
import com.medical.my_medicos.activities.pg.model.QuizPGExam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class PreprationIndexingSwgtLive extends Fragment {
    private WeeklyQuizAdapterSwgt quizAdapter;
    private ArrayList<QuizPGExam> quizpg = new ArrayList<>();
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
                        }
                    } else {
                        Log.e(TAG, "Error fetching attempted quizzes: ", task.getException());
                    }
                    onCompleted.accept(attemptedQuizIds);
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchLiveQuizzes(FirebaseFirestore db, String title, Timestamp now, Set<String> attemptedQuizIds) {
        CollectionReference quizzCollection = db.collection("PGupload").document("CWT").collection("Quiz");
        Query query = quizzCollection.whereLessThanOrEqualTo("from", now)
                .whereGreaterThanOrEqualTo("to", now);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                quizpg.clear(); // Clearing the list to ensure it only contains current live quizzes
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String quizId = document.getId();
                    if (!attemptedQuizIds.contains(quizId)) {
                        handleEachQuiz(document, title, now);
                    }
                }
                Collections.sort(quizpg, Comparator.comparing(QuizPGExam::getTo));

                if (quizpg.isEmpty()) {
                    Log.d(TAG, "No live quizzes found.");
                    nocardpg.setVisibility(View.VISIBLE); // Show the 'nocardpg' layout
                } else {
                    nocardpg.setVisibility(View.GONE); // Hide the 'nocardpg' layout
                }
            } else {
                Log.e(TAG, "Error getting live quizzes: ", task.getException());
                nocardpg.setVisibility(View.VISIBLE); // Show the 'nocardpg' layout in case of error
            }
            progressBar.setVisibility(View.GONE);
            quizAdapter.notifyDataSetChanged(); // Notify the adapter that data has changed
        });
    }

    private void handleEachQuiz(QueryDocumentSnapshot document, String title, Timestamp now) {
        String quizTitle = document.getString("title");
        String speciality = document.getString("speciality");
        Timestamp to = document.getTimestamp("to");
        Timestamp from = document.getTimestamp("from");
        List<Object> dataList = (List<Object>) document.get("data");
        int dataSize = (dataList != null) ? dataList.size() : 0;  // Get the size of the 'Data' array (number of questions)

        String id = document.getString("qid");
        String index = document.contains("index") ? document.getString("index") : "Loading";

        String type = document.contains("type") ? document.getString("type") : "Basic";

        if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
            QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, type, index,String.valueOf(dataSize));
            quizpg.add(quiz); // Add the quiz to the list
        }
    }
}
