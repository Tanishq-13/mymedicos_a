package com.medical.my_medicos.activities.job;

import static android.content.ContentValues.TAG;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.medical.my_medicos.activities.cme.CmeActivity;
import com.medical.my_medicos.activities.guide.CmeGuideActivity;
import com.medical.my_medicos.activities.guide.JobGuideActivity;
import com.medical.my_medicos.activities.home.sidedrawer.HomeSideActivity;
import com.medical.my_medicos.activities.job.category.JobsApplyActivity2;
import com.medical.my_medicos.activities.job.category.JobsPostedYou;
import com.medical.my_medicos.adapter.job.MyAdapter;
import com.medical.my_medicos.adapter.job.MyAdapter6;
import com.medical.my_medicos.adapter.job.MyAdapter7;
import com.medical.my_medicos.R;
import com.medical.my_medicos.adapter.job.items.jobitem;
import com.medical.my_medicos.adapter.job.items.jobitem1;
import com.medical.my_medicos.adapter.job.items.jobitem2;
import com.medical.my_medicos.list.subSpecialitiesData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.CirclePromptFocal;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;


public class JobsActivity extends AppCompatActivity {

    RecyclerView recyclerView1,recommendedforyouonly;
    private final String[][] subspecialities = subSpecialitiesData.subspecialities;
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Toolbar toolbar;
    TextView interestjob,interestjob2;
    ImageView cart_icon;
    String Speciality;
    MyAdapter adapterjob;
    CardView cardjobs;
    CardView applied,managejobs;

    private boolean dataLoadedJobs = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

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

        toolbar = findViewById(R.id.jobstoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        floatingActionButton=findViewById(R.id.floatingActionButton);
        interestjob = findViewById(R.id.interest);
        interestjob2 = findViewById(R.id.interest2);
        cardjobs = findViewById(R.id.cardjobs);
        managejobs=findViewById(R.id.managejobs);
        managejobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JobsActivity.this, JobsPostedYou.class);
                startActivity(i);
            }
        });

        applied = findViewById(R.id.applied);
        applied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JobsActivity.this, JobsApplyActivity2.class);
                startActivity(i);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(JobsActivity.this, PostJobActivity.class);
                startActivity(i);

            }
        });

        String[] specialitiesArray = getResources().getStringArray(R.array.specialityjobs);
        List<String> specialitiesList = Arrays.asList(specialitiesArray);
        List<jobitem2> joblist2 = new ArrayList<jobitem2>();
        for (String subspeciality : specialitiesList) {
            joblist2.add(new jobitem2(subspeciality));
        }

        recyclerView2 = findViewById(R.id.recyclerview6);
        int spanCount = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
        recyclerView2.setLayoutManager(gridLayoutManager);
        recyclerView2.setAdapter(new MyAdapter7(this, joblist2));

        //.....Recommended for you............

        List<jobitem> joblist = new ArrayList<jobitem>();
        recommendedforyouonly = findViewById(R.id.recommendedforyouonly);
        recommendedforyouonly.setLayoutManager(new LinearLayoutManager
                (JobsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        FirebaseFirestore dc = FirebaseFirestore.getInstance();
        dc.collection("JOB")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            FirebaseUser user = auth.getCurrentUser();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map<String, Object> dataMap = document.getData();
                                String speciality = ((String) dataMap.get("Speciality"));
                                String Organiser = ((String) dataMap.get("JOB Organiser"));
                                String Location = ((String) dataMap.get("Location"));
                                String date = ((String) dataMap.get("date"));
                                String Title = ((String) dataMap.get("JOB Title"));
                                String Category = ((String) dataMap.get("Job type"));
                                String documentid = ((String) dataMap.get("documentId"));
                                Log.d("Error in Speciality",speciality);
                                String User=((String) dataMap.get("User"));
                                Log.d("user2",user.getPhoneNumber());


                                if ((speciality!=null)&&(Speciality!=null)) {
                                    int b=(user.getPhoneNumber()).compareTo(User);
                                    int a = Speciality.compareTo(speciality);
                                    Log.d("phonenumber", String.valueOf(b));
                                    if ((a == 0)&&(b != 0)) {
                                        Log.d("Speciality",String.valueOf(a));
                                        Log.d("phonenumber", String.valueOf(b));

                                        jobitem c = new jobitem(speciality, Organiser, Location, date, Title, Category, documentid,User);
                                        joblist.add(c);
                                    }
                                }
                                Log.d("speciality2", speciality);

                                recommendedforyouonly.setLayoutManager(new LinearLayoutManager(JobsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                adapterjob = new MyAdapter(JobsActivity.this, joblist); // Pass the joblist to the adapter
                                recommendedforyouonly.setAdapter(adapterjob);
                            }
                            Log.d("Something went wrong", joblist.toString());
                            if (joblist.isEmpty()){
                                cardjobs.setVisibility(View.VISIBLE);
                                TextView nocontent = findViewById(R.id.descriptionTextView);
                            }
                        }
                        else {
                        }

                    }
                });

        //.....Recently Added..........

        recyclerView1 = findViewById(R.id.recyclerview5);
        List<jobitem1> recentjoblist = new ArrayList<jobitem1>();

        FirebaseFirestore recent = FirebaseFirestore.getInstance();
        recent.collection("JOB")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task ) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map<String, Object> dataMap = document.getData();
                                String speciality = "";
                                String Organiser = (String) dataMap.get("JOB Organiser");
                                String Location = (String) dataMap.get("Location");
                                String date = (String) dataMap.get("date");
                                String user = (String) dataMap.get("User");
                                String Category=((String) dataMap.get("Job type"));
                                String Title=((String) dataMap.get("JOB Title"));
                                String documentid=((String) dataMap.get("documentId"));

                                jobitem1 c = new jobitem1(speciality, Organiser, Location, date, user,Title , Category,documentid);
                                recentjoblist.add(c);
                                recyclerView1.setLayoutManager(new LinearLayoutManager(getApplication(),LinearLayoutManager.HORIZONTAL, false));
                                recyclerView1.setAdapter( new MyAdapter6(getApplication(),recentjoblist));
                                Log.d("speciality2", speciality);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        if (!dataLoadedJobs) {
            fetchjobdata();
            fetchUserJobData();
        }
    }

    private void fetchUserJobData() {
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
                                            String userInterest = (String) dataMap.get("Interest");
                                            String userInterest2 = (String) dataMap.get("Interest2");

                                            Preferences preferences = Preferences.userRoot();
                                            preferences.put("interest", userInterest);
                                            preferences.put("interest2", userInterest2);

                                            fetchjobdata();
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
    private void fetchjobdata() {
        Preferences preferences = Preferences.userRoot();
        if (preferences.get("username", null) != null) {
            System.out.println("Key '" + "username" + "' exists in preferences.");
            String username = preferences.get("username", null);
            Log.d("usernaem", username);
        }
        String interest = preferences.get("interest", "");
        String interest2 = preferences.get("interest2", "");
        interestjob.setText(interest);
        interestjob2.setText(interest2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.jobs_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.drawer_jobsposted) {
            Intent i = new Intent(this, JobsPostedYou.class);
            startActivity(i);
        } else if (itemId == R.id.drawer_jobsapplied) {
            Intent i = new Intent(this, JobsApplyActivity2.class);
            startActivity(i);
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}