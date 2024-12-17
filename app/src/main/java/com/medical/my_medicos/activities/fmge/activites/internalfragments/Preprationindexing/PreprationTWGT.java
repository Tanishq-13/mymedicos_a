package com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.BottomSheetSpinnerFragmentFmge;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.adapter.Twgt.PreprationTwgtPageAdapter;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.tablayouts.twgt.PreprationIndexFMGETwgtBookmark;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.tablayouts.twgt.PreprationIndexingTwgtAll;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.tablayouts.twgt.preprationIndexingTwgtHY;
import com.medical.my_medicos.activities.fmge.adapters.WeeklyFmgeQuizAdapter;
import com.medical.my_medicos.activities.fmge.model.QuizFmge;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreprationTWGT#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreprationTWGT extends Fragment {
    private WeeklyFmgeQuizAdapter quizAdapter;
    private ArrayList<QuizFmge> quizpg;
    Fragment currentFragment;
    private static final String ARG_SPECIALITY = "speciality";
    private String speciality;
    String title1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public void PreprationSWGT() {
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
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prepration_t_w_g_t_fmge, container, false);


ImageView sortButton = getActivity().findViewById(R.id.filter);

        com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.adapter.Twgt.PreprationTwgtPageAdapter adapter = new PreprationTwgtPageAdapter(getActivity(), speciality);
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
                        currentFragment= PreprationIndexFMGETwgtBookmark.newInstance(speciality);
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
            BottomSheetSpinnerFragmentFmge bottomSheet = new BottomSheetSpinnerFragmentFmge(speciality);
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