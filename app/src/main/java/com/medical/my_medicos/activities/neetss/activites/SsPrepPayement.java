package com.medical.my_medicos.activities.neetss.activites;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.insiders.WeeklyQuizInsiderActivity;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.prefs.Preferences;

import de.hdodenhof.circleimageview.CircleImageView;

public class SsPrepPayement extends AppCompatActivity {

    private DatabaseReference database;
    TextView user_name_dr,user_email_dr;
    CircleImageView profilepicture;
    TextView currentcoinspg;
    private String currentUid;

    private boolean dataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pg_prep_payment);

        database = FirebaseDatabase.getInstance().getReference();
        FirebaseUser current =FirebaseAuth.getInstance().getCurrentUser();

        currentUid =current.getPhoneNumber();
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

        Intent intent = getIntent();
        String title1 = intent.getStringExtra("Title1");
        String title = intent.getStringExtra("Title");
        String Due = intent.getStringExtra("Due");

        TextView quizNameTextView = findViewById(R.id.quizNameTextView1);
        TextView dueDateTextView = findViewById(R.id.DueDate1);
        dueDateTextView.setText(Due);
        quizNameTextView.setText(title);

        TextView currentcoinspg = findViewById(R.id.currentcoinspg);

        CheckBox agreeCheckbox = findViewById(R.id.agreeCheckboxpg);
        LinearLayout startExamLayout = findViewById(R.id.startexamination1);

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
                    Toast.makeText(SsPrepPayement.this, "Please agree to the terms to start the examination", Toast.LENGTH_SHORT).show();
                    startExamLayout.setEnabled(false); // Disable the layout
                }
            }
        });
        // Set OnClickListener on start examination layout
        startExamLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(title, title1);
            }
        });

        configureWindow();
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


    private void startExamination(String title, String title1) {
        database.child("profiles")
                .child(currentUid)
                .child("coins")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            Integer coinsValue = snapshot.getValue(Integer.class);

                            if (coinsValue != null) {

                                int newCoinsValue = coinsValue - 50;
                                if (newCoinsValue >= 0) {

                                    database.child("profiles")
                                            .child(currentUid)
                                            .child("coins")
                                            .setValue(newCoinsValue);

                                    showQuizInsiderActivity(title, title1);

                                    Toast.makeText(SsPrepPayement.this, "Welcome", Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(SsPrepPayement.this, "Insufficient Credits", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
        Intent intent = new Intent(SsPrepPayement.this, WeeklyQuizInsiderActivity.class);
        intent.putExtra("Title1", title1);
        intent.putExtra("Title", title);

        startActivity(intent);
    }
}
