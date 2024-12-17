package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FilterViewModel extends ViewModel {
    private final MutableLiveData<String> selectedFilter = new MutableLiveData<>("All (Default)");
    private final MutableLiveData<String> selectedSubspeciality = new MutableLiveData<>(null);

    public void setSelectedFilter(String filter) {
        selectedFilter.setValue(filter);
    }

    public LiveData<String> getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedSubspeciality(String subspeciality) {
        selectedSubspeciality.setValue(subspeciality);
    }

    public LiveData<String> getSelectedSubspeciality() {
        return selectedSubspeciality;
    }
}
