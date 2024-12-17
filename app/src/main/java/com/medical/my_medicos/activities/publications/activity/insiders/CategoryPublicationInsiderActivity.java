package com.medical.my_medicos.activities.publications.activity.insiders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.publications.activity.PublicationActivity;
import com.medical.my_medicos.activities.publications.adapters.CategoryAdapter;
import com.medical.my_medicos.activities.publications.adapters.insiders.CategoryInsiderAdapter;
import com.medical.my_medicos.activities.publications.model.Category;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.ActivityCategoryPublicationInsiderBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryPublicationInsiderActivity extends AppCompatActivity {

    ActivityCategoryPublicationInsiderBinding binding;
    ArrayList<Category> categories;
    CategoryInsiderAdapter categoryInsiderAdapter;
    Toolbar toolbarpublications;
    CategoryInsiderAdapter categoryAdapterInsider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryPublicationInsiderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        toolbarpublications = findViewById(R.id.publicationstoolbar);
        setSupportActionBar(toolbarpublications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCategories();
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

    void initCategories() {
        categories = new ArrayList<>();
        categoryInsiderAdapter = new CategoryInsiderAdapter(this, categories);

        getCategories();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        binding.categoriesListInsider.setLayoutManager(layoutManager);
        binding.categoriesListInsider.setAdapter(categoryInsiderAdapter);
    }

    void getCategories() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, ConstantsDashboard.GET_SPECIALITY, new Response.Listener<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("err", response);
                    JSONObject mainObj = new JSONObject(response);
                    if (mainObj.getString("status").equals("success")) {
                        JSONArray categoriesArray = mainObj.getJSONArray("data");

                        for (int i = 0; i < categoriesArray.length(); i++) {
                            JSONObject object = categoriesArray.getJSONObject(i);
                            int priority = object.getInt("priority");
                            if (priority == 3) {
                                Category category = new Category(
                                        object.getString("id"),
                                        priority
                                );
                                categories.add(category);
                                Log.e("Something went wrong..", String.valueOf(priority));
                            }
                        }
                        categoryInsiderAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Error","Error Here");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error","Something went wrong");
            }
        });

        queue.add(request);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}