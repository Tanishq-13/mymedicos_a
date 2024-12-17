package com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.tablayouts.twgt;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.adapters.WeeklyFmgeQuizAdapter;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.Model.twgt.Quiz;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.twgt.PreprationIndexTwgtBookmark;
import com.medical.my_medicos.activities.pg.adapters.WeeklyQuizAdapter;
import com.medical.my_medicos.activities.pg.model.QuizPG;

import java.util.ArrayList;
import java.util.List;

public class PreprationIndexFMGETwgtBookmark extends Fragment implements QuizDataDelegate {

    private DatabaseReference database;
    private String phoneNumber;
    private FirebaseFirestore firestore;
    private List<Quiz> bookmarkedQuizzes;
    private RecyclerView recyclerView;
    private WeeklyFmgeQuizAdapter quizAdapter;
    private LinearLayout noitem;
    private String speciality; // Add this field to store the speciality parameter
    private ProgressBar progressBar; // Add ProgressBar field

    public PreprationIndexFMGETwgtBookmark() {
        // Required empty public constructor
    }

    public static PreprationIndexFMGETwgtBookmark newInstance(String speciality) {
        PreprationIndexFMGETwgtBookmark fragment = new PreprationIndexFMGETwgtBookmark();
        Bundle args = new Bundle();
        args.putString("speciality", speciality); // Pass the speciality parameter
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            speciality = getArguments().getString("speciality"); // Retrieve the speciality parameter
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prepration_index_twgt_bookmark, container, false);

        // Initialize Firebase and RecyclerView
        database = FirebaseDatabase.getInstance().getReference();
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        phoneNumber = current.getPhoneNumber();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noitem=view.findViewById(R.id.noitemfound);
        bookmarkedQuizzes = new ArrayList<>();
        quizAdapter = new WeeklyFmgeQuizAdapter(getContext(), new ArrayList<>());  // Initialize with an empty list
        recyclerView.setAdapter(quizAdapter);

        progressBar = view.findViewById(R.id.progressBar); // Initialize ProgressBar

        fetchBookmarkedQuizzes();

        return view;
    }

    private void fetchBookmarkedQuizzes() {
        progressBar.setVisibility(View.VISIBLE); // Show progress bar

        DatabaseReference bookmarkRef = database.child("profiles").child(phoneNumber).child("bookmarks");
        bookmarkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<QuizPG> quizList = new ArrayList<>();  // Create a new list for the quizzes
                int bookmarkCount = (int) dataSnapshot.getChildrenCount(); // Total number of bookmarks
                final int[] fetchedCount = {0}; // Counter for fetched quizzes

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Boolean isBookmarked = snapshot.getValue(Boolean.class);
                    if (Boolean.TRUE.equals(isBookmarked)) { // Check if the bookmark is true
                        String quizId = snapshot.getKey();
                        Log.d("chckbkmr",quizId);

                        fetchQuizDetailsFromFirestore(quizId, quizList, bookmarkCount, fetchedCount); // Pass counter values
                    }
                }

                if (quizList.isEmpty()) {
                    progressBar.setVisibility(View.GONE); // Hide progress bar if no quizzes
                }// bar when data fetching is done
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE); // Hide progress bar in case of an error
                // Handle errors here
            }
        });
    }

    private void fetchQuizDetailsFromFirestore(String quizId, ArrayList<QuizPG> quizList, int bookmarkCount, int[] fetchedCount) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.d("FetchQuiz", "User not logged in");
            return;
        }

        String userId = user.getPhoneNumber();

        // Check if the quizId is in QuizResults collection for the current user
        firestore.collection("QuizResults")
                .document(userId)
                .collection("Weekley")
                .document(quizId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Quiz ID is present in QuizResults
                            Log.d("FetchQuiz", "Quiz ID is already present in QuizResults");
                        } else {
                            // Quiz ID is not present, fetch the quiz details from PGupload
                            firestore.collection("Fmge")
                                    .document("Weekley")
                                    .collection("Quiz")
                                    .document(quizId)
                                    .get()
                                    .addOnCompleteListener(quizTask -> {
                                        if (quizTask.isSuccessful()) {
                                            DocumentSnapshot quizDocument = quizTask.getResult();
                                            if (quizDocument != null && quizDocument.exists()) {
                                                String title = quizDocument.getString("title");
                                                String quizSpeciality = quizDocument.getString("speciality");
                                                String thumbnail=quizDocument.getString("thumbnail");
                                                Timestamp to = quizDocument.getTimestamp("to");
                                                String hyo=quizDocument.getString("hyOption");
                                                boolean type = quizDocument.contains("type");
                                                String index1 =quizDocument.getString("index");
                                                ;
                                                boolean index = quizDocument.contains("index");
                                                List<Object> dataList = (List<Object>) quizDocument.get("Data");
                                                int dataSize = (dataList != null) ? dataList.size() : 0;  // Get the size of the 'Data' array (number of questions)
                                                String type1 = quizDocument.getString("hyOption");

                                                if (speciality.equals(quizSpeciality)) {
                                                    QuizPG quiz= new QuizPG(title, type1, to, quizId, quizSpeciality, index1,thumbnail,String.valueOf(dataSize));

                                                    quizList.add(quiz);

                                                    Log.d("FetchQuiz", title + " added to list." +" "+type1);
                                                }
                                                Log.d("quizz1",String.valueOf(quizList.size()));

                                            } else {
                                                Log.d("FetchQuiz", "No such quiz document: " + quizId);
                                            }
                                            Log.d("quizz2",String.valueOf(quizList.size()));

                                        }
                                        Log.d("quizz3",String.valueOf(quizList.size()));
                                        if(!quizList.isEmpty()){
                                            noitem.setVisibility(View.GONE);
                                        }
                                        quizAdapter.updateQuizList(quizList); // Update adapter with new list

                                    });


                        }
                        Log.d("quizz",String.valueOf(quizList.size()));

                        // Increment the fetched counter and check if all bookmarks have been fetched
                        fetchedCount[0]++;
                        if (fetchedCount[0] == bookmarkCount) {
                            Log.d("quizll",String.valueOf(quizList.size()));
                            // All bookmarks have been processed, update the adapter
                            progressBar.setVisibility(View.GONE); // Hide progress bar
                        }
                    }
                });
    }

    @Override
    public void onQuizDataFetched(ArrayList<QuizPG> quizList) {
        // Update the adapter with new data
        quizAdapter.updateQuizList(quizList);

        // You can manage any additional UI updates or logic related to fetched data here
    }
}