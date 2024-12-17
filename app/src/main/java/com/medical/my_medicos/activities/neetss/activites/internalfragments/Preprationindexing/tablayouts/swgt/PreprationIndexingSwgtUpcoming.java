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

import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter.Swgt.ExamUpcomingadapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreprationIndexingSwgtUpcoming#newInstance} factory method to
 * create an instance of this fragment.
 */


public class PreprationIndexingSwgtUpcoming extends Fragment {
    private ExamUpcomingadapter quizAdapter;
    private ArrayList<QuizSSExam> quizpg = new ArrayList<>();
    private String speciality;
    private LinearLayout nocardpg;  // Reference to the nocardpg layout

    // Parameter key
    private static final String ARG_SPECIALITY = "speciality";

    public PreprationIndexingSwgtUpcoming() {
        // Required empty public constructor
    }

    public static PreprationIndexingSwgtUpcoming newInstance(String speciality) {
        PreprationIndexingSwgtUpcoming fragment = new PreprationIndexingSwgtUpcoming();
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
        View view = inflater.inflate(R.layout.fragment_prepration_indexing_swgt_upcoming, container, false);

        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        nocardpg = view.findViewById(R.id.nocardpg);  // Initialize nocardpg layout
        quizAdapter = new ExamUpcomingadapter(getContext(), quizpg);
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

        CollectionReference quizzCollection = db.collection("Neetss").document(title1).collection("CWT");
        Query query = quizzCollection.orderBy("from", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getId();
                    Timestamp now = Timestamp.now();

                    if (!subcollectionIds.contains(id)) {
                        String title = document.getString("title");
                        String speciality = document.getString("speciality");
                        Timestamp to = document.getTimestamp("to");

                        Log.d("Speciality coming", speciality);
                        Log.d("Title to compare", title1);
                        boolean type = document.contains("type");
                        boolean index = document.contains("index");
                        Timestamp from = document.getTimestamp("from");

                        boolean paid1 = document.contains("type");

                        if (index) {
                            String index1 = document.getString("index");
                            if (paid1) {
                                Object field = document.get("type");
                                if (field instanceof String) {
                                    String type1 = document.getString("type");
                                    if (type1.equals("Paid")) {
                                        type1 = document.getString("hyOption");
                                        int r = speciality.compareTo(title1);

                                        if (r == 0) {
                                            if (now.compareTo(from) < 0 && (title.isEmpty() || speciality.equals(title))) {
                                                QuizSSExam quiz = new QuizSSExam(title, speciality, to, id, type1, index1);
                                                quizpg.add(quiz);
                                            }
                                        }
                                    } else {
                                        int r = speciality.compareTo(title1);

                                        if (r == 0) {
                                            if (now.compareTo(from) < 0 && (title.isEmpty() || speciality.equals(title))) {
                                                QuizSSExam quiz = new QuizSSExam(title, speciality, to, id, type1, index1);
                                                quizpg.add(quiz);
                                            }
                                        }

                                    }

                                } else if (field instanceof Boolean) {
                                    int r = speciality.compareTo(title1);

                                    if (r == 0) {
                                        if (now.compareTo(from) < 0 && (title.isEmpty() || speciality.equals(title))) {
                                            QuizSSExam quiz = new QuizSSExam(title, speciality, to, id, "Free", index1);
                                            quizpg.add(quiz);
                                        }
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
