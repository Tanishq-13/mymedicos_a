package com.medical.my_medicos.activities.university.activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.guide.UgGuideActivity;
import com.medical.my_medicos.activities.guide.UniversityGuidesActivity;
import com.medical.my_medicos.activities.ug.UgExamActivity;
import com.medical.my_medicos.activities.university.adapters.StatesAdapter;
import com.medical.my_medicos.activities.university.adapters.UniversitiesAdapter;
import com.medical.my_medicos.activities.university.adapters.UpdatesAdapter;
import com.medical.my_medicos.activities.university.model.States;
import com.medical.my_medicos.activities.university.model.Universities;
import com.medical.my_medicos.activities.university.model.Updates;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.databinding.ActivityUniversityupdatesBinding;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class UniversityActivity extends AppCompatActivity {
    ActivityUniversityupdatesBinding binding;
    UniversitiesAdapter universitiesAdapter;
    ArrayList<Universities> universities;
    ArrayList<States> statesofindia;
    StatesAdapter statesAdapter;
    UpdatesAdapter updateAdapter;
    ArrayList<Updates> updates;
    Toolbar toolbar;

    LinearLayout totheguide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUniversityupdatesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.universitytoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        totheguide = findViewById(R.id.totheguide);
        totheguide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UniversityActivity.this, UniversityGuidesActivity.class);
                startActivity(i);
            }
        });

        initStates();
        initSliderUpdates();
    }

    private void initSliderUpdates() {
        getUpdatesSlider();
    }

    void initStates() {
        statesofindia = new ArrayList<>();
        statesAdapter = new StatesAdapter(this, statesofindia);

        getStates();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.statesList.setLayoutManager(layoutManager);
        binding.statesList.setAdapter(statesAdapter);
    }

    void getStates() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Updates")
                .document("States")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ArrayList<String> statesArray = (ArrayList<String>) document.get("data");
                                if (statesArray != null) {
                                    for (String stateName : statesArray) {
                                        States state = new States(stateName);
                                        statesofindia.add(state);
                                    }
                                    statesAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    void getUpdatesSlider() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, ConstantsDashboard.GET_UPDATES_SLIDER_URL, response -> {
            try {
                JSONArray homesliderArray = new JSONArray(response);
                for (int i = 0; i < homesliderArray.length(); i++) {
                    JSONObject childObj = homesliderArray.getJSONObject(i);
                    binding.carousel.addData(
                            new CarouselItem(
                                    childObj.getString("url"),
                                    childObj.getString("action")
                            )
                    );
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        queue.add(request);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}