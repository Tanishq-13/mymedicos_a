package com.medical.my_medicos.activities.cme;

import static com.medical.my_medicos.list.subSpecialitiesData.subspecialities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.medical.my_medicos.activities.home.sidedrawer.extras.BottomSheetForChatWithUs;
import com.medical.my_medicos.activities.job.JobsActivity;
import com.medical.my_medicos.activities.job.PostJobActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostCmeActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String current = user.getPhoneNumber();
    String field3, field4;
    String selectedMode;
    EditText cmetitle, cmeorg, cmepresenter, cmevenu, virtuallink, cme_place;
    Button postcme;

    public FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseFirestore dc = FirebaseFirestore.getInstance();
    private Spinner subspecialitySpinner;
    private Spinner specialitySpinner;
    private Spinner modeSpinner;
    String subspecialities1;
    public DatabaseReference cmeref = db.getReference().child("CME's");
    private ProgressDialog progressDialog;
    private static final int MAX_CHARACTERS = 1000;
    private EditText etName, etClass, etPhoneNumber;
    private TextView btnDatePicker, btnTimePicker;
    private TextView tvDate, tvTime;
    private Calendar calendar;
    private SimpleDateFormat dateFormat, timeFormat;
    static final int REQ = 1;
    private Uri pdfData;
    TextView room, offlineroom;
    private DatabaseReference databasereference;
    private StorageReference storageReference;
    private TextView addPdf, uploadPdfBtn;
    String downloadUrl = null;
    LinearLayout cmeholderplace, cmevirtuallinkholder;
    private ProgressDialog pd;
    private String pdfName;
    private String currentuserSpeciality;
    private ArrayAdapter<CharSequence> specialityAdapter;
    private ArrayAdapter<CharSequence> subspecialityAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_cme);

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
                Intent i = new Intent(PostCmeActivity.this, CmeActivity.class);
                startActivity(i);
                finish();
            }
        });

        addPdf = findViewById(R.id.addPdf);
        room = findViewById(R.id.room);
        offlineroom = findViewById(R.id.offlineroom);
        room.setVisibility(View.GONE);
        offlineroom.setVisibility(View.GONE);
        String current = user.getPhoneNumber();
        Log.d("PhONE number", current);

        //..............
        databasereference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this);

        addPdf = findViewById(R.id.addPdf);

        uploadPdfBtn = findViewById(R.id.uploadpdfbtn);

        addPdf.setOnClickListener(view -> {
            openGallery();
        });
        uploadPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pdfData == null) {
                    Toast.makeText(PostCmeActivity.this, "Select a Document", Toast.LENGTH_SHORT).show();
                } else {
                    uploadPdf();
                }
            }
        });

        //..................

        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnTimePicker = findViewById(R.id.btnTimePicker);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        progressDialog = new ProgressDialog(this);

        specialitySpinner = findViewById(R.id.cmespeciality);
        subspecialitySpinner = findViewById(R.id.cmesubspeciality);
        modeSpinner = findViewById(R.id.cmemode);


        // For Speciality............
        specialityAdapter = ArrayAdapter.createFromResource(this,
                R.array.speciality, android.R.layout.simple_spinner_item);
        specialityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialitySpinner.setAdapter(specialityAdapter);
        //...........................

        // For Subspeciality..........
        subspecialityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        subspecialityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subspecialitySpinner.setAdapter(subspecialityAdapter);
        // Initially, hide the subspeciality spinner
        subspecialitySpinner.setVisibility(View.GONE);
        //............................

        specialitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Check if the selected speciality has subspecialities
                int specialityIndex = specialitySpinner.getSelectedItemPosition();
                if (specialityIndex >= 0 && specialityIndex < subspecialities.length && subspecialities[specialityIndex].length > 0) {
                    String[] subspecialityArray = subspecialities[specialityIndex];
                    subspecialityAdapter.clear();
                    subspecialityAdapter.add("Select Subspeciality");
                    for (String subspeciality : subspecialityArray) {
                        subspecialityAdapter.add(subspeciality);
                    }
                    // Show the subspeciality spinner
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
                    // Hide the subspeciality spinner
                    subspecialitySpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
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
                        Log.d("Something went wrong..", String.valueOf(areEqualIgnoreCase));
                        if ((current != null) && (field3 != null)) {
                            int r = current.compareTo(field3);
                            if (r == 0) {
                                field4 = ((String) dataMap.get("Name"));
                                currentuserSpeciality = ((String) dataMap.get("Interest"));
                                Log.d("Something went wrong..", field4);
                                cmeorg.setText(field4);
                            }
                        }

                    }
                } else {
                    // Handle the error
                    Toast.makeText(PostCmeActivity.this, "Error fetching data from Firebase Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // For Mode..............
        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(this,
                R.array.mode, android.R.layout.simple_spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);
        //.........................

        cmetitle = findViewById(R.id.cme_title);
        cmeorg = findViewById(R.id.cme_organiser);
        cmeorg.setEnabled(false);
        cmeorg.setTextColor(Color.parseColor("#80000000"));
        cmeorg.setBackgroundResource(R.drawable.rounded_edittext_background);
        cmepresenter = findViewById(R.id.cme_presenter);
        cmevenu = findViewById(R.id.cme_venu);
        virtuallink = findViewById(R.id.cme_virtuallink);
        cmevirtuallinkholder = findViewById(R.id.cmevirtuallinkholder);
        cme_place = findViewById(R.id.cme_place);
        cmeholderplace = findViewById(R.id.cmeholderplace);
        postcme = findViewById(R.id.post_btn);
        TextView charCount = findViewById(R.id.char_counter);

        cmevenu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int currentCount = charSequence.length();
                charCount.setText(currentCount + "/" + MAX_CHARACTERS);

                if (currentCount > MAX_CHARACTERS) {
                    charCount.setTextColor(Color.RED);
                    postcme.setEnabled(false);
                    Toast.makeText(PostCmeActivity.this, "Character limit exceeded (2000 characters max)", Toast.LENGTH_SHORT).show();
                } else {
                    charCount.setTextColor(Color.DKGRAY);
                    postcme.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        postcme.setEnabled(false);
        postcme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Context context = view.getContext();

                    if (isDataValid()) {
                        postCme(currentuserSpeciality);
                        Intent i = new Intent(context, CmeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);

                    } else {
                        Toast.makeText(PostCmeActivity.this, "Please fill in all mandatory fields", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        Spinner cmemodeSpinner = findViewById(R.id.cmemode);
        cmemodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedMode = cmemodeSpinner.getSelectedItem().toString();

                if (selectedMode.equals("Online")) {

                    virtuallink.setVisibility(View.VISIBLE);
                    cmevirtuallinkholder.setVisibility(View.VISIBLE);
                    room.setVisibility(View.VISIBLE);
                    offlineroom.setVisibility(View.GONE);
                    cmeholderplace.setVisibility(View.GONE);
                    cme_place.setVisibility(View.GONE);

                } else if (selectedMode.equals("Offline")) {

                    virtuallink.setVisibility(View.GONE);
                    cmevirtuallinkholder.setVisibility(View.GONE);
                    room.setVisibility(View.GONE);
                    offlineroom.setVisibility(View.VISIBLE);
                    cme_place.setVisibility(View.VISIBLE);
                    cmeholderplace.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
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

    private boolean isDataValid() {
        String title = cmetitle.getText().toString().trim();
        String organiser = cmeorg.getText().toString().trim();
        String presenter = cmepresenter.getText().toString().trim();
        String venu = cmevenu.getText().toString().trim();
        String date = tvDate.getText().toString().trim();
        String time = tvTime.getText().toString().trim();
        String selectedSpinnerItemSpeciality = specialitySpinner.getSelectedItem().toString().trim();
        String selectedSpinnerItemMode = modeSpinner.getSelectedItem().toString().trim();
        String[] presentersArray = presenter.split("\\s*,\\s*");
        String link = virtuallink.getText().toString().trim();
        String place = cme_place.getText().toString().trim();

        if (TextUtils.isEmpty(selectedSpinnerItemSpeciality) || selectedSpinnerItemSpeciality.equals(getString(R.string.select_speciality))) {
            Toast.makeText(this, "Select a Speciality", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(selectedSpinnerItemMode) || selectedSpinnerItemMode.equals(getString(R.string.select_mode))) {
            Toast.makeText(this, "Select a Mode", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(title) || title.length() > 50) {
            cmetitle.setError(TextUtils.isEmpty(title) ? "Title Required" : "Title must be 50 characters or less");
            return false;
        }
        cmetitle.setError(null);

        if (TextUtils.isEmpty(organiser)) {
            cmeorg.setError("Organizer Required");
            return false;
        }
        cmeorg.setError(null);

        if (TextUtils.isEmpty(presenter)) {
            cmepresenter.setError("Presenter Required");
            return false;
        }
        cmepresenter.setError(null);

        if (TextUtils.isEmpty(venu) || venu.length() > MAX_CHARACTERS) {
            cmevenu.setError(TextUtils.isEmpty(venu) ? "Location Required" : "Character limit exceeded");
            return false;
        }
        cmevenu.setError(null);

        if (TextUtils.isEmpty(tvDate.getText())) {
            tvDate.setError("Select Date");
            return false;
        }

        if (TextUtils.isEmpty(tvTime.getText())) {
            tvTime.setError("Select Time");
            return false;
        }

        if (selectedMode.equals("Offline")) {
            if (TextUtils.isEmpty(cme_place.getText().toString().trim()) || !place.matches("https://maps.app.goo.gl/.*")) {
                cme_place.setError("Invalid Location");
                return false;
            }
        } else if (selectedMode.equals("Online")) {
            if (!link.matches("https://us04web\\.zoom\\.us/.*") || TextUtils.isEmpty(virtuallink.getText().toString().trim())) {
                virtuallink.setError("Invalid link format");
                return false;
            }
        }

        virtuallink.setError(null);
        cme_place.setError(null);

        return !TextUtils.isEmpty(title)
                && !TextUtils.isEmpty(organiser)
                && !TextUtils.isEmpty(presenter)
                && !TextUtils.isEmpty(venu)
                && !TextUtils.isEmpty(date)
                && !TextUtils.isEmpty(time)
                && !TextUtils.isEmpty(selectedSpinnerItemMode)
                && !TextUtils.isEmpty(selectedSpinnerItemSpeciality);
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
                while (!uriTask.isComplete()) ;
                Uri uri = uriTask.getResult();
                uploadData(String.valueOf(uri));
                downloadUrl = String.valueOf(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(PostCmeActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PostCmeActivity.this, "Pdf Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(PostCmeActivity.this, "Failed to upload!", Toast.LENGTH_SHORT).show();
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

            if (pdfData.toString().startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = PostCmeActivity.this.getContentResolver().query(pdfData, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (pdfData.toString().startsWith("file://")) {
                pdfName = new File(pdfData.toString()).getName();
            }
            addPdf.setText(pdfName);
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String selectedDate = dateFormat.format(calendar.getTime());
                        tvDate.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        String selectedTime = timeFormat.format(calendar.getTime());
                        tvTime.setText(selectedTime);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void postCme(String currentuserSpeciality) {
        String title = cmetitle.getText().toString().trim();
        String organiser = cmeorg.getText().toString().trim();
        String presenter = cmepresenter.getText().toString().trim();
        String[] presentersArray = presenter.split("\\s*,\\s*");
        String venu = cmevenu.getText().toString().trim();
        String link = virtuallink.getText().toString().trim();
        String place = cme_place.getText().toString().trim();

        Spinner cmemodeSpinner = findViewById(R.id.cmemode);
        String mode = cmemodeSpinner.getSelectedItem().toString();

        Spinner cmespecialitySpinner = findViewById(R.id.cmespeciality);
        String speciality = cmespecialitySpinner.getSelectedItem().toString();

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDate = currentDateTime.format(dateFormatter);
        String formattedTime = currentDateTime.format(timeFormatter);

        String selectedDate = tvDate.getText().toString();
        String selectedTime = tvTime.getText().toString();

        HashMap<String, Object> usermap = new HashMap<>();
        usermap.put("CME Title", title);
        usermap.put("CME Organiser", organiser);
        usermap.put("CME Presenter", Arrays.asList(presentersArray));
        usermap.put("CME Venue", venu);
        usermap.put("Virtual Link", link);
        usermap.put("CME Place", place);
        usermap.put("User", current);
        usermap.put("Cme pdf", downloadUrl);
        usermap.put("Date", formattedDate);
        usermap.put("Time", formattedTime);
        usermap.put("Mode", mode);
        usermap.put("Speciality", speciality);
        usermap.put("SubSpeciality", subspecialities1);
        usermap.put("Selected Date", selectedDate);
        usermap.put("Selected Time", selectedTime);
        usermap.put("endtime", null);

        progressDialog.setMessage("Posting...");
        progressDialog.show();

        DatabaseReference newPostRef = cmeref.push();
        String generatedDocId = newPostRef.getKey();
        newPostRef.setValue(usermap)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        usermap.put("documentId", generatedDocId);
                        dc.collection("CME").document(generatedDocId).set(usermap)
                                .addOnCompleteListener(subTask -> {
                                    if (subTask.isSuccessful()) {
                                        Toast.makeText(PostCmeActivity.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                                        Log.d("token2", speciality);
                                    } else {
                                        Toast.makeText(PostCmeActivity.this, "Task Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(PostCmeActivity.this, "Task Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
