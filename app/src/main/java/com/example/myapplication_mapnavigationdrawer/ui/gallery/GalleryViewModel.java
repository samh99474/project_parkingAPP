package com.example.myapplication_mapnavigationdrawer.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("暫無紀錄，開通黃金釋迦會員後即可使用");
    }

    public LiveData<String> getText() {
        return mText;
    }
}