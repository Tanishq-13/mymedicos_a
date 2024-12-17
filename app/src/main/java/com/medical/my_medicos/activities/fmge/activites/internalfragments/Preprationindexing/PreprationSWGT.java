package com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.adapter.Swgt.PreprationSwgtPagerAdapter;

public class PreprationSWGT extends Fragment {

    private static final String ARG_SPECIALITY = "speciality";
    private String speciality;
    private FilterViewModel filterViewModel;

    public PreprationSWGT() {
        // Required empty public constructor
    }

    public static PreprationSWGT newInstance(String speciality) {
        PreprationSWGT fragment = new PreprationSWGT();
        Bundle args = new Bundle();
        args.putString(ARG_SPECIALITY, speciality);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            speciality = getArguments().getString(ARG_SPECIALITY);
        }
        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prepration_s_w_g_t, container, false);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView sortButton = view.findViewById(R.id.filter);

        PreprationSwgtPagerAdapter adapter = new PreprationSwgtPagerAdapter(getActivity(), speciality);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Live");
                    break;
                case 1:
                    tab.setText("Upcoming");
                    break;
                case 2:
                    tab.setText("Past");
                    break;
            }
        }).attach();

        sortButton.setOnClickListener(v -> {
            // If the dropdown or additional action is needed, handle it here.
        });

        return view;
    }
}
