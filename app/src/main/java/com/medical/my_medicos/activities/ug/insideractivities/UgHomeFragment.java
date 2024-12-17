package com.medical.my_medicos.activities.ug.insideractivities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.ug.model.UgExamViewModel;
import com.medical.my_medicos.adapter.job.items.jobitem2;
import com.medical.my_medicos.adapter.ug.MyAdapter8;
import com.medical.my_medicos.adapter.ug.UgAdapter1;
import com.medical.my_medicos.adapter.ug.items.ugitem1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.databinding.FragmentUgHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UgHomeFragment extends Fragment {
    private FragmentUgHomeBinding binding;
    private RecyclerView recyclerView;
    RecyclerView recyclerView2;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private String field1, field2, field3, field4,field6,field7;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UgExamViewModel ugExamViewModel =
                new ViewModelProvider(this).get(UgExamViewModel.class);

        binding = FragmentUgHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Log.d("commit","commit");

        recyclerView = root.findViewById(R.id.ug_recycleview);
        String[] specialitiesArray = getResources().getStringArray(R.array.specialityjobs);
        List<String> specialitiesList = Arrays.asList(specialitiesArray);
        List<jobitem2> joblist2 = new ArrayList<jobitem2>();
        for (String subspeciality : specialitiesList) {
            joblist2.add(new jobitem2(subspeciality));
        }

        recyclerView2 = root.findViewById(R.id.recyclerview6);
        int spanCount = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView2.setLayoutManager(gridLayoutManager);
        recyclerView2.setAdapter(new MyAdapter8(getContext(), joblist2));
        fetchData();

        return root;
    }

    private void fetchData() {

        List<ugitem1> items = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Query query = db.collection("UGConfirm");
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
                                    String field5=((String) dataMap.get("Date"));
                                    String pdf=((String) dataMap.get("pdf"));

                                    ugitem1 u = new ugitem1(field1, field2, pdf, field3, field4,field5,field6,field7);
                                    items.add(u);


                                }
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                recyclerView.setAdapter(new UgAdapter1(getContext(), items));
                            } else {
                            }
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
}
}