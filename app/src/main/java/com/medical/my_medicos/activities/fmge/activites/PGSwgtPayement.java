package com.medical.my_medicos.activities.fmge.activites;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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
import com.medical.my_medicos.activities.pg.activites.Neetexaminsider;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.prefs.Preferences;

import de.hdodenhof.circleimageview.CircleImageView;

public class PGSwgtPayement extends AppCompatActivity {

    private DatabaseReference database;
    TextView user_name_dr, user_email_dr;
    CircleImageView profilepicture;
    ImageView sharebtnforneetexam;
    String examtitle, examdescription, examDue;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String current_user = user.getPhoneNumber();
    String receivedData;
    private String currentUid;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String examId;
    String title1;
    ProgressDialog progressDialog;
    private String about;

    @SuppressLint({"RestrictedApi", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pgswgt_payement);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        Intent intent = getIntent();
        examtitle = intent.getStringExtra("title");
        examDue = intent.getStringExtra("to");

        examId = getIntent().getStringExtra("qid");
        if (examId == null || examId.isEmpty()) {
            examId = intent.getStringExtra("examId");
            fetchDataForNull();
        }

        examtitle = intent.getStringExtra("Title");
        title1 = intent.getStringExtra("Title1");
        String Due = intent.getStringExtra("Due");

        if (examId != null && !examId.isEmpty()) {
            Log.d(TAG, "Opened from ExamQuizAdapter");
            TextView quizNameTextView = findViewById(R.id.quizNameTextView);
            TextView dueDateTextView = findViewById(R.id.DueDate);
            quizNameTextView.setText(examtitle);
            dueDateTextView.setText(Due);

        } else {
            Log.d(TAG, "Opened from dynamic link");
            TextView quizNameTextView = findViewById(R.id.quizNameTextView);
            TextView dueDateTextView = findViewById(R.id.DueDate);
            quizNameTextView.setText(examtitle);
            dueDateTextView.setText(Due);
        }

        // Set the exam ID
        TextView qidTextView = findViewById(R.id.quizid);
        qidTextView.setText(examId);

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
                    Toast.makeText(PGSwgtPayement.this, "Please agree to the terms to start the examination", Toast.LENGTH_SHORT).show();
                    startExamLayout.setEnabled(false); // Disable the layout
                }
            }
        });

        // Set OnClickListener on start examination layout
        startExamLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(examtitle, title1);
            }
        });

        sharebtnforneetexam = findViewById(R.id.sharebtnforneetexam);

        sharebtnforneetexam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ensure documentid is initialized
                if (examId == null || examId.isEmpty()) {
                    Toast.makeText(PGSwgtPayement.this, "Document ID is not available.", Toast.LENGTH_SHORT).show();
                    return;
                }
                createlink(current_user, examId, examtitle, examdescription); // Pass examId here
            }
        });

        handleDeepLink();
        configureWindow();
    }

    private void fetchDataForNull() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference quizCollection = db.collection("PGupload").document("Weekley").collection("Quiz");

        // Fetch the specific document using its ID
        quizCollection.document(examId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    examtitle = document.getString("title");
                    examDue = String.valueOf(document.getDate("to"));
                    title1 = document.getString("speciality");
                    TextView quizNameTextView = findViewById(R.id.quizNameTextView);
                    TextView dueDateTextView = findViewById(R.id.DueDate);
                    quizNameTextView.setText(examtitle);
                    dueDateTextView.setText(examDue);
                    System.out.println("Author: " + examtitle + "\nDue Date: " + examDue);
                } else {
                    System.out.println("No such document!");
                }
            } else {
                System.err.println("Failed with: " + task.getException());
            }
        });
    }

    public void createlink(String custid, String examId, String examtitle, String examdescription) {
        Log.e("main", "create link");
        Log.d("createlink", "custid: " + custid + ", examId: " + examId);

        if (examId == null || examId.isEmpty()) {
            Log.e("createlink", "Exam ID is null or empty");
            return;
        }

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.mymedicos.in/examdetails?custid=" + custid + "&examId=" + examId + "&title=" + examtitle + "&due=" + examdescription))
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
        DocumentReference docRef = db.collection("PGupload")
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

                String encodedExamTitle = encode(examtitle);
                String encodedExamDescription = encode(examdescription);

                String shareLinkText = "Checkout this Exam" + quizTitle + " \uD83E\uDE7A" + " at " +
                        timeUntilOpen + " in " +
                        "https://app.mymedicos.in/?" +
                        "link=http://www.mymedicos.in/examdetails?examId=" + examId +
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

    private void handleDeepLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String examId = deepLink.getQueryParameter("examId");
                            String title = deepLink.getQueryParameter("title");
                            String due = deepLink.getQueryParameter("due");

                            Intent intent = new Intent(PGSwgtPayement.this, PGSwgtPayement.class);
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(PGSwgtPayement.this);
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
            System.out.println("Key '" + "username" + "' exists in preferences.");
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
                                    Log.d(TAG, document.getId() + " => " + document.getData());
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

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showConfirmationDialog(String title, String title1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to proceed?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showQuizInsiderActivity(title, title1, examId);  // Pass examId here
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showQuizInsiderActivity(String title, String title1, String examId) {  // Add examId parameter
        Intent intent = new Intent(PGSwgtPayement.this, Neetexaminsider.class);
        intent.putExtra("Title1", title1);
        Log.e("Show title1", title1);
        intent.putExtra("Title", title);
        Log.e("Show title here", title);
        intent.putExtra("quiz_id", examId);  // Pass quiz_id to the next activity

        startActivity(intent);
    }
}
