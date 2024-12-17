package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.notes;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.Model.Index.IndexData;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.adapter.Indexadapter.IndexAdapter;

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
        db.collection("PGupload").document("Indexs").collection("Index")

                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        indexList.clear();

                        Log.d("data", "Fetching index data for speciality: " + speciality);
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("data2", "data");
                            if (speciality.compareTo((String) document.get("speciality"))==0) {
                                Log.d("data3", "data");
                                List<String> dataList = (List<String>) document.get("Data");
                                if (dataList != null) {
                                    for (String data : dataList) {
                                        Log.d("data2", data);
                                        indexList.add(new IndexData(data));
                                    }
                                }
                            }
                        }

                        Log.d("Firestore", "Data fetch successful. Documents count: " + task.getResult().size());
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.e("Firestore Error", "Error fetching index data", task.getException());
                    }
                });
    }


}

