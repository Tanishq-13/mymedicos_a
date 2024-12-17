package com.medical.my_medicos.activities.slideshow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.medical.my_medicos.activities.publications.model.Product;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.ActivitySearchPublicationBinding;
import com.medical.my_medicos.databinding.ActivitySearchSlideshowBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchSlideshowActivity extends AppCompatActivity {

    ActivitySearchSlideshowBinding binding;
    SlideshowAdapter slideshowAdapter;
    ArrayList<Slideshow> slideshows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchSlideshowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        slideshows = new ArrayList<>();
        slideshowAdapter = new SlideshowAdapter(this, slideshows);

        String query = getIntent().getStringExtra("query");

        // Set the query as the title of the search
        TextView titleTextView = findViewById(R.id.titleofthesearch);
        titleTextView.setText(query);

        getSlideshow(query);

        ImageView backToPublicationActivity = findViewById(R.id.backtothepublicationactivity);
        backToPublicationActivity.setOnClickListener(v -> {
            finish();
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        binding.slidersList.setLayoutManager(layoutManager);
        binding.slidersList.setAdapter(slideshowAdapter);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getSlideshow(String query) {
        RequestQueue queue = Volley.newRequestQueue(SearchSlideshowActivity.this);

        String url = ConstantsDashboard.GET_ALL_SLIDESHOW;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if ("success".equals(object.optString("status"))) {
                    JSONArray dataArray = object.getJSONArray("data");
                    slideshows.clear();

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject slideshowObj = dataArray.getJSONObject(i);

                        String title = slideshowObj.optString("title").toLowerCase();
                        if (title.contains(query.toLowerCase())) {
                            String fileUrl = slideshowObj.optString("file");
                            String type = slideshowObj.optString("type");

                            if (slideshowObj.has("images")) {
                                JSONArray imagesArray = slideshowObj.getJSONArray("images");
                                ArrayList<Slideshow.Image> images = new ArrayList<>();
                                for (int j = 0; j < imagesArray.length(); j++) {
                                    JSONObject imageObj = imagesArray.getJSONObject(j);
                                    String imageUrl = imageObj.optString("url");
                                    String imageId = imageObj.optString("id");
                                    images.add(new Slideshow.Image(imageId, imageUrl));
                                }
                                Slideshow slideshowItem = new Slideshow(title, images, fileUrl, type);
                                slideshows.add(slideshowItem);
                            } else {
                                Slideshow slideshowItem = new Slideshow(title, new ArrayList<>(), fileUrl, type);
                                slideshows.add(slideshowItem);
                            }
                        }
                    }
                    slideshowAdapter.notifyDataSetChanged();
                    if (slideshows.isEmpty()) {
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
