package com.hsb.covid_19_predictor_fyp_v10.ui.covid;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CovidViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CovidViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}