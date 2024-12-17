package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.twgt;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.FilterViewModel;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.tablayouts.twgt.QuizDataDelegate;
import com.medical.my_medicos.activities.fmge.adapters.WeeklyFmgeQuizAdapter;
import com.medical.my_medicos.activities.fmge.model.QuizFmge;
import com.medical.my_medicos.activities.pg.adapters.WeeklyQuizAdapter;
import com.medical.my_medicos.activities.pg.model.QuizPG;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PreprationIndexingTwgtAll extends Fragment implements QuizDataDelegate {
    private WeeklyQuizAdapter quizAdapter;
    private ArrayList<QuizFmge> quizpg = new ArrayList<>();
    private String speciality;
    private FilterViewModel filterViewModel;
    private ProgressBar progressBar;
    private LinearLayout nocardpg;

    private static final String ARG_SPECIALITY = "speciality";

    public PreprationIndexingTwgtAll() {
        // Required empty public constructor
    }

    public static com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.twgt.PreprationIndexingTwgtAll newInstance(String speciality) {
        com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.twgt.PreprationIndexingTwgtAll fragment = new PreprationIndexingTwgtAll();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prepration_indexing_twgt_all, container, false);

        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        quizAdapter = new WeeklyQuizAdapter(getContext(), new ArrayList<>());  // Pass an empty list initially
        recyclerView.setAdapter(quizAdapter);

        // Initialize the progress bar and nocardpg layout
        progressBar = view.findViewById(R.id.progressBar);
        nocardpg = view.findViewById(R.id.nocardpg);

        filterViewModel.setSelectedSubspeciality("All (Default)");

        // Set initial filter if it's not already set
        if (filterViewModel.getSelectedSubspeciality().getValue() == null) {
            filterViewModel.setSelectedSubspeciality(speciality != null ? speciality : "All (Default)");
        }
        // Fetch questions with the initial speciality
        getQuestions(filterViewModel.getSelectedSubspeciality().getValue());

        // Observe the selected subspeciality and fetch questions accordingly
        filterViewModel.getSelectedSubspeciality().observe(getViewLifecycleOwner(), this::getQuestions);

        return view;
    }

    @Override
    public void onQuizDataFetched(ArrayList<QuizPG> quizList) {
        // Update the adapter with new data
        quizAdapter.updateQuizList(quizList);

        // Show or hide the nocardpg layout based on the quiz list size
        if (quizList.isEmpty()) {
            nocardpg.setVisibility(View.VISIBLE);
        } else {
            nocardpg.setVisibility(View.GONE);
        }
    }

    void getQuestions(String subspeciality) {
        // Show the progress bar before starting the task
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<String> subcollectionIds = new ArrayList<>();

        if (user != null) {
            String userId = user.getPhoneNumber();
            CollectionReference quizResultsCollection = db.collection("QuizResultsPGPrep").document(userId).collection("Weekley");

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
        Log.d("ihh","l");
        CollectionReference quizzCollection = db.collection("PGupload").document("Weekley").collection("Quiz");
        Query query = quizzCollection.orderBy("from", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(task -> {
            // Hide the progress bar when the task completes
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful()) {
                ArrayList<QuizPG> quizList = new ArrayList<>();  // Create a new list for the quizzes

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getId();

                    if (!subcollectionIds.contains(id)) {
                        String title = document.getString("title");
                        String quizSpeciality = document.getString("speciality");
                        String thumbnail=document.getString("thumbnail");
                        Timestamp to = document.getTimestamp("to");
                        String hyo=document.getString("hyOption");
                        boolean type = document.contains("type");
                        String index1;
                        boolean index = document.contains("index");
                        List<Object> dataList = (List<Object>) document.get("Data");
                        int dataSize = (dataList != null) ? dataList.size() : 0;  // Get the size of the 'Data' array (number of questions)
                        AtomicBoolean isSolved = new AtomicBoolean(false);

                        if (speciality.equals(quizSpeciality)) {
                            if (index) {
                                index1 = document.getString("index");
                            } else {
                                index1 = "Loading";
                            }

                            Log.d("PreprationIndexingTwgtAll", "Adding Quiz: " + title + " with index: " + index1);

                            if (type) {
                                Object field = document.get("type");
                                if (field instanceof String) {
                                    String type1 = document.getString("type");
                                    if (type1.equals("Paid")) {
                                        type1 = document.getString("hyOption");
                                        if ("All (Default)".equals(subspeciality) || subspeciality.equals(document.getString("index"))) {
                                            DocumentReference quizResultRef = db.collection("QuizResults")
                                                    .document(user.getPhoneNumber())
                                                    .collection("Weekley")
                                                    .document(id);
                                            QuizPG quizday = new QuizPG(title, hyo, to, id, quizSpeciality, index1, thumbnail, String.valueOf(dataSize));
                                            quizList.add(quizday);

                                            quizResultRef.get().addOnSuccessListener(quizResultSnapshot -> {
                                                isSolved.set(quizResultSnapshot.exists());
//                                                QuizPG quizday = new QuizPG(title, hyo, to, id, quizSpeciality, index1, thumbnail, String.valueOf(dataSize));
                                                quizday.setSolved(isSolved.get());

                                                Log.d("PreprationIndexingTwgtAll", "Added Paid Quiz: " + title);
                                            });
                                            quizday.setLocked(false);
                                            quizAdapter.notifyDataSetChanged();

                                        }
                                    } else {
                                        if ("All (Default)".equals(subspeciality) || subspeciality.equals(document.getString("index"))) {
                                            QuizPG quizday = new QuizPG(title, hyo, to, id, quizSpeciality, index1, thumbnail, String.valueOf(dataSize));

                                            DocumentReference quizResultRef = db.collection("QuizResults")
                                                    .document(user.getPhoneNumber())
                                                    .collection("Weekley")
                                                    .document(id);
                                            DocumentReference quizProgressRef = db.collection("QuizProgress")
                                                    .document(user.getPhoneNumber())
                                                    .collection("pgneet")
                                                    .document(id);
// Initially set isSolved to false, add to list
                                            quizday.setLocked(true);

                                            quizday.setProgress(false);
                                            quizday.setSolved(false);
                                            quizList.add(quizday);  // Add immediately with isSolved = false

// Fetch the isSolved status asynchronously
                                            quizResultRef.get().addOnSuccessListener(quizResultSnapshot -> {
                                                // Update isSolved if the document exists
                                                boolean solved = quizResultSnapshot.exists();
                                                quizday.setSolved(solved);

                                                // Notify the adapter about the change in isSolved status
                                                quizAdapter.notifyItemChanged(quizList.indexOf(quizday));

                                                Log.d("PreprationIndexingTwgtAll", "Updated Quiz isSolved: " + title);
                                            });
                                            quizProgressRef.get().addOnSuccessListener(quizProgressSnapshot->{
                                                boolean isProgress=quizProgressSnapshot.exists();

                                                quizday.setProgress(isProgress);
//                                                Log.d("isprogress",String.valueOf(quizday.isProgress())+quizday.getTitle());

                                                quizAdapter.notifyDataSetChanged();
                                            });
                                            Log.d("isSolved", String.valueOf(quizday.isSolved()));

                                            Log.d("PreprationIndexingTwgtAll", "Added Paid Quiz: " + title);

                                        }
                                    }
                                } else if (field instanceof Boolean) {
                                    if ("All (Default)".equals(subspeciality) || subspeciality.equals(document.getString("index"))) {
                                        QuizPG quizday = new QuizPG(title,hyo, to, id,quizSpeciality , index1,thumbnail,String.valueOf(dataSize));


                                        quizList.add(quizday);
                                        Log.d("PreprationIndexingTwgtAll", "Added Basic Quiz: " + title);
                                    }
                                }
                            } else {
                                if ("All (Default)".equals(subspeciality) || subspeciality.equals(document.getString("index"))) {
                                    QuizPG quizday = new QuizPG(title,"Free", to, id,quizSpeciality , index1,thumbnail,String.valueOf(dataSize));


                                    quizList.add(quizday);
                                    Log.d("PreprationIndexingTwgtAll", "Added Basic Quiz (No Type): " + title);
                                }
                            }
                        }
                    }
                }

                Log.d("PreprationIndexingTwgtAll", "Total quizzes added: " + quizList.size());

                // Pass the fetched quiz data to the adapter via the delegate method
                onQuizDataFetched(quizList);
            } else {
                Log.d("PreprationIndexingTwgtAll", "Error getting documents: ", task.getException());
            }
        });
    }
}
