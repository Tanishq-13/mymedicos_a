package com.medical.my_medicos.activities.ug.fragments;

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

import com.medical.my_medicos.R;
import com.medical.my_medicos.adapter.ug.UgAdapter1;
import com.medical.my_medicos.adapter.ug.items.ugitem1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResourcesFragment extends Fragment {
    RecyclerView recyclerView;
    String title;
    String pdf;
    private String field1, field2, field3, field4,field6,field7;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resources, container, false);
        recyclerView = view.findViewById(R.id.jobs_recyclerview_resources);
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString("Title");
            Log.d("Title", title);
        }

        refreshData();

        return view;
    }
    public void refreshData() {
        List<ugitem1> items = new ArrayList<>();

        FirebaseFirestore dc = FirebaseFirestore.getInstance();

        dc.collection("UGConfirm")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> dataMap = document.getData();
                                String category = (String) dataMap.get("Type");

                                if ("Resources".equalsIgnoreCase(category)) {
                                    field3 = ((String) dataMap.get("UG Title"));
                                    field4 = ((String) dataMap.get("UG Description"));
                                    field1 = (String) dataMap.get("UG Organiser");
                                    field2 = ((String) dataMap.get("Speciality"));
                                    field6 = ((String) dataMap.get("User"));
                                    Long downloadsValue = (Long) dataMap.get("Downloads");
                                    field7 = String.valueOf(downloadsValue);

                                    String field5 = ((String) dataMap.get("Date"));
                                    pdf = ((String) dataMap.get("pdf"));

                                    int a = field2.compareTo(title);
                                    if (a == 0) {
                                        ugitem1 u = new ugitem1(field1, field2, pdf, field3, field4, field5,field6,field7);
                                        items.add(u);
                                    }
                                }
                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            recyclerView.setAdapter(new UgAdapter1(getContext(), items));
                        } else {
                            Log.d("ResourcesFragment", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
