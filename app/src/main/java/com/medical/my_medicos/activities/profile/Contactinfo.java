package com.medical.my_medicos.activities.profile;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.medical.my_medicos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import de.hdodenhof.circleimageview.CircleImageView;

public class Contactinfo extends AppCompatActivity {
    private EditText email, phone;
    EditText presentaddress, permanentaddress, agedr;
    private Spinner location, speciality;
    String selectedGender;
    String selectedMode;
    ImageView currentImageView;
    Button Submit;
    private ArrayAdapter<CharSequence> locationAdapter, specialityAdapter;
    private CircleImageView avatarImageView;
    RadioButton rb;
    private CardView uploadAvatarCardView;
    String documentid;
    private FirebaseAuth mAuth;
    public FirebaseDatabase profiledb = FirebaseDatabase.getInstance();
    public DatabaseReference userref = profiledb.getReference().child("users");
    private StorageReference mStorageRef;
    FirebaseFirestore dc = FirebaseFirestore.getInstance();
    HashMap<String, Object> usermap = new HashMap<>();
    String userid;
    String currentaddress;
    String fulladdress ;
    String drage ;

    Toolbar toolbar;

    private ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactinfo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        toolbar = findViewById(R.id.contactsstoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        location = findViewById(R.id.location);
        avatarImageView = findViewById(R.id.avatarImageView);
        currentImageView = findViewById(R.id.currentImageView);

        uploadAvatarCardView = findViewById(R.id.upload_avatar);

        email = findViewById(R.id.email1);
        phone = findViewById(R.id.phonenumber);

        email.setEnabled(false);
        email.setTextColor(Color.parseColor("#80000000"));
        email.setBackgroundResource(R.drawable.rounded_edittext_background);

        phone.setEnabled(false);
        phone.setTextColor(Color.parseColor("#80000000"));
        phone.setBackgroundResource(R.drawable.rounded_edittext_background);

        userid = currentUser.getPhoneNumber();
        email.setText(userid);

        dc.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        String field3 = ((String) dataMap.get("Phone Number"));


                        if (field3 != null && userid != null) {
                            int r = userid.compareTo(field3);
                            documentid=document.getId();
                            Log.d("documentid",documentid);

                            if (r == 0) {
                                String field4 = ((String) dataMap.get("Email ID"));
                                Log.d("Something went wrong", field4);
                                phone.setText(field4);
                                break;
                            }
                        } else {
                            Log.e(TAG, "Field3 or userid is null");
                        }
                    }
                } else {
                    Toast.makeText(Contactinfo.this, "Error fetching data from Firebase Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });

        userref = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getPhoneNumber());
        mStorageRef = FirebaseStorage.getInstance().getReference().child("users").child(currentUser.getPhoneNumber());

        presentaddress = findViewById(R.id.present1);
        permanentaddress = findViewById(R.id.permanent1);
        agedr = findViewById(R.id.age1);

        locationAdapter = ArrayAdapter.createFromResource(this,
                R.array.indian_cities, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(locationAdapter);

        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                int locationIndex = location.getSelectedItemPosition();
                selectedMode = location.getSelectedItem().toString();
                usermap.put("location", selectedMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        RadioGroup radioGroup = findViewById(R.id.radiogrp);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

                if (selectedRadioButton != null) {
                    String selectedText = selectedRadioButton.getText().toString();

                    Log.d("RadioButton"+selectedRadioButtonId, "Selected Text: " + selectedText);
                    selectedGender=selectedText;
                }
            }
        });

        CardView conSubmitButton = findViewById(R.id.upload_avatar);
        conSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        Submit=findViewById(R.id.consubmit);

        currentaddress = presentaddress.getText().toString().trim();
        fulladdress = permanentaddress.getText().toString().trim();
        drage = agedr.getText().toString().trim();
        CheckBox sameAddressCheckBox = findViewById(R.id.checkbox1);
        sameAddressCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String presentAddress = presentaddress.getText().toString();
                    permanentaddress.setText(presentAddress);
                } else {
                    permanentaddress.setText("");
                }
            }
        });

        dc.collection("users")
                .whereEqualTo("Phone Number", userid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> dataMap = document.getData();

                                String presentAddress = (String) dataMap.get("present");
                                String permanentAddress = (String) dataMap.get("permanent");
                                String age = (String) dataMap.get("Age");

                                presentaddress.setText(presentAddress);
                                permanentaddress.setText(permanentAddress);
                                agedr.setText(age);

                            }
                        } else {
                            // Handle the error
                            Toast.makeText(Contactinfo.this, "Error fetching data from Firebase Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postinfo();

            }
        });

        fetchUserData();
    }

    public void fetchUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    private void fetchUserProfileImage(String userId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("users").child(userId).child("profile_image.jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(currentImageView);
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Error fetching profile image: " + exception.getMessage());
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            StorageReference imageRef = mStorageRef.child("profile_image.jpg");
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    userref.child("profileImage").setValue(imageUrl);
                    progressDialog.dismiss();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Contactinfo.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    }, 1000);
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
            });
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                avatarImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void checkUserProfile() {
        Log.d("checkuserprofilecalled","2");
         FirebaseFirestore db;
         FirebaseUser currentUser;
        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        if (currentUser != null) {
            Log.d("checkuserprofilecalled","3");

            String phoneNumber = currentUser.getPhoneNumber();
            if (phoneNumber != null) {
                db.collection("users")
                        .whereEqualTo("Phone Number", phoneNumber)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty()) {
                                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                    boolean isDetailsComplete = document.contains("Age")
                                            && document.contains("permanent")
                                            && document.contains("present");

                                    checkIfAvatarExists(isDetailsComplete);
                                } else {

                                }
                            } else {

                            }
                        });
            } else {

            }
        } else {

        }
    }

    private void checkIfAvatarExists(boolean isDetailsComplete) {
        FirebaseFirestore db;
        FirebaseUser currentUser;
        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        Log.d("checkuserprofilecalled","4");

        StorageReference avatarRef = storage.getReference().child("users/" + currentUser.getPhoneNumber() + "/profile_image.jpg");
        avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (isDetailsComplete) {
                Log.d("isdetailscomplete"+ isDetailsComplete,avatarRef.toString());
                Log.d("checkuserprofilecalled","10");

                if (currentUser != null) {
                    Log.d("checkuserprofilecalled","11");

                    String phoneNumber = currentUser.getPhoneNumber();
                    if (phoneNumber != null) {
                        Log.d("checkuserprofilecalled","12");
                        Log.d("checkuserprofilecalled",phoneNumber);
                        db.collection("users")
                                .whereEqualTo("Phone Number", phoneNumber)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (!querySnapshot.isEmpty()) {
                                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                            Boolean updatesProfile = document.contains("Updatesprofile");
                                            Log.d("checkuserprofilecalled","5");
                                            Log.d("checkuserprofilecalled",updatesProfile.toString());

                                            if (updatesProfile==true){
                                                Log.d("checkuserprofilecalled","5");
                                                Boolean profile=document.getBoolean("Updatesprofile");
                                                if (profile==false){
                                                    Log.d("checkuserprofilecalled","77");
                                                    Log.d("checkuserprofilecalled",profile.toString());
                                                    db.collection("users")
                                                            .whereEqualTo("Phone Number", phoneNumber)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            String userId = document.getId();
                                                                            updateProfileField(userId,phoneNumber);
                                                                            Log.d("checkuserprofilecalled","6");
                                                                             // Assume only one document matches
                                                                        }
                                                                    } else {
                                                                        // Handle the error
                                                                        // Log.e("Firestore Error", "Error getting documents: ", task.getException());
                                                                    }
                                                                }
                                                            });

                                                }



                                            }
                                            else{
                                                Log.d("checkuserprofilecalled","24");
                                                db.collection("users")
                                                        .whereEqualTo("Phone Number", phoneNumber)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        String userId = document.getId();
                                                                        updateProfileField(userId,phoneNumber);
                                                                        Log.d("checkuserprofilecalled","25");
                                                                         // Assume only one document matches
                                                                    }
                                                                } else {
                                                                    // Handle the error
                                                                    // Log.e("Firestore Error", "Error getting documents: ", task.getException());
                                                                }
                                                            }
                                                        });

                                            }



                                        } else {

                                        }
                                    } else {

                                    }
                                });
                    } else {

                    }
                } else {

                }



            } else {

            }
        }).addOnFailureListener(exception -> {

        });
    }
    private void updateProfileField(String userId,String phoneNumber) {
        FirebaseFirestore db;
        FirebaseDatabase database;
        database = FirebaseDatabase.getInstance();

        db = FirebaseFirestore.getInstance();
        Log.d("checkuserprofilecalled","7");
        db.collection("users").document(userId)

                .update("Updatesprofile", true)
                .addOnSuccessListener(aVoid -> {
                    database.getReference().child("profiles")
                            .child(phoneNumber)
                            .child("coins")
                            .setValue(10);
                    // Profile updated successfully
                    Toast.makeText(Contactinfo.this, "10 MedCoins ARE ADDED", Toast.LENGTH_SHORT).show();
                     Log.d("Firestore Success", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    // Log.e("Firestore Error", "Error updating document", e);
                });
    }
    private void postinfo() {
        currentaddress = presentaddress.getText().toString().trim();
        fulladdress= permanentaddress.getText().toString().trim();

        drage = agedr.getText().toString().trim();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("Something went wrong ",currentaddress);
        Log.d("Something went wrong",documentid);

        DocumentReference docRef = db.collection("users").document(documentid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {


                            checkUserProfile();


                            // Proceed with updating the document
                            docRef.update("present", currentaddress,
                                            "permanent", fulladdress,
                                            "Age", drage,
                                            "Location", selectedMode)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            checkUserProfile();
                                            Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!");
                                            Toast.makeText(Contactinfo.this, "Successfully Ended", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(ContentValues.TAG, "Error updating document", e);
                                        }
                                    });
                        } else {
                            Log.d(ContentValues.TAG, "updatesprofile is false or null");
                            Toast.makeText(Contactinfo.this, "Profile updates are not allowed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(ContentValues.TAG, "No such document");
                    }

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}