package com.example.myapplication_mapnavigationdrawer.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication_mapnavigationdrawer.MainActivity;
import com.example.myapplication_mapnavigationdrawer.R;

public class infowindow extends AppCompatActivity {

    private Button back ;
    private Button reservation;
    private TextView remain_show;
    private TextView price_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infowindow);

        back = findViewById(R.id.back);
        remain_show = findViewById(R.id.remain_show);
        price_show = findViewById(R.id.price_show);

        remain_show.setText(String.format("12"));
        price_show.setText(String.format("80/h"));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        reservation = findViewById(R.id.reservation);
        reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(infowindow.this,"您已成功預約",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
