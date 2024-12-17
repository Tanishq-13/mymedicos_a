package com.medical.my_medicos.activities.neetss.activites;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.HomeActivity;
import com.medical.my_medicos.activities.neetss.activites.extras.CreditsActivity;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.HomePgFragment;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.NeetExamSSFragment;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.PreparationSSFragment;
import com.medical.my_medicos.databinding.ActivitySsPrepActivtyBinding;

import java.util.HashMap;
import java.util.Map;


public class SsprepActivity extends AppCompatActivity {
    ActivitySsPrepActivtyBinding binding;
    BottomNavigationView bottomNavigationPg;
    BottomAppBar bottomAppBarPg;
    private TextView currentStreaksTextView;
    private FirebaseFirestore db;
    private ImageView backtothehomefrompg;
    private int lastSelectedItemId = 0;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySsPrepActivtyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkIfSpecializationSelected();
        currentStreaksTextView = findViewById(R.id.currentstraks);

        backtothehomefrompg = findViewById(R.id.backtothehomefrompg);
        backtothehomefrompg.setOnClickListener(view -> {
            Intent i = new Intent(SsprepActivity.this, HomeActivity.class);
            startActivity(i);
        });
//        fetchStreakCount();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUid = currentUser.getPhoneNumber();
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference userRef = database.getReference().child("profiles").child(currentUid);

            // Fetch coins
            userRef.child("coins").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer coinsValue = snapshot.getValue(Integer.class);
                    if (coinsValue != null) {
                        binding.currentcoinspg.setText(String.valueOf(coinsValue));
                    } else {
                        binding.currentcoinspg.setText("0");
                    }
                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error loading coins from database: " + error.getMessage());
                }
            });

            // Fetch streaks
            userRef.child("streaks").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer streaksValue = snapshot.getValue(Integer.class);
                    if (streaksValue != null) {
                        binding.currentstraks.setText(String.valueOf(streaksValue));
                    } else {
                        binding.currentstraks.setText("0");
                    }
                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error loading streaks from database: " + error.getMessage());
                }
            });
        }


        setupBottomAppBar();

        HomePgFragment homeFragment = HomePgFragment.newInstance();
        replaceFragment(homeFragment);
        LinearLayout openpgdrawerIcon = findViewById(R.id.creditscreen);
        openpgdrawerIcon.setOnClickListener(v -> openHomeSidePgActivity());

        configureWindow();
    }
    private void loadContent() {
        // Load the main content of the activity, e.g., fragments
        HomePgFragment homeFragment = HomePgFragment.newInstance();
        replaceFragment(homeFragment);
    }

    @SuppressLint("RestrictedApi")
    private void saveSpecializationToDatabase(String specialization) {
        if ("Medicine".equals(specialization)) {
           specialization="medical";
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUid = currentUser.getPhoneNumber();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference().child("profiles").child(currentUid).child("Neetss");
            Log.d("neetssss",(String.valueOf(database.getReference())));
            userRef.setValue(specialization)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Specialization saved successfully");
                            loadContent(); // Load the main content after saving
                        } else {
                            Log.e(TAG, "Error saving specialization: ", task.getException());
                        }
                    });
        }
    }
    private void checkIfSpecializationSelected() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUid = currentUser.getPhoneNumber();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference().child("profiles").child(currentUid).child("Neetss");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (false) {
                        // Load the main content directly if specialization is already selected
                        loadContent();
                    } else {
                        // Show the bottom sheet form if not selected
                        showBottomSheetForm();
                    }
                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error checking specialization: ", error.toException());
                }
            });
        }
    }
    private void showBottomSheetForm() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_form_ss, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        // Find views by ID
        Spinner specializationSpinner = view.findViewById(R.id.specialization_spinner);
        TextView specializationDescription = view.findViewById(R.id.specialization_description);
        CheckBox confirmationCheckbox = view.findViewById(R.id.confirmation_checkbox);
        Button submitButton = view.findViewById(R.id.submit_button);

        // Descriptions map
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("Medicine", "Medicine specialization focuses on the comprehensive care and management of adult patients. It includes a broad range of sub-specialties like Cardiology, Nephrology, and Endocrinology, among others.");
        descriptions.put("Surgery", "Surgery specialization trains doctors in various surgical procedures and techniques. Sub-specialties include General Surgery, Neurosurgery, Cardiothoracic Surgery, and more.");
        descriptions.put("Pediatric", "Pediatrics deals with the medical care of infants, children, and adolescents. This specialization includes areas like Neonatology, Pediatric Cardiology, and Pediatric Neurology.");

        // Set initial description
        String initialSpecialization = specializationSpinner.getSelectedItem().toString();
        specializationDescription.setText(descriptions.get(initialSpecialization));

        // Update description based on selection
        specializationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSpecialization = parent.getItemAtPosition(position).toString();
                specializationDescription.setText(descriptions.get(selectedSpecialization));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                specializationDescription.setText("");
            }
        });

        // Enable Submit button only if checkbox is checked
        confirmationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            submitButton.setEnabled(isChecked);
        });

        // Handle submit button click
        submitButton.setOnClickListener(v -> {
            String selectedSpecialization = specializationSpinner.getSelectedItem().toString();
            saveSpecializationToDatabase(selectedSpecialization);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
    }




    private void fetchStreakCount() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String phoneNumber = currentUser.getPhoneNumber(); // Implement this method to get the current user's phone number
        DocumentReference userDocRef = db.collection("users").document(phoneNumber);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Long streakCount = document.getLong("streakCount");
                    if (streakCount != null) {
                        currentStreaksTextView.setText(String.valueOf(streakCount));
                    } else {
                        currentStreaksTextView.setText("0");
                    }
                } else {
                    currentStreaksTextView.setText("0");
                }
            } else {
                currentStreaksTextView.setText("Error");
                // Handle the error
            }
        });
    }

    private void configureWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private void updateToolbarColor(Fragment fragment) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarheading = findViewById(R.id.toolbarheading);
        ImageView backtothehomefrompg = findViewById(R.id.backtothehomefrompg);
        LinearLayout creditscreen = findViewById(R.id.creditscreen);
        TextView currentcoinspg = findViewById(R.id.currentcoinspg);

        if (fragment instanceof NeetExamSSFragment || fragment instanceof PreparationSSFragment) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            toolbarheading.setTextColor(ContextCompat.getColor(this, R.color.unselected));
            backtothehomefrompg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.arrow_bk));
            creditscreen.setBackground(ContextCompat.getDrawable(this, R.drawable.categoryblack));
            currentcoinspg.setTextColor(ContextCompat.getColor(this, R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            toolbarheading.setTextColor(ContextCompat.getColor(this, R.color.white));
            backtothehomefrompg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.arrow_bkwhite));
            creditscreen.setBackground(ContextCompat.getDrawable(this, R.drawable.categorywhite));
            currentcoinspg.setTextColor(ContextCompat.getColor(this, R.color.unselected));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    private void setupBottomAppBar() {
        bottomAppBarPg = binding.bottomappabarpginsider;
        bottomNavigationPg = bottomAppBarPg.findViewById(R.id.bottomNavigationViewpginsider);
        bottomNavigationPg.setBackground(null);

        bottomNavigationPg.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_pghome) {
                if (lastSelectedItemId != R.id.navigation_pghome) {
                    replaceFragment(HomePgFragment.newInstance());
                    lastSelectedItemId = R.id.navigation_pghome;
                }
                return true;
            } else if (itemId == R.id.navigation_pgneet) {
                if (lastSelectedItemId != R.id.navigation_pgneet) {
                    replaceFragment(PreparationSSFragment.newInstance());
                    lastSelectedItemId = R.id.navigation_pgneet;
                }
                return true;
            } else if (itemId == R.id.navigation_pgpreparation) {
                if (lastSelectedItemId != R.id.navigation_pgpreparation) {
                    replaceFragment(NeetExamSSFragment.newInstance());
                    lastSelectedItemId = R.id.navigation_pgpreparation;
                }
                return true;
//            } else if (itemId == R.id.navigation_userprepprofile) {
//                if (lastSelectedItemId != R.id.navigation_userprepprofile) {
//                    replaceFragment(MePgFragment.newInstance());
//                    lastSelectedItemId = R.id.navigation_userprepprofile;
//                }
//                return true;
//            }
            }

            return false;
        });
    }

    public void openHomeSidePgActivity() {
        Intent settingsIntent = new Intent(SsprepActivity.this, CreditsActivity.class);
        startActivity(settingsIntent);
    }

    @SuppressLint("RestrictedApi")
    private void replaceFragment(Fragment fragment) {
        Log.d(TAG, "Replacing fragment with: " + fragment.getClass().getSimpleName());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_pg, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        updateToolbarColor(fragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
