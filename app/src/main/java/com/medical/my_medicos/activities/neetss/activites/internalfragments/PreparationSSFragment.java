package com.medical.my_medicos.activities.neetss.activites.internalfragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.medical.my_medicos.activities.neetss.adapters.SpecialitiesSSAdapter;
import com.medical.my_medicos.activities.neetss.model.SpecialitiesSS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreparationSSFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayoutPreparation;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private SpecialitiesSSAdapter adapter;
    private ArrayList<SpecialitiesSS> specialitiesList;

    public static PreparationSSFragment newInstance() {
        PreparationSSFragment fragment = new PreparationSSFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_preparation_ss, container, false);

        // Initialize currentUser
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize RecyclerView and adapter
        recyclerView = rootView.findViewById(R.id.recyclerView);
        specialitiesList = new ArrayList<>();
        adapter = new SpecialitiesSSAdapter(getContext(), specialitiesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();
            fetchNeetssFromRealtimeDatabase(userId);
        }

        swipeRefreshLayoutPreparation = rootView.findViewById(R.id.swipeRefreshLayoutPreparation);
        swipeRefreshLayoutPreparation.setOnRefreshListener(this::refreshContent);

        return rootView;
    }

    private void fetchNeetssFromRealtimeDatabase(String userId) {
        DatabaseReference neetssRef = FirebaseDatabase.getInstance()
                .getReference("profiles")
                .child(userId)
                .child("Neetss");

        neetssRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String neetssValue = dataSnapshot.getValue(String.class);
                    if (neetssValue != null) {
                        fetchFromFirestore(neetssValue);
                    } else {
                        Log.e("RealtimeDB", "Neetss value is null");
                    }
                } else {
                    Log.e("RealtimeDB", "Neetss node does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RealtimeDB", "Failed to read Neetss value", databaseError.toException());
            }
        });
    }

    private void fetchFromFirestore(String neetssValue) {
        // Adjust the value to match the Firestore document ID if necessary
        switch (neetssValue.toLowerCase()) {
            case "medical":
                neetssValue = "Medical";
                break;
            case "pediatric":
                neetssValue = "Paediatric";
                break;
            case "surgical":
                neetssValue = "Surgical";
                break;
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("CategoriesNeetss")
                .document(neetssValue)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> dataMap = document.getData();
                            if (dataMap != null) {
                                handleFirestoreData(dataMap);
                            } else {
                                Log.e("Firestore", "No data found in the document");
                            }
                        } else {
                            Log.e("Firestore", "No such document in Firestore");
                        }
                    } else {
                        Log.e("Firestore", "Failed to fetch document", task.getException());
                    }
                });
    }

    private void handleFirestoreData(Map<String, Object> dataMap) {
        specialitiesList.clear(); // Clear the list before adding new data

        // Assuming the 'Data' key holds a list of specialities
        Object data = dataMap.get("Data");
        if (data instanceof List<?>) {
            List<?> dataList = (List<?>) data;

            // Iterate over the list and create SpecialitiesSS objects
            for (int i = 0; i < dataList.size(); i++) {
                Object obj = dataList.get(i);
                if (obj instanceof String) {
                    String name = (String) obj;
                    int priority = i + 1; // Assigning priority based on position, can be customized
                    specialitiesList.add(new SpecialitiesSS(name, priority));
                }
            }
        } else {
            Log.e("Firestore", "'Data' field is not a list");
        }

        adapter.notifyDataSetChanged(); // Notify the adapter that data has changed
    }

    private void refreshContent() {
        specialitiesList.clear();
        adapter.notifyDataSetChanged();
        swipeRefreshLayoutPreparation.setRefreshing(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
