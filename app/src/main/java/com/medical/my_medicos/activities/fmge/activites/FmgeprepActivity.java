package com.medical.my_medicos.activities.fmge.activites;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.FmgeExamFragment;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.HomeFmgeFragment;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.PreparationFmgeFragment;
import com.medical.my_medicos.activities.guide.NewsGuideActivity;
import com.medical.my_medicos.activities.guide.PgGuideActivity;
import com.medical.my_medicos.activities.home.HomeActivity;
import com.medical.my_medicos.activities.home.fragments.ClubFragment;
import com.medical.my_medicos.activities.home.fragments.HomeFragment;
import com.medical.my_medicos.activities.home.fragments.SlideshowFragment;
import com.medical.my_medicos.activities.news.NewsActivity;
import com.medical.my_medicos.activities.pg.activites.extras.CreditsActivity;
import com.medical.my_medicos.activities.pg.activites.internalfragments.HomePgFragment;
import com.medical.my_medicos.activities.pg.activites.internalfragments.MePgFragment;
import com.medical.my_medicos.activities.pg.activites.internalfragments.NeetExamFragment;
import com.medical.my_medicos.activities.pg.activites.internalfragments.PreparationPgFragment;
import com.medical.my_medicos.databinding.ActivityFmgeprepBinding;
import com.medical.my_medicos.databinding.ActivityPgprepBinding;

import java.util.Map;
public class FmgeprepActivity extends AppCompatActivity {
    ActivityFmgeprepBinding binding;
    BottomNavigationView bottomNavigationPg;
    BottomAppBar bottomAppBarPg;
    private ImageView backtothehomefrompg;
    private int lastSelectedItemId = 0;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFmgeprepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backtothehomefrompg = findViewById(R.id.backtothehomefrompg);
        backtothehomefrompg.setOnClickListener(view -> {
            Intent i = new Intent(FmgeprepActivity.this, HomeActivity.class);
            startActivity(i);
        });

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY; // Remove SYSTEM_UI_FLAG_HIDE_NAVIGATION
        decorView.setSystemUiVisibility(uiOptions);

//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String currentUid = currentUser.getPhoneNumber();
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//            database.getReference().child("profiles")
//                    .child(currentUid)
//                    .child("coins")
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            Integer coinsValue = snapshot.getValue(Integer.class);
//                            if (coinsValue != null) {
//                                binding.currentcoinspg.setText(String.valueOf(coinsValue));
//                            } else {
//                                binding.currentcoinspg.setText("0");
//                            }
//                        }
//
//                        @SuppressLint("RestrictedApi")
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Log.e(TAG, "Error loading coins from database: " + error.getMessage());
//                        }
//                    });
//        }

        setupBottomAppBar();

        PreparationFmgeFragment homeFragment = PreparationFmgeFragment.newInstance();
        replaceFragment(homeFragment);
//        LinearLayout openpgdrawerIcon = findViewById(R.id.creditscreen);
//        openpgdrawerIcon.setOnClickListener(v -> openHomeSidePgActivity());
        configureWindow();
    }

    private void configureWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private void updateToolbarColor(Fragment fragment) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarheading = findViewById(R.id.toolbarheading);
        ImageView backtothehomefrompg = findViewById(R.id.backtothehomefrompg);
//        LinearLayout creditscreen = findViewById(R.id.creditscreen);
//        TextView currentcoinspg = findViewById(R.id.currentcoinspg);

        if (fragment instanceof FmgeExamFragment || fragment instanceof PreparationFmgeFragment) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            toolbarheading.setTextColor(ContextCompat.getColor(this, R.color.unselected));
            backtothehomefrompg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.arrow_bk));
//            creditscreen.setBackground(ContextCompat.getDrawable(this, R.drawable.categoryblack));
//            currentcoinspg.setTextColor(ContextCompat.getColor(this, R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            toolbarheading.setTextColor(ContextCompat.getColor(this, R.color.white));
            backtothehomefrompg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.arrow_bkwhite));
//            creditscreen.setBackground(ContextCompat.getDrawable(this, R.drawable.categorywhite));
//            currentcoinspg.setTextColor(ContextCompat.getColor(this, R.color.unselected));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
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
                    replaceFragment(HomeFmgeFragment.newInstance());
                    lastSelectedItemId = R.id.navigation_pghome;
                }
                return true;
            } else if (itemId == R.id.navigation_pgneet) {
                if (lastSelectedItemId != R.id.navigation_pgneet) {
                    replaceFragment(PreparationFmgeFragment.newInstance());
                    lastSelectedItemId = R.id.navigation_pgneet;
                }
                return true;
            } else if (itemId == R.id.navigation_pgpreparation) {
                if (lastSelectedItemId != R.id.navigation_pgpreparation) {
                    replaceFragment(FmgeExamFragment.newInstance());
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
        Intent settingsIntent = new Intent(FmgeprepActivity.this, CreditsActivity.class);
        startActivity(settingsIntent);
    }

    @SuppressLint("RestrictedApi")
    private void replaceFragment(Fragment fragment) {
        Log.d(TAG, "Replacing fragment with: " + fragment.getClass().getSimpleName());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_fmge, fragment);
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
