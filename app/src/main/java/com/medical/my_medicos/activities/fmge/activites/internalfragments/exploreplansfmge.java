package com.medical.my_medicos.activities.fmge.activites.internalfragments;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments.AllFMGEPrep;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments.ClinicalFMGEPrep;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments.ParaFMGEPrep;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments.PreFMGEPrep;
import com.medical.my_medicos.activities.pg.adapters.PlanAdapter;
import com.medical.my_medicos.activities.pg.model.Plan;
import com.medical.my_medicos.databinding.ActivityPgprepBinding;

import java.util.ArrayList;
import java.util.List;

public class exploreplansfmge extends AppCompatActivity {
//    private RecyclerView recyclerView;
    private PlanAdapter planAdapter;
    private Fragment currentFragment;  // To hold the currently loaded fragment

    private List<Plan> planList = new ArrayList<>();
    private FirebaseFirestore firestore;
    ActivityPgprepBinding binding;
    BottomNavigationView bottomNavigationPg;
    BottomAppBar bottomAppBarPg;
    private RecyclerView recyclerView;
    private TextView currentStreaksTextView;
    private FirebaseFirestore db;
    private ImageView backtothehomefrompg;
    private int lastSelectedItemId = 0;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exploreplansfmge);
        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        SegmentTabLayout segmentTabLayout = findViewById(R.id.tablayoutprep);
        String[] titles = {"FMGE", "PG", "NEETSS"};
        segmentTabLayout.setTabData(titles);

        // Fetch FMGE plans by default when the activity starts
        fetchPlans("FMGE");

        // Set up tab selection listener
        segmentTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:  // Tab 0: FMGE Plans
                        fetchPlans("FMGE");
                        break;
                    case 1:  // Tab 1: PG Plans
                        fetchPlans("PG");
                        break;
                    case 2:  // Tab 2: NEETSS Plans
                        fetchPlans("NEET SS");
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {
                // Optionally handle tab reselect
            }
        });

    }

    private void fetchPlans(String ca) {
        CollectionReference plansRef = firestore.collection("Plans").document(ca).collection("Subscriptions");

        plansRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                planList.clear();  // Clear the list before adding new data

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String planName = document.getString("PlanName");
                    String discountedPrice = document.getString("Discount_Price");
                    String planTagline = document.getString("PlanTagline");
                    String planPrice = document.getString("PlanPrice");
                    String planThumbnail = document.getString("PlanThumbnail");
                    String planID = document.getString("planID");


                    // Get the array of plan features (PlansFeature)
                    List<String> plansFeature = (List<String>) document.get("PlanFeatures");

                    // Create a new Plan object
                    Plan plan = new Plan(planName, planTagline, planPrice, planThumbnail, plansFeature, discountedPrice,planID);
                    Log.d("plansfmge",planName);
                    planList.add(plan);  // Add to the list
                }

                // Set up the adapter with the fetched data
                planAdapter = new PlanAdapter(planList,exploreplansfmge.this);
                recyclerView.setAdapter(planAdapter);
            } else {
                // Handle Firestore error
                Log.d("FirestoreError", "Error fetching plans", task.getException());
            }
        });
    }
    private void setupBottomAppBar() {
        bottomAppBarPg = binding.bottomappabarpginsider;
        bottomNavigationPg = bottomAppBarPg.findViewById(R.id.bottomNavigationViewpginsider);
        bottomNavigationPg.setBackground(null);

        bottomNavigationPg.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_pghome) {
                if (lastSelectedItemId != R.id.navigation_pghome) {
//                    replaceFragment(HomePgFragment.newInstance());
                    lastSelectedItemId = R.id.navigation_pghome;
                }
                return true;
            } else if (itemId == R.id.navigation_pgneet) {
                if (lastSelectedItemId != R.id.navigation_pgneet) {
//                    replaceFragment(PreparationPgFragment.newInstance());
                    lastSelectedItemId = R.id.navigation_pgneet;
                }
                return true;
            } else if (itemId == R.id.navigation_pgpreparation) {
                if (lastSelectedItemId != R.id.navigation_pgpreparation) {
//                    replaceFragment(NeetExamFragment.newInstance());
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


}
