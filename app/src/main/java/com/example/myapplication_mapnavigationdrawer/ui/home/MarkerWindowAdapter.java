package com.example.myapplication_mapnavigationdrawer.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication_mapnavigationdrawer.R;

public class MarkerWindowAdapter extends AppCompatActivity {


    private TextView windowadapter_parkinglot_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_window_adapter);
/*
        windowadapter_parkinglot_name = findViewById(R.id.windowadapter_parkinglot_name);

        Bundle b = getIntent().getExtras();
        String string_parkinglot_name = b.getString("string_parkinglot_name");

        windowadapter_parkinglot_name.setText(String.format("拉%s",string_parkinglot_name));

 */


    }
}
