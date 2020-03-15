package com.example.myapplication_mapnavigationdrawer.ui.PersonalInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PersonalInfoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PersonalInfoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is personal infomation fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}