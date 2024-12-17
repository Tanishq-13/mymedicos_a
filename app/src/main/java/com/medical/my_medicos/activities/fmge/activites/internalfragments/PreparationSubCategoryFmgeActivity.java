package com.medical.my_medicos.activities.fmge.activites.internalfragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.adapters.SpecialitiesFMGEAdapter;
import com.medical.my_medicos.activities.fmge.model.SpecialitiesFmge;
import com.medical.my_medicos.activities.pg.adapters.SpecialitiesPGAdapter;
import com.medical.my_medicos.activities.pg.model.SpecialitiesPG;
import com.medical.my_medicos.activities.slideshow.insider.SpecialitySlideshowInsiderActivity;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.ActivityPreparationCategoryDisplayBinding;
import com.medical.my_medicos.databinding.ActivityPreparationSubCategoryBinding;
import com.medical.my_medicos.databinding.ActivityPreparationSubCategoryFmgeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PreparationSubCategoryFmgeActivity extends AppCompatActivity {

    ActivityPreparationSubCategoryFmgeBinding binding;
    SwipeRefreshLayout swipeRefreshLayoutPreparationCategory;
    SpecialitiesFMGEAdapter specialitiesPGAdapter;
    ArrayList<SpecialitiesFmge> specialitiespost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreparationSubCategoryFmgeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showShimmer(true);

        ImageView backToHomeImageView = findViewById(R.id.backtothehomefromprepsubcategory);
        backToHomeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String categoryTitle = getIntent().getStringExtra("CATEGORY_TITLE");
        Log.d("CategoryTitle", categoryTitle);

        List<Integer> priorities = getPriorityForCategoryTitle(categoryTitle);

        if (!priorities.isEmpty()) {
            Log.d("Priority", "Selected priorities: " + priorities);
            binding.categorytitlepreparation.setText(categoryTitle);
            swipeRefreshLayoutPreparationCategory = binding.getRoot().findViewById(R.id.swipeRefreshLayoutPreparationCategory);
            swipeRefreshLayoutPreparationCategory.setOnRefreshListener(this::refreshContent);

            initSpecialities(priorities);
        } else {
            Log.e("PriorityError", "Invalid priorities for categoryTitle: " + categoryTitle);
        }

        configureWindow();
    }

    private void configureWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private List<Integer> getPriorityForCategoryTitle(String categoryTitle) {
        List<Integer> priorities = new ArrayList<>();
        switch (categoryTitle) {
            case "Pre & Para Clinical Subjects":
                priorities.add(1);
                priorities.add(2);
                break;
            case "Clinical Subjects":
                priorities.add(3);
                break;
            default:
                Log.e("PriorityError", "Unknown category title: " + categoryTitle);
                // You could handle this by returning an empty list or specific error handling
        }
        return priorities;
    }

    void initSpecialities(List<Integer> priorities) {
        specialitiespost = new ArrayList<>();
        specialitiesPGAdapter = new SpecialitiesFMGEAdapter(this, specialitiespost);

        getSpecialityPG(priorities);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        binding.categoriesinternalpractice.setLayoutManager(layoutManager);
        binding.categoriesinternalpractice.setAdapter(specialitiesPGAdapter);
    }

    void getSpecialityPG(List<Integer> priorities) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, ConstantsDashboard.GET_SPECIALITY, new Response.Listener<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("Response", response);
                    JSONObject mainObj = new JSONObject(response);
                    showShimmer(false);
                    if (mainObj.getString("status").equals("success")) {
                        JSONArray specialityArray = mainObj.getJSONArray("data");

                        specialitiespost.clear();

                        for (int i = 0; i < specialityArray.length(); i++) {
                            JSONObject object = specialityArray.getJSONObject(i);
                            int objectPriority = object.getInt("priority");

                            if (priorities.contains(objectPriority)) {
                                SpecialitiesFmge speciality = new SpecialitiesFmge(
                                        object.getString("id"),
                                        objectPriority
                                );
                                specialitiespost.add(speciality);
                            }
                        }
                        specialitiesPGAdapter.notifyDataSetChanged();
                    } else {
                        // Handle the case where the server response indicates failure
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error response
            }
        });

        queue.add(request);
    }

    private void refreshContent() {
        swipeRefreshLayoutPreparationCategory.setRefreshing(false);
    }

    private void showShimmer(boolean show) {
        if (show) {
            binding.shimmercomeupprep.setVisibility(View.VISIBLE);
            binding.shimmercomeupprep.playAnimation();
        } else {
            binding.shimmercomeupprep.setVisibility(View.GONE);
            binding.shimmercomeupprep.cancelAnimation();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
