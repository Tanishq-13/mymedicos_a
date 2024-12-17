package com.medical.my_medicos.activities.ug;

import static com.medical.my_medicos.list.subSpecialitiesData.subspecialities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.sidedrawer.extras.BottomSheetForChatWithUs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UgPostInsiderActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String current = user.getPhoneNumber();
    static final int REQ = 1;
    private Uri pdfData;
    private String pdfName;
    String field3, field4;
    String downloadUrl;
    EditText ugtitle, ugorg, ugvenu;
    Button postug;
    public FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseFirestore dc = FirebaseFirestore.getInstance();
    private Spinner subspecialitySpinner;
    private Spinner specialitySpinner, ugtypeSpinner, ugcitySpinner;
    String subspecialities1;
    public DatabaseReference ugref = db.getReference().child("UG's");
    private ProgressDialog progressDialog;
    private static final int MAX_CHARACTERS = 1000;
    private Calendar calendar;
    private ProgressDialog pd;
    private SimpleDateFormat dateFormat, timeFormat;
    static final int REQUEST_STORAGE_PERMISSION = 1;
    static final int REQUEST_STORAGE_ACCESS = 2;
    private ArrayAdapter<CharSequence> specialityAdapter;
    private ArrayAdapter<CharSequence> ugAdapter;
    private ArrayAdapter<CharSequence> subspecialityAdapter;
    private CardView btnAccessStorage;
    private Button uploadpdfbtnjobs;
    private TextView addPdf, uploadPdfBtn;
    private DatabaseReference databasereference;
    private StorageReference storageReference;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ug_post_insider);
        addPdf = findViewById(R.id.addPdfjobs);

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
                Intent i = new Intent(UgPostInsiderActivity.this, UgExamActivity.class);
                startActivity(i);
                finish();
            }
        });

        TextView whatsappLayout = findViewById(R.id.connectwithus);
        whatsappLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheet();
            }
        });

        databasereference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        addPdf = findViewById(R.id.addPdfug);

        uploadPdfBtn = findViewById(R.id.uploadpdfbtnug);

        addPdf.setOnClickListener(view -> {
            openGallery();
        });
        pd = new ProgressDialog(this);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault());

        progressDialog = new ProgressDialog(this);

        specialitySpinner = findViewById(R.id.ugspeciality);
        subspecialitySpinner = findViewById(R.id.ugsubspeciality);
        ugtypeSpinner = findViewById(R.id.ugtype);
        ugAdapter = ArrayAdapter.createFromResource(this,
                R.array.ugtype, android.R.layout.simple_spinner_item);
        ugAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ugtypeSpinner.setAdapter(ugAdapter);
        ugcitySpinner = findViewById(R.id.ugcity);
        ArrayAdapter<CharSequence> myadapter = ArrayAdapter.createFromResource(this,
                R.array.indian_cities, android.R.layout.simple_spinner_item);
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ugcitySpinner.setAdapter(myadapter);

        specialityAdapter = ArrayAdapter.createFromResource(this,
                R.array.speciality, android.R.layout.simple_spinner_item);
        specialityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialitySpinner.setAdapter(specialityAdapter);

        subspecialityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        subspecialityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subspecialitySpinner.setAdapter(subspecialityAdapter);
        subspecialitySpinner.setVisibility(View.GONE);
        specialitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                int specialityIndex = specialitySpinner.getSelectedItemPosition();
                if (specialityIndex >= 0 && specialityIndex < subspecialities.length && subspecialities[specialityIndex].length > 0) {
                    String[] subspecialityArray = subspecialities[specialityIndex];
                    subspecialityAdapter.clear();
                    subspecialityAdapter.add("Select Subspeciality");
                    for (String subspeciality : subspecialityArray) {
                        subspecialityAdapter.add(subspeciality);
                    }
                    subspecialitySpinner.setVisibility(View.VISIBLE);
                    subspecialitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            subspecialities1 = subspecialitySpinner.getSelectedItem().toString();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } else {
                    subspecialitySpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        dc.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        field3 = ((String) dataMap.get("Phone Number"));
                        boolean areEqualIgnoreCase = current.equalsIgnoreCase(field3);
                        Log.d("vivek", String.valueOf(areEqualIgnoreCase));
                        int r = current.compareTo(field3);
                        if (r == 0) {
                            field4 = ((String) dataMap.get("Name"));
                            Log.d("veefe", field4);
                            ugorg.setText(field4);
                        }

                    }
                } else {
                    Toast.makeText(UgPostInsiderActivity.this, "Error fetching data from Firebase Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ugtitle = findViewById(R.id.ug_title);
        ugorg = findViewById(R.id.ug_organiser);

        ugorg.setEnabled(false);
        ugorg.setTextColor(Color.parseColor("#80000000"));
        ugorg.setBackgroundResource(R.drawable.rounded_edittext_background);
        ugvenu = findViewById(R.id.ug_venu);

        postug = findViewById(R.id.post_ug_btn);
        uploadPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pdfData == null) {
                    Toast.makeText(UgPostInsiderActivity.this, "Select a Document", Toast.LENGTH_SHORT).show();
                } else {
                    uploadPdf();
                }
            }
        });

        TextView charCount = findViewById(R.id.char_counter);
        ugvenu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int currentCount = charSequence.length();
                charCount.setText(currentCount + "/" + MAX_CHARACTERS);

                if (currentCount > MAX_CHARACTERS) {
                    charCount.setTextColor(Color.RED);
                    postug.setEnabled(false);
                    Toast.makeText(UgPostInsiderActivity.this, "Character limit exceeded (2000 characters max)", Toast.LENGTH_SHORT).show();
                } else {
                    charCount.setTextColor(Color.DKGRAY);
                    postug.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        postug.setEnabled(false);

        postug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    postUG();
                }
            }
        });
    }

    private void openBottomSheet() {
        BottomSheetDialogFragment bottomSheetFragment = new BottomSheetForChatWithUs();

        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void postUG() {
        String title = ugtitle.getText().toString().trim();
        String organiser = ugorg.getText().toString().trim();
        String venu = ugvenu.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            ugtitle.setError("Title Required");
            return;
        }
        if (TextUtils.isEmpty(organiser)) {
            ugorg.setError("Organizer Required");
            return;
        }
        if (TextUtils.isEmpty(venu)) {
            ugvenu.setError("Email Required");
            return;
        }

        if (venu.length() > MAX_CHARACTERS) {
            ugvenu.setError("Character limit exceeded");
            return;
        } else {
            ugvenu.setError(null);
        }

        Spinner ugspecialitySpinner = findViewById(R.id.ugspeciality);
        String speciality = ugspecialitySpinner.getSelectedItem().toString();
        String type = ugtypeSpinner.getSelectedItem().toString();
        String city = ugcitySpinner.getSelectedItem().toString();

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDate = currentDateTime.format(dateFormatter);
        String formattedTime = currentDateTime.format(timeFormatter);

        // Add this line to initialize downloads count to 0
        int initialDownloads = 0;

        HashMap<String, Object> usermap = new HashMap<>();
        usermap.put("UG Title", title);
        usermap.put("UG Organiser", field4);
        usermap.put("UG Description", venu);
        usermap.put("User", current);
        usermap.put("Date", formattedDate);
        usermap.put("Time", formattedTime);
        usermap.put("Speciality", speciality);
        usermap.put("SubSpeciality", subspecialities1);
        usermap.put("Type", type);
        usermap.put("City", city);
        usermap.put("pdf", downloadUrl);
        // Add this line to set the initial Downloads count
        usermap.put("Downloads", initialDownloads);

        progressDialog.setMessage("Posting...");
        progressDialog.show();

        ugref.push().setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();

                if (task.isSuccessful()) {
                    dc.collection("UG").add(usermap);
                    Toast.makeText(UgPostInsiderActivity.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UgPostInsiderActivity.this, "Task Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void incrementDownloadsCount() {
        ugref.orderByChild("pdf").equalTo(downloadUrl).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        int currentDownloads = dataSnapshot.child("Downloads").getValue(Integer.class);
                        currentDownloads++;
                        dataSnapshot.getRef().child("Downloads").setValue(currentDownloads);
                    }
                }
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
                Toast.makeText(UgPostInsiderActivity.this, "Pdf Uploaded Successfully", Toast.LENGTH_SHORT).show();
                ugtitle.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UgPostInsiderActivity.this, "Failed to upload!", Toast.LENGTH_SHORT).show();
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
                    cursor = UgPostInsiderActivity.this.getContentResolver().query(pdfData,null,null,null,null);
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
    private void uploadPdf() {
        pd.setTitle("Please wait..");
        pd.setMessage("Uploading Pdf..");
        pd.show();

        StorageReference reference = storageReference.child("pdf/" + pdfName + "-" + System.currentTimeMillis() + ".pdf");

        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri uri = task.getResult();
                        downloadUrl = String.valueOf(uri);
                        uploadData(String.valueOf(uri));
                        pd.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UgPostInsiderActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
