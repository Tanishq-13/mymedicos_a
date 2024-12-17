package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter.PreprationNotesPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreprationNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PreprationNotesFragment extends Fragment {

    private static final String ARG_SPECIALITY = "speciality";

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
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        PreprationNotesPagerAdapter adapter = new PreprationNotesPagerAdapter(getActivity(), speciality);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Index");
                    break;
                case 1:
                    tab.setText("Notes");
                    break;
            }
        }).attach();

        return view;
    }
}
