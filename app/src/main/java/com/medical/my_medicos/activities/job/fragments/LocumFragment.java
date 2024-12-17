package com.medical.my_medicos.activities.job.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.adapter.job.MyAdapter6;
import com.medical.my_medicos.R;
import com.medical.my_medicos.adapter.job.items.jobitem1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocumFragment extends Fragment {
    RecyclerView recyclerView;
    String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locum, container, false);
        recyclerView = view.findViewById(R.id.jobs_recyclerview_locum);
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString("Title");
            Log.d("Title", title);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataLocum();
    }

    public void loadDataLocum() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<jobitem1> locumJobList = new ArrayList<>();
        db.collection("JOB")
                .orderBy("date", Query.Direction.ASCENDING) // Ensure the date field is in a sortable format
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> dataMap = document.getData();
                                String category = (String) dataMap.get("Job type");

                                if ("Locum".equalsIgnoreCase(category)) {
                                    String organiser = (String) dataMap.get("JOB Organiser");
                                    String location = (String) dataMap.get("Location");
                                    String date = (String) dataMap.get("date");
                                    String speciality = (String) dataMap.get("Speciality");
                                    String user = (String) dataMap.get("User");
                                    String title = (String) dataMap.get("JOB Title");
                                    String documentId = (String) dataMap.get("documentId");
                                    Log.d("Title", speciality);
                                    int r = title.compareTo(speciality);

                                    if (r == 0) {
                                        jobitem1 job = new jobitem1(speciality, organiser, location, date, user, title, category, documentId);
                                        locumJobList.add(job);
                                    }
                                }
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                            recyclerView.setAdapter(new MyAdapter6(getActivity(), locumJobList));
                        } else {
                            Log.d("LocumFragment", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
