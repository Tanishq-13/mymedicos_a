package com.medical.my_medicos.activities.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.medical.my_medicos.R;
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
import com.medical.my_medicos.activities.guide.UgGuideActivity;
import com.medical.my_medicos.activities.guide.VerficationGuideActivity;
import com.medical.my_medicos.activities.ug.UgExamActivity;

import java.util.HashMap;

public class Personalinfo extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    private ArrayAdapter<CharSequence> medicalcouncilAdapter;

    String current = user.getPhoneNumber();

    Button verificationbtnformedical;

    Spinner mcncouncilselected;
    FirebaseFirestore mcnumber = FirebaseFirestore.getInstance();
    EditText medicalcouncilnumber;

    LinearLayout viewinstruction;

    public FirebaseDatabase db = FirebaseDatabase.getInstance();
    public DatabaseReference medicalref = db.getReference().child("Medical Council Number Request");
    private ProgressDialog progressDialog;

    HashMap<String, Object> userMap = new HashMap<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);

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

        Toolbar toolbar = findViewById(R.id.verificationtoolbar);
        setSupportActionBar(toolbar);

        mcncouncilselected = findViewById(R.id.mcncouncilselected);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_bk);
        }

        progressDialog = new ProgressDialog(this);
        medicalcouncilnumber = findViewById(R.id.mcnumber);
        verificationbtnformedical = findViewById(R.id.verificationButton);

        checkPhoneNumberExists();

        if (isVerificationPending()) {
            showVerificationPopup();
        }

        verificationbtnformedical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    verificationbtnformedical();
                }
            }
        });

        mcnspinner();
    }

    private void mcnspinner(){
        medicalcouncilAdapter = ArrayAdapter.createFromResource(this,
                R.array.indian_medical_council, android.R.layout.simple_spinner_item);
        medicalcouncilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mcncouncilselected.setAdapter(medicalcouncilAdapter);

        mcncouncilselected.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedMedicalLocation = adapterView.getItemAtPosition(position).toString();
                userMap.put("location", selectedMedicalLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }


    private boolean isVerificationPending() {
        // Have to Create Function here
        return false;
    }

    private void showVerificationPopup() {
        final Dialog popupDialog = new Dialog(this);
        popupDialog.setContentView(R.layout.popupforverification);
        popupDialog.setTitle("Verification Pending");
        popupDialog.show();
    }

    private void checkPhoneNumberExists() {
        medicalref.orderByChild("User").equalTo(current).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    verificationbtnformedical.setVisibility(View.INVISIBLE);
                    medicalcouncilnumber.setEnabled(false);
                    mcncouncilselected.setEnabled(false);
                    verificationbtnformedical.setEnabled(false);
                    medicalcouncilnumber.setHint("In Review");
                    mcncouncilselected.setTag("In Review");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error checking phone number: " + databaseError.getMessage());
            }
        });
    }

    public void verificationbtnformedical() {
        String organiser = medicalcouncilnumber.getText().toString().trim();

        if (TextUtils.isEmpty(organiser)) {
            medicalcouncilnumber.setError("Medical Council Number Required");
            return;
        }

        HashMap<String, Object> usermap = new HashMap<>();
        usermap.put("Medical Council Number", organiser);
        usermap.put("User", current);

        progressDialog.setMessage("Generating Request...");
        progressDialog.show();

        medicalref.push().setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();

                if (task.isSuccessful()) {
                    String generatedDocId = medicalref.push().getKey();
                    usermap.put("documentId", generatedDocId);
                    mcnumber.collection("Medical Council Number Request").document(generatedDocId).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Personalinfo.this, "Verification Requested", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Personalinfo.this, "Verification Request Failed", Toast.LENGTH_SHORT).show();
                                Log.e("Firebase", "Error writing to Firebase: " + task.getException());
                            }
                        }
                    });
                } else {
                    Toast.makeText(Personalinfo.this, "Failed to Proceed, Try again later", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Error writing to Firebase: " + task.getException());
                }
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}