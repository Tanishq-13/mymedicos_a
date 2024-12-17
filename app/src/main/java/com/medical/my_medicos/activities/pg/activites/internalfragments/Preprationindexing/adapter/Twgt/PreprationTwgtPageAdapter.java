package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.adapter.Twgt;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.twgt.PreprationIndexTwgtBookmark;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.twgt.PreprationIndexingTwgtAll;

import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.twgt.preprationIndexingTwgtHY;

public class PreprationTwgtPageAdapter extends FragmentStateAdapter {
    private final String speciality;

    public PreprationTwgtPageAdapter(@NonNull FragmentActivity fragmentActivity, String speciality) {
        super(fragmentActivity);
        this.speciality = speciality;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return PreprationIndexingTwgtAll.newInstance(speciality);
            case 1:
                return preprationIndexingTwgtHY.newInstance(speciality);
            case 2:
                return PreprationIndexTwgtBookmark.newInstance(speciality);
            default:
                return PreprationIndexingTwgtAll.newInstance(speciality);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
