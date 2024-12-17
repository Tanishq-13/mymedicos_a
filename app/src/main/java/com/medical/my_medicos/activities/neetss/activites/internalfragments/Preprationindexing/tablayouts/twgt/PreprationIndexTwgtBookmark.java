package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.twgt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.Model.twgt.Quiz;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter.QuizAdapter;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.twgt.QuizDataDelegateBookmark;

import java.util.ArrayList;
import java.util.List;

public class PreprationIndexTwgtBookmark extends Fragment implements QuizDataDelegateBookmark {

    private DatabaseReference database;
    private String phoneNumber;
    private FirebaseFirestore firestore;
    private List<Quiz> bookmarkedQuizzes;
    private RecyclerView recyclerView;
    private QuizAdapter quizAdapter;
    private String speciality;
    private ProgressBar progressBar;
    private String neetss; // Add field to store neetss value

    public PreprationIndexTwgtBookmark() {
        // Required empty public constructor
    }

    public static PreprationIndexTwgtBookmark newInstance(String speciality) {
        PreprationIndexTwgtBookmark fragment = new PreprationIndexTwgtBookmark();
        Bundle args = new Bundle();
        args.putString("speciality", speciality);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            speciality = getArguments().getString("speciality");
        }
        // Initialize Firebase
        database = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        phoneNumber = current != null ? current.getPhoneNumber() : "";
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prepration_index_twgt_bookmark, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookmarkedQuizzes = new ArrayList<>();
        quizAdapter = new QuizAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(quizAdapter);

        progressBar = view.findViewById(R.id.progressBar);

        fetchNeetssValue(); // Fetch the neetss value first

        return view;
    }

    private void fetchNeetssValue() {
        DatabaseReference neetssRef = database.child("profiles").child(phoneNumber).child("neetss");
        neetssRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                neetss = dataSnapshot.getValue(String.class);
                Log.d("FetchNeetssValue", "Neetss value: " + neetss);

                // Fetch bookmarked quizzes after getting the neetss value
                fetchBookmarkedQuizzes(neetss);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FetchNeetssValue", "Failed to fetch neetss value", databaseError.toException());
                fetchBookmarkedQuizzes(neetss); // Proceed even if there's an error
            }
        });
    }

    private void fetchBookmarkedQuizzes(String neetss) {
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference bookmarkRef = database.child("profiles").child(phoneNumber).child("bookmarks");
        bookmarkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Quiz> quizList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Boolean isBookmarked = snapshot.getValue(Boolean.class);
                    if (Boolean.TRUE.equals(isBookmarked)) {
                        String quizId = snapshot.getKey();
                        fetchQuizDetailsFromFirestore(quizId, quizList, neetss);
                    }
                }

                onQuizDataFetched(quizList);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.e("FetchBookmarkedQuizzes", "Failed to fetch bookmarks", databaseError.toException());
            }
        });
    }

    private void fetchQuizDetailsFromFirestore(String quizId, ArrayList<Quiz> quizList, String neetss) {
        if (phoneNumber.isEmpty()) {
            Log.d("FetchQuiz", "Phone number not available");
            return;
        }

        firestore.collection("QuizResults")
                .document(phoneNumber)
                .collection("Weekley")
                .document(quizId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Log.d("FetchQuiz", "Quiz ID is already present in QuizResults");
                        } else {
                            firestore.collection("Neetss")
                                    .document(neetss)
                                    .collection("Weekley")
                                    .document(quizId)
                                    .get()
                                    .addOnCompleteListener(quizTask -> {
                                        if (quizTask.isSuccessful()) {
                                            DocumentSnapshot quizDocument = quizTask.getResult();
                                            if (quizDocument != null && quizDocument.exists()) {
                                                String title = quizDocument.contains("title") ? quizDocument.getString("title") : "Unknown Title";
                                                Timestamp dueDate = quizDocument.contains("to") ? quizDocument.getTimestamp("to") : null;
                                                String index = quizDocument.contains("index") ? quizDocument.getString("index") : "Unknown Index";
                                                String title1 = quizDocument.contains("speciality") ? quizDocument.getString("speciality") : "Unknown Index";
                                                String id = quizDocument.contains("qid") ? quizDocument.getString("qid") : "Unknown Index";
                                                String quizSpeciality = quizDocument.contains("speciality") ? quizDocument.getString("speciality") : "Unknown Index";

                                                Quiz quiz = new Quiz();
                                                quiz.setTitle(title);
                                                quiz.setId(id);
                                                quiz.setTitle1(title1);
                                                quiz.setDueDate(dueDate);
                                                quiz.setType(true);
                                                quiz.setIndex(index);

                                                if (speciality.equals(quizSpeciality) && neetss.equals(quizSpeciality)) { // Use neetss value
                                                    quizList.add(quiz);
                                                    quizAdapter.notifyDataSetChanged();
                                                }
                                            } else {
                                                Log.d("FetchQuiz", "No such quiz document");
                                            }
                                        } else {
                                            Log.d("FetchQuiz", "Get quiz failed with ", quizTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.d("FetchQuiz", "Get failed with ", task.getException());
                    }
                });
    }

    @Override
    public void onQuizDataFetched(ArrayList<Quiz> quizList) {
        quizAdapter.updateQuizList(quizList);
        // Additional UI updates or logic related to fetched data can be handled here
    }
}
