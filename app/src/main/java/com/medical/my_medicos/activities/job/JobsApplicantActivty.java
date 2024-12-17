package com.medical.my_medicos.activities.job;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.adapter.job.AdapterforApplicants;
import com.medical.my_medicos.adapter.job.items.jobitem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JobsApplicantActivty extends AppCompatActivity {
    List<jobitem> locumJobList = new ArrayList<jobitem>();
    RecyclerView recyclerView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String current=user.getPhoneNumber();
    String receivedData,documentid;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applucant_jobs);

        Toolbar toolbar = findViewById(R.id.applicanttoolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.arrowbackforappbar);
        }

        recyclerView=findViewById(R.id.recyclerviewapplycant);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user")) {
            receivedData = intent.getStringExtra("user");
            documentid=intent.getStringExtra("documentid");
        }
        fetchdata(documentid);

        FirebaseFirestore dc = FirebaseFirestore.getInstance();
        dc.collection("JOB")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> dataMap = document.getData();
                                String Category = (String) dataMap.get("Job type");
                                    String speciality = (String) dataMap.get("Speciality");
                                    String user = (String) dataMap.get("User");
                                    String Documentid = (String) dataMap.get("documentId");
                                    Log.d("title",speciality);
                            }
                        } else {
                            Log.d("LocumFragment", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void fetchdata(String documentid ){
        FirebaseFirestore dc = FirebaseFirestore.getInstance();

        dc.collection("JOBsApply")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> dataMap = document.getData();
                                String Category="";
                                String Organiser = (String) dataMap.get("Experienced");
                                String Location = (String) dataMap.get("cover");
                                String date = (String) dataMap.get("Age");
                                String speciality ="";
                                String pdf=((String) dataMap.get("Job pdf"));
                                String user = (String) dataMap.get("User");
                                String Title = (String) dataMap.get("Full name");
                                String Documentid = (String) dataMap.get("Jobid");
                                int r=Documentid.compareTo(documentid);
                                Log.d("documentid",documentid);
                                Log.d("Documentid",Documentid);


                                if (r==0) {
                                    jobitem c = new jobitem(user, Organiser, Location, date, Title, pdf,Documentid,user);
                                    locumJobList.add(c);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false));
                                    recyclerView.setAdapter(new AdapterforApplicants(getApplication(), locumJobList));
                                }
                            }
                        } else {
                            Log.d("LocumFragment", "Error getting documents: ", task.getException());
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
