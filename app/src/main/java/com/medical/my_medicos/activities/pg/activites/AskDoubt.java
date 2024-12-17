package com.medical.my_medicos.activities.pg.activites;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.internalfragments.LiveFragment;
import com.medical.my_medicos.activities.pg.activites.internalfragments.ClosedFragment;
import com.medical.my_medicos.activities.pg.adapters.SubjectAdapter;
import com.medical.my_medicos.activities.pg.model.Subject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AskDoubt extends AppCompatActivity {
    Fragment currentFragment;
    ImageView nc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        setContentView(R.layout.activity_ask_doubt);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nc=findViewById(R.id.newchat);
        SegmentTabLayout segmentTabLayout = findViewById(R.id.tablayoutprep);
        String[] titles = {"Live", "Closed"};
        segmentTabLayout.setTabData(titles);

        // Set default tab to "Live" and load corresponding fragment
        loadFragment(new LiveFragment());

        segmentTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        currentFragment = new LiveFragment();
                        break;
                    case 1:
                        currentFragment = new ClosedFragment();
                        break;
                }
                loadFragment(currentFragment);
            }

            @Override
            public void onTabReselect(int position) {
                // Optional: handle reselection of the tab if necessary
            }
        });

        //bottomsheet for new chat
        nc.setOnClickListener(v -> showSubjectBottomSheet());

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_change, fragment);
        transaction.commit();
    }
    private void showSubjectBottomSheet() {
        // Create a list of Subject objects
        List<Subject> subjects = new ArrayList<>();

        // Firestore instance
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Fetch all documents from MentorRegistration collection
        firestore.collection("MentorRegistration")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Iterate through all documents
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String docId = document.getId();
                        String interest = document.getString("Interest");
//                        String interest2 = document.getString("Interest2");

                        // Add interests to the list (if not null or empty)
                        if (interest != null && !interest.isEmpty()) {
                            subjects.add(new Subject(interest, docId));
                        }
//                        if (interest2 != null && !interest2.isEmpty()) {
//                            subjects.add(new Subject(interest2, docId));
//                        }
                    }

                    // Show the bottom sheet only after fetching data
                    showBottomSheet(subjects);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("FirestoreError", "Failed to fetch subjects", e);
                });
    }

    private void showBottomSheet(List<Subject> subjects) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_subjects, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recyclerViewSubjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SubjectAdapter adapter = new SubjectAdapter(subjects, subject -> {
            // Start ChatActivity with subject name and DocID
            Intent intent = new Intent(AskDoubt.this, ChatActivity.class);
            intent.putExtra("isnew", "new");
            intent.putExtra("Subjectname", subject.getName());
            intent.putExtra("DocID", subject.getDocId());
            startActivity(intent);

            // Dismiss the bottom sheet
            bottomSheetDialog.dismiss();
        });
        recyclerView.setAdapter(adapter);

        bottomSheetDialog.show();
    }

}

