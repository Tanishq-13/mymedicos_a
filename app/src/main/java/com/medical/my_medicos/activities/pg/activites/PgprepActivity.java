package com.medical.my_medicos.activities.pg.activites;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.R;
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
import com.medical.my_medicos.databinding.ActivityPgprepBinding;

import java.util.Map;
public class PgprepActivity extends AppCompatActivity {
    ActivityPgprepBinding binding;
    BottomNavigationView bottomNavigationPg;
    BottomAppBar bottomAppBarPg;
    private TextView currentStreaksTextView;
    private ImageView dbt;
    private FirebaseFirestore db;
    private ImageView backtothehomefrompg;
    private int lastSelectedItemId = 0;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPgprepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currentStreaksTextView = findViewById(R.id.currentstraks);
        dbt=findViewById(R.id.msg);
        backtothehomefrompg = findViewById(R.id.backtothehomefrompg);
        backtothehomefrompg.setOnClickListener(view -> {
            Intent i = new Intent(PgprepActivity.this, HomeActivity.class);
            startActivity(i);
        });
//        fetchStreakCount();
        dbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AskDoubt activity
                Intent intent = new Intent(v.getContext(), AskDoubt.class);
                v.getContext().startActivity(intent);
            }
        });        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY; // Remove SYSTEM_UI_FLAG_HIDE_NAVIGATION
        decorView.setSystemUiVisibility(uiOptions);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        setupBottomAppBar();

        PreparationPgFragment homeFragment = PreparationPgFragment.newInstance();
        replaceFragment(homeFragment);
        LinearLayout openpgdrawerIcon = findViewById(R.id.creditscreen);
//        openpgdrawerIcon.setOnClickListener(v -> openHomeSidePgActivity());

        configureWindow();
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
        LinearLayout creditscreen = findViewById(R.id.creditscreen);
        TextView currentcoinspg = findViewById(R.id.currentcoinspg);

        if (fragment instanceof NeetExamFragment || fragment instanceof PreparationPgFragment) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            toolbarheading.setTextColor(ContextCompat.getColor(this, R.color.unselected));
            backtothehomefrompg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.arrow_bk));
//            creditscreen.setBackground(ContextCompat.getDrawable(this, R.drawable.categoryblack));
//            currentcoinspg.setTextColor(ContextCompat.getColor(this, R.color.white));
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

            Fragment selectedFragment = null;

            if (itemId == R.id.navigation_pghome) {
                selectedFragment = HomePgFragment.newInstance();
            } else if (itemId == R.id.navigation_pgneet) {
                selectedFragment = PreparationPgFragment.newInstance();
            } else if (itemId == R.id.navigation_pgpreparation) {
                selectedFragment = NeetExamFragment.newInstance();
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);  // Fragment change
                return true;  // Allow the navigation icon to change
            }

            return false;  // No item matched
        });
    }


    public void openHomeSidePgActivity() {
        Intent settingsIntent = new Intent(PgprepActivity.this, CreditsActivity.class);
        startActivity(settingsIntent);
    }

    @SuppressLint("RestrictedApi")
    private void replaceFragment(Fragment fragment) {
        Log.d(TAG, "Replacing fragment with: " + fragment.getClass().getSimpleName());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_fmge, fragment);
        fragmentTransaction.commit();
//        updateToolbarColor(fragment);
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
