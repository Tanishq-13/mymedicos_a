package com.medical.my_medicos.activities.fmge.activites.internalfragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.PreprationNotesFragment;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.PreprationSWGT;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.PreprationTWGT;
import com.medical.my_medicos.databinding.ActivityPreprationindexingBinding;

public class PreprationindexingActivityFMGE extends AppCompatActivity {

    private ActivityPreprationindexingBinding binding;
    private ImageView bck;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String speciality = getIntent().getStringExtra("specialityFmgeName");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        binding = ActivityPreprationindexingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bck=findViewById(R.id.backtothehomefrompg);
        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();  // Triggers the phone's back functionality
            }
        });
        TextView tt=findViewById(R.id.toolbar);
        tt.setText(speciality);
        ImageView sortButton=findViewById(R.id.filter);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewpginsiderindexing1);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            String itemId = String.valueOf(item.getItemId());

            if (itemId.equals(String.valueOf(R.id.navigation_pgnotes))) {
                selectedFragment = PreprationNotesFragment.newInstance(speciality);
            } else if (itemId.equals(String.valueOf(R.id.navigation_pgtwgt))) {
                sortButton.setVisibility(View.VISIBLE);
                selectedFragment = PreprationTWGT.newInstance(speciality);
                Log.d("speciality is fragment2",String.valueOf(R.id.navigation_pgtwgt));
                Log.d("speciality is fragment",itemId);
            } else if (itemId.equals(String.valueOf(R.id.navigation_pgswgt))) {
                Log.d("speciality is fragment1",String.valueOf(R.id.navigation_pgswgt));
                Log.d("speciality is fragment1",itemId);
                sortButton.setVisibility(View.VISIBLE);

                selectedFragment = PreprationSWGT.newInstance(speciality);
            } else {
                selectedFragment = PreprationNotesFragment.newInstance(speciality);
            }

            loadFragment(selectedFragment);
            return true;
        });





        // Load default fragment
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
