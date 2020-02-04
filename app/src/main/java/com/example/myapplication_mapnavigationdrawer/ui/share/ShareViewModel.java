package com.example.myapplication_mapnavigationdrawer.ui.share;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShareViewModel extends ViewModel {

    private MutableLiveData<String> mText;


    public ShareViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Help fragment \n 如有問題請撥打客服電話 : 0800-092-000");
    }

    public LiveData<String> getText() {
        return mText;
    }
}