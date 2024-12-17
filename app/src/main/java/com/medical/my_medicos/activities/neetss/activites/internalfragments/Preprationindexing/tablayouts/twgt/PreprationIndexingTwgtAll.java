package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.twgt;

import static androidx.media3.common.MediaLibraryInfo.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
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
import com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizSSAdapter;
import com.medical.my_medicos.activities.neetss.model.QuizSS;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.FilterViewModel;

import java.util.ArrayList;
import java.util.List;

public class PreprationIndexingTwgtAll extends Fragment implements QuizDataDelegate {

    private WeeklyQuizSSAdapter quizAdapter;
    private ArrayList<QuizSS> quizpg = new ArrayList<>();
    private String speciality;
    private FilterViewModel filterViewModel;
    private ProgressBar progressBar;
    private LinearLayout nocardpg;

    private static final String ARG_SPECIALITY = "speciality";
    private static final String TAG = "PreprationIndexingTwgtAll";

    public PreprationIndexingTwgtAll() {
        // Required empty public constructor
    }

    public static PreprationIndexingTwgtAll newInstance(String speciality) {
        PreprationIndexingTwgtAll fragment = new PreprationIndexingTwgtAll();
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
        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prepration_indexing_twgt_all, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        quizAdapter = new WeeklyQuizSSAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(quizAdapter);

        progressBar = view.findViewById(R.id.progressBar);
        nocardpg = view.findViewById(R.id.nocardpg);

        filterViewModel.setSelectedSubspeciality("All (Default)");

        if (filterViewModel.getSelectedSubspeciality().getValue() == null) {
            filterViewModel.setSelectedSubspeciality(speciality != null ? speciality : "All (Default)");
        }

        getQuestions(filterViewModel.getSelectedSubspeciality().getValue());

        filterViewModel.getSelectedSubspeciality().observe(getViewLifecycleOwner(), this::getQuestions);

        return view;
    }

    @Override
    public void onQuizDataFetched(ArrayList<QuizSS> quizList) {
        quizAdapter.updateQuizList(quizList);

        if (quizList.isEmpty()) {
            nocardpg.setVisibility(View.VISIBLE);
        } else {
            nocardpg.setVisibility(View.GONE);
        }
    }

    private void getQuestions(String subspeciality) {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<String> subcollectionIds = new ArrayList<>();

        if (user != null) {
            String userId = user.getPhoneNumber();

            // Step 1: Fetch NEET SS value from Firebase Realtime Database
            fetchNeetSSValue(userId, db, subspeciality, subcollectionIds);
        } else {
            Log.e(TAG, "User is not logged in.");
            progressBar.setVisibility(View.GONE);
        }
    }

    private void fetchNeetSSValue(String userId, FirebaseFirestore db, String subspeciality, List<String> subcollectionIds) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("profiles").child(userId).child("Neetss");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String neetSSStatus = dataSnapshot.getValue(String.class);
                Log.d(TAG, "NEET SS Status: " + neetSSStatus);

                // Step 2: Fetch attempted quiz collection IDs
                fetchAttemptedQuizCollectionIds(db, userId, neetSSStatus, subspeciality, subcollectionIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching NEET SS status", databaseError.toException());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchAttemptedQuizCollectionIds(FirebaseFirestore db, String userId, String neetSSStatus,String subspeciality ,List<String> subcollectionIds) {
        CollectionReference quizResultsCollection = db.collection("QuizResultsPGPrep").document(userId).collection("Weekley");

        quizResultsCollection.get().addOnCompleteListener(subcollectionTask -> {
            if (subcollectionTask.isSuccessful()) {
                for (QueryDocumentSnapshot subdocument : subcollectionTask.getResult()) {
                    String subcollectionId = subdocument.getId();
                    subcollectionIds.add(subcollectionId);
                    Log.d(TAG, "Subcollection ID: " + subcollectionId);
                }

                // Step 3: Fetch quiz details from PGupload
                fetchQuizzes(db, neetSSStatus, subspeciality, subcollectionIds);
            } else {
                Log.e(TAG, "Error fetching subcollections", subcollectionTask.getException());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchQuizzes(FirebaseFirestore db, String neetSSStatus, String subspeciality, List<String> subcollectionIds) {
        CollectionReference quizzCollection = db.collection("Neetss").document(neetSSStatus).collection("Weekley");
        Query query = quizzCollection.orderBy("from", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful()) {
                ArrayList<QuizSS> quizList = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getId();

                    if (!subcollectionIds.contains(id)) {
                        String title = document.getString("title");
                        String quizSpeciality = document.getString("speciality");
                        Timestamp to = document.getTimestamp("to");
                        boolean type = document.contains("type");
                        String index1 = document.contains("index") ? document.getString("index") : "Loading";

                        if (speciality.equals(quizSpeciality)) {
                            String type1 = determineQuizType(document);
                            if ("All (Default)".equals(subspeciality) || subspeciality.equals(document.getString("index"))) {
                                QuizSS quizday = new QuizSS(title, quizSpeciality, to, id, type1, index1);
                                quizList.add(quizday);
                                Log.d(TAG, "Added Quiz: " + title);
                            }
                        }
                    }
                }

                Log.d(TAG, "Total quizzes added: " + quizList.size());
                onQuizDataFetched(quizList);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private String determineQuizType(QueryDocumentSnapshot document) {
        Object field = document.get("type");
        if (field instanceof String) {
            String type1 = document.getString("type");
            return type1.equals("Paid") ? document.getString("hyOption") : type1;
        } else if (field instanceof Boolean) {
            return "Basic";
        }
        return "Basic";
    }
}
