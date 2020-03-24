package com.example.myapplication_mapnavigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ReservationActivity extends AppCompatActivity {
    private String set_phone;
    private String set_license;
    private EditText phone;
    private EditText license;
    private EditText date;
    private EditText time;
    private Button reserve;
    private Button cancel;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private static final String TAG = "ReservationActivity";
    private TextView user_phone;
    private TextView user_license;
    private Context context;
    private Button btn_reserve;



    //寫入UserId UserName PhoneNumber LicensePlateNumber到firebase
    //預約中==1
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        final FirebaseFirestore users = FirebaseFirestore.getInstance();
        final FirebaseFirestore reservation_db = FirebaseFirestore.getInstance();
//抓取註冊資料直接顯示
        DocumentReference docRef = reservation_db.collection("users").document("i");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                        set_phone = document.getData().get("手機號碼").toString();
                        set_license = document.getData().get("車牌號碼").toString();

                    } else {
                        Log.e(TAG, "No such document");
                    }

                    user_phone.setText(set_phone);
                    user_license.setText(set_license);
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });
//上傳預約車位資料
        reserve = findViewById(R.id.reserve);
        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = findViewById(R.id.phone);
                String phone_number = phone.getText().toString();
                license = findViewById(R.id.license);
                String license_number = license.getText().toString();
                date = findViewById(R.id.date);
                String date_data = date.getText().toString();
                time = findViewById(R.id.time);
                String time_data = time.getText().toString();

            }
        });
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("手機", phone);
        reservation.put("車牌號碼", license);
        reservation.put("預約日期", date);
        reservation.put("預約時間", time);
        users.collection("users").add(reservation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("123", documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error adding document", e);
            }
        });
        Intent i = new Intent();
        setResult(1, i);
        finish();

    }
}


        /*cancel.setOnClickListener(new View.OnClickListener() {        //取消鍵
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/







//