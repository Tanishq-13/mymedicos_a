package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.notes.PreprationIndexNotesNotes;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.notes.PreprationIndexingNotesIndex;

public class PreprationNotesPagerAdapter extends FragmentStateAdapter {

    private String speciality;

    public PreprationNotesPagerAdapter(@NonNull FragmentActivity fragmentActivity, String speciality) {
        super(fragmentActivity);
        this.speciality = speciality;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return PreprationIndexingNotesIndex.newInstance(speciality);
            case 1:
                return PreprationIndexNotesNotes.newInstance(speciality);
            default:
                return PreprationIndexingNotesIndex.newInstance(speciality);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
