package com.medical.my_medicos.activities.fmge.activites;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.login.bottom_controls.PrivacyPolicyActivity;
import com.medical.my_medicos.activities.login.bottom_controls.TermsandConditionsActivity;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.prefs.Preferences;

import de.hdodenhof.circleimageview.CircleImageView;

public class FmgeExamPayment extends AppCompatActivity {

    private DatabaseReference database;
    TextView user_name_dr, user_email_dr;
    CircleImageView profilepicture;
    ImageView sharebtnforneetexam;
    String fmgetitle, fmgedescription, fmgeDue;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String current_user = user.getPhoneNumber();
    private String currentUid;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String fmgeId;
    String title1;
    ProgressDialog progressDialog;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fmge_exam_payment);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        Intent intent = getIntent();
        fmgetitle = intent.getStringExtra("title");
        fmgeDue = intent.getStringExtra("to");

        fmgeId = getIntent().getStringExtra("qid");
        if (fmgeId == null || fmgeId.isEmpty()) {
            fmgeId = intent.getStringExtra("fmgeId");
            fetchDataForNull();
        }

        fmgetitle = intent.getStringExtra("Title");
        title1 = intent.getStringExtra("Title1");
        String Due = intent.getStringExtra("Due");

        if (fmgeId != null && !fmgeId.isEmpty()) {
            Log.d(TAG, "Opened from ExamQuizAdapter");
            TextView quizNameTextView = findViewById(R.id.quizNameTextView);
            TextView dueDateTextView = findViewById(R.id.DueDate);
            quizNameTextView.setText(fmgetitle);
            dueDateTextView.setText(Due);
        } else {
            Log.d(TAG, "Opened from dynamic link");
            TextView quizNameTextView = findViewById(R.id.quizNameTextView);
            TextView dueDateTextView = findViewById(R.id.DueDate);
            quizNameTextView.setText(fmgetitle);
            dueDateTextView.setText(Due);
        }

        // Set the exam ID
        TextView qidTextView = findViewById(R.id.quizid);
        qidTextView.setText(fmgeId);

        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = current.getPhoneNumber();

        database = FirebaseDatabase.getInstance().getReference();
        user_name_dr = findViewById(R.id.currentusernamewillcomehere);
        profilepicture = findViewById(R.id.profilepicture);
        user_email_dr = findViewById(R.id.currentuseremailid);

        fetchdata();
        fetchUserData();

        ImageView backToHomeImageView = findViewById(R.id.backtothehomefrompg);
        backToHomeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView textView1 = findViewById(R.id.help_support_coupon);
        String htmlText1 = "Having trouble? <strong><u>Reach out</u></strong>";
        textView1.setText(Html.fromHtml(htmlText1, Html.FROM_HTML_MODE_LEGACY));

        TextView help_support = findViewById(R.id.help_support_coupon);
        help_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        TextView qidcomehereText = findViewById(R.id.quizid);
        qidcomehereText.setText(fmgeId);

        CheckBox agreeCheckbox = findViewById(R.id.agreeCheckbox);
        LinearLayout startExamLayout = findViewById(R.id.startexamination);

        startExamLayout.setEnabled(false); // Initially disable the layout

        // Set an OnClickListener on the CheckBox
        agreeCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the CheckBox is checked
                if (agreeCheckbox.isChecked()) {
                    startExamLayout.setEnabled(true); // Enable the layout
                } else {
                    Toast.makeText(FmgeExamPayment.this, "Please agree to the terms to start the examination", Toast.LENGTH_SHORT).show();
                    startExamLayout.setEnabled(false); // Disable the layout
                }
            }
        });

        // Set OnClickListener on start examination layout
        startExamLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(fmgetitle, title1);
            }
        });

        sharebtnforneetexam = findViewById(R.id.sharebtnforneetexam);

        sharebtnforneetexam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ensure documentid is initialized
                if (fmgeId == null || fmgeId.isEmpty()) {
                    Toast.makeText(FmgeExamPayment.this, "Document ID is not available.", Toast.LENGTH_SHORT).show();
                    return;
                }
                createlink(current_user, fmgeId, fmgetitle, fmgedescription); // Pass examId here
            }
        });

        handleDeepLink();
        configureWindow();
    }

    private void fetchDataForNull() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference quizCollection = db.collection("Fmge").document("Weekley").collection("Quiz");

        // Fetch the specific document using its ID
        quizCollection.document(fmgeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    fmgetitle = document.getString("title");
                    fmgeDue = String.valueOf(document.getDate("to"));
                    title1 = document.getString("speciality");
                    TextView quizNameTextView = findViewById(R.id.quizNameTextView);
                    TextView dueDateTextView = findViewById(R.id.DueDate);
                    quizNameTextView.setText(fmgetitle);
                    dueDateTextView.setText(fmgeDue);
                } else {
                    System.out.println("No such document!");
                }
            } else {
                System.err.println("Failed with: " + task.getException());
            }
        });
    }

    public void createlink(String custid, String fmgeId, String fmgetitle, String fmgedescription) {
        Log.e("main", "create link");
        Log.d("createlink", "custid: " + custid + ", fmgeId: " + fmgeId);

        if (fmgeId == null || fmgeId.isEmpty()) {
            Log.e("createlink", "Fmge ID is null or empty");
            return;
        }

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.mymedicos.in/fmgedetails?custid=" + custid + "&fmgeId=" + fmgeId + "&title=" + fmgetitle + "&due=" + fmgedescription))
                .setDomainUriPrefix("https://app.mymedicos.in")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.e("main", "Long refer " + dynamicLinkUri);

        createreferlink(custid, fmgeId);
    }

    public void createreferlink(String custid, String fmgeId) {
        if (fmgeId == null || fmgeId.isEmpty()) {
            Log.e(ContentValues.TAG, "Fmge ID is null or empty");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Fmge")
                .document("Weekley")
                .collection("Quiz")
                .document(fmgeId);

        Log.d("createreferlink", "Fetching document for fmgeId: " + fmgeId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String quizTitle = documentSnapshot.getString("title");
                Timestamp startTimeStamp = (Timestamp) documentSnapshot.get("from");
                Timestamp endTimeStamp = (Timestamp) documentSnapshot.get("to");

                String encodedExamTitle = encode(fmgetitle);
                String encodedExamDescription = encode(fmgedescription);

                String shareLinkText = "Checkout this FMGE Test " + quizTitle + " at " +
                        "https://app.mymedicos.in/?" +
                        "link=http://www.mymedicos.in/fmgedetails?fmgeId=" + fmgeId +
                        "&st=" + encodedExamTitle +
                        "&sd=" + encodedExamDescription +
                        "&apn=" + getPackageName() +
                        "&si=" + "https://res.cloudinary.com/dlgrxj8fp/image/upload/v1709416117/mwkegbnreoldjn4lksnn.png";
                Log.e("Fmge Detailed Activity", "Sharelink - " + shareLinkText);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, shareLinkText);
                intent.setType("text/plain");
                startActivity(intent);
            } else {
                Log.e(ContentValues.TAG, "No such document with documentId: " + fmgeId);
            }
        }).addOnFailureListener(e -> {
            Log.e(ContentValues.TAG, "Error fetching exam details for documentId: " + fmgeId, e);
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

    private void handleDeepLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String examId = deepLink.getQueryParameter("fmgeId");
                            String title = deepLink.getQueryParameter("title");
                            String due = deepLink.getQueryParameter("due");

                            Intent intent = new Intent(FmgeExamPayment.this, FmgeExamPayment.class);
                            intent.putExtra("Title", title);
                            intent.putExtra("Due", due);
                            intent.putExtra("qid", examId);
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(this, e -> Log.w("DeepLink", "getDynamicLink:onFailure", e));
    }

    private void configureWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FmgeExamPayment.this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_login_layout, null);
        bottomSheetView.findViewById(R.id.check_mail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.check_privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PrivacyPolicyActivity.class);
                v.getContext().startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.check_terms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TermsandConditionsActivity.class);
                v.getContext().startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@mymedicos.in"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Facing issue in {Problem here}");

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
    }

    private void fetchdata() {
        Preferences preferences = Preferences.userRoot();
        if (preferences.get("username", null) != null) {
            String username = preferences.get("username", null);
            Log.d("username", username);
        }
        String username = preferences.get("username", "");
        String email = preferences.get("email", "");
        user_name_dr.setText(username);
        user_email_dr.setText(email);
    }

    private void fetchUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String docID = document.getId();
                                    Map<String, Object> dataMap = document.getData();
                                    String field1 = (String) dataMap.get("Phone Number");

                                    if (field1 != null && currentUser.getPhoneNumber() != null) {
                                        int a = field1.compareTo(currentUser.getPhoneNumber());
                                        if (a == 0) {
                                            String userName = (String) dataMap.get("Name");
                                            String userEmail = (String) dataMap.get("Email ID");
                                            Preferences preferences = Preferences.userRoot();
                                            preferences.put("username", userName);
                                            preferences.put("email", userEmail);
                                            preferences.put("docId", docID);
                                            user_name_dr.setText(userName);

                                            fetchdata();
                                            fetchUserProfileImage(userId);
                                        }
                                    } else {
                                        Log.e(TAG, "Field1 or currentUser.getPhoneNumber() is null");
                                    }
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    @SuppressLint("RestrictedApi")
    private void fetchUserProfileImage(String userId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("users").child(userId).child("profile_image.jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(profilepicture);
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Error fetching profile image: " + exception.getMessage());
        });
    }

    private void showConfirmationDialog(String title, String title1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to start the examination?");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            showQuizInsiderActivity(title, title1, fmgeId);
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Method to show the quiz instructions activity
    private void showQuizInsiderActivity(String title, String title1, String fmgeId) {
        Intent intent = new Intent(FmgeExamPayment.this, Fmgeexaminsider.class);
        intent.putExtra("Title1", title1);
        intent.putExtra("Title", title);
        intent.putExtra("fmgeId", fmgeId);
        startActivity(intent);
    }
}
