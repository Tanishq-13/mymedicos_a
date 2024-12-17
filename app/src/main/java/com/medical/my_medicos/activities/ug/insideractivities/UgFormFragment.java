package com.medical.my_medicos.activities.ug.insideractivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.ug.UgPostInsiderActivity;
import com.medical.my_medicos.activities.ug.model.UgFormViewModel;
import com.medical.my_medicos.adapter.job.items.jobitem2;
import com.medical.my_medicos.adapter.ug.UgAdapter1;
import com.medical.my_medicos.adapter.ug.items.ugitem1;
import com.medical.my_medicos.databinding.FragmentUgformBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UgFormFragment extends Fragment {

    Button movetoug;
    private RecyclerView recyclerViewconfirmed;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String field1, field2, field3, field4, field6,field7;
    private FragmentUgformBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UgFormViewModel ugFormViewModel = new ViewModelProvider(this).get(UgFormViewModel.class);

        binding = FragmentUgformBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewconfirmed = root.findViewById(R.id.uploadlistug);
        String[] specialitiesArray = getResources().getStringArray(R.array.specialityjobs);
        List<String> specialitiesList = Arrays.asList(specialitiesArray);
        List<jobitem2> joblist2 = new ArrayList<>();
        for (String subspeciality : specialitiesList) {
            joblist2.add(new jobitem2(subspeciality));
        }

        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });

        fetchData();

        movetoug = root.findViewById(R.id.newupdatebtn);
        movetoug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UgPostInsiderActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private void fetchData() {
        List<ugitem1> items = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                String currentUserPhoneNumber = currentUser.getPhoneNumber();

                Query query = db.collection("UGConfirm")
                        .whereEqualTo("User", currentUserPhoneNumber);

                query.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> dataMap = document.getData();

                                        field3 = ((String) dataMap.get("UG Title"));
                                        field4 = ((String) dataMap.get("UG Description"));
                                        field1 = (String) dataMap.get("UG Organiser");
                                        field2 = ((String) dataMap.get("Speciality"));
                                        field6 = ((String) dataMap.get("User"));
                                        Long downloadsValue = (Long) dataMap.get("Downloads");
                                        field7 = String.valueOf(downloadsValue);
                                        String field5 = ((String) dataMap.get("Date"));
                                        String pdf = ((String) dataMap.get("pdf"));

                                        ugitem1 u = new ugitem1(field1, field2, pdf, field3, field4, field5, field6,field7);
                                        items.add(u);
                                    }

                                    if (items.isEmpty()) {
                                        binding.noResultsforUG.setVisibility(View.VISIBLE);
                                    } else {
                                        binding.noResultsforUG.setVisibility(View.GONE);
                                        recyclerViewconfirmed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                        recyclerViewconfirmed.setAdapter(new UgAdapter1(getContext(), items));
                                    }

                                } else {
                                    // Handle the error if the query is not successful
                                }

                                // After fetching data, complete the refresh
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
            } else {
                // Handle the case where the user is not signed in
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
