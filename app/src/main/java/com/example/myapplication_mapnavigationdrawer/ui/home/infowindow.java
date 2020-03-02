package com.example.myapplication_mapnavigationdrawer.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication_mapnavigationdrawer.R;

import org.jetbrains.annotations.Nullable;

public class infowindow extends AppCompatActivity {

    private Button back ;
    private Button reservation;
    private Button btn_favorite;
    private Button parkinglot_street_view_image_url;
    private TextView parkinglot_lot;
    private TextView parkinglot_is_opening;
    private TextView parkinglot_name;
    private TextView parkinglot_today_service_time;
    private TextView parkinglot_simple_description;
    private TextView parkinglot_full_description;
    private TextView parkinglot_address;
    private TextView parkinglot_tel;
    private TextView parkinglot_detail_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infowindow);

        back = findViewById(R.id.back);
        btn_favorite = findViewById(R.id.btn_favorite);
        reservation = findViewById(R.id.reservation);
        parkinglot_name = findViewById(R.id.parkinglot_name);
        parkinglot_lot = findViewById(R.id.parkinglot_lot);
        parkinglot_is_opening = findViewById(R.id.parkinglot_is_opening);
        parkinglot_today_service_time = findViewById(R.id.parkinglot_today_service_time);
        parkinglot_simple_description = findViewById(R.id.parkinglot_simple_description);
        parkinglot_full_description = findViewById(R.id.detail_price);
        parkinglot_address = findViewById(R.id.parkinglot_address);
        parkinglot_tel = findViewById(R.id.parkinglot_tel);
        parkinglot_detail_info = findViewById(R.id.parkinglot_detail_info);

        Bundle b = getIntent().getExtras();
        String string_parkinglot_name = b.getString("string_parkinglot_name");
        String string_parkinglot_snippet = b.getString("string_parkinglot_snippet");

        parkinglot_name.setText(String.format("%s",string_parkinglot_name));//停車場名 Title

        String[] split_string_parkinglot_snippet = string_parkinglot_snippet.split(",");//split(指定符號) ，可依指定符號把字串分開成陣列

        if(split_string_parkinglot_snippet[0].equals("-1") == true){
            parkinglot_lot.setText(String.format("無資訊"));
        }
        else if (split_string_parkinglot_snippet[1].equals("-1") == true){
            parkinglot_lot.setText(String.format(split_string_parkinglot_snippet[0]));
        }
        else{
            parkinglot_lot.setText(String.format(split_string_parkinglot_snippet[0]+"  /  剩餘車位："+split_string_parkinglot_snippet[1]));
        }
/*
        windowadapter_parkinglot_lot.setText(windowadapter_parkinglot_lot.getText()+"\n●費率: "+split_string_parkinglot_snippet[2]);
*/

        if(split_string_parkinglot_snippet[4].matches("true")){
            parkinglot_is_opening.setText(String.format("   營業中   "));
        }else {
            parkinglot_is_opening.setText(String.format("   休息中   "));
        }

        parkinglot_today_service_time.setText(String.format(split_string_parkinglot_snippet[5]));
        parkinglot_simple_description.setText(String.format(split_string_parkinglot_snippet[2]));
        parkinglot_full_description.setText(String.format(split_string_parkinglot_snippet[3])); //之後改成detail_info
        parkinglot_address.setText(String.format(split_string_parkinglot_snippet[6]));

        if(split_string_parkinglot_snippet[7].matches("")){
            parkinglot_tel.setText(String.format("無資訊"));   //仍然有BUG，tel若為空值，點進去依然閃退
        }else {
            parkinglot_tel.setText(String.format(split_string_parkinglot_snippet[7]));
        }




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(infowindow.this,"您已成功預約",Toast.LENGTH_SHORT).show();
            }
        });

        if(split_string_parkinglot_snippet[10].matches("false")){
            btn_favorite.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp),null,null);
            btn_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    btn_favorite.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_favorite_black_24dp),null,null);
                    Toast.makeText(infowindow.this,"您已成功加入到我的最愛",Toast.LENGTH_SHORT).show();
                }
            });

        } else{
            btn_favorite.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_favorite_black_24dp),null,null);
            btn_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    btn_favorite.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp),null,null);
                    Toast.makeText(infowindow.this,"您已移除我的最愛",Toast.LENGTH_SHORT).show();
                }
            });

        }





        parkinglot_street_view_image_url = findViewById(R.id.parkinglot_street_view_image_url);

        //街景URL
        String strUri = "google.streetview:cbll="+
                String.valueOf(split_string_parkinglot_snippet[8])+","+//讀取lat
                String.valueOf(split_string_parkinglot_snippet[9]);     //讀取lng
        final String string_strUri1 = strUri;
        parkinglot_street_view_image_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下面放街景url即可
                Intent intent_street_view = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(string_strUri1));
                startActivity(intent_street_view);
            }
        });


    }
}
