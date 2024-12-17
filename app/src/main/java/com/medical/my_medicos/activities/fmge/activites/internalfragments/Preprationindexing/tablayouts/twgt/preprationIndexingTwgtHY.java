package com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.tablayouts.twgt;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.adapters.WeeklyFmgeQuizAdapter;
import com.medical.my_medicos.activities.fmge.model.QuizFmge;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.FilterViewModel;
import com.medical.my_medicos.activities.pg.model.QuizPG;

import java.util.ArrayList;
import java.util.List;

public class preprationIndexingTwgtHY extends Fragment implements QuizDataDelegate {
    private WeeklyFmgeQuizAdapter quizAdapter;
    private ArrayList<QuizPG> quizpg = new ArrayList<>();
    private String speciality;
    private FilterViewModel filterViewModel;
    private LinearLayout nocardpg;

    // Parameter key
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prepration_indexing_twgt_h_y_fmge, container, false);

        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        quizAdapter = new WeeklyFmgeQuizAdapter(getContext(), quizpg);
        recyclerView.setAdapter(quizAdapter);

        // Initialize the nocardpg layout
        nocardpg = view.findViewById(R.id.nocardpg);

        // Observe the selected subspeciality and fetch questions accordingly
        filterViewModel.getSelectedSubspeciality().observe(getViewLifecycleOwner(), this::getQuestions);

        // Set initial filters if they are null
        if (filterViewModel.getSelectedSubspeciality().getValue() == null) {
            filterViewModel.setSelectedSubspeciality(speciality != null ? speciality : "All (Default)");
        }

        return view;
    }

    private void getQuestions(String subspeciality) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<String> subcollectionIds = new ArrayList<>();

        if (user != null) {
            String userId = user.getPhoneNumber();
            CollectionReference quizResultsCollection = db.collection("QuizResults").document(userId).collection("Weekley");

            quizResultsCollection.get().addOnCompleteListener(subcollectionTask -> {
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

        CollectionReference quizzCollection = db.collection("Fmge").document("Weekley").collection("Quiz");
        Query query = quizzCollection.orderBy("from", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<QuizPG> fetchedQuizzes = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getId();

                    if (!subcollectionIds.contains(id)) {
                        String title = document.getString("title");
                        String quizSpeciality = document.getString("speciality");
                        Timestamp to = document.getTimestamp("to");
                        String thumbnail=document.getString("thumbnail");
                        String index1 = document.contains("index") ? document.getString("index") : "Loading";
                        List<Object> dataList = (List<Object>) document.get("Data");
                        int dataSize = (dataList != null) ? dataList.size() : 0;
                        if (speciality.equals(quizSpeciality)) {
                            boolean type = document.contains("type");
                            if (type) {
                                String type1 = document.getString("type");
                                if (!"Free".equals(type1)) {
                                    type1 = document.getString("hyOption");
                                }

                                if ("All (Default)".equals(subspeciality) || subspeciality.equals(document.getString("index"))) {
                                    QuizPG quizday = new QuizPG(title, quizSpeciality, to, id, type1, index1,thumbnail,String.valueOf(dataSize));
                                    fetchedQuizzes.add(quizday);
                                }
                            }
                        }
                    }
                }
                onQuizDataFetched(fetchedQuizzes);  // Pass data to the delegate
            } else {
                Log.d("PreprationIndexingSwgtLive", "Error getting documents: ", task.getException());
            }
        });
    }

    @Override
    public void onQuizDataFetched(ArrayList<QuizPG> quizList) {
        quizpg.clear();
        quizpg.addAll(quizList);
        quizAdapter.notifyDataSetChanged();

        if (quizpg.isEmpty()) {
            nocardpg.setVisibility(View.VISIBLE);
        } else {
            nocardpg.setVisibility(View.GONE);
        }
    }
}
