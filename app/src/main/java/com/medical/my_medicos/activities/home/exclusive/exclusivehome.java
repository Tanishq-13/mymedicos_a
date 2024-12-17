package com.medical.my_medicos.activities.home.exclusive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.medical.my_medicos.databinding.ActivityExclusivehomeBinding;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.exclusive.fragments.explore;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.MBinding;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class exclusivehome extends Fragment {
    private ActivityExclusivehomeBinding binding;
    Carousel hh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=ActivityExclusivehomeBinding.inflate(inflater, container, false);
        getsliderHome();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle system bar insets for immersive UI
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize tab navigation
        initializeTabs(view);
    }

    private void initializeTabs(View view) {
        // Set up tab click listeners
//        TextView exploreTab = view.findViewById(R.id.tabExplore);
//        TextView neetUgTab = view.findViewById(R.id.tabNeetUG);
//        TextView neetPgTab = view.findViewById(R.id.tabNeetPG);
//        TextView aiimsTab = view.findViewById(R.id.tabAiims);
//        hh=view.findViewById(R.id.homecarousel);
        // Set click listeners for tabs
//        exploreTab.setOnClickListener(v -> loadFragment(new explore()));
//        neetUgTab.setOnClickListener(v -> loadFragment(new NeetUGFragment()));
//        neetPgTab.setOnClickListener(v -> loadFragment(new NeetPGFragment()));
//        aiimsTab.setOnClickListener(v -> loadFragment(new AiimsFragment()));

        // Load default fragment (e.g., "Explore")
        loadFragment(new explore());
    }

    private void loadFragment(Fragment fragment) {
        // Replace the content of the FrameLayout with the selected fragment
        if (getChildFragmentManager() != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.tabContentFrame, fragment);
            transaction.commit();
        }
    }
    void getsliderHome() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        StringRequest request = new StringRequest(Request.Method.GET, ConstantsDashboard.GET_HOME_SLIDER_URL, response -> {
            try {
                JSONArray newssliderArray = new JSONArray(response);
                for (int i = 0; i < newssliderArray.length(); i++) {
                    JSONObject childObj = newssliderArray.getJSONObject(i);
                    binding.homecarousel.addData(
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

}
