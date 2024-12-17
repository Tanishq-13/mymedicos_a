package com.medical.my_medicos.activities.publications.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.publications.adapters.ProductAdapter;
import com.medical.my_medicos.activities.publications.adapters.SearchPublicationAdapter;
import com.medical.my_medicos.activities.publications.model.Product;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.ActivitySearchPublicationBinding;
import com.medical.my_medicos.databinding.ActivitySearchingPublicationBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchingPublicationActivity extends AppCompatActivity {

    ActivitySearchingPublicationBinding binding;
    SearchPublicationAdapter searchPublicationAdapter;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchingPublicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        products = new ArrayList<>();
        searchPublicationAdapter = new SearchPublicationAdapter(this, products);

        String query = getIntent().getStringExtra("query");

        // Set the query as the title of the search
        TextView titleTextView = findViewById(R.id.titleofthesearch);
        titleTextView.setText(query);

        getProducts(query);

        ImageView backToPublicationActivity = findViewById(R.id.backtothepublicationactivity);
        backToPublicationActivity.setOnClickListener(v -> {
            finish();
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(searchPublicationAdapter);

        configureWindow();
    }

    private void configureWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getProducts(String query) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = ConstantsDashboard.GET_SPECIALITY_ALL_PRODUCT;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray productsArray = object.getJSONArray("data");
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject childObj = productsArray.getJSONObject(i);
                        // Assuming each childObj directly contains the product details and an id
                        String title = childObj.getString("Title");
                        if (title.toLowerCase().contains(query.toLowerCase())) {
                            Product product = new Product(
                                    childObj.getString("id"), // Adjusted to place id as the first parameter
                                    title,
                                    childObj.getString("thumbnail"),
                                    childObj.getString("Author"),
                                    childObj.getDouble("Price"),
                                    childObj.getString("Type"),
                                    childObj.getString("Category"),
                                    childObj.getString("Subject"),
                                    childObj.getString("URL")
                            );
                            products.add(product);
                        }
                    }
                    searchPublicationAdapter.notifyDataSetChanged();
                    if (products.isEmpty()) {
                        binding.noResults.setVisibility(View.VISIBLE);
                    } else {
                        binding.noResults.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle error
        });

        queue.add(request);
    }

}