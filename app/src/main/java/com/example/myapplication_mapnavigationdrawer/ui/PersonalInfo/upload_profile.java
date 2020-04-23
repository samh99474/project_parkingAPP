package com.example.myapplication_mapnavigationdrawer.ui.PersonalInfo;

public class upload_profile {
    private String mName;
    private String mImageUrl;

    public upload_profile(String string_uid, String toString) {
    }

    public void upload_profile() {
        //empty constructor needed
    }

    public void Upload(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
