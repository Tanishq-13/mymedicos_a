package com.medical.my_medicos.activities.pg.activites.internalfragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.medical.my_medicos.activities.home.fragments.ClubFragment;
import com.medical.my_medicos.activities.home.fragments.SlideshowFragment;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.PreprationNotesFragment;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.PreprationSWGT;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.PreprationTWGT;
import com.medical.my_medicos.databinding.ActivityPreprationindexingBinding;
import com.medical.my_medicos.R;

public class PreprationindexingActivity extends AppCompatActivity {

    private ActivityPreprationindexingBinding binding;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String speciality = getIntent().getStringExtra("specialityPgName");

        binding = ActivityPreprationindexingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TextView tt=findViewById(R.id.toolbar);
//        setSupportActionBar(binding.toolbar);
        tt.setText(speciality);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        // Set the back button to finish the current activity and return to the previous one
        binding.backtothehomefrompg.setOnClickListener(v -> {
            finish(); // Finish the current activity
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white)); // Replace with your color resource
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewpginsiderindexing1);
        bottomNavigationView.setBackground(null);
        ImageView sortButton=findViewById(R.id.filter);
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
                sortButton.setVisibility(View.VISIBLE);
                Log.d("speciality is fragment1", String.valueOf(R.id.navigation_pgswgt));
                Log.d("speciality is fragment1", itemId);
                selectedFragment = PreprationSWGT.newInstance(speciality);
            } else {
                sortButton.setVisibility(View.VISIBLE);

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