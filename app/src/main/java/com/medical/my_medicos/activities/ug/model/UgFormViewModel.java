package com.medical.my_medicos.activities.ug.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UgFormViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UgFormViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is form fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}