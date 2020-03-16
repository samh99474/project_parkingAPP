package com.example.myapplication_mapnavigationdrawer.ui.home;

import android.net.http.SslCertificate;

import com.google.android.gms.common.api.Result;

public class MyGsonData {
    Result data;
    public class Result{

        Results[] parkinglots;

        public class Results{
            String id;
            String name;
            String address;
            String tel;
            int total_lots;
            int available_lots;
            String today_service_time;
            Boolean is_opening;

            Boolean has_motor;
            String motor_total_lots;
            String motor_available_lots;

            Boolean has_heavy_motor;
            double lat;
            double lng;

            Resultss current_price_text;
            public class Resultss {
                String simple_description;
                String full_description;
            }

            String[][] detail_info;

            Resultsss street_view;
            public class Resultsss{
                String street_view_url;
                String street_view_image_url;
            }
        }
    }
}
