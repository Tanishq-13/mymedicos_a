package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.adapter.CustomSpinnerAdapter;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.filterdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BottomSheetSpinnerFragment extends BottomSheetDialogFragment {
    private FilterViewModel filterViewModel;
    private FirebaseFirestore db;
    private CustomSpinnerAdapter spinnerAdapter;
    private Spinner sortSpinner;
    private String speciality;

    public BottomSheetSpinnerFragment(String speciality) {
        this.speciality = speciality;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_spinner_fmge, container, false);

        sortSpinner = view.findViewById(R.id.sort_spinner);
        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);
        db = FirebaseFirestore.getInstance();

        // Initialize spinner with default options
        initializeSpinnerWithDefaults();

        // Fetch options from Firestore
        fetchOptionsFromFirestore();

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterdata selectedOption = (filterdata) parent.getItemAtPosition(position);
                String heading=selectedOption.getIndexHeading();
                filterViewModel.setSelectedSubspeciality(heading);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterViewModel.setSelectedSubspeciality("All (Default)");
            }
        });

        return view;
    }

    private void initializeSpinnerWithDefaults() {
        List<filterdata> defaultOptions = new ArrayList<>();

        // Use getContext() to avoid accessing the context before attachment
        if (getContext() != null) {
            spinnerAdapter = new CustomSpinnerAdapter(getContext(), R.layout.spinner_item, defaultOptions);
            sortSpinner.setAdapter(spinnerAdapter);
        }
    }

    private void fetchOptionsFromFirestore() {
        db.collection("PGupload").document("Indexs").collection("Index")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<filterdata> options = new ArrayList<>();
                        options.add(new filterdata("All (Default)", options.size())); // Default option

                        // Map to store quiz counts for each index
                        Map<String, Integer> quizCounts = new HashMap<>();

                        // Fetch quizzes and compute counts based on matching speciality and index
                        db.collection("PGupload").document("Weekley").collection("Quiz")
                                .get()
                                .addOnCompleteListener(quizTask -> {
                                    if (quizTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot quizDoc : quizTask.getResult()) {
                                            String quizSpeciality = quizDoc.getString("speciality");
                                            String quizIndex = quizDoc.getString("index"); // Single string field

                                            if (speciality.equals(quizSpeciality) && quizIndex != null) {
                                                // Increment the quiz count for the matching index
                                                quizCounts.put(quizIndex, quizCounts.getOrDefault(quizIndex, 0) + 1);
                                            }
                                        }

                                        // Populate filterdata list using quiz counts
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            if (speciality.equals(doc.getString("speciality"))) {
                                                List<String> dataList = (List<String>) doc.get("Data");
                                                if (dataList != null) {
                                                    for (String heading : dataList) {
                                                        int count = quizCounts.getOrDefault(heading, 0);
                                                        options.add(new filterdata(heading, count));
                                                    }
                                                }
                                            }
                                        }

                                        // Handle case where only default option is available
                                        if (options.size() == 1) {
                                            options.add(new filterdata("Option 1", 0));
                                            options.add(new filterdata("Option 2", 0));
                                            options.add(new filterdata("Option 3", 0));
                                        }

                                        updateSpinnerOptions(options);
                                    } else {
                                        Toast.makeText(getContext(), "Error fetching quizzes", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "Error fetching options", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateSpinnerOptions(List<filterdata> options) {
        spinnerAdapter = new CustomSpinnerAdapter(requireContext(), R.layout.spinner_item, options);
        sortSpinner.setAdapter(spinnerAdapter);
    }
}
