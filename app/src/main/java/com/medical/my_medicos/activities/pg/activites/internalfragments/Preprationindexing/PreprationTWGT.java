package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.adapter.Twgt.PreprationTwgtPageAdapter;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.notes.PreprationIndexNotesNotes;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.notes.PreprationIndexingNotesIndex;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.twgt.PreprationIndexTwgtBookmark;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.twgt.PreprationIndexingTwgtAll;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.twgt.preprationIndexingTwgtHY;

public class PreprationTWGT extends Fragment {
    private String speciality;
    Fragment currentFragment;
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

//        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TabLayout tabLayout = view.findViewById(R.id.tabLayout);
//        ViewPager2 viewPager = view.findViewById(R.id.viewPager3);
//        tabLayout.addTab(tabLayout.newTab().setText("ALL"));
//        tabLayout.addTab(tabLayout.newTab().setText("8High Yield"));
//        tabLayout.addTab(tabLayout.newTab().setText("Bookmark"));
 ImageView sortButton = getActivity().findViewById(R.id.filter);
        sortButton.setVisibility(View.VISIBLE);
        PreprationTwgtPageAdapter adapter = new PreprationTwgtPageAdapter(getActivity(), speciality);
//        viewPager.setAdapter(adapter);

        SegmentTabLayout segmentTabLayout = view.findViewById(R.id.tablayoutprep);
        String[] titles = {"All", "High Yield", "Bookmark"};
        segmentTabLayout.setTabData(titles);
        currentFragment = PreprationIndexingTwgtAll.newInstance(speciality);

        loadFragment(currentFragment);

        segmentTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        currentFragment = PreprationIndexingTwgtAll.newInstance(speciality);
                        break;
                    case 1:
                        currentFragment = preprationIndexingTwgtHY.newInstance(speciality); // Assuming this fragment also has a newInstance method
                        break;
                    case 2:
                        currentFragment=PreprationIndexTwgtBookmark.newInstance(speciality);
                        break;
                    default:
                        currentFragment=PreprationIndexingTwgtAll.newInstance(speciality);
                }
                loadFragment(currentFragment);
            }

            @Override
            public void onTabReselect(int position) {
                // Optional: handle reselection of the tab if necessary
            }
        });

        sortButton.setOnClickListener(v -> {
            BottomSheetSpinnerFragment bottomSheet = new BottomSheetSpinnerFragment(speciality);
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });

        return view;
    }
    private void loadFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.viewPager, fragment)
                .commit();
    }
}
