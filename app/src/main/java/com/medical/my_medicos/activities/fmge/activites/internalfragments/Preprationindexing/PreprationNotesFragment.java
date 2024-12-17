package com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.adapter.PreprationNotesPagerAdapter;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments.AllFMGEPrep;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments.ClinicalFMGEPrep;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments.ParaFMGEPrep;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments.PreFMGEPrep;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.notes.PreprationIndexNotesNotes;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.notes.PreprationIndexingNotesIndex;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreprationNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PreprationNotesFragment extends Fragment {

    private static final String ARG_SPECIALITY = "speciality";
    Fragment currentFragment;  // To hold the currently loaded fragment

    private String speciality;

    public PreprationNotesFragment() {
        // Required empty public constructor
    }

    public static PreprationNotesFragment newInstance(String speciality) {
        PreprationNotesFragment fragment = new PreprationNotesFragment();
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
        View view = inflater.inflate(R.layout.fragment_prepration_notes, container, false);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TabLayout tabLayout = view.findViewById(R.id.tabLayout);
//        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ViewPager2 viewPager = view.findViewById(R.id.viewPager);
//        Frame
//        PreprationNotesPagerAdapter adapter = new PreprationNotesPagerAdapter(getActivity(), speciality);
//        viewPager.setAdapter(adapter);
        SegmentTabLayout segmentTabLayout = view.findViewById(R.id.tablayoutprep);
        String[] titles = {"Index", "Notes"};
        segmentTabLayout.setTabData(titles);
//        currentFragment =new PreprationIndexingNotesIndex();
//        loadFragment(currentFragment);

// Set the initial fragment with the speciality
        currentFragment = PreprationIndexingNotesIndex.newInstance(speciality);
        loadFragment(currentFragment);

        segmentTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        currentFragment = PreprationIndexingNotesIndex.newInstance(speciality);
                        break;
                    case 1:
                        currentFragment = PreprationIndexNotesNotes.newInstance(speciality); // Assuming this fragment also has a newInstance method
                        break;
                }
                loadFragment(currentFragment);
            }

            @Override
            public void onTabReselect(int position) {
                // Optional: handle reselection of the tab if necessary
            }
        });

//        return view;



        return view;
    }
    private void loadFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.viewPager, fragment)
                .commit();
    }
}
