package com.medical.my_medicos.activities.neetss.activites.internalfragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.PreprationNotesFragment;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.PreprationSWGT;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.PreprationTWGT;
import com.medical.my_medicos.databinding.ActivityPreprationindexingBinding;
import com.medical.my_medicos.databinding.PreprationIndexingActivitySsBinding;

public class PreprationindexingActivitySS extends AppCompatActivity {

    private PreprationIndexingActivitySsBinding binding;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String speciality = getIntent().getStringExtra("specialityPgName");

        binding = PreprationIndexingActivitySsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(speciality);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        // Set the back button to finish the current activity and return to the previous one
        binding.backtothehomefrompg.setOnClickListener(v -> {
            finish(); // Finish the current activity
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewpginsiderindexing1);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;
            String itemId = String.valueOf(item.getItemId());

            if (itemId.equals(String.valueOf(R.id.navigation_pgnotes))) {
                selectedFragment = PreprationNotesFragment.newInstance(speciality);
            } else if (itemId.equals(String.valueOf(R.id.navigation_pgtwgt))) {
                selectedFragment = PreprationTWGT.newInstance(speciality);
                Log.d("speciality is fragment", String.valueOf(R.id.navigation_pgtwgt));
                Log.d("speciality is fragment", itemId);
            } else if (itemId.equals(String.valueOf(R.id.navigation_pgswgt))) {
                Log.d("speciality is fragment1", String.valueOf(R.id.navigation_pgswgt));
                Log.d("speciality is fragment1", itemId);
                selectedFragment = PreprationSWGT.newInstance(speciality);
            } else {
                selectedFragment = PreprationNotesFragment.newInstance(speciality);
            }

            loadFragment(selectedFragment);
            return true;
        });

        loadFragment(PreprationNotesFragment.newInstance(speciality));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }
}