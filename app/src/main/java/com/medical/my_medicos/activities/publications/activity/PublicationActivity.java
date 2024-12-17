package com.medical.my_medicos.activities.publications.activity;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.publications.activity.mainfragments.HomePublicationFragment; // Ensure this import is correct
import com.medical.my_medicos.activities.publications.activity.mainfragments.SearchPublicationFragment;
import com.medical.my_medicos.activities.publications.activity.mainfragments.UsersPublicationFragment;
import com.medical.my_medicos.activities.publications.activity.mainfragments.WaitlistPublicationFragment;
import com.medical.my_medicos.databinding.ActivityPublicationBinding;

public class PublicationActivity extends AppCompatActivity {

    ActivityPublicationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPublicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        ImageView backToHomeImageView = findViewById(R.id.backtothehomefrompublication);
        backToHomeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LottieAnimationView robotCall = findViewById(R.id.robotcall);
        robotCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.popuprobot.getVisibility() == View.GONE) {
                    // Show the popup
                    binding.popuprobot.setVisibility(View.VISIBLE);
                    binding.popuprobot.startAnimation(AnimationUtils.loadAnimation(PublicationActivity.this, R.anim.slide_up));
                }
            }
        });

        configureWindow();
        setupBottomNavigation();
        loadDefaultFragment();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (binding.popuprobot.getVisibility() == View.VISIBLE) {
                Rect outRect = new Rect();
                binding.popuprobot.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    binding.popuprobot.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
                    binding.popuprobot.setVisibility(View.GONE);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void configureWindow() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private void setupBottomNavigation() {
        binding.libicon.setOnClickListener(view -> replaceFragment(new HomePublicationFragment()));
        binding.contentpage.setOnClickListener(view -> replaceFragment(new UsersPublicationFragment()));
        binding.cartpage.setOnClickListener(view -> replaceFragment(new WaitlistPublicationFragment()));
        binding.searchpage.setOnClickListener(view -> replaceFragment(new SearchPublicationFragment()));
    }

    private void loadDefaultFragment() {
        // Load HomePublicationFragment by default
        replaceFragment(new HomePublicationFragment());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_publication, fragment);
        fragmentTransaction.commit();
    }
}
