package com.medical.my_medicos.activities.fmge.activites;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.insiders.WeeklyQuizFMGEInsiderActivity;
import com.medical.my_medicos.activities.pg.activites.PgPrepPayement;
import com.medical.my_medicos.activities.pg.activites.insiders.WeeklyQuizInsiderActivity;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.prefs.Preferences;

import de.hdodenhof.circleimageview.CircleImageView;

public class FmgePrepPayement extends AppCompatActivity {

    private DatabaseReference database;
    private String phoneNumber;
    private CheckBox agreeCheckbox;
    private String examtitle;
    private String examdescription;
    private LinearLayout startExamLayout,share;
    private ImageView bck;
    FirebaseUser user ;
    String current_user ;
    private String quizTitle, quizId, dueDate,title1,id;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pg_prep_payment);
        database = FirebaseDatabase.getInstance().getReference();
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        agreeCheckbox = findViewById(R.id.agreeCheckboxpg);
        bck=findViewById(R.id.backtothehomefrompg);
        startExamLayout = findViewById(R.id.startexamination1);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView bck=findViewById(R.id.backtothehomefrompg);
        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Simulate the back button press
                onBackPressed();
            }
        });
        current_user = user.getPhoneNumber();
        if (current == null) {
//            Log.e(TAG, "No current user, finishing activity.");
            finish();
            return;
        }
        bck.setOnClickListener(v -> onBackPressed());

        phoneNumber = current.getPhoneNumber();
        if (phoneNumber == null) {
//            Log.e(TAG, "Current user has no phone number, finishing activity.");
            finish();
            return;
        }

        ImageButton starButton = findViewById(R.id.imageButton6);
        starButton.setImageResource(R.drawable.bkmrk);

        Intent intent = getIntent();
        quizId = intent.getStringExtra("Id");
        if (quizId == null) {
//            Log.e(TAG, "No quiz ID provided, finishing activity.");
            finish();
            return;
        }

        checkBookmarkState(starButton, quizId);
        starButton.setOnClickListener(v -> toggleBookmark(starButton, quizId));
        agreeCheckbox.setOnClickListener(v -> {
            if (agreeCheckbox.isChecked()) {
                startExamLayout.setEnabled(true);
            } else {
                Toast.makeText(FmgePrepPayement.this, "Please agree to the terms to start the examination", Toast.LENGTH_SHORT).show();
                startExamLayout.setEnabled(false);
            }
        });
        startExamLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startExamination();
            }
        });
        quizTitle=intent.getStringExtra("Title");
        title1=intent.getStringExtra("Title1");
        id=intent.getStringExtra("Id");
        dueDate=intent.getStringExtra("Due");
        String tptt=intent.getStringExtra("toptext");
        TextView tp=findViewById(R.id.toptext);
        tp.setText("");
        share=findViewById(R.id.sharelayout);
        TextView quizNameTextView = findViewById(R.id.quizNameTextView1);
        TextView dueDateTextView = findViewById(R.id.DueDate1);
        dueDateTextView.setText(intent.getStringExtra("Id"));
        quizNameTextView.setText(intent.getStringExtra("Title"));

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ensure documentid is initialized
                if (id == null || id.isEmpty()) {
                    Toast.makeText(FmgePrepPayement.this, "Document ID is not available.", Toast.LENGTH_SHORT).show();
                    return;
                }
//                Toast.makeText(FmgePrepPayement.this, "Document ID is2 available.", Toast.LENGTH_SHORT).show();

                createlink(current_user, id, quizTitle, examdescription); // Pass examId here
            }
        });
        handleDeepLink();

    }
    private void handleDeepLink() {
        Log.d("HomeActivity23", "Score: ");

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String examId = deepLink.getQueryParameter("fmgeId");
                            String title = deepLink.getQueryParameter("title");
                            String due = deepLink.getQueryParameter("due");

                            Intent intent = new Intent(FmgePrepPayement.this, FmgePrepPayement.class);
                            intent.putExtra("Title", title);
                            intent.putExtra("Due", due);
                            intent.putExtra("qid", examId);
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(this, e -> Log.w("DeepLink", "getDynamicLink:onFailure", e));
    }
    private void checkBookmarkState(ImageButton starButton, String quizId) {
        DatabaseReference bookmarkRef = database.child("profiles").child(phoneNumber).child("bookmarks").child(quizId);
        bookmarkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue(Boolean.class)) {
                    starButton.setImageResource(R.drawable.ylwbkmrk);
                    isClicked = true;
                } else {
                    starButton.setImageResource(R.drawable.bkmrk);
                    isClicked = false;
                }
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error when checking bookmark state: " + databaseError.getMessage());
            }
        });
    }

    private void toggleBookmark(ImageButton starButton, String quizId) {
        DatabaseReference bookmarkRef = database.child("profiles").child(phoneNumber).child("bookmarks").child(quizId);
        if (isClicked) {
            bookmarkRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    starButton.setImageResource(R.drawable.bkmrk);
                    isClicked = false;
                }
            });
        } else {
            bookmarkRef.setValue(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    starButton.setImageResource(R.drawable.ylwbkmrk);
                    isClicked = true;
                }
            });
        }

    }

    private void startExamination() {
        if (agreeCheckbox.isChecked()) {
            Intent startExamIntent = new Intent(FmgePrepPayement.this, WeeklyQuizInsiderActivity.class);
            startExamIntent.putExtra("Title", quizTitle);
            startExamIntent.putExtra("Title1", title1);
            startExamIntent.putExtra("DueDate", dueDate);
            startExamIntent.putExtra("quizid",quizId);
            startExamIntent.putExtra("section","FMGE");

            startActivity(startExamIntent);
        } else {
            Toast.makeText(this, "You must agree to the terms first.", Toast.LENGTH_SHORT).show();
        }
    }
    public void createlink(String custid, String examId, String examtitle, String examdescription) {
        Log.e("main", "create link");
        Log.d("createlink", "custid: " + custid + ", examId: " + examId);

        if (examId == null || examId.isEmpty()) {
            Log.e("createlink", "Exam ID is null or empty");
            return;
        }

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.mymedicos.in/fmgedetails?custid=" + custid + "&fmgeId=" + examId + "&title=" + examtitle + "&due=" + examdescription))
                .setDomainUriPrefix("https://app.mymedicos.in")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.e("main", "Long refer " + dynamicLinkUri);

        createreferlink(custid, examId);
    }
    public void createreferlink(String custid, String examId) {
        if (examId == null || examId.isEmpty()) {
            Log.e(ContentValues.TAG, "Exam ID is null or empty");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Fmge")
                .document("Weekley")
                .collection("Quiz")
                .document(examId);

        Log.d("createreferlink", "Fetching document for examId: " + examId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String quizTitle = documentSnapshot.getString("title");
                Timestamp startTimeStamp = (Timestamp) documentSnapshot.get("from");
                Timestamp endTimeStamp = (Timestamp) documentSnapshot.get("to");
                long timeUntilOpen = startTimeStamp.getSeconds() - System.currentTimeMillis() / 1000;

                String encodedExamTitle = encode(quizTitle);
                String encodedExamDescription = encode(examdescription);

                String shareLinkText = "Checkout this Exam" + quizTitle + " \uD83E\uDE7A" + " at " +
                        timeUntilOpen + " in " +
                        "https://app.mymedicos.in/?" +
                        "link=http://www.mymedicos.in/fmgedetails?fmgeId=" + examId +
                        "&st=" + encodedExamTitle +
                        "&sd=" + encodedExamDescription +
                        "&apn=" + getPackageName() +
                        "&si=" + "https://res.cloudinary.com/dlgrxj8fp/image/upload/v1709416117/mwkegbnreoldjn4lksnn.png";
                Log.e("Exam Detailed Activity", "Sharelink - " + shareLinkText);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, shareLinkText);
                intent.setType("text/plain");
                startActivity(intent);
            } else {
                Log.e(ContentValues.TAG, "No such document with documentId: " + examId);
            }
        }).addOnFailureListener(e -> {
            Log.e(ContentValues.TAG, "Error fetching exam details for documentId: " + examId, e);
        });

    }
    private String encode(String s) {
        if (s == null) {
            return "";
        }

        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}