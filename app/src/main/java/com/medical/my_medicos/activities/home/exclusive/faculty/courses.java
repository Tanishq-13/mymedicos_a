package com.medical.my_medicos.activities.home.exclusive.faculty;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.title;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.FmgeprepActivity;
import com.medical.my_medicos.activities.home.exclusive.adapter.ReviewsAdapter;
import com.medical.my_medicos.activities.home.exclusive.model.CourseCard;
import com.medical.my_medicos.activities.home.exclusive.model.Instructor;
import com.medical.my_medicos.activities.home.exclusive.model.Review;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class courses extends AppCompatActivity {
    private boolean isExpanded = false; // To track the current state
    private boolean isExpanded1 = false; // To track the current state
    private RecyclerView reviewsRecyclerView;
    private ReviewsAdapter reviewsAdapter;
    private LinearLayout itemList,itemList1;
    private Button livedemo,prchs;
    private List<Review> reviewList = new ArrayList<>();
    private FirebaseFirestore db;
    private TextView showMore,showMore1;
    private String instid,courseId;
    private Instructor instructor;
    private ImageView downArrow,downArrow1;
    CourseCard course;

    public courses(){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_courses);

        // Apply window insets to adjust layout margins
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {

            // Retrieve the system insets (status and navigation bars)
            WindowInsetsCompat systemInsets = insets;

            // Adjust padding for navigation bar overlap
            v.setPadding(
                    0,
                    0,
                    0,
                    systemInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );

            // Return the insets to apply
            return insets;
        });

        itemList = findViewById(R.id.itemList);
        itemList1 = findViewById(R.id.itemList1);
        showMore1=findViewById(R.id.showmore1);
        showMore = findViewById(R.id.showmore);
        livedemo = findViewById(R.id.livedemo);
        prchs = findViewById(R.id.prchs);
        livedemo.setOnClickListener(v -> {
            Intent intent = new Intent(courses.this, FmgeprepActivity.class);
            startActivity(intent);
        });
        prchs.setOnClickListener(v -> showAlert("Under Construction", "App is under construction. Check back later."));

// Create a method to show the alert dialog


// Set click listeners for the buttons

        downArrow = findViewById(R.id.dwn);
        downArrow1= findViewById(R.id.dwn1);
        db = FirebaseFirestore.getInstance();

        courseId=getIntent().getStringExtra("courseId");
        // Initially show only 4 items
        toggleItems(false);

        Log.d("iopp",String.valueOf(courseId));


        // Set click listener for Show More / Show Less
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpanded = !isExpanded; // Toggle the state
                toggleItems(isExpanded);
            }
        });
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView); // Add this to your layout
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsAdapter = new ReviewsAdapter(reviewList);
        toggleitems1(false);
        reviewsRecyclerView.setAdapter(reviewsAdapter);
        fetchReviews(courseId);

        // Set click listener for Show More / Show Less
        showMore1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpanded1 = !isExpanded1; // Toggle the state
                toggleitems1(isExpanded1);
            }
        });

        course = (CourseCard) getIntent().getSerializableExtra("docId");

        if (true) {
            populateUI(course);

        } else {
            Log.e("CoursesActivity", "No docId passed!");
        }
    }
    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void fetchReviews(String courseId) {
        db.collection("Exclusive_Course")
                .document(courseId)
                .collection("REVIEWS")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reviewList.clear(); // Clear the list for fresh data
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String docId = document.getId();
                        String studentName = document.getString("studentname");
                        String studentReview = document.getString("studentreview");
                        int rating = document.getLong("rating").intValue();
                        Date date = document.getDate("date");

                        // Create Review object
                        Review review = new Review(docId, studentName, studentReview, rating, date);
                        reviewList.add(review);
                    }
                    reviewsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("courses", "Error fetching reviews: " + e.getMessage()));
    }
    private void fetchCourseDetails(String docId) {
        db.collection("ExclusiveCourse")
                .document(docId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Populate UI with course data
                        String coverUrl = documentSnapshot.getString("cover");
                        String description = documentSnapshot.getString("description");
//                        String docId = document.getString("docId");
                        boolean isFeatured = documentSnapshot.getBoolean("featured");
                        String lang = documentSnapshot.getString("lang");
                        String lastUpdated =("last_updated");
                        String name = documentSnapshot.getString("name");
                        boolean premiumStatus = documentSnapshot.getBoolean("premium_status");
                        long ratedBy = documentSnapshot.getLong("rated_by");
                        String ratingAvg = documentSnapshot.getString("rating_avg");
                        String subject = documentSnapshot.getString("subject");
                        String title = documentSnapshot.getString("title");
                        Log.d("nnnn",description);
                        // Create Course object
//                        CourseCard course = new CourseCard(
//                                coverUrl, description, docId, isFeatured, lang, lastUpdated,
//                                name, premiumStatus, ratedBy, ratingAvg, subject, title
//                        );

                        // Populate UI (Optional)
                    } else {
                        Log.e("CoursesActivity", "Document not found!");
                    }
                })
                .addOnFailureListener(e -> Log.e("CoursesActivity", "Error fetching course details", e));
    }
private void toggleitems1(boolean showAll){
    int childrenCount1 = itemList1.getChildCount();
    for (int i = 0; i < childrenCount1; i++) {
        View child1 = itemList1.getChildAt(i);
        // Show only the first 4 items if not expanded
        child1.setVisibility(i < 4 || showAll ? View.VISIBLE : View.GONE);
    }

    // Update the text and arrow icon
    if (showAll) {
        showMore1.setText("Show Less");
        downArrow1.setImageResource(R.drawable.arrowup_svgrepo_com); // Replace with your up arrow icon
    } else {
        showMore1.setText("Show More");
        downArrow1.setImageResource(R.drawable.arrowdown_svgrepo_com); // Replace with your down arrow icon
    }
}
    private void toggleItems(boolean showAll) {
        int childrenCount = itemList.getChildCount();
        for (int i = 0; i < childrenCount; i++) {
            View child = itemList.getChildAt(i);
            // Show only the first 4 items if not expanded
            child.setVisibility(i < 4 || showAll ? View.VISIBLE : View.GONE);
        }

        // Update the text and arrow icon
        if (showAll) {
            showMore.setText("Show Less");
            downArrow.setImageResource(R.drawable.arrowup_svgrepo_com); // Replace with your up arrow icon
        } else {
            showMore.setText("Show More");
            downArrow.setImageResource(R.drawable.arrowdown_svgrepo_com); // Replace with your down arrow icon
        }
    }
    private void populateUI(CourseCard course){
        ImageView img=findViewById(R.id.topImageView);
        Glide.with(this).load(course.getCover()).into(img);
        TextView crsdesc=findViewById(R.id.courseTitle);
        TextView rtng=findViewById(R.id.courseRating);

        crsdesc.setText(course.getTitle());
        String tt=course.getRatingAvg();
        tt=tt+" ⭐ "+"  ("+String.valueOf(course.getRatedBy())+")";
        rtng.setText(tt);


        TextView cr=findViewById(R.id.courseDescription);
        cr.setText(course.getDescription());
        TextView sn=findViewById(R.id.subjectName);
        sn.setText(course.getSubject());
        instid=course.getDocId();
        Log.d("iopp",course.getDocId());
        getinstructor();
//        TextView docname=findViewById(R.id.instructorName);
//        docname.setText(instructor.getName());
        TextView crsdesc1=findViewById(R.id.courseTitle);
        crsdesc1.setText(course.getTitle());



    }
    private void getinstructor(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("MentorRegistration")
                .document(instid) // Use the docId to reference the specific document
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
                        Log.d("oppp",name);
                        // Log fetched data
                        Log.d("facultyoverview", "Fetched data for " + name + ": " + interest1);

                        // Create an Instructor object using the fetched fields
                        instructor = new Instructor(
                                bio,
                                instid,
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
                        Log.d("oppp","name");

                        Log.e("facultyoverview", "Instructor not found for docId: " + instid);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("oppp","name");

                    Log.e("facultyoverview", "Error fetching data: " + e.getMessage());
                });
//        Log.d("indt",instid);
//                TextView docname=findViewById(R.id.instructorName);
//                docname.setText(instructor.getName());
    }
    void populateFields(Instructor is){
        TextView docname=findViewById(R.id.instructorName);
        docname.setText(is.getName());
    }

}