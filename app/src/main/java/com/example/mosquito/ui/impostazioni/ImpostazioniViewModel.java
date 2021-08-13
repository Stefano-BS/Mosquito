package com.example.mosquito.ui.impostazioni;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ImpostazioniViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ImpostazioniViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Impostazioni");
    }

    public LiveData<String> getText() {
        return mText;
    }
}