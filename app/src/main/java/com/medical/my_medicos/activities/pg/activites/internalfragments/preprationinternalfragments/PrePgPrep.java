package com.medical.my_medicos.activities.pg.activites.internalfragments.preprationinternalfragments;

import android.annotation.SuppressLint;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.internalfragments.PreparationPgFragment;
import com.medical.my_medicos.activities.pg.adapters.SpecialitiesPGAdapter;
import com.medical.my_medicos.activities.pg.model.SpecialitiesPG;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrePgPrep#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrePgPrep extends Fragment implements PreparationPgFragment.SearchableFragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference databaseReference;
    private ArrayList<SpecialitiesPG> categoryItems;
    private ArrayList<SpecialitiesPG> fullCategoryItems; // To store all items for filtering

    SpecialitiesPGAdapter specialitiesPGAdapter;
    private RecyclerView recyclerView;  private static final String TAG = "AllPgPrep";

    private DocumentReference documentReference;private ProgressBar progressBar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PrePgPrep() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrePgPrep.
     */
    // TODO: Rename and change types and number of parameters
    public static PrePgPrep newInstance(String param1, String param2) {
        PrePgPrep fragment = new PrePgPrep();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view= inflater.inflate(R.layout.fragment_pre_pg_prep, container, false);
        documentReference = FirebaseFirestore.getInstance().collection("Categories").document("39liVyLEjII6dtzolxSZ");
        Log.d(TAG, "Document Reference Path: " + documentReference.getPath());
        // Initialize ArrayList and Adapter
        categoryItems = new ArrayList<>();
        specialitiesPGAdapter = new SpecialitiesPGAdapter(getContext(), categoryItems);
        fullCategoryItems = new ArrayList<>(); // This will keep the full list for filtering

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(specialitiesPGAdapter);
        progressBar = view.findViewById(R.id.progressBar);


        // Fetch data from Firebase
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

                    if (documentSnapshot.contains("Pre")) {
                        Log.d(TAG, "'All' field exists");

                        List<String> specialties = (List<String>) documentSnapshot.get("Pre");
                        for (String specialtyName : specialties) {
                            if (specialtyName != null) {
                                Log.d(TAG, "Specialty: " + specialtyName);
                                categoryItems.add(new SpecialitiesPG(specialtyName, 1));
                                fullCategoryItems.add(new SpecialitiesPG(specialtyName,1));// Assuming value 1 for each specialty
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
                specialitiesPGAdapter.notifyDataSetChanged();

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
            for (SpecialitiesPG item : fullCategoryItems) {
                if (item.getName().toLowerCase().contains(query)) {
                    categoryItems.add(item); // Add filtered items
                }
            }
        }

        // Notify the adapter that the data has changed
        specialitiesPGAdapter.notifyDataSetChanged();
    }
}