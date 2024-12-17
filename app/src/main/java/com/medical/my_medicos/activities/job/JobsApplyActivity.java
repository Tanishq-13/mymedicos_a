package com.medical.my_medicos.activities.job;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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
import com.google.firebase.storage.UploadTask;
import com.medical.my_medicos.activities.home.sidedrawer.extras.BottomSheetForChatWithUs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JobsApplyActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String current = user.getPhoneNumber();
    String field3,field4,field5;
    private Uri pdfData;
    static final int REQ = 1;
    private DatabaseReference databasereference;
    private StorageReference storageReference;
    private TextView addPdf,uploadPdfBtn;
    String downloadUrl = null;
    private ProgressDialog pd;
    private String pdfName;

    EditText applicantname, jobage, jobcover, jobemail;
    Button applyjob;

    Spinner jobgender;
    String Jobid;

    public FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseFirestore dc = FirebaseFirestore.getInstance();
    private Spinner genderSpinner;
    String receivedData;
    public DatabaseReference cmeref = db.getReference().child("JOBsApply");
    String documentid=null;
    private ArrayAdapter<CharSequence> genderAdapter;
    private TextView uploadpdfbtnjobs;
    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_jobs1);

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


        ImageView backArrow = findViewById(R.id.backbtn);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JobsApplyActivity.this, JobsActivity.class);
                startActivity(i);
                finish();
            }
        });

        applicantname = findViewById(R.id.name);
        jobemail = findViewById(R.id.Email);
        jobage = findViewById(R.id.Age);
        jobgender = findViewById(R.id.genderapplicant);
        jobcover = findViewById(R.id.cover);
        uploadpdfbtnjobs = findViewById(R.id.uploadpdfbtnjobs);
        addPdf = findViewById(R.id.addPdfjobs);
        //..............
        databasereference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this);

        addPdf = findViewById(R.id.addPdfjobs);

        uploadPdfBtn = findViewById(R.id.uploadpdfbtnjobs);

        addPdf.setOnClickListener(view -> {
            openGallery();
        });
        Intent intent = getIntent();
        if (intent != null) {
            receivedData = intent.getStringExtra("user");
            Jobid = intent.getStringExtra("documentid");

        }

        uploadPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pdfData == null){
                    Toast.makeText(JobsApplyActivity.this, "Select a Document", Toast.LENGTH_SHORT).show();
                }else{
                    uploadPdf();
                }
            }
        });

        genderSpinner = findViewById(R.id.genderapplicant);

        genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        dc.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        field3 = ((String) dataMap.get("Phone Number"));
                        boolean areEqualIgnoreCase = current.equalsIgnoreCase(field3);
                        Log.d("Something went wrong..", String.valueOf(areEqualIgnoreCase));
                        int r=current.compareTo(field3);
                        if (r==0){
                            field4 = ((String) dataMap.get("Name"));
                            Log.d("Issue in Fetching the name...",field4);
                            field5 = ((String) dataMap.get("Email ID"));
                            Log.d("Issue in Fetching the name...",field5);
                            applicantname.setText(field4);
                            jobemail.setText(field5);

                        }
                    }
                } else {
                    Toast.makeText(JobsApplyActivity.this, "Error fetching data from Firebase Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });

        applicantname.setEnabled(false);
        applicantname.setTextColor(Color.parseColor("#80000000"));
        applicantname.setBackgroundResource(R.drawable.rounded_edittext_background);

        jobemail.setEnabled(false);
        jobemail.setTextColor(Color.parseColor("#80000000"));
        jobemail.setBackgroundResource(R.drawable.rounded_edittext_background);


        applyjob = findViewById(R.id.post_btn2);
        Log.d(receivedData,"Something went wrong..");

        applyjob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    apply();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Something went wrong...", String.valueOf(e));
                }
            }
        });

        TextView whatsappLayout = findViewById(R.id.connectwithus);
        whatsappLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheet();
            }
        });
    }
    private void openBottomSheet() {
        BottomSheetDialogFragment bottomSheetFragment = new BottomSheetForChatWithUs();

        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void uploadPdf() {
        pd.setTitle("Please wait..");
        pd.setMessage("Uploading Pdf..");
        pd.show();
        StorageReference reference = storageReference.child("pdf/"+pdfName+"-"+System.currentTimeMillis()+".pdf");
        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri uri = uriTask.getResult();
                uploadData(String.valueOf(uri));
                downloadUrl=String.valueOf(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(JobsApplyActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(String valueOf) {
        String uniqueKey = databasereference.child("pdf").push().getKey();
        HashMap data = new HashMap();
        data.put("pdfUrl", downloadUrl);

        databasereference.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(JobsApplyActivity.this, "Pdf Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(JobsApplyActivity.this, "Failed to upload!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQ);
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ && resultCode == RESULT_OK) {
            pdfData = data.getData();

            if(pdfData.toString().startsWith("content://")){
                Cursor cursor = null;
                try {
                    cursor = JobsApplyActivity.this.getContentResolver().query(pdfData,null,null,null,null);
                    if(cursor != null && cursor.moveToFirst()){
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else if (pdfData.toString().startsWith("file://")){
                pdfName = new File(pdfData.toString()).getName();
            }
            addPdf.setText(pdfName);
        }
    }

    public void apply() {
        if (current == null) {
            Toast.makeText(JobsApplyActivity.this, "User information not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String age = jobage.getText().toString().trim();
        String gender = jobgender.getSelectedItem().toString().trim();
        String cover = jobcover.getText().toString().trim();

        if (TextUtils.isEmpty(field4)) {
            applicantname.setError("Applicant Name Required");
            return;
        }
        if (TextUtils.isEmpty(field5)) {
            jobemail.setError("Email Required");
            return;
        }
        if (TextUtils.isEmpty(age)) {
            jobage.setError("Age Required");
            return;
        }
        if (TextUtils.isEmpty(cover)) {
            jobcover.setError("Cover Required");
            return;
        }

        HashMap<String, Object> usermap = new HashMap<>();
        usermap.put("Full name", field4);
        usermap.put("Email ID", field5);
        usermap.put("Age", age);
        usermap.put("Gender", gender);
        usermap.put("cover", cover);
        usermap.put("User", current);
        usermap.put("Experienced", "mode");
        usermap.put("Job pdf",downloadUrl);
        usermap.put("Jobid", Jobid);

        Log.d(receivedData, "Something went wrong..");
        dc.collection("JOBsApply")
                .add(usermap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(JobsApplyActivity.this, "Applied Successfully!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                        Intent intent = new Intent(JobsApplyActivity.this, JobsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
