package com.example.myapplication_mapnavigationdrawer;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ParkinglotActivity extends AppCompatActivity {


    //if(預約||使用==0){ButtonA1可按}else{ButtonA1不可按 顏色改暗}
    private String choose;

    private RadioButton A1;
    private RadioButton A2;
    private RadioButton A3;
    final private String string_A1 = "A1";
    final private String string_A2 = "A2";
    final private String string_A3 = "A3";
    private String string_order_number;
    private TextView parkinglot_name;
    private TextView parkinglot_price;
    private TextView parkinglot_address;
    private TextView pay;
    private TextView space_temp;

    private String string_name, string_phone, string_email, string_uid, string_license, string_reservate_time;
    private TextView Text_name, Text_phone, Text_email, Text_license;
    private int mYear, mMonth, mDay, mHour, mMin;
    private Timestamp timestamp;

    private Button gotoreservation;
    private Button returntomap;
    private Button btn_timepicker;

    private static final String TAG = "ParkinglotActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkinglot);

        parkinglot_name = findViewById(R.id.parkinglot_name);
        parkinglot_price = findViewById(R.id.parkinglot_price);
        parkinglot_address = findViewById(R.id.parkinglot_address);
        pay = findViewById(R.id.pay);
        Text_name = findViewById(R.id.Text_name);
        Text_phone = findViewById(R.id.Text_phone);
        Text_email = findViewById(R.id.Text_email);
        Text_license = findViewById(R.id.Text_license);
        btn_timepicker = findViewById(R.id.btn_timepicker);
        space_temp = findViewById(R.id.space_temp);
        space_temp.setText("");


        gotoreservation = findViewById(R.id.gotoreservation);
        returntomap = findViewById(R.id.returntomap);

        Bundle b = getIntent().getExtras();
        String string_parkinglot_name = b.getString("string_parkinglot_name");
        String string_parkinglot_simple_description = b.getString("parkinglot_simple_description");//price
        String string_parkinglot_address = b.getString("parkinglot_address");

        parkinglot_name.setText(string_parkinglot_name);
        parkinglot_price.setText(string_parkinglot_simple_description);
        ///////////////////////////////////Step 3，付款資訊/////////////////////////////////////////////
        pay.setText(string_parkinglot_simple_description);  //費率
        parkinglot_address.setText(string_parkinglot_address);
        //////////////////////////////////////////////////////////////////////////////////////////////


        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            string_email = user.getEmail();//抓取使用者email
            string_uid = user.getUid();     //抓取使用者UID
        }
        final DocumentReference docRef_user = firestore.collection("users").document(string_uid);

        final DocumentReference docRef_A1 = firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid").document("A1");
        final DocumentReference docRef_A2 = firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid").document("A2");
        final DocumentReference docRef_A3 = firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid").document("A3");

        /*
        if(docRef_A1 != null){
            docRef_A1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取parking grid/A1 是否使用中或已被預約
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                            reserved = (Boolean) document.get("預約中");
                            if(document.get("使用中") != null){
                                using = (Boolean) document.get("使用中");
                            }


                            if(space_temp.getText().toString().matches("")){
                                if(reserved || using){  //假如A1車位已經被預約或使用中，使A2不能選取
                                    space_temp.setText("");
                                }else {
                                    space_temp.setText("A1");
                                }
                            }


                        } else {
                            Log.e(TAG, "No such document");
                        }
                    } else {
                        Log.e(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        if(docRef_A2 != null) {

            docRef_A2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取parking grid/A2 是否使用中或已被預約
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                            reserved = (Boolean) document.get("預約中");
                            if(document.get("使用中") != null){
                                using = (Boolean) document.get("使用中");
                            }

                            if(space_temp.getText().toString().matches("")){
                                if(reserved || using){  //假如A1車位已經被預約或使用中，使A2不能選取
                                    space_temp.setText("");
                                }else {
                                    space_temp.setText("A2");
                                }
                            }

                        } else {
                            Log.e(TAG, "No such document");
                        }
                    } else {
                        Log.e(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        if(docRef_A3 != null) {
            docRef_A3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取parking grid/A3 是否使用中或已被預約
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                            reserved = (Boolean) document.get("預約中");
                            if(document.get("使用中") != null){
                                using = (Boolean) document.get("使用中");
                            }

                            if(space_temp.getText().toString().matches("")){
                                if(reserved || using){  //假如A1車位已經被預約或使用中，使A2不能選取
                                    space_temp.setText("");
                                }else {
                                    space_temp.setText("A3");
                                }
                            }

                        } else {
                            Log.e(TAG, "No such document");
                        }
                    } else {
                        Log.e(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
            ///////////////////////////////////判斷停車場車位是否足夠/////////////////////////////////////////////
        Toast.makeText(ParkinglotActivity.this,space_temp.getText().toString(), Toast.LENGTH_SHORT).show();
        if(space_temp.getText().toString().matches("A1")){
            string_choose_grid = "A1";
        }else {
            if(space_temp.getText().toString().matches("A2")){
                string_choose_grid = "A2";
            }else {
                if(space_temp.getText().toString().matches("A3")){
                    string_choose_grid = "A3";
                }else{
                    string_choose_grid = "";  //停車位已滿
                    gotoreservation.setText("車位已滿");
                    //gotoreservation.setClickable(false);
                    //gotoreservation.setEnabled(false);
                }
            }
        }
        string_choose_grid = "A3";
*/


///////////////////////////////////Step 1，預約資料/////////////////////////////////////////////
        docRef_user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取user
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e(TAG, "DocumentSnapshot data: " + document.getData());

                        if (document.getData().get("名子") != null) {
                            string_name = document.getData().get("名子").toString();
                            Text_name.setText(string_name);
                        }
                        if (document.getData().get("手機號碼") != null) {
                            string_phone = document.getData().get("手機號碼").toString();
                            Text_phone.setText(string_phone);
                        }
                        if (document.getData().get("車牌號碼") != null) {
                            string_license = document.getData().get("車牌號碼").toString();
                            Text_license.setText(string_license);

                        }
                        Text_email.setText(string_email);

                    } else {
                        Log.e(TAG, "No such document");
                    }
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });

        btn_timepicker.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                Calendar time_picker = Calendar.getInstance();
                mYear = time_picker.get(Calendar.YEAR);
                mMonth = time_picker.get(Calendar.MONTH);
                mDay = time_picker.get(Calendar.DAY_OF_MONTH);
                mHour = time_picker.get(Calendar.HOUR_OF_DAY);
                mMin = time_picker.get(Calendar.MINUTE);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm z");    //取得現在日期時間
                String currentDateandTime = sdf.format(new Date());
                Toast.makeText(ParkinglotActivity.this,"現在時間:"
                        +currentDateandTime, Toast.LENGTH_SHORT).show();

                SimpleDateFormat sdf_yyyy_mm_dd = new SimpleDateFormat("yyyy/MM/dd");    //取得現在日期時間
                final String currentDateandTime_yyyy_mm_dd = sdf_yyyy_mm_dd.format(new Date());

                SimpleDateFormat sdf_yyyy = new SimpleDateFormat("yyyy");    //取得現在日期時間 (月)
                final String currentDateandTime_yyyy = sdf_yyyy.format(new Date());
                SimpleDateFormat sdf_MM = new SimpleDateFormat("MM");    //取得現在日期時間 (月)
                final String currentDateandTime_MM = sdf_MM.format(new Date());
                SimpleDateFormat sdf_dd = new SimpleDateFormat("dd");    //取得現在日期時間 (天)
                final String currentDateandTime_dd = sdf_dd.format(new Date());
                SimpleDateFormat sdf_hh = new SimpleDateFormat("HH");    //取得現在日期時間 (時)
                final String currentDateandTime_hh = sdf_hh.format(new Date());
                SimpleDateFormat sdf_mm = new SimpleDateFormat("mm");    //取得現在日期時間 (分)
                final String currentDateandTime_mm = sdf_mm.format(new Date());

                TimePickerDialog timePickerDialog = new TimePickerDialog(ParkinglotActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            if(minute<10){
                                btn_timepicker.setText("\t"+currentDateandTime_yyyy_mm_dd + "," + hourOfDay + ":" + "0" + minute);
                                string_order_number = string_uid+currentDateandTime_MM+currentDateandTime_dd+hourOfDay+ "0" + minute;
                            }else {
                                btn_timepicker.setText("\t"+currentDateandTime_yyyy_mm_dd + "," + hourOfDay + ":" + minute);
                                string_order_number = string_uid+currentDateandTime_MM+currentDateandTime_dd+hourOfDay + minute;
                            }

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, Integer.valueOf(currentDateandTime_yyyy));
                        calendar.set(Calendar.MONTH, Integer.valueOf(currentDateandTime_MM)-1);//因為bug會自動month+1，所以要減回去
                        calendar.set(Calendar.DATE, Integer.valueOf(currentDateandTime_dd));
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);

                        String string_date = DateFormat.format("yyyy-MM-dd HH:mm:ss", calendar).toString();//HH要大寫
                        timestamp = new Timestamp(java.sql.Timestamp.valueOf( string_date ));


                    }
                },mHour, mMin, true);
                timePickerDialog.show();
            }
        });

        gotoreservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Text_name.getText().toString().matches("") || Text_phone.getText().toString().matches("") ||
                        Text_email.getText().toString().matches("") || Text_license.getText().toString().matches("") || btn_timepicker.getText().toString().matches("請選擇時間")){
                    Toast.makeText(ParkinglotActivity.this,"資料尚未填寫完畢", Toast.LENGTH_SHORT).show();
                }
                else {

                    AlertDialog.Builder dialog_reservate_send = new AlertDialog.Builder(ParkinglotActivity.this);
                    dialog_reservate_send.setTitle("確定預約");
                    dialog_reservate_send.setMessage("確定送出後則無法取消");

                    dialog_reservate_send.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Map<String, Boolean> A1info_boolean = new HashMap<>();
                            Map<String, Object> A1info_string = new HashMap<>();
                            Map<String, Timestamp> A1info_timestamp = new HashMap<>();
                            A1info_timestamp.put("預約日期時間", timestamp);

                            A1info_boolean.put("預約中", true);
                            A1info_boolean.put("使用中", false);
                            A1info_string.put("姓名",Text_name.getText().toString());
                            A1info_string.put("手機", Text_phone.getText().toString());
                            A1info_string.put("User_uid",string_uid);
                            A1info_string.put("預約車牌",Text_license.getText().toString());
                            A1info_string.put("訂單編號",string_order_number);

                            firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid").document(Text_license.getText().toString()).set(A1info_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e(TAG, "A1 successfully reserved!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error writing document", e);
                                }
                            });

                            firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid").document(Text_license.getText().toString()).set(A1info_string, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e(TAG, "A1 successfully reserved!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error writing document", e);
                                }
                            });

                            firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid").document(Text_license.getText().toString()).set(A1info_timestamp, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e(TAG, "A1 successfully reserved!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error writing document", e);
                                }
                            });

                            ////////寫入user預約紀錄////
                            Map<String, Boolean> A1_record_boolean = new HashMap<>();
                            Map<String, Object> A1_record_string = new HashMap<>();
                            Map<String, Timestamp> A1record_timestamp = new HashMap<>();
                            A1record_timestamp.put("預約日期時間", timestamp);

                            A1_record_boolean.put("按下結束按鈕", false);
                            A1_record_boolean.put("是否重新計費", false);
                            A1_record_boolean.put("訂單完成", false);
                            A1_record_boolean.put("預約中", true);
                            A1_record_boolean.put("使用中", false);
                            A1_record_string.put("停車場",parkinglot_name.getText().toString());
                            A1_record_string.put("費率", parkinglot_price.getText().toString());
                            A1_record_string.put("地址",parkinglot_address.getText().toString());
                            A1_record_string.put("訂單編號",string_order_number);
                            A1_record_string.put("預約車牌",string_license);
                            //A1info_string.put("結束時間日期","");
                            //A1info_string.put("應付金額","");
                            firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(A1_record_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e(TAG, "successfully write to user record!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error writing document", e);
                                }
                            });

                            firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(A1_record_string, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e(TAG, "successfully write to user record!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error writing document", e);
                                }
                            });

                            firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(A1record_timestamp, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e(TAG, "successfully write to user record!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error writing document", e);
                                }
                            });

                            /*
                            switch (string_choose_grid){
                                case string_A1:
                                    Map<String, Boolean> A1info_boolean = new HashMap<>();
                                    Map<String, Object> A1info_string = new HashMap<>();
                                    A1info_boolean.put("預約中", true);
                                    A1info_string.put("姓名",Text_name.getText().toString());
                                    A1info_string.put("手機", Text_phone.getText().toString());
                                    A1info_string.put("User_uid",string_uid);
                                    A1info_string.put("預約車牌",Text_license.getText().toString());
                                    A1info_string.put("預約日期時間",btn_timepicker.getText().toString());
                                    A1info_string.put("訂單編號",string_order_number);

                                    firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid").document(Text_license.getText().toString()).set(A1info_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "A1 successfully reserved!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });

                                    firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid").document(Text_license.getText().toString()).set(A1info_string, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "A1 successfully reserved!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });

                                    ////////寫入user預約紀錄////
                                    Map<String, Boolean> A1_record_boolean = new HashMap<>();
                                    Map<String, Object> A1_record_string = new HashMap<>();
                                    A1_record_boolean.put("訂單完成", false);
                                    A1_record_boolean.put("預約中", true);
                                    A1_record_boolean.put("使用中", false);
                                    A1_record_string.put("停車場",parkinglot_name.getText().toString());
                                    A1_record_string.put("費率", parkinglot_price.getText().toString());
                                    A1_record_string.put("車位",string_A1);
                                    A1_record_string.put("預約日期時間",btn_timepicker.getText().toString());
                                    A1_record_string.put("地址",parkinglot_address.getText().toString());
                                    A1_record_string.put("訂單編號",string_order_number);
                                    A1_record_string.put("預約車牌",string_license);
                                    //A1info_string.put("結束時間日期","");
                                    //A1info_string.put("應付金額","");
                                    firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(A1_record_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "successfully write to user record!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });

                                    firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(A1_record_string, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "successfully write to user record!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });


                                    break;

                                case string_A2:
                                    Map<String, Boolean> A2info_boolean = new HashMap<>();
                                    Map<String, Object> A2info_string = new HashMap<>();
                                    A2info_boolean.put("預約中", true);
                                    A2info_string.put("姓名",Text_name.getText().toString());
                                    A2info_string.put("手機", Text_phone.getText().toString());
                                    A2info_string.put("User_uid",string_uid);
                                    A2info_string.put("預約車牌",Text_license.getText().toString());
                                    A2info_string.put("預約日期時間",btn_timepicker.getText().toString());
                                    A2info_string.put("訂單編號",string_order_number);

                                    firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid").document(Text_license.getText().toString()).set(A2info_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "A2 successfully reserved!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });

                                    firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid").document(Text_license.getText().toString()).set(A2info_string, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "A2 successfully reserved!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });

                                    ////////寫入user預約紀錄////
                                    Map<String, Boolean> A2_record_boolean = new HashMap<>();
                                    Map<String, Object> A2_record_string = new HashMap<>();
                                    A2_record_boolean.put("訂單完成", false);
                                    A2_record_boolean.put("預約中", true);
                                    A2_record_boolean.put("使用中", false);
                                    A2_record_string.put("停車場",parkinglot_name.getText().toString());
                                    A2_record_string.put("費率", parkinglot_price.getText().toString());
                                    A2_record_string.put("車位",string_A2);
                                    A2_record_string.put("預約日期時間",btn_timepicker.getText().toString());
                                    A2_record_string.put("地址",parkinglot_address.getText().toString());
                                    A2_record_string.put("訂單編號",string_order_number);
                                    A2_record_string.put("預約車牌",string_license);
                                    //A1info_string.put("結束時間日期","");
                                    //A1info_string.put("應付金額","");
                                    firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(A2_record_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "successfully write to user record!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });

                                    firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(A2_record_string, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "successfully write to user record!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });

                                    break;

                                case string_A3:
                                    Map<String, Boolean> A3info_boolean = new HashMap<>();
                                    Map<String, Object> A3info_string = new HashMap<>();
                                    Map<String, Timestamp> A3info_timestamp = new HashMap<>();
                                    A3info_boolean.put("預約中", true);
                                    A3info_string.put("姓名",Text_name.getText().toString());
                                    A3info_string.put("手機", Text_phone.getText().toString());
                                    A3info_string.put("User_uid",string_uid);
                                    A3info_string.put("預約車牌",Text_license.getText().toString());
                                    //A3info_string.put("預約日期時間",btn_timepicker.getText().toString());
                                    A3info_string.put("訂單編號",string_order_number);

                                    A3info_timestamp.put("預約日期時間",timestamp);

                                    firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid").document(Text_license.getText().toString()).set(A3info_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "A3 successfully reserved!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });

                                    firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid").document(Text_license.getText().toString()).set(A3info_string, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "A3 successfully reserved!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });

                                    ////////寫入user預約紀錄////
                                    Map<String, Boolean> A3_record_boolean = new HashMap<>();
                                    Map<String, Object> A3_record_string = new HashMap<>();
                                    A3_record_boolean.put("訂單完成", false);
                                    A3_record_boolean.put("預約中", true);
                                    A3_record_boolean.put("使用中", false);
                                    A3_record_string.put("停車場",parkinglot_name.getText().toString());
                                    A3_record_string.put("費率", parkinglot_price.getText().toString());
                                    A3_record_string.put("車位",string_A3);
                                    A3_record_string.put("預約日期時間",btn_timepicker.getText().toString());
                                    A3_record_string.put("地址",parkinglot_address.getText().toString());
                                    A3_record_string.put("訂單編號",string_order_number);
                                    A3_record_string.put("預約車牌",string_license);
                                    //A1info_string.put("結束時間日期","");
                                    //A1info_string.put("應付金額","");
                                    firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(A3_record_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "successfully write to user record!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });

                                    firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(A3_record_string, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "successfully write to user record!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });


                                    break;
                            }

                             */

                            Toast.makeText(ParkinglotActivity.this,"預約成功!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ParkinglotActivity.this, MainActivity.class);
                            startActivity(intent);

                        }
                    });

                    dialog_reservate_send.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    dialog_reservate_send.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog_reservate_send.show();
                }
            }
        });



        returntomap.setOnClickListener(new View.OnClickListener() { //取消
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
        }

           /* public void onChildChanged(DataSnapshot A1info, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Comment newComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();
            }
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


