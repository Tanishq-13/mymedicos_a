package com.medical.my_medicos.activities.home.exclusive.faculty;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.exclusive.fragments.BlankcourseFragment;
import com.medical.my_medicos.activities.home.exclusive.model.Instructor;

import de.hdodenhof.circleimageview.CircleImageView;

public class facultyoverview extends AppCompatActivity implements CoursesFragment.CourseCheckListener{
    Instructor instructor;
    ImageView arrowbk;
    int fl=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facultyoverview);
        SegmentTabLayout segmentTabLayout = findViewById(R.id.tablayoutprep);
        String[] titles = {"Courses", "Review"};
        arrowbk=findViewById(R.id.arrowbk);
        arrowbk.setOnClickListener(v -> {
            // Finish the current activity
            finish();
        });
        segmentTabLayout.setTabData(titles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        String docId = getIntent().getStringExtra("instructor_id");
        loadFragment(new CoursesFragment(docId));
        segmentTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 0) {
                    // Load CoursesFragment
                    if(true)
                    loadFragment(new CoursesFragment(docId));
                    else
                        loadFragment(new BlankcourseFragment());
                } else if (position == 1) {
                    loadFragment(new BlankFragment());
                    // Load ReviewFragment (implement similarly if needed)
                }
            }

            @Override
            public void onTabReselect(int position) {
                // Optional
            }
        });

        // Retrieve the docId from Intent
        if (docId != null) {
            Log.d("docid", docId);
            fetchInstructorDetails(docId);
        } else {
            Log.e("facultyoverview", "No docId passed!");
        }
    }

    private void fetchInstructorDetails(String docId) {
        // Initialize Firebase Realtime Database reference
        // Reference the Firestore collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Fetch the document with the given docId from the MentorRegistration collection
        db.collection("MentorRegistration")
                .document(docId) // Use the docId to reference the specific document
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Parse the Instructor object from the document
                        String bio = documentSnapshot.getString("Bio");
//                        String docId = documentSnapshot.getString("DocID");
                        String email = documentSnapshot.getString("Email ID");
                        String interest1 = documentSnapshot.getString("Interest");
                        String interest2 = documentSnapshot.getString("Interest2");
                        String location = documentSnapshot.getString("Location");
                        String mcn = documentSnapshot.getString("MCN");
                        String name = documentSnapshot.getString("Name");
                        String phoneNumber = documentSnapshot.getString("Phone Number");
                        String prefix = documentSnapshot.getString("Prefix");
                        String profile = documentSnapshot.getString("Profile");
                        Boolean verified = documentSnapshot.getBoolean("Verified");
                        String aadharNumber = documentSnapshot.getString("aadharnumber");
                        Boolean exclusive = documentSnapshot.getBoolean("exclusive");
                        String msgToStudents = documentSnapshot.getString("msg_to_students");

                        // Log fetched data
                        Log.d("facultyoverview", "Fetched data for " + name + ": " + interest1);

                        // Create an Instructor object using the fetched fields
                        Instructor instructor = new Instructor(
                                bio,
                                docId,
                                email,
                                interest1,
                                interest2,
                                location,
                                mcn,
                                name,
                                phoneNumber,
                                prefix,
                                profile,
                                verified != null ? verified : false,
                                aadharNumber,
                                exclusive != null ? exclusive : false,
                                msgToStudents
                        );

                        // Populate UI with the fetched Instructor data
                        populateFields(instructor);
                    } else {
                        Log.e("facultyoverview", "Instructor not found for docId: " + docId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("facultyoverview", "Error fetching data: " + e.getMessage());
                });

    }

        private void populateFields(Instructor instructor) {
        // Update your views with the fetched data
        TextView nameTextView = findViewById(R.id.titleTextView);
        TextView bioTextView = findViewById(R.id.description);
        TextView interestsTextView = findViewById(R.id.location);
        CircleImageView img=findViewById(R.id.img);
        Glide.with(getApplicationContext())
                .load(instructor.getProfile())
                .into(img);
        nameTextView.setText(instructor.getName());
        bioTextView.setText(instructor.getBio());
        interestsTextView.setText(
                instructor.getInterest1() + ", " + instructor.getInterest2()
        );
//            TextView crsdesc=findViewById(R.id.courseTitle);

        // Log the instructor data for debugging
        Log.d("facultyoverview", "Instructor Details: " + instructor.toString());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.viewPager, fragment)
                .commit();
    }

    @Override
    public void onCoursePresent() {
        fl = 1;
    }
}
