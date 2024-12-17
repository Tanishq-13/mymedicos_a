package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter.Twgt.PreprationTwgtPageAdapter;

public class PreprationTWGT extends Fragment {
    private String speciality;
    private FilterViewModel filterViewModel;

    // Arguments
    private static final String ARG_SPECIALITY = "speciality";

    public PreprationTWGT() {
        // Required empty public constructor
    }

    public static PreprationTWGT newInstance(String speciality) {
        PreprationTWGT fragment = new PreprationTWGT();
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
        View view = inflater.inflate(R.layout.fragment_prepration_t_w_g_t, container, false);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TabLayout tabLayout = view.findViewById(R.id.tabLayout);
//        ViewPager2 viewPager = view.findViewById(R.id.viewPager3);
        tabLayout.addTab(tabLayout.newTab().setText("ALL"));
        tabLayout.addTab(tabLayout.newTab().setText("High Yield"));
        tabLayout.addTab(tabLayout.newTab().setText("Bookmark"));

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView sortButton = view.findViewById(R.id.filter);

        PreprationTwgtPageAdapter adapter = new PreprationTwgtPageAdapter(getActivity(), speciality);
//        viewPager.setAdapter(adapter);

//        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
//            switch (position) {
//                case 0:
//                    tab.setText("All");
//                    break;
//                case 1:
//                    tab.setText("High Yield");
//                    break;
//
//                case 2:
//                    tab.setText("Bookmark");
//                    break;
//            }
//        }).attach();

        sortButton.setOnClickListener(v -> {
            BottomSheetSpinnerFragment bottomSheet = new BottomSheetSpinnerFragment(speciality);
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });

        return view;
    }
}
