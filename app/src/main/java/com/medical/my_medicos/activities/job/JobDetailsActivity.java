package com.medical.my_medicos.activities.job;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentReference;
import com.medical.my_medicos.activities.job.fragments.TermsandConditionDialogueFragment;
import com.medical.my_medicos.adapter.job.Adapter8;
import com.medical.my_medicos.adapter.job.MyAdapter6;
import com.medical.my_medicos.R;
import com.medical.my_medicos.adapter.job.items.jobitem1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.adapter.ug.MyAdapter8;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JobDetailsActivity extends AppCompatActivity {
    Button shareButton;
    RecyclerView recyclerView1;
    MyAdapter8 adapter1;
    FloatingActionButton floatingActionButton;
    private ViewPager2 pager;
    private TabLayout tabLayout;
    RecyclerView recyclerView2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button sharebtnforjobs;
    String speciality,Organiser,Location;
    String receivedData,documentid;

    String jobtitle,jobdescription;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String current=user.getPhoneNumber();
    Toolbar toolbar;
    String documentid1;
    String pdf = null;
    TextView jobTitleTextView,companyNameTextView,locationTextView,salaryEditText,organizername,dateofpost,openingsEditText,timelinedurationwillcomehere,authorSpecialityTextView,authorSubSpecialityTextView,jobDescriptionContentTextView,jobtype,durationforjob,salarytypeTextView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_jobs);
        LinearLayout downloadPdfButton = findViewById(R.id.downloadPdfButton);

        showLoadForJob();

        String documentId = getIntent().getStringExtra("documentid");
        String documentId2;

        if (documentId != null && !documentId.isEmpty()) {
            fetchJobDetails(documentId);
        } else {
            // Handle the case when documentId is null or empty
            Log.e(TAG, "DocumentId is null or empty");
            documentId2 = getIntent().getStringExtra("jobId");
            Log.e(documentId2,"print");
            fetchJobDetails2(documentId2);
        }

        // Find views by their IDs
        jobTitleTextView = findViewById(R.id.jobTitleTextView);
        companyNameTextView = findViewById(R.id.companyNameTextView);
        locationTextView = findViewById(R.id.locationTextView);
        salarytypeTextView = findViewById(R.id.salarytype);
        salaryEditText = findViewById(R.id.salaryEditText);
        organizername = findViewById(R.id.organizername);
        dateofpost = findViewById(R.id.jobposteddate);
        openingsEditText = findViewById(R.id.openingsEditText);
        durationforjob = findViewById(R.id.duration);
        timelinedurationwillcomehere = findViewById(R.id.timelinedurationwillcomehere);
        authorSpecialityTextView = findViewById(R.id.authorSpecialityTextView);
        authorSubSpecialityTextView = findViewById(R.id.authorSubSpecialityTextView);
        jobDescriptionContentTextView = findViewById(R.id.jobDescriptionContentTextView);
        jobtype = findViewById(R.id.Job_type);
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("user")&&(intent.hasExtra("documentid"))) {
            // Retrieve the extra data
            receivedData = intent.getStringExtra("user");
            documentid=intent.getStringExtra("documentid");
            documentid1=intent.getStringExtra("documentid");

        }
        Toolbar toolbar = findViewById(R.id.jobsinsidertoolbar);

        // Set the support action bar
        setSupportActionBar(toolbar);

        // Set the navigation icon and listener
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_bk);
        }

        // Set the click listener for the navigation icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button apply=findViewById(R.id.applyButton);
        Button alreadyapply=findViewById(R.id.Alreadyapplied);
        alreadyapply.setVisibility(View.GONE);
        LinearLayout applylinear=findViewById(R.id.applylinear);

        Button applycant=findViewById(R.id.applycant);
        apply.setVisibility(View.GONE);
        applycant.setVisibility(View.GONE);
        int r;
        if (receivedData != null && current != null) {
            r = receivedData.compareTo(current);
            Log.d("received data1", receivedData);
        } else {
            r = -1;
            Log.e(TAG, "receivedData or current is null");
        }
        if (r==0){
            apply.setVisibility(View.GONE);
            applycant.setVisibility(View.VISIBLE);
            applycant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent j=new Intent(context, JobsApplicantActivty.class);
                    j.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    j.putExtra("user",receivedData);
                    j.putExtra("documentid",documentid);
                    context.startActivity(j);
                }
            });

        }

        else{
            apply.setVisibility(View.VISIBLE);
            applycant.setVisibility(View.GONE);
            apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TermsandConditionDialogueFragment dialog = new TermsandConditionDialogueFragment(new TermsandConditionDialogueFragment.OnTermsAcceptedListener() {
                        @Override
                        public void onTermsAccepted() {
                            Context context = view.getContext();
                            Intent j = new Intent(context, JobsApplyActivity.class);
                            j.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            j.putExtra("user", receivedData);
                            j.putExtra("documentid", documentid);
                            context.startActivity(j);
                        }
                    });
                    dialog.show(getSupportFragmentManager(), "TermsAndConditionsDialog");
                }
            });

        }

        FirebaseFirestore dc = FirebaseFirestore.getInstance();
        dc.collection("JOB")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task ) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map<String, Object> dataMap = document.getData();
                                String user = ((String) dataMap.get("documentId"));

                                int r=0;
                                if (documentid1!=null) {
                                    r = documentid1.compareTo(user);
                                    Log.d("Something went wrong", String.valueOf(r));
                                }
                                if (r==0) {

                                    jobTitleTextView.setText((String) dataMap.get("JOB Title"));
                                    organizername.setText((String) dataMap.get("JOB Organiser"));
                                    salaryEditText.setText((String) dataMap.get("Job Salary"));
                                    jobDescriptionContentTextView.setText((String) dataMap.get("JOB Description"));
                                    openingsEditText.setText((String) dataMap.get("JOB Opening"));
                                    durationforjob.setText((String) dataMap.get("Job Duration"));
                                    timelinedurationwillcomehere.setText((String) dataMap.get("Duration Timeline"));
                                    dateofpost.setText((String) dataMap.get("date"));
                                    locationTextView.setText((String) dataMap.get("Location"));
                                    salarytypeTextView.setText((String) dataMap.get("Salary type"));
                                    authorSpecialityTextView.setText((String) dataMap.get("Speciality"));
                                    String subSpeciality = (String) dataMap.get("SubSpeciality");
                                    if (subSpeciality != null && !subSpeciality.isEmpty()) {
                                        authorSubSpecialityTextView.setVisibility(View.VISIBLE);
                                        authorSubSpecialityTextView.setText(subSpeciality);
                                    } else {
                                        authorSubSpecialityTextView.setVisibility(View.GONE);
                                    }
                                    companyNameTextView.setText((String) dataMap.get("Hospital"));
                                    jobtype.setText((String) dataMap.get("Job type"));
                                    pdf = ((String) dataMap.get("Jobs pdf"));
                                    if (pdf == null) {
                                        downloadPdfButton.setVisibility(View.GONE);
                                    } else {
                                        downloadPdfButton.setVisibility(View.VISIBLE);
                                        downloadPdfButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                downloadPdf(pdf);
                                            }
                                        });
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        sharebtnforjobs = findViewById(R.id.sharebtnforjobs);

        sharebtnforjobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createlink(current, documentid, jobtitle,jobdescription);
            }
        });

        handleDeepLink();
        setSystemBarColors();
    }

    private void showLoadForJob() {
        RelativeLayout loaderForJobs = findViewById(R.id.loaderforjobs);
        loaderForJobs.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loaderForJobs.setVisibility(View.GONE);
            }
        }, 2000);
    }
    public void createlink(String custid, String jobId,String jobtitle, String jobdescription){
        Log.e("main","create link");

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.mymedicos.in/jobdetails?custid=" + custid + "&jobId=" + jobId))
                .setDomainUriPrefix("https://app.mymedicos.in")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.e("main"," Long refer "+ dynamicLink.getUri());

        createreferlink(custid, jobId);
    }
    public void createreferlink(String custid, String jobId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("JOB").document(jobId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String jobTitle = documentSnapshot.getString("JOB Title");
                String jobHospital = documentSnapshot.getString("Hospital");
                String jobLocation = documentSnapshot.getString("Location");
                String jobDescription = documentSnapshot.getString("JOB Description");

                // Attempt to URL encode jobTitle and jobDescription
                String encodedJobTitle = encode(jobTitle);
                String encodedJobDescription = encode(jobDescription);

                String sharelinktext = "Hey Candidate,\nCheckout this new job role of " + jobTitle + " \uD83E\uDE7A" + " at " +
                        jobHospital + " in " +
                        jobLocation + " :\n\n" +
                        "https://app.mymedicos.in/?" +
                        "link=http://www.mymedicos.in/jobdetails?jobId=" + jobId +
                        "&st=" + encodedJobTitle +
                        "&sd=" + encodedJobDescription +
                        "&apn=" + getPackageName() +
                        "&si=" + "https://res.cloudinary.com/dlgrxj8fp/image/upload/v1709416117/mwkegbnreoldjn4lksnn.png";

                Log.e("Job Detailed Activity", "Sharelink - " + sharelinktext);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, sharelinktext.toString());
                intent.setType("text/plain");
                startActivity(intent);
            } else {
                Log.e(TAG, "No such document with documentId: " + jobId);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching job details for documentId: " + jobId, e);
        });
    }

    private String encode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return URLEncoder.encode(s);
        }
    }
    //....
    private void handleDeepLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String jobId = deepLink.getQueryParameter("jobId");
                            Intent intent = getIntent();
                            intent.putExtra("jobId", jobId);
                            setIntent(intent);
                        }
                    }
                })
                .addOnFailureListener(this, e -> Log.w("DeepLink", "getDynamicLink:onFailure", e));
    }
    private void fetchJobDetails(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("JOB").document(documentId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                String jobTitle = documentSnapshot.getString("JOB Title");
                String organiser = documentSnapshot.getString("JOB Organiser");
                String salary = documentSnapshot.getString("Job Salary");
                String jobDescription = documentSnapshot.getString("JOB Description");
                String openings = documentSnapshot.getString("JOB Opening");
                String roleduration = documentSnapshot.getString("Job Duration");
                String time = documentSnapshot.getString("Duration Timeline");
                String date = documentSnapshot.getString("date");
                String location = documentSnapshot.getString("Location");
                String salarytype = documentSnapshot.getString("Salary type");
                String speciality = documentSnapshot.getString("Speciality");
                String subspeciality = documentSnapshot.getString("SubSpeciality");
                String hospital = documentSnapshot.getString("Hospital");
                String type = documentSnapshot.getString("Job type");

                updateUI(jobTitle, hospital, jobDescription,  location,  salary, organiser, date, openings, time, speciality, subspeciality, type, roleduration, salarytype);

            } else {
                Log.e(TAG, "No such document with documentId: " + documentId);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching job details for documentId: " + documentId, e);
        });
    }
    private void fetchJobDetails2(String documentId2) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("JOB").document(documentId2);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                String jobTitle = documentSnapshot.getString("JOB Title");
                String organiser = documentSnapshot.getString("JOB Organiser");
                String salary = documentSnapshot.getString("Job Salary");
                String jobDescription = documentSnapshot.getString("JOB Description");
                String openings = documentSnapshot.getString("JOB Opening");
                String roleduration = documentSnapshot.getString("Job Duration");
                String time = documentSnapshot.getString("Duration Timeline");
                String date = documentSnapshot.getString("date");
                String location = documentSnapshot.getString("Location");
                String salarytype = documentSnapshot.getString("Salary type");
                String speciality = documentSnapshot.getString("Speciality");
                String subspeciality = documentSnapshot.getString("SubSpeciality");
                String hospital = documentSnapshot.getString("Hospital");
                String type = documentSnapshot.getString("Job type");

                updateUI(jobTitle, hospital, jobDescription,  location,  salary, organiser, date, openings, time, speciality, subspeciality, type, roleduration, salarytype);

            } else {
                Log.e(TAG, "No such document with documentId: " + documentId2);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching job details for documentId: " + documentId2, e);
        });
    }
    private void updateUI(String jobTitle, String hospital, String jobDescription, String location, String salary,String organiser,String date,String openings,String time,String speciality,String subspeciality,String type,String roleduration,String salarytype) {

        jobTitleTextView.setText(jobTitle);
        companyNameTextView.setText(hospital);
        jobDescriptionContentTextView.setText(jobDescription);
        locationTextView.setText(location);
        salarytypeTextView.setText(salarytype);
        salaryEditText.setText(salary);
        organizername.setText(organiser);
        dateofpost.setText(date);
        openingsEditText.setText(openings);
        timelinedurationwillcomehere.setText(time);
        authorSpecialityTextView.setText(speciality);
        authorSubSpecialityTextView.setText(subspeciality);
        jobtype.setText(type);
        durationforjob.setText(roleduration);

    }

    public void downloadPdf(String pdfUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfUrl));
        request.setTitle("Downloading PDF");
        request.setDescription("Please wait...");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "your_pdf_filename.pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
        }
        Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
    }

    private void setSystemBarColors() {
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
    }
}