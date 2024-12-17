package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.swgt;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;

import com.medical.my_medicos.activities.neetss.model.QuizSSExam;
import com.medical.my_medicos.activities.neetss.model.Swgtmodel;
import com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizAdapterSwgt;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class PreprationIndexingSwgtLive extends Fragment {
    private com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizAdapterSwgt quizAdapter;
    private ArrayList<QuizSSExam> quizpg = new ArrayList<>();
    private String speciality;
    LinearLayout nocardpg;
    ProgressBar progressBar;

    private static final String ARG_SPECIALITY = "speciality";

    public PreprationIndexingSwgtLive() {
        // Required empty public constructor
    }

    public static com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.swgt.PreprationIndexingSwgtLive newInstance(String speciality) {
        com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.swgt.PreprationIndexingSwgtLive fragment = new com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.swgt.PreprationIndexingSwgtLive();
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
        // Get the current user's phone number (used as the user ID)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String currentUid = user.getPhoneNumber();
            Log.d(TAG, "Fetching live quizzes for user: " + currentUid);

            // Get reference to Firebase Realtime Database to fetch user specialization
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference().child("profiles").child(currentUid);

            // Add a listener to fetch the specialization for the current user
            userRef.child("Neetss").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Fetch specialization
                    String specialization = snapshot.getValue(String.class);
                    if (specialization != null) {
                        Log.d(TAG, "User specialization fetched: " + specialization);

                        // Use the fetched specialization as the document ID in Firestore
                        queryLiveQuizzes(db, specialization, title, now, attemptedQuizIds);
                    } else {
                        Log.e(TAG, "Specialization is null for user: " + currentUid);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error fetching specialization: " + error.getMessage());
                }
            });
        } else {
            Log.e(TAG, "User is not logged in.");
        }
    }

    // Fetch live quizzes based on the user's specialization
    @SuppressLint("NotifyDataSetChanged")
    private void queryLiveQuizzes(FirebaseFirestore db, String specialization, String title, Timestamp now, Set<String> attemptedQuizIds) {
        // Use the specialization fetched from Realtime Database as the document ID
        CollectionReference quizCollection = db.collection("Neetss").document(specialization).collection("CWT");

        Log.d(TAG, "Querying live quizzes in Firestore for specialization: " + specialization);

        Query query = quizCollection
                .whereLessThanOrEqualTo("from", now)
                .whereGreaterThanOrEqualTo("to", now);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                quizpg.clear(); // Clear the list before adding new quizzes

                // Iterate over each document in the query result
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String quizId = document.getId();
                    // Only add quizzes that haven't been attempted
                    if (!attemptedQuizIds.contains(quizId)) {
                        handleEachQuiz(document, title, now);
                    }
                }

                // Sort quizzes based on the "to" timestamp
                Collections.sort(quizpg, Comparator.comparing(QuizSSExam::getTo));

                // Update UI visibility based on quiz availability
                if (quizpg.isEmpty()) {
                    Log.d(TAG, "No live quizzes found.");
                    nocardpg.setVisibility(View.VISIBLE);
                } else {
                    nocardpg.setVisibility(View.GONE);
                }
            } else {
                Log.e(TAG, "Error fetching live quizzes: ", task.getException());
                nocardpg.setVisibility(View.VISIBLE); // Show 'no card' layout in case of error
            }

            // Hide progress bar and notify adapter that data has changed
            progressBar.setVisibility(View.GONE);
            quizAdapter.notifyDataSetChanged();
        });
    }


    private void handleEachQuiz(QueryDocumentSnapshot document, String title, Timestamp now) {
        String quizTitle = document.getString("title");
        String speciality = document.getString("speciality");
        Timestamp to = document.getTimestamp("to");
        Timestamp from = document.getTimestamp("from");

        String id = document.getString("qid");
        String index = document.contains("index") ? document.getString("index") : "Loading";

        String type = document.contains("type") ? document.getString("type") : "Free";

        if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
            QuizSSExam quiz = new QuizSSExam(quizTitle, speciality, to, id, type, index);
            quizpg.add(quiz); // Add the quiz to the list
        }
    }
}
