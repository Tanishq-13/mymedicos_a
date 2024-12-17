package com.medical.my_medicos.activities.pg.activites.internalfragments;

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
import com.medical.my_medicos.activities.pg.adapters.SpecialitiesPGAdapter;
import com.medical.my_medicos.activities.pg.adapters.SpecialityPGForMaterialAdapter;
import com.medical.my_medicos.activities.pg.model.SpecialitiesPG;
import com.medical.my_medicos.activities.slideshow.insider.SpecialitySlideshowInsiderActivity;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.ActivityPreparationCategoryDisplayBinding;
import com.medical.my_medicos.databinding.ActivityPreparationSubCategoryBinding;
import com.medical.my_medicos.databinding.ActivityPreparationSubCategoryForMaterialBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PreparationSubCategoryForMaterialActivity extends AppCompatActivity {

    ActivityPreparationSubCategoryForMaterialBinding binding;

    SwipeRefreshLayout swipeRefreshLayoutPreparationCategory;
    SpecialityPGForMaterialAdapter specialitiesPGAdapter;
    ArrayList<SpecialitiesPG> specialitiespost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreparationSubCategoryForMaterialBinding.inflate(getLayoutInflater());
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

        int priority = getPriorityForCategoryTitle(categoryTitle);

        if (priority != -1) {
            Log.d("Priority", "Selected priority: " + priority);
            binding.categorytitlepreparation.setText(categoryTitle);
            swipeRefreshLayoutPreparationCategory = binding.getRoot().findViewById(R.id.swipeRefreshLayoutPreparationCategory);
            swipeRefreshLayoutPreparationCategory.setOnRefreshListener(this::refreshContent);

            initSpecialities(priority);
        } else {
            Log.e("PriorityError", "Invalid priority for categoryTitle: " + categoryTitle);
        }

        configureWindow();
    }

    private void configureWindow() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private int getPriorityForCategoryTitle(String categoryTitle) {
        if ("Pre - Clinical Subjects".equals(categoryTitle)) {
            return 1;
        } else if ("Para - Clinical Subjects".equals(categoryTitle)) {
            return 2;
        } else if ("Clinical Subjects".equals(categoryTitle)) {
            return 3;
        } else {
            Log.e("PriorityError", "Unknown category title: " + categoryTitle);
            return -1; // Invalid priority
        }
    }



    void initSpecialities(int priority) {
        specialitiespost = new ArrayList<>();
        specialitiesPGAdapter = new SpecialityPGForMaterialAdapter(this, specialitiespost);

        getSpecialityPG(priority);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        binding.categoriesinternalpractice.setLayoutManager(layoutManager);
        binding.categoriesinternalpractice.setAdapter(specialitiesPGAdapter);
    }

    void getSpecialityPG(int priority) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, ConstantsDashboard.GET_SPECIALITY, new Response.Listener<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("err", response);
                    JSONObject mainObj = new JSONObject(response);
                    showShimmer(false);
                    if (mainObj.getString("status").equals("success")) {
                        JSONArray specialityArray = mainObj.getJSONArray("data");

                        specialitiespost.clear();

                        for (int i = 0; i < specialityArray.length(); i++) {
                            JSONObject object = specialityArray.getJSONObject(i);
                            int objectPriority = object.getInt("priority");

                            if (objectPriority == priority) {
                                SpecialitiesPG speciality = new SpecialitiesPG(
                                        object.getString("id"),
                                        objectPriority
                                );
                                specialitiespost.add(speciality);
                                Log.e("Something went wrong..", String.valueOf(objectPriority));
                            }
                        }

                        binding.categoriesinternalpractice.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                            @Override
                            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                                View child = rv.findChildViewUnder(e.getX(), e.getY());
                                int position = rv.getChildAdapterPosition(child);

                                if (position != RecyclerView.NO_POSITION) {
                                    if (position == specialitiespost.size() - 1 && specialitiespost.get(position).getPriority() == -1) {
                                        Intent intent = new Intent(PreparationSubCategoryForMaterialActivity.this, SpecialitySlideshowInsiderActivity.class);
                                        startActivity(intent);
                                    } else {
                                    }
                                }
                                return false;
                            }

                            @Override
                            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                            }

                            @Override
                            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                            }
                        });

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
