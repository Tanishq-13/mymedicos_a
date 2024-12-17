package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.swgt;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizAdapterSwgt;
import com.medical.my_medicos.activities.neetss.model.QuizSSExam;
import com.medical.my_medicos.activities.neetss.model.Swgtmodel;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter.Swgt.Swgtpastadapter;


import java.util.ArrayList;
import java.util.List;
public class PreprationIndexingSwgtPast extends Fragment {
    private Swgtpastadapter quizAdapter;
    private ArrayList<QuizSSExam> quizpg = new ArrayList<>();
    private String speciality;
    private LinearLayout nocardpg;  // Reference to the nocardpg layout

    private static final String ARG_SPECIALITY = "speciality";

    public PreprationIndexingSwgtPast() {
        // Required empty public constructor
    }

    public static PreprationIndexingSwgtPast newInstance(String speciality) {
        PreprationIndexingSwgtPast fragment = new PreprationIndexingSwgtPast();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prepration_indexing_swgt_past, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        nocardpg = view.findViewById(R.id.nocardpg);  // Initialize nocardpg layout
        quizAdapter = new Swgtpastadapter(getContext(), quizpg);
        recyclerView.setAdapter(quizAdapter);

        getSpecializationAndFetchQuestions();
        return view;
    }

    private void getSpecializationAndFetchQuestions() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getPhoneNumber();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference().child("profiles").child(userId);

            // Fetch the 'Neetss' field from Firebase Realtime Database
            userRef.child("Neetss").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String fetchedSpecialization = snapshot.getValue(String.class);
                    if (fetchedSpecialization != null) {
                        Log.d(TAG, "User specialization fetched: " + fetchedSpecialization);
                        // Use the fetched specialization to get quizzes
                        getQuestions(fetchedSpecialization);
                    } else {
                        Log.e(TAG, "Specialization is null for user: " + userId);
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

    void getQuestions(String title1) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<String> subcollectionIds = new ArrayList<>();

        if (user != null) {
            String userId = user.getPhoneNumber();
            CollectionReference quizResultsCollection = db.collection("QuizResults").document(userId).collection("Weekley");

            quizResultsCollection.get()
                    .addOnCompleteListener(subcollectionTask -> {
                        if (subcollectionTask.isSuccessful()) {
                            for (QueryDocumentSnapshot subdocument : subcollectionTask.getResult()) {
                                String subcollectionId = subdocument.getId();
                                subcollectionIds.add(subcollectionId);
                                Log.d("Subcollection ID", subcollectionId);
                            }
                        } else {
                            Log.e("Subcollection ID", "Error fetching subcollections", subcollectionTask.getException());
                        }
                    });
        }

        // Fetch quizzes based on the 'speciality' from Firestore
        CollectionReference quizzCollection = db.collection("Neetss").document(title1).collection("CWT");
        Query query = quizzCollection.orderBy("from", Query.Direction.ASCENDING);
        Timestamp now = Timestamp.now();

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String quizTitle = document.getString("title");
                    String speciality = document.getString("speciality");
                    Timestamp to = document.getTimestamp("to");
                    String id = document.getString("qid");
                    boolean index = document.contains("index");
                    boolean paid1 = document.contains("type");
                    if (index) {
                        String index1 = document.getString("index");

                        if (paid1) {
                            Object field = document.get("type");
                            Timestamp from = document.getTimestamp("from");
                            if (field instanceof String) {
                                String type1 = document.getString("type");
                                if (type1.equals("Paid")) {
                                    type1 = document.getString("hyOption");
                                    if (now.compareTo(to) > 0 && (title1.isEmpty() || speciality.equals(title1))) {
                                        int r = speciality.compareTo(title1);
                                        if (r == 0) {
                                            QuizSSExam quiz = new QuizSSExam(quizTitle, speciality, to, id, type1, index1);
                                            quizpg.add(quiz);
                                        }
                                    }
                                } else {
                                    if (now.compareTo(to) > 0 && (title1.isEmpty() || speciality.equals(title1))) {
                                        int r = speciality.compareTo(title1);
                                        if (r == 0) {
                                            QuizSSExam quiz = new QuizSSExam(quizTitle, speciality, to, id, type1, index1);
                                            quizpg.add(quiz);
                                        }
                                    }
                                }

                            } else if (field instanceof Boolean) {
                                if (now.compareTo(to) > 0 && (title1.isEmpty() || speciality.equals(title1))) {
                                    int r = speciality.compareTo(title1);
                                    if (r == 0) {
                                        QuizSSExam quiz = new QuizSSExam(quizTitle, speciality, to, id, "Free", index1);
                                        quizpg.add(quiz);
                                    }
                                }
                            }
                        }
                    }
                }

                quizAdapter.notifyDataSetChanged();
                // Show nocardpg layout if quiz list is empty
                if (quizpg.isEmpty()) {
                    nocardpg.setVisibility(View.VISIBLE);
                } else {
                    nocardpg.setVisibility(View.GONE);
                }

            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
}
