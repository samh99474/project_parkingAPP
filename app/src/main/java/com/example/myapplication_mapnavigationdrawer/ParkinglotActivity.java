package com.example.myapplication_mapnavigationdrawer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication_mapnavigationdrawer.model.Rating;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ParkinglotActivity extends AppCompatActivity {




        //if(預約||使用==0){ButtonA1可按}else{ButtonA1不可按 顏色改暗}
        private Button A1;
        private Button A2;
        private Button A3;
        private Button gotoreservation;
        private FirebaseFirestore mFirestore;
        private Query mQuery;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_parkinglot);

            final FirebaseFirestore user_db = FirebaseFirestore.getInstance();

            A1 = findViewById(R.id.btn_info);
            A1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //抓取parking grid/A1/停車紀錄/01
                    final FirebaseFirestore parkingA1 = FirebaseFirestore.getInstance();
                    DocumentReference A1info = parkingA1.collection("parking grid").document("A1");
                }
            });
        }
}


                        /*@Override
                        public void onClick(View v) {
                                switch (v.getId()) {
                                        case R.id.gotoreservation:
                                                onSubmitClicked(v);
                                                break;
                                        case R.id.returntomap:
                                                onCancelClicked(v);
                                                break;
                                }
                        }
*/
                       /* public void onSubmitClicked(View view) {




                                dismiss();
                        }

                        public void onCancelClicked(View view) {

                                dismiss();
                        }
*/




