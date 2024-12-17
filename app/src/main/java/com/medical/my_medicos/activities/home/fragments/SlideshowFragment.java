package com.medical.my_medicos.activities.home.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.slideshow.PaidSlideshowAdapter;
import com.medical.my_medicos.activities.slideshow.SearchSlideshowActivity;
import com.medical.my_medicos.activities.slideshow.SlideshareCategoryAdapter;
import com.medical.my_medicos.activities.slideshow.SlideshareFormActivity;
import com.medical.my_medicos.activities.slideshow.Slideshow;
import com.medical.my_medicos.activities.slideshow.SlideshowAdapter;
import com.medical.my_medicos.activities.slideshow.insider.SpecialitySlideshowInsiderActivity;
import com.medical.my_medicos.activities.slideshow.model.SlideshareCategory;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.FragmentSlideshowBinding;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {
    private FragmentSlideshowBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    SlideshareCategoryAdapter slideshareCategoryAdapterslideshow;
    ArrayList<SlideshareCategory> slidesharecategoriesslideshow;
    private SlideshowAdapter slideshowAdapter;
    private ArrayList<Slideshow> slideshows;

    //.......Paid....
    private PaidSlideshowAdapter paidslideshowAdapter;
    private ArrayList<Slideshow> paidslideshows;

//    private LinearLayout progressBar;

    TextView opentheuserupload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = requireActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.backgroundcolor));
        }

        binding.searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for now
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for now
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for now
            }
        });

        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // Not needed for now
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                String query = text.toString();
                if (!TextUtils.isEmpty(query)) {
                    Intent intent = new Intent(getContext(), SearchSlideshowActivity.class);
                    intent.putExtra("query", query);
                    startActivity(intent);
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    // Handle back button click
                }
            }
        });

        opentheuserupload = rootView.findViewById(R.id.opentheuserupload);

        opentheuserupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SlideshareFormActivity.class);
                startActivity(i);
            }
        });

        NestedScrollView scrollView = rootView.findViewById(R.id.swipeforslider);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.beepsound);
        mediaPlayer.setVolume(0.1f, 0.1f);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            boolean isAtTop = scrollView.getScrollY() == 0;
            swipeRefreshLayout.setEnabled(isAtTop);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            mediaPlayer.start();
            refreshData();
        });

        initCategoriesSlideshow();
        initSlideshowSlider();
        initSliderContent();
        initSliderFormeContent();
        return rootView;
    }
//    private void showProgressBar(LinearLayout progressBar) {
//        this.progressBar.setVisibility(View.VISIBLE);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SlideshowFragment.this.progressBar.setVisibility(View.GONE);
//            }
//        }, 3000);
//    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshData() {
        slideshows.clear();
        paidslideshows.clear();
        slideshowAdapter.notifyDataSetChanged();
        paidslideshowAdapter.notifyDataSetChanged();

        getSlideshowRecent();
        getSlideshowFormeRecent();

        swipeRefreshLayout.setRefreshing(false);
    }

    //......Category.....
    void initCategoriesSlideshow() {
        slidesharecategoriesslideshow = new ArrayList<>();
        slideshareCategoryAdapterslideshow = new SlideshareCategoryAdapter(requireContext(), slidesharecategoriesslideshow);

        getCategoriesSlideshow();

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.slideshowpptlistcategory.setLayoutManager(layoutManager);
        binding.slideshowpptlistcategory.setAdapter(slideshareCategoryAdapterslideshow);
    }
    void getCategoriesSlideshow() {

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest request = new StringRequest(Request.Method.GET, ConstantsDashboard.GET_SPECIALITY, new Response.Listener<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("err", response);
                    JSONObject mainObj = new JSONObject(response);
                    if (mainObj.getString("status").equals("success")) {
                        JSONArray slidesharecategoriesArray = mainObj.getJSONArray("data");
                        int categoriesCount = Math.min(slidesharecategoriesArray.length(), 40);
                        for (int i = 0; i < categoriesCount; i++) {
                            JSONObject object = slidesharecategoriesArray.getJSONObject(i);

                            // Add categories with priority 1, 2, and 3
                            int priority = object.getInt("priority");
                            if (priority >= 1 && priority <= 3) {
                                SlideshareCategory slideshowcategory = new SlideshareCategory(
                                        object.getString("id"),
                                        priority // Set the priority
                                );
                                slidesharecategoriesslideshow.add(slideshowcategory);
                                Log.e("Priority", String.valueOf(priority));
                            }
                        }


                        slideshareCategoryAdapterslideshow.notifyDataSetChanged();
                        binding.slideshowpptlistcategory.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                            @Override
                            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                                View child = rv.findChildViewUnder(e.getX(), e.getY());
                                int position = rv.getChildAdapterPosition(child);

                                if (position != RecyclerView.NO_POSITION) {
                                    if (position == slidesharecategoriesslideshow.size() - 1 && slidesharecategoriesslideshow.get(position).getPriority() == -1) {
                                        Intent intent = new Intent(requireContext(), SpecialitySlideshowInsiderActivity.class);
                                        startActivity(intent);
                                    } else {

                                    }
                                }
                                return false;
                            }
                            @Override
                            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

                            @Override
                            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
                        });
                        slideshareCategoryAdapterslideshow.notifyDataSetChanged();
                    } else {
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

    //.....Slider for slideshow.....
    void getsliderSlideShow() {
        RequestQueue queue = Volley.newRequestQueue(requireContext()); // Use requireContext()

        StringRequest request = new StringRequest(Request.Method.GET, ConstantsDashboard.GET_SLIDESHOW_SLIDER_URL, response -> {
            try {
                JSONArray slideshowsliderArray = new JSONArray(response);
                for (int i = 0; i < slideshowsliderArray.length(); i++) {
                    JSONObject childObj = slideshowsliderArray.getJSONObject(i);
                    binding.slideshowcarousel.addData(
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
            // Handle error if needed
        });

        queue.add(request);
    }
    private void initSlideshowSlider() {
        getsliderSlideShow();
    }

    //.....Paid...
    void initSliderContent() {
        paidslideshows = new ArrayList<>();
        paidslideshowAdapter = new PaidSlideshowAdapter(getActivity(), paidslideshows);
        getSlideshowRecent();

        // Use requireContext() or getContext() to get a valid context
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.slideshowpptlist.setLayoutManager(layoutManager);
        binding.slideshowpptlist.setAdapter(paidslideshowAdapter);
    }
    void getSlideshowRecent() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        String url = ConstantsDashboard.GET_SLIDESHOW_HOME;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if ("success".equals(object.optString("status"))) {
                    JSONArray dataArray = object.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject slideshowObj = dataArray.getJSONObject(i);

                        String fileUrl = slideshowObj.optString("file");
                        String title = slideshowObj.optString("title");
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
                            // Now you can create your Slideshow object with images
                            Slideshow slideshowItem = new Slideshow(title, images, fileUrl,type);
                            paidslideshows.add(slideshowItem);
                        } else {
                            // If "images" array does not exist, create Slideshow without images
                            Slideshow slideshowItem = new Slideshow(title, new ArrayList<>(), fileUrl,type);
                            paidslideshows.add(slideshowItem);
                        }
                    }
                    paidslideshowAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle error
        });

        queue.add(request);
    }

    //.....For me...
    void initSliderFormeContent() {
        slideshows = new ArrayList<>();
        slideshowAdapter = new SlideshowAdapter(getActivity(), slideshows);

        getSlideshowFormeRecent();

        // Use requireContext() or getContext() to get a valid context
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.featured.setLayoutManager(layoutManager);
        binding.featured.setAdapter(slideshowAdapter);
    }
    void getSlideshowFormeRecent() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        String url = ConstantsDashboard.GET_SLIDESHOW_FEATURED;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if ("success".equals(object.optString("status"))) {
                    JSONArray dataArray = object.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject slideshowObj = dataArray.getJSONObject(i);

                        String fileUrl = slideshowObj.optString("file");
                        String title = slideshowObj.optString("title");
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
                            // Now you can create your Slideshow object with images
                            Slideshow slideshowItem = new Slideshow(title, images, fileUrl,type);
                            slideshows.add(slideshowItem);
                        } else {
                            // If "images" array does not exist, create Slideshow without images
                            Slideshow slideshowItem = new Slideshow(title, new ArrayList<>(), fileUrl,type);
                            slideshows.add(slideshowItem);
                        }
                    }
                    slideshowAdapter.notifyDataSetChanged();
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