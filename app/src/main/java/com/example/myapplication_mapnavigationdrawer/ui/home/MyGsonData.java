package com.example.myapplication_mapnavigationdrawer.ui.home;

import com.google.android.gms.common.api.Result;

public class MyGsonData {
    Result data;
    public class Result{

        Results[] parkinglots;

        public class Results{
            String id;
            String name;
            Double lat;
            Double lng;
        }
    }
}
