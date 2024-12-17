package com.medical.my_medicos.activities.neetss.activites;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.login.bottom_controls.PrivacyPolicyActivity;
import com.medical.my_medicos.activities.login.bottom_controls.TermsandConditionsActivity;
import com.medical.my_medicos.activities.neetss.activites.extras.CreditsActivity;
import com.medical.my_medicos.activities.publications.activity.PaymentPublicationActivity;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.prefs.Preferences;

import de.hdodenhof.circleimageview.CircleImageView;

public class NeetssExamPayment extends AppCompatActivity {

    private DatabaseReference database;
    TextView user_name_dr,user_email_dr;
    CircleImageView profilepicture;
    TextView currentcoinspg;
    ImageView sharebtnforneetexam;
    String examtitle,examdescription,examDue;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String current_user=user.getPhoneNumber();
    String receivedData;
    private String currentUid;
    private int examFee = 50;
    private boolean dataLoaded = false;
    private int pendingDiscount = 0; // Default is no discount
    private String couponId; // To store the ID of the applied coupon
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String examId;
    String title1;
    ProgressDialog progressDialog;
    private String about;

    private String validatedCouponCode = null;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neet_exam_payment);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        Intent intent = getIntent();
        examtitle = intent.getStringExtra("title");
        examDue = intent.getStringExtra("to");

        examId = getIntent().getStringExtra("qid");
        if (examId == null || examId.isEmpty()){
            examId = intent.getStringExtra("examId");
            getSpecializationAndFetchQuestions();

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

        FirebaseUser current =FirebaseAuth.getInstance().getCurrentUser();
        currentUid =current.getPhoneNumber();

        database = FirebaseDatabase.getInstance().getReference();
        user_name_dr = findViewById(R.id.currentusernamewillcomehere);
        profilepicture = findViewById(R.id.profilepicture);
        user_email_dr = findViewById(R.id.currentuseremailid);

        if (!dataLoaded) {
            fetchdata();
            fetchUserData();
        }

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
        qidcomehereText.setText(examId);

        currentcoinspg = findViewById(R.id.currentcoinspg);

        CheckBox agreeCheckbox = findViewById(R.id.agreeCheckbox);
        LinearLayout startExamLayout = findViewById(R.id.startexamination);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUid = currentUser.getPhoneNumber();
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            database.getReference().child("profiles")
                    .child(currentUid)
                    .child("coins")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer coinsValue = snapshot.getValue(Integer.class);
                            if (coinsValue != null) {
                                currentcoinspg.setText(String.valueOf(coinsValue));
                            } else {
                                currentcoinspg.setText("0");
                            }
                        }

                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error loading coins from database: " + error.getMessage());
                        }
                    });
        }

        startExamLayout.setEnabled(false); // Initially disable the layout

        // Set an OnClickListener on the CheckBox
        agreeCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the CheckBox is checked
                if (agreeCheckbox.isChecked()) {
                    startExamLayout.setEnabled(true); // Enable the layout
                } else {
                    Toast.makeText(NeetssExamPayment.this, "Please agree to the terms to start the examination", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(NeetssExamPayment.this, "Document ID is not available.", Toast.LENGTH_SHORT).show();
                    return;
                }
                createlink(current_user, examId, examtitle, examdescription); // Pass examId here
            }
        });


        TextView coupon_submit = findViewById(R.id.coupon_submit);
        EditText coupon_apply=findViewById(R.id.coupon_apply);

        coupon_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCouponCode = coupon_apply.getText().toString();
                validateAndApplyCoupon(enteredCouponCode);
            }
        });

        handleDeepLink();
        configureWindow();

    }
    private void getSpecializationAndFetchQuestions() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getPhoneNumber();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference().child("profiles").child(userId);

            // Fetch the 'Neetss' field from Firebase Realtime Database
            userRef.child("Neetss").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String fetchedSpecialization = snapshot.getValue(String.class);
                    if (fetchedSpecialization != null) {
                        Log.d(ContentValues.TAG, "User specialization fetched: " + fetchedSpecialization);
                        // Use the fetched specialization to get quizzes
                        fetchDataForNull(fetchedSpecialization);

                    } else {
                        Log.e(ContentValues.TAG, "Specialization is null for user: " + userId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(ContentValues.TAG, "Error fetching specialization: " + error.getMessage());
                }
            });
        } else {
            Log.e(ContentValues.TAG, "User is not logged in.");
        }
    }


    private void fetchDataForNull( String fetchedSpecialization) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference quizCollection = db.collection("Neetss").document(fetchedSpecialization).collection("Weekley");

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
    //....
    private void handleDeepLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String examId = deepLink.getQueryParameter("examId");
                            String title = deepLink.getQueryParameter("title");
                            String due = deepLink.getQueryParameter("due");

                            Intent intent = new Intent(NeetssExamPayment.this, NeetssExamPayment.class);
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
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(NeetssExamPayment.this);
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
            Log.d("usernaem", username);
        }
        String username = preferences.get("username", "");
        String email = preferences.get("email", "");
        String location = preferences.get("location", "");
        String interest = preferences.get("interest", "");
        String phone = preferences.get("userphone", "");
        String prefix = preferences.get("prefix", "");
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
                                            String userLocation = (String) dataMap.get("Location");
                                            String userInterest = (String) dataMap.get("Interest");
                                            String userPhone = (String) dataMap.get("Phone Number");
                                            String userPrefix = (String) dataMap.get("Prefix");
                                            String userAuthorized = (String) dataMap.get("authorized");
                                            Boolean mcnVerified = (Boolean) dataMap.get("MCN verified");
                                            Preferences preferences = Preferences.userRoot();
                                            preferences.put("username", userName);
                                            preferences.put("email", userEmail);
                                            preferences.put("location", userLocation);
                                            preferences.put("interest", userInterest);
                                            preferences.put("userphone", userPhone);
                                            preferences.put("docId", docID);
                                            preferences.put("prefix", userPrefix);
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
    @SuppressLint("RestrictedApi")
    public void validateAndApplyCoupon(String code) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getPhoneNumber() != null) {
            String currentPhoneNumber = currentUser.getPhoneNumber();

            // First check if the coupon has been used by this user
            DocumentReference userCouponDoc = db.collection("CouponUsed").document(currentPhoneNumber);
            userCouponDoc.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> usedCoupons = (List<String>) documentSnapshot.get("coupon_used");
                    if (usedCoupons != null && usedCoupons.contains(code)) {
                        // Coupon has already been used by this user
                        showCustomCouponUsedDialog();
                    } else {
                        // Proceed to validate the coupon if not used
                        checkCouponValidity(code, db);
                    }
                } else {
                    // No coupons used yet, proceed to validate new coupon
                    checkCouponValidity(code, db);
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Error checking used coupons", e));
        } else {
            Toast.makeText(this, "User not logged in or phone number unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomCouponUsedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogue_coupon_already_used, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TextView buttonOk = dialogView.findViewById(R.id.dialog_button);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void checkCouponValidity(String code, FirebaseFirestore db) {
        db.collection("Coupons")
                .whereEqualTo("code", code)
                .whereEqualTo("status", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                couponId = document.getId(); // Store the coupon ID when it's validated
                                pendingDiscount = Objects.requireNonNull(document.getLong("discount")).intValue();
                                about = Objects.requireNonNull(document.getString("about"));
                                // Store the coupon code temporarily instead of adding to used list
                                validatedCouponCode = code;  // Assume validatedCouponCode is a class member variable
                                showCustomDialog("Coupon Applied Successfully!", code, document.getString("about"));
                            }
                        } else {
                            Toast.makeText(NeetssExamPayment.this, "Not a valid coupon code", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showCustomDialog(String title, String couponCode, String couponDescription) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_coupon_applied, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        TextView dialogButton = dialogView.findViewById(R.id.dialog_button);

        // Set the title and message with coupon details
        dialogTitle.setText(title + ": " + couponCode);
        dialogMessage.setText(couponDescription);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showInsufficientCreditsDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(NeetssExamPayment.this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.custom_bottom_sheet_up_insufficient, null);

        TextView text = bottomSheetView.findViewById(R.id.text_insufficient_credits);
        text.setText("You have Insufficient Credit Point");

        TextView closeButton = bottomSheetView.findViewById(R.id.close);
        TextView intentCredits = bottomSheetView.findViewById(R.id.intenttocredit);
        CardView puchase100credits = bottomSheetView.findViewById(R.id.puchase100credits);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        intentCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss(); // Dismiss the bottom sheet dialog
                Intent intent = new Intent(NeetssExamPayment.this, CreditsActivity.class); // Create an intent for the CreditsActivity
                startActivity(intent); // Start the activity
            }
        });

        puchase100credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet129();
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void showBottomSheet129() {
        View bottomSheetView = LayoutInflater.from(NeetssExamPayment.this).inflate(R.layout.bottom_sheet_up_250, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(NeetssExamPayment.this);
        bottomSheetDialog.setContentView(bottomSheetView);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView textClickMe = bottomSheetView.findViewById(R.id.paymentpartcredit129);

        textClickMe.setOnClickListener(v -> {
            processCreditsOrderPackage2();
        });
        bottomSheetDialog.show();
    }

    void processCreditsOrderPackage2() {
        progressDialog.show();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("users");

            usersRef.whereEqualTo("Phone Number", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RequestQueue queue = Volley.newRequestQueue(this);

                                String url = ConstantsDashboard.GET_ORDER_ID_99_41 + userId + "/" + "package2";
                                Log.d("API Request URL", url);

                                StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
                                    try {
                                        Log.d("API Response", response);
                                        JSONObject requestBody = new JSONObject(response);

                                        if (requestBody.getString("status").equals("success")) {
                                            Toast.makeText(NeetssExamPayment.this, "Success order.", Toast.LENGTH_SHORT).show();
                                            String orderNumber = requestBody.getString("order_id");
                                            Log.e("Order ID check", orderNumber);
                                            new android.app.AlertDialog.Builder(NeetssExamPayment.this)
                                                    .setTitle("Order Successful")
                                                    .setCancelable(false)
                                                    .setMessage("Your order number is: " + orderNumber)
                                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                                        Intent intent = new Intent(NeetssExamPayment.this, PaymentPublicationActivity.class);
                                                        intent.putExtra("orderCode", orderNumber);
                                                        startActivity(intent);
                                                    }).show();
                                        } else {
                                            Toast.makeText(NeetssExamPayment.this, "Failed order.", Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                        Log.e("res", response);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, error -> {
                                    error.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(NeetssExamPayment.this, "Volley Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                                queue.add(request);
                            }
                        } else {
                            // Handle the error when the document is not found
                            progressDialog.dismiss();
                            Toast.makeText(NeetssExamPayment.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressDialog.dismiss();
            Toast.makeText(NeetssExamPayment.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("RestrictedApi")
    private void addCouponToUsedList(String phoneNumber, String couponCode) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userCouponDoc = db.collection("CouponUsed").document(phoneNumber);

        // First, attempt to get the document to check if it exists
        userCouponDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document exists, proceed with updating
                    updateCouponUsedList(userCouponDoc, couponCode);
                } else {
                    // Document does not exist, create it first
                    Map<String, Object> newUserCouponData = new HashMap<>();
                    newUserCouponData.put("coupon_used", Arrays.asList(couponCode)); // Initialize with first coupon
                    userCouponDoc.set(newUserCouponData)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "New coupon list created for user"))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to create new coupon list for user", e));
                }
            } else {
                Log.e(TAG, "Failed to check if user coupon document exists", task.getException());
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void updateCouponUsedList(DocumentReference userCouponDoc, String couponCode) {
        // Use set with merge option to create the document if it does not exist
        Map<String, Object> updates = new HashMap<>();
        updates.put("coupon_used", FieldValue.arrayUnion(couponCode));

        userCouponDoc.set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Coupon code added to user's used list"))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding coupon code to used list", e));
    }

    private void applyCoupon(int discount, String couponId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("profiles").child(currentUid).child("coins");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer coinsValue = snapshot.getValue(Integer.class);
                if (coinsValue != null && coinsValue >= discount) {
                    int newCoinsValue = coinsValue - Math.max(discount, examFee);
                    ref.setValue(newCoinsValue);
                    Toast.makeText(NeetssExamPayment.this, "Coupon applied successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NeetssExamPayment.this, "Insufficient Coins", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NeetssExamPayment.this, "Error fetching coins", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startExamination(String title, String title1) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("profiles").child(currentUid).child("coins");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer coinsValue = snapshot.getValue(Integer.class);
                if (coinsValue != null) {
                    int coinsAfterDiscount = Math.max(0, examFee - pendingDiscount);
                    int newCoinsValue = coinsValue - coinsAfterDiscount;

                    if (newCoinsValue >= 0) {
                        ref.setValue(newCoinsValue);

                        // Save the coupon to used list only when the exam starts
                        if (validatedCouponCode != null) {
                            addCouponToUsedList(currentUser.getPhoneNumber(), validatedCouponCode, couponId);
                            validatedCouponCode = null;  // Reset the validated coupon code
                        }

                        showQuizInsiderActivity(title, title1);
                        Toast.makeText(NeetssExamPayment.this, "Welcome to the exam. Fee applied: " + newCoinsValue + " coins", Toast.LENGTH_SHORT).show();
                    } else {
                        showInsufficientCreditsDialog();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NeetssExamPayment.this, "Error fetching coins", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void addCouponToUsedList(String phoneNumber, String couponCode, String couponId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userCouponDoc = db.collection("CouponUsed").document(phoneNumber);

        // First, attempt to get the document to check if it exists
        userCouponDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document exists, proceed with updating
                    updateCouponUsedList(userCouponDoc, couponCode);
                } else {
                    // Document does not exist, create it first
                    Map<String, Object> newUserCouponData = new HashMap<>();
                    newUserCouponData.put("coupon_used", Arrays.asList(couponCode));
                    userCouponDoc.set(newUserCouponData)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "New coupon list created for user"))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to create new coupon list for user", e));
                }
            } else {
                Log.e(TAG, "Failed to check if user coupon document exists", task.getException());
            }
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
        builder.setMessage("Starting this quiz will deduct 50 med coins from your account. Do you want to proceed?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startExamination(title, title1);
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
    // Method to show the quiz instructions activity
    private void showQuizInsiderActivity(String title, String title1) {
        Intent intent = new Intent(NeetssExamPayment.this, Neetssexaminsider.class);
        intent.putExtra("Title1", title1);
        Log.e("Show title1",title1);
        intent.putExtra("Title", title);
        Log.e("Show title here",title);

        startActivity(intent);
    }
}