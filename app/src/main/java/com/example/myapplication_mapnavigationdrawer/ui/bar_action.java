package com.example.myapplication_mapnavigationdrawer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication_mapnavigationdrawer.R;

public class bar_action extends AppCompatActivity {
    private Button btn_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_action);

        btn_query = findViewById(R.id.btn_query);

        btn_query = findViewById(R.id.btn_query);
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(bar_action.this, "無法使用此功能，請先付費NT100，即可升級鑽石會員", Toast.LENGTH_SHORT).show();
            }
        });
    }
}