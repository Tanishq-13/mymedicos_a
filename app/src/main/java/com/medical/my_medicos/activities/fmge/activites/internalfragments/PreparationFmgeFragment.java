package com.medical.my_medicos.activities.fmge.activites.internalfragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.SegmentTabLayout;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments.AllFMGEPrep;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments.ClinicalFMGEPrep;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments.ParaFMGEPrep;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.preprationinternalfragments.PreFMGEPrep;
import com.medical.my_medicos.databinding.FragmentPreparationFmgeBinding;

public class PreparationFmgeFragment extends Fragment {

    FragmentPreparationFmgeBinding binding;
    SwipeRefreshLayout swipeRefreshLayoutPreparation;
    SearchView searchView;

    private Fragment currentFragment;  // To hold the currently loaded fragment

    public static PreparationFmgeFragment newInstance() {
        PreparationFmgeFragment fragment = new PreparationFmgeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPreparationFmgeBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Initialize SegmentTabLayout
        SegmentTabLayout segmentTabLayout = rootView.findViewById(R.id.tablayoutprep);
        String[] titles = {"All", "Pre", "Para", "Clinical"};
        segmentTabLayout.setTabData(titles);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayoutPreparation = binding.getRoot().findViewById(R.id.swipeRefreshLayoutPreparation);
        swipeRefreshLayoutPreparation.setEnabled(false);

        // Initialize SearchView
        searchView = rootView.findViewById(R.id.searchView);
        searchView.setQueryHint(Html.fromHtml("<font color='#ABABAB' style='text-decoration:none;'>"
                + getResources().getString(R.string.hintSearchMess) + "</font>", Html.FROM_HTML_MODE_LEGACY));

        TextView searchTextView = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchTextView.setTextSize(16);
        // Load the custom font from resources
        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.inter); // Ensure to replace `this` with the correct context

        // Apply the font to the search hint
        if (searchTextView != null) {
            searchTextView.setTypeface(typeface);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterRecyclerView(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterRecyclerView(newText);
                return false;
            }
        });

        // Load the default fragment (ALL) initially
        currentFragment = new AllFMGEPrep();
        loadFragment(currentFragment);

        // Set up tab selection listener
        segmentTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        currentFragment = new AllFMGEPrep();
                        break;
                    case 1:
                        currentFragment = new PreFMGEPrep();
                        break;
                    case 2:
                        currentFragment = new ParaFMGEPrep();
                        break;
                    case 3:
                        currentFragment = new ClinicalFMGEPrep();
                        break;
                }
                loadFragment(currentFragment);
            }

            @Override
            public void onTabReselect(int position) {
                // Optional: handle reselection of the tab if necessary
            }
        });

        return rootView;
    }

    // Helper method to load a fragment into the FrameLayout
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.view_page_prep, fragment);
        transaction.commit();
    }

    // Method to filter the RecyclerView items in the current fragment
    private void filterRecyclerView(String query) {
        if (currentFragment instanceof SearchableFragment) {
            ((SearchableFragment) currentFragment).filter(query);  // Assuming each fragment implements this interface
        }
    }

    public interface SearchableFragment {
        void filter(String query);
    }

}
