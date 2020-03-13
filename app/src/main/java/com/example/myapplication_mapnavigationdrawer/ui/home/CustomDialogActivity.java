package com.example.myapplication_mapnavigationdrawer.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication_mapnavigationdrawer.R;

public class CustomDialogActivity extends AppCompatActivity {
    private Button btn_confirm;
    private Button btn_cancel;
    private TextView text_content;
    private TextView text_title;
    private TextView text_close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_dialog);

        btn_confirm = (Button)findViewById(R.id.btn_confirm);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        text_close = (TextView)findViewById(R.id.text_close);
        text_title  = (TextView)findViewById(R.id.text_title);
        text_content = (TextView)findViewById(R.id.text_content);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View v) {
                Toast.makeText(CustomDialogActivity.this,"you click confirm!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View v) {
                Toast.makeText(CustomDialogActivity.this,"you click cancel!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        text_close.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View v) {
                Toast.makeText(CustomDialogActivity.this,"you click close!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }
}
