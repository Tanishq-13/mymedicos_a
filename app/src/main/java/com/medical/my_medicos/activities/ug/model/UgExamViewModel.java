package com.medical.my_medicos.activities.ug.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UgExamViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UgExamViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}