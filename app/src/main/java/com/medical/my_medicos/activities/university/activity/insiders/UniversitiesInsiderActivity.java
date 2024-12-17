package com.medical.my_medicos.activities.university.activity.insiders;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.publications.utils.Constants;
import com.medical.my_medicos.activities.university.adapters.insiders.UniversitiesInsiderAdapter;
import com.medical.my_medicos.activities.university.model.Universities;
import com.medical.my_medicos.databinding.ActivityUniversitiesInsiderBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class UniversitiesInsiderActivity extends AppCompatActivity {

    ActivityUniversitiesInsiderBinding binding;
    ArrayList<Universities> universities;
    Toolbar toolbaruniversities;
    UniversitiesInsiderAdapter universitiesAdapterInsider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUniversitiesInsiderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbaruniversities = findViewById(R.id.universitiestoolbar);
        setSupportActionBar(toolbaruniversities);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        initCategories();

    }

    void initCategories() {
        universities = new ArrayList<>();
        universitiesAdapterInsider = new UniversitiesInsiderAdapter(this, universities);

        getCategories();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        binding.universitiesListInsider.setLayoutManager(layoutManager);
        binding.universitiesListInsider.setAdapter(universitiesAdapterInsider);
    }

    void getCategories() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_CATEGORIES_URL, new Response.Listener<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("err", response);
                    JSONObject mainObj = new JSONObject(response);
                    if (mainObj.getString("status").equals("success")) {
                        JSONArray categoriesArray = mainObj.getJSONArray("categories");
                        for (int i = 0; i < categoriesArray.length(); i++) {
                            JSONObject object = categoriesArray.getJSONObject(i);
                            Universities university = new Universities(
                                    object.getString("name"),
                                    Constants.CATEGORIES_IMAGE_URL + object.getString("icon"),
                                    object.getString("color"),
                                    object.getString("brief"),
                                    object.getInt("id")
                            );
                            universities.add(university);
                        }
                        universitiesAdapterInsider.notifyDataSetChanged();
                    } else {
                        // DO nothing
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle the back arrow click, finish the current activity
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}