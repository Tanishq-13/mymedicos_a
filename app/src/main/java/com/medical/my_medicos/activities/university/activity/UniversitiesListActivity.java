package com.medical.my_medicos.activities.university.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.HomeActivity;
import com.medical.my_medicos.activities.home.sidedrawer.extras.BottomSheetForChatWithUs;
import com.medical.my_medicos.activities.home.sidedrawer.extras.BottomSheetForCommunity;
import com.medical.my_medicos.activities.news.NewsActivity;
import com.medical.my_medicos.activities.university.adapters.UpdatesAdapter;
import com.medical.my_medicos.activities.university.model.Updates;
import com.medical.my_medicos.databinding.ActivityUniversityListBinding;

import java.util.ArrayList;

public class UniversitiesListActivity extends AppCompatActivity {
    private ActivityUniversityListBinding binding;
    private UpdatesAdapter updateAdapter;
    private ArrayList<Updates> updates;
    private Toolbar toolbar;
    private ImageView backtothehomefromupdates;
    LottieAnimationView notavailebeuniversities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUniversityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        notavailebeuniversities = findViewById(R.id.notavailebeuniversities);

        updates = new ArrayList<>();
        updateAdapter = new UpdatesAdapter(this, updates);

        String stateName = getIntent().getStringExtra("stateName");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.grey));
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(stateName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        backtothehomefromupdates = findViewById(R.id.backtothehomefromupdates);
        backtothehomefromupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Button connectWithUsButton = findViewById(R.id.connectwithusforuniversityadmin);
        connectWithUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetChatwithus();
            }
        });
    }

    private void openBottomSheetChatwithus() {
        BottomSheetDialogFragment bottomSheetFragment = new BottomSheetForChatWithUs();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
