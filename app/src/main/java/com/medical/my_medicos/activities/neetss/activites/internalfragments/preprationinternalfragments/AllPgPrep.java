package com.medical.my_medicos.activities.neetss.activites.internalfragments.preprationinternalfragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.adapters.SpecialitiesSSAdapter;
import com.medical.my_medicos.activities.neetss.model.SpecialitiesSS;

import java.util.ArrayList;
import java.util.List;

public class AllPgPrep extends Fragment {

    private static final String TAG = "AllPgPrep";

    private DocumentReference documentReference;
    private ArrayList<SpecialitiesSS> categoryItems;
    private SpecialitiesSSAdapter specialitiesPGAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_pg_prep, container, false);

        // Initialize Firestore Document reference
        documentReference = FirebaseFirestore.getInstance().collection("NeetCategories").document("A2JKiPrEKYmo5mhUv0pI");
        Log.d(TAG, "Document Reference Path: " + documentReference.getPath());

        // Initialize ArrayList and Adapter
        categoryItems = new ArrayList<>();
        specialitiesPGAdapter = new SpecialitiesSSAdapter(getContext(), categoryItems);

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(specialitiesPGAdapter);

        // Initialize ProgressBar
        progressBar = view.findViewById(R.id.progressBar);

        // Fetch data from Firestore
        fetchData();

        return view;
    }

    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@NonNull DocumentSnapshot documentSnapshot, @NonNull FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Error fetching document: " + e.getMessage());
                    Toast.makeText(getContext(), "Error fetching data.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                categoryItems.clear();

                if (documentSnapshot.exists() && documentSnapshot.getData().containsKey("All")) {
                    Object data = documentSnapshot.get("All");
                    if (data instanceof List<?>) {
                        List<?> rawList = (List<?>) data;
                        for (Object obj : rawList) {
                            if (obj instanceof String) {
                                categoryItems.add(new SpecialitiesSS((String) obj, 1));
                            }
                        }
                    } else {
                        Log.d(TAG, "Expected 'All' to be a List<String>, but did not match.");
                    }
                } else {
                    Log.d(TAG, "Document does not exist or missing 'All' field");
                    Toast.makeText(getContext(), "No data available.", Toast.LENGTH_LONG).show();
                }

                specialitiesPGAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

}
