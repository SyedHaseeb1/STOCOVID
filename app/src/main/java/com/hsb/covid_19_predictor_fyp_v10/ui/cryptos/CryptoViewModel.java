package com.hsb.covid_19_predictor_fyp_v10.ui.cryptos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CryptoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CryptoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}