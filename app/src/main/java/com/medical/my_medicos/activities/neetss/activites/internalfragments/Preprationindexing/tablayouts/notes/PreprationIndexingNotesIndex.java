package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.notes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.Model.Index.IndexData;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter.Indexadapter.IndexAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreprationIndexingNotesIndex#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreprationIndexingNotesIndex extends Fragment {
    private static final String ARG_SPECIALITY = "speciality";
    private RecyclerView recyclerView;
    private IndexAdapter adapter;
    private List<IndexData> indexList;
    private FirebaseFirestore db;
    private String speciality;

    public PreprationIndexingNotesIndex() {
        // Required empty public constructor
    }

    public static PreprationIndexingNotesIndex newInstance(String speciality) {
        PreprationIndexingNotesIndex fragment = new PreprationIndexingNotesIndex();
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

        db = FirebaseFirestore.getInstance();
        indexList = new ArrayList<>();
        adapter = new IndexAdapter(indexList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prepration_indexing_notes_index, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchIndexData();

        return view;
    }

    private void fetchIndexData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();
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
                            // Adjust neetssValue to match the required format if necessary
//                            String formattedNeetssValue = formatNeetssValue(neetssValue);
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
        } else {
            Log.e("RealtimeDB", "User is not authenticated");
        }
    }

//    private String formatNeetssValue(String neetssValue) {
//        // This function formats the Neetss value if necessary
//        switch (neetssValue.toLowerCase()) {
//            case "medical":
//                return "Medical";
//            case "pediatric":
//                return "Paediatric";
//            case "surgical":
//                return "Surgical";
//            default:
//                return neetssValue; // Return as-is if no formatting needed
//        }
//    }

    private void fetchFromFirestore(String neetssValue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Using the formatted Neetss value to build the Firestore path
        db.collection("Neetss")
                .document(neetssValue) // Document fetched from Realtime Database
                .collection("Indexs")
                .document("Index")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            indexList.clear();

                            Log.d("data", "Fetching index data for speciality: " + speciality);

                            // Check if the document contains the speciality field and data list
                            if (speciality.equals(document.getString("speciality"))) {
                                Log.d("data3", "Speciality matched");

                                List<String> dataList = (List<String>) document.get("Data");
                                if (dataList != null) {
                                    for (String data : dataList) {
                                        Log.d("data2", data);
                                        indexList.add(new IndexData(data));
                                    }
                                }
                            } else {
                                Log.d("Firestore", "Speciality did not match or speciality field not found");
                            }

                            adapter.notifyDataSetChanged();

                            Log.d("Firestore", "Data fetch successful.");
                        } else {
                            Log.e("Firestore Error", "Document does not exist");
                        }
                    } else {
                        Log.e("Firestore Error", "Error fetching index data", task.getException());
                    }
                });
    }
}
