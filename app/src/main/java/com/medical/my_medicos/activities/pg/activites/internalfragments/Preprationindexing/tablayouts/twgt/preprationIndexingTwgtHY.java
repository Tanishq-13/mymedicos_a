package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.twgt;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.FilterViewModel;
import com.medical.my_medicos.activities.pg.adapters.WeeklyQuizAdapter;
import com.medical.my_medicos.activities.pg.model.QuizPG;

import java.util.ArrayList;
import java.util.List;



public class preprationIndexingTwgtHY extends Fragment implements QuizDataDelegate {
    private WeeklyQuizAdapter quizAdapter;
    private ArrayList<QuizPG> quizpg = new ArrayList<>();
    private String speciality;
    private FilterViewModel filterViewModel;
    private LinearLayout nocardpg;  // Reference to the nocardpg layout

    private static final String ARG_SPECIALITY = "speciality";

    public preprationIndexingTwgtHY() {
        // Required empty public constructor
    }

    public static preprationIndexingTwgtHY newInstance(String speciality) {
        preprationIndexingTwgtHY fragment = new preprationIndexingTwgtHY();
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
        View view = inflater.inflate(R.layout.fragment_prepration_indexing_twgt_h_y, container, false);

        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        quizAdapter = new WeeklyQuizAdapter(getContext(), new ArrayList<>());  // Initialize with an empty list
        recyclerView.setAdapter(quizAdapter);

        // Initialize the nocardpg layout
        nocardpg = view.findViewById(R.id.nocardpg);

        filterViewModel.getSelectedSubspeciality().observe(getViewLifecycleOwner(), subspeciality -> {
            getQuestions(subspeciality);
        });

        // Set initial filters if they are null
        if (filterViewModel.getSelectedSubspeciality().getValue() == null) {
            filterViewModel.setSelectedSubspeciality(speciality);
        }
        if (filterViewModel.getSelectedSubspeciality().getValue() == null) {
            filterViewModel.setSelectedSubspeciality("All (Default)");
        }

        getQuestions(speciality);

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

        CollectionReference quizzCollection = db.collection("PGupload").document("Weekley").collection("Quiz");
        Query query = quizzCollection.orderBy("from", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<QuizPG> quizList = new ArrayList<>();  // Create a new list for the quizzes
                quizpg.clear();  // Clear the list before adding new items
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getId();

                    if (true) {
                        String title = document.getString("title");
                        String quizSpeciality = document.getString("speciality");
                        String thumbnail=document.getString("thumbnail");
                        Timestamp to = document.getTimestamp("to");
                        String index1;

                        List<Object> dataList = (List<Object>) document.get("Data");
                        int dataSize = (dataList != null) ? dataList.size() : 0;
                        if (speciality.equals(quizSpeciality)) {
                            boolean index = document.contains("index");
                            if (index) {
                                index1 = document.getString("index");
                            } else {
                                index1 = "Loading";
                            }
                            boolean type = document.contains("type");

                            if (type) {
                                Object field = document.get("type");
                                if (field instanceof String) {
                                    String type1 = document.getString("type");
                                    if (type1.compareTo("Free") != 0) {
                                        type1 = document.getString("hyOption");
                                        if (speciality.equals(quizSpeciality)) {
                                            Log.d("TYPE q ", type1);
                                            if ("All (Default)".equals(subspeciality) || subspeciality.equals(document.getString("index"))) {
                                                QuizPG quizday = new QuizPG(title, type1, to, id, quizSpeciality, index1,thumbnail,String.valueOf(dataSize));
                                                quizList.add(quizday);  // Add quiz to the list
                                                Log.d("PreprationIndexingTwgtHY", "Adding quiz: " + quizday.getTitle() + " with index: " + quizday.getIndex());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Pass the fetched quiz data to the adapter via the delegate method
                onQuizDataFetched(quizList);
            } else {
                Log.d("PreprationIndexingTwgtHY", "Error getting documents: ", task.getException());
            }
        });
    }
}
