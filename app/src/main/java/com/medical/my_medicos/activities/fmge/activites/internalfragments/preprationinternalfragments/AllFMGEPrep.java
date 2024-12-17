package com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.EventListener;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.PreparationFmgeFragment;
import com.medical.my_medicos.activities.fmge.adapters.SpecialitiesFMGEAdapter;
import com.medical.my_medicos.activities.fmge.model.SpecialitiesFmge;
import com.medical.my_medicos.activities.pg.activites.internalfragments.PreparationPgFragment;

import java.util.ArrayList;
import java.util.List;

public class AllFMGEPrep extends Fragment implements PreparationFmgeFragment.SearchableFragment {

    private static final String TAG = "AllFMGEPrep";

    private DocumentReference documentReference;
    private ArrayList<SpecialitiesFmge> categoryItems;
    private ArrayList<SpecialitiesFmge> fullCategoryItems; // To store all items for filtering
    private SpecialitiesFMGEAdapter specialitiesFMGEAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_fmge_prep, container, false);

        // Initialize Firestore Document reference
        documentReference = FirebaseFirestore.getInstance().collection("Categories").document("39liVyLEjII6dtzolxSZ");
        Log.d(TAG, "Document Reference Path: " + documentReference.getPath());

        // Initialize ArrayList and Adapter
        categoryItems = new ArrayList<>();
        fullCategoryItems = new ArrayList<>(); // This will keep the full list for filtering
        specialitiesFMGEAdapter = new SpecialitiesFMGEAdapter(getContext(), categoryItems);

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(specialitiesFMGEAdapter);

        // Initialize ProgressBar
        progressBar = view.findViewById(R.id.progressBar);

        // Fetch data from Firestore
        fetchData();

        return view;
    }

    private void fetchData() {
        // Show the loader
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@NonNull DocumentSnapshot documentSnapshot, @NonNull FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Error fetching document: " + e.getMessage());
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    return;
                }

                categoryItems.clear();
                fullCategoryItems.clear();

                if (documentSnapshot.exists()) {
                    Log.d(TAG, "Document exists");

                    if (documentSnapshot.contains("All")) {
                        Log.d(TAG, "'All' field exists");

                        List<String> specialties = (List<String>) documentSnapshot.get("All");
                        for (String specialtyName : specialties) {
                            if (specialtyName != null) {
                                Log.d(TAG, "Specialty: " + specialtyName);
                                SpecialitiesFmge item = new SpecialitiesFmge(specialtyName, 1); // Assuming value 1 for each specialty
                                categoryItems.add(item);
                                fullCategoryItems.add(item); // Add to the full list for filtering
                            } else {
                                Log.d(TAG, "Specialty has null value");
                            }
                        }
                    } else {
                        Log.d(TAG, "'All' field not found in document");
                    }
                } else {
                    Log.d(TAG, "Document does not exist");
                }

                // Notify adapter
                specialitiesFMGEAdapter.notifyDataSetChanged();

                // Hide the loader and show RecyclerView
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void filter(String query) {
        // Perform filtering based on the query
        query = query.toLowerCase();
        categoryItems.clear();

        if (query.isEmpty()) {
            categoryItems.addAll(fullCategoryItems); // If no query, show full list
        } else {
            for (SpecialitiesFmge item : fullCategoryItems) {
                if (item.getName().toLowerCase().contains(query)) {
                    categoryItems.add(item); // Add filtered items
                }
            }
        }

        // Notify the adapter that the data has changed
        specialitiesFMGEAdapter.notifyDataSetChanged();
    }
}
