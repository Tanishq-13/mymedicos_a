package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing;

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
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter.CustomSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

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
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_spinner, container, false);

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
                String selectedOption = (String) parent.getItemAtPosition(position);
                filterViewModel.setSelectedSubspeciality(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterViewModel.setSelectedSubspeciality("All (Default)");
            }
        });

        return view;
    }

    private void initializeSpinnerWithDefaults() {
        List<String> defaultOptions = new ArrayList<>();
        defaultOptions.add("All (Default)");
        defaultOptions.add("Option 1");
        defaultOptions.add("Option 2");
        defaultOptions.add("Option 3");
        spinnerAdapter = new CustomSpinnerAdapter(requireContext(), R.layout.spinner_item, defaultOptions);
        sortSpinner.setAdapter(spinnerAdapter);
    }

    private void fetchOptionsFromFirestore() {
        db.collection("PGupload").document("Indexs").collection("Index")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> options = new ArrayList<>();
                        options.add("All (Default)");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (speciality.compareTo((String) document.get("speciality")) == 0) {
                                List<String> dataList = (List<String>) document.get("Data");
                                if (dataList != null) {
                                    options.addAll(dataList);
                                }
                            }
                        }
                        if (options.size() == 1) { // Only default option available
                            options.add("Option 1");
                            options.add("Option 2");
                            options.add("Option 3");
                        }
                        updateSpinnerOptions(options);
                    } else {
                        Toast.makeText(getContext(), "Error fetching options", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateSpinnerOptions(List<String> options) {
        spinnerAdapter = new CustomSpinnerAdapter(requireContext(), R.layout.spinner_item, options);
        sortSpinner.setAdapter(spinnerAdapter);
    }
}
