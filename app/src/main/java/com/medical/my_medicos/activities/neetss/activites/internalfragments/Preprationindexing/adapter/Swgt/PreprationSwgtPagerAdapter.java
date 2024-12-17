package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter.Swgt;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.swgt.PreprationIndexingSwgtLive;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.swgt.PreprationIndexingSwgtPast;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.swgt.PreprationIndexingSwgtUpcoming;

public class PreprationSwgtPagerAdapter extends FragmentStateAdapter {

    private final String speciality;

    public PreprationSwgtPagerAdapter(@NonNull FragmentActivity fragmentActivity, String speciality) {
        super(fragmentActivity);
        this.speciality = speciality;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return PreprationIndexingSwgtLive.newInstance(speciality);
            case 1:
                return PreprationIndexingSwgtUpcoming.newInstance(speciality);
            case 2:
                return PreprationIndexingSwgtPast.newInstance(speciality);
            default:
                return PreprationIndexingSwgtLive.newInstance(speciality);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
