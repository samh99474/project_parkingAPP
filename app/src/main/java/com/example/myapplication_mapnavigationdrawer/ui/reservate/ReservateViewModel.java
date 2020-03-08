package com.example.myapplication_mapnavigationdrawer.ui.reservate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReservateViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ReservateViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("暫無紀錄，開通黃金釋迦會員後即可使用");
    }

    public LiveData<String> getText() {
        return mText;
    }
}