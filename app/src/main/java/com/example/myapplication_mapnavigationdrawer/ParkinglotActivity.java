package com.example.myapplication_mapnavigationdrawer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication_mapnavigationdrawer.ui.PaySetting.ChoosePayMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;

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
    private TextView parkinglot_address, wallet_remaining, reservate_time;
    private TextView pay_simple_description, pay_full_description, show_parkinglot_phone, show_parkinglot_remain;
    private TextView space_temp;

    private String string_name, string_phone, string_email, string_uid, string_license,
            string_reservate_time, full_description, simple_description, parkinglot_price_number, string_parkinglot_address, parkinglot_phone,
            parkinglot_all_user_id;
    private String parkinglot_total_space;
    private ArrayList<String> list_parkinglot_all_user_id = new ArrayList<>();
    private Double string_wallet_remaining;
    private Long parkinglot_remain_space, number_wallet_remaining;

    private TextView Text_name, Text_phone, Text_email, Text_license;
    private int mYear, mMonth, mDay, mHour, mMin;
    private Timestamp timestamp;

    private Button gotoreservation;
    private Button returntomap;

    private static final String TAG = "ParkinglotActivity";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkinglot);

        parkinglot_name = findViewById(R.id.parkinglot_name);
        parkinglot_price = findViewById(R.id.parkinglot_price);
        parkinglot_address = findViewById(R.id.parkinglot_address);
        show_parkinglot_phone = findViewById(R.id.show_parkinglot_phone);
        show_parkinglot_remain = findViewById(R.id.show_parkinglot_remain);
        wallet_remaining = findViewById(R.id.wallet_remaining);
        pay_simple_description = findViewById(R.id.pay_simple_description);
        pay_full_description = findViewById(R.id.pay_full_description);
        Text_name = findViewById(R.id.Text_name);
        Text_phone = findViewById(R.id.Text_phone);
        Text_email = findViewById(R.id.Text_email);
        Text_license = findViewById(R.id.Text_license);
        reservate_time = findViewById(R.id.reservate_time);
        space_temp = findViewById(R.id.space_temp);
        space_temp.setText("");


        gotoreservation = findViewById(R.id.gotoreservation);
        returntomap = findViewById(R.id.returntomap);

        Bundle b = getIntent().getExtras();
        String string_parkinglot_name = b.getString("string_parkinglot_name");
        //String string_parkinglot_simple_description = b.getString("parkinglot_simple_description");//price
        //String string_parkinglot_address = b.getString("parkinglot_address");
        //final String string_parkinglot_phone = b.getString("parkinglot_phone");

        parkinglot_name.setText(string_parkinglot_name);
        //parkinglot_price.setText(string_parkinglot_simple_description);
        ///////////////////////////////////Step 3，付款資訊/////////////////////////////////////////////
        //pay.append(string_parkinglot_simple_description);  //費率
       // parkinglot_address.setText(string_parkinglot_address);
        //////////////////////////////////////////////////////////////////////////////////////////////


        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            string_email = user.getEmail();//抓取使用者email
            string_uid = user.getUid();     //抓取使用者UID

            firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("parking grid")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int count = 0;
                                for (DocumentSnapshot document : task.getResult()) {
                                    count++;

                                    parkinglot_all_user_id = document.getData().get("User_uid").toString();
                                    list_parkinglot_all_user_id.add(parkinglot_all_user_id);
                                }

                                if(list_parkinglot_all_user_id.toString().contains(string_uid)){

                                    gotoreservation.setText("已預約過此停車場");
                                    gotoreservation.setClickable(false);
                                    gotoreservation.setEnabled(false);
                                    Drawable img = getResources().getDrawable(R.drawable.ic_close_black_24dp);
                                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                    gotoreservation.setCompoundDrawables(img, null, null, null);

                                }

                            } else {
                                Log.d("record", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        final DocumentReference docRef_user = firestore.collection("users").document(string_uid);

        final DocumentReference docRef_parkinglot_info = firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("info").document("detail_info");

        if (docRef_parkinglot_info != null) {
            docRef_parkinglot_info.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取parking grid/A1 是否使用中或已被預約
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                            full_description = (String) document.get("full_description");
                            simple_description = (String) document.get("simple_description");
                            parkinglot_remain_space = (Long) document.get("剩餘車位");
                            parkinglot_total_space = (String) document.get("總車位");
                            parkinglot_price_number = (String) document.get("費率");
                            string_parkinglot_address = (String) document.get("地址");
                            parkinglot_phone = (String) document.get("電話");

                            if (parkinglot_remain_space <= 0) {
                                show_parkinglot_remain.setText("車位已滿");
                                gotoreservation.setText("車位已滿");
                                gotoreservation.setClickable(false);
                                gotoreservation.setEnabled(false);
                                Drawable img = getResources().getDrawable(R.drawable.ic_close_black_24dp);
                                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                gotoreservation.setCompoundDrawables(img, null, null, null);
                            }else {
                                show_parkinglot_remain.setText("總車位:"+parkinglot_total_space+"\t/\t剩餘車位:"+parkinglot_remain_space);
                            }

                            pay_simple_description.setText(simple_description);
                            pay_full_description.setText(full_description);
                            parkinglot_address.setText(string_parkinglot_address);
                            show_parkinglot_phone.setText(parkinglot_phone);


                        } else {
                            Log.e(TAG, "No such document");
                        }
                    } else {
                        Log.e(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }



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
                        if (document.getDouble("錢包") != null) {
                            number_wallet_remaining = document.getLong("錢包");
                            //number_wallet_remaining = Double.valueOf(string_wallet_remaining);

                            if (number_wallet_remaining.doubleValue() < 30) {
                                gotoreservation.setText("餘額過低");
                                gotoreservation.setClickable(false);
                                gotoreservation.setEnabled(false);
                                Drawable img = getResources().getDrawable(R.drawable.ic_close_black_24dp);
                                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                gotoreservation.setCompoundDrawables(img, null, null, null);
                            }
                        } else {
                            number_wallet_remaining = Long.valueOf(0);
                            gotoreservation.setText("無餘額");
                            gotoreservation.setClickable(false);
                            gotoreservation.setEnabled(false);
                            Drawable img = getResources().getDrawable(R.drawable.ic_close_black_24dp);
                            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                            gotoreservation.setCompoundDrawables(img, null, null, null);
                        }


                        Text_email.setText(string_email);
                        wallet_remaining.setText(String.valueOf(number_wallet_remaining));

                    } else {
                        Log.e(TAG, "No such document");
                    }
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });

        Calendar time_picker = Calendar.getInstance();
        mYear = time_picker.get(Calendar.YEAR);
        mMonth = time_picker.get(Calendar.MONTH);
        mDay = time_picker.get(Calendar.DAY_OF_MONTH);
        mHour = time_picker.get(Calendar.HOUR_OF_DAY);
        mMin = time_picker.get(Calendar.MINUTE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm z");    //取得現在日期時間
        String currentDateandTime = sdf.format(new Date());
        Toast.makeText(ParkinglotActivity.this, "現在時間:"
                + currentDateandTime, Toast.LENGTH_SHORT).show();

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

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.valueOf(currentDateandTime_yyyy));
        calendar.set(Calendar.MONTH, Integer.valueOf(currentDateandTime_MM) - 1);//因為bug會自動month+1，所以要減回去
        calendar.set(Calendar.DATE, Integer.valueOf(currentDateandTime_dd));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(currentDateandTime_hh));
        calendar.set(Calendar.MINUTE, Integer.valueOf(currentDateandTime_mm));
        calendar.set(Calendar.SECOND, 0);

        String string_date = DateFormat.format("yyyy-MM-dd HH:mm:ss", calendar).toString();//HH要大寫
        string_order_number = string_uid + currentDateandTime_MM + currentDateandTime_dd + currentDateandTime_hh + currentDateandTime_mm;
        reservate_time.setText(string_date);
        timestamp = new Timestamp(java.sql.Timestamp.valueOf(string_date));

/*
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
 */

        gotoreservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Text_name.getText().toString().matches("") || Text_phone.getText().toString().matches("") ||
                        Text_email.getText().toString().matches("") || Text_license.getText().toString().matches("")) {
                    Toast.makeText(ParkinglotActivity.this, "資料尚未填寫完畢", Toast.LENGTH_SHORT).show();
                } else {

                    AlertDialog.Builder dialog_reservate_send = new AlertDialog.Builder(ParkinglotActivity.this);
                    dialog_reservate_send.setTitle("確定預約");
                    dialog_reservate_send.setMessage("(一)車輛請於預約時間之後30分鐘內進場。" +
                            "\n(二)若預約時數逾30分鐘內車輛未進場，將自動取消訂單。" +
                            "\n(三)確定預約後，預約車牌將無法變動。" +
                            "\n(四)車輛出場時，將自動扣款，完成訂單。" +
                            "\n\n確定送出後則無法更改內容");

                    dialog_reservate_send.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Map<String, Boolean> A1info_boolean = new HashMap<>();
                            Map<String, Object> A1info_string = new HashMap<>();
                            Map<String, Timestamp> A1info_timestamp = new HashMap<>();
                            A1info_timestamp.put("預約日期時間", timestamp);

                            A1info_boolean.put("reservating", true);
                            A1info_boolean.put("using", false);
                            A1info_string.put("姓名", Text_name.getText().toString());
                            A1info_string.put("手機", Text_phone.getText().toString());
                            A1info_string.put("User_uid", string_uid);
                            A1info_string.put("預約車牌", Text_license.getText().toString());
                            A1info_string.put("訂單編號", string_order_number);

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
                            Map<String, Object> A1_record_number = new HashMap<>();
                            Map<String, Object> A1_record_string = new HashMap<>();
                            Map<String, Timestamp> A1record_timestamp = new HashMap<>();
                            A1record_timestamp.put("預約日期時間", timestamp);

                            A1_record_number.put("parking_total_sec",0);
                            A1_record_boolean.put("按下結束按鈕", false);
                            A1_record_boolean.put("是否重新計費", false);
                            A1_record_boolean.put("訂單完成", false);
                            A1_record_boolean.put("訂單取消", false);
                            A1_record_boolean.put("reservating", true);
                            A1_record_boolean.put("using", false);
                            A1_record_boolean.put("overtime_thirty", false);
                            A1_record_boolean.put("had_notified_car_in", false);
                            A1_record_boolean.put("had_notified_car_out", false);
                            A1_record_string.put("停車場", parkinglot_name.getText().toString());
                            A1_record_string.put("費率", parkinglot_price.getText().toString());
                            A1_record_string.put("地址", parkinglot_address.getText().toString());
                            A1_record_string.put("聯絡電話", parkinglot_phone);
                            A1_record_string.put("訂單編號", string_order_number);
                            A1_record_string.put("預約車牌", string_license);
                            A1_record_string.put("應付金額", "0");
                            //A1info_string.put("結束時間日期","");
                            //A1info_string.put("應付金額","");
                            firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(A1_record_number, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                            //跳通知，告知10分鐘內離場
                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            int notificationId = createID();
                            String channelId = "channel-id";
                            String channelName = "停車場預約APP";
                            int importance = NotificationManager.IMPORTANCE_HIGH;

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                NotificationChannel mChannel = new NotificationChannel(
                                        channelId, channelName, importance);
                                notificationManager.createNotificationChannel(mChannel);
                            }

                            NotificationCompat.InboxStyle inboxStyle =
                                    new NotificationCompat.InboxStyle();

                            String[] events = {
                                    "(一)車輛請於預約時間之後30分鐘內進場。",
                                    "(二)若預約時數逾30分鐘內車輛未進場，將自動取消訂單。",
                                    "(三)車輛出場時，將自動扣款，完成訂單。"};

                            // Sets a title for the Inbox in expanded layout
                            inboxStyle.setBigContentTitle("您已成功預約\t-\t" + parkinglot_name.getText().toString());
                            // Moves events into the expanded layout
                            for (int i = 0; i < events.length; i++) {
                                inboxStyle.addLine(events[i]);
                            }

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                                    .setSmallIcon(R.mipmap.loticon)//R.mipmap.ic_launcher
                                    .setContentTitle("您已成功預約:")
                                    .setContentText("您已成功預約\t-\t" + parkinglot_name.getText().toString() + "\t請在30分鐘內進場，若有疑問可聯絡該停車場客服電話，尋求協助。")
                                    .setStyle(inboxStyle)
                                    .setVibrate(new long[]{100, 250})
                                    .setLights(Color.YELLOW, 500, 5000)
                                    .setAutoCancel(true)
                                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                            stackBuilder.addNextIntent(new Intent(ParkinglotActivity.this, MainActivity.class));
                            Intent myintent = new Intent(ParkinglotActivity.this, MainActivity.class);
                            PendingIntent contentIntent = PendingIntent.getActivity(ParkinglotActivity.this, 0,
                                    myintent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(contentIntent);

                            notificationManager.notify(notificationId, mBuilder.build());

                            Toast.makeText(ParkinglotActivity.this, "預約成功!", Toast.LENGTH_SHORT).show();

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

        //資料更新時，刷新頁面
        firestore.collection("reservatable parkinglot").document(parkinglot_name.getText().toString()).collection("info")
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen error", e);
                            return;
                        }

                        for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                            if (change.getType() == DocumentChange.Type.ADDED) {
                                Log.d(TAG, "New city:" + change.getDocument().getData());
                            }

                            String source = querySnapshot.getMetadata().isFromCache() ?
                                    "local cache" : "server";
                            Log.d(TAG, "Data fetched from " + source);

                            DocumentSnapshot documentSnapshot = change.getDocument();
                            String id = documentSnapshot.getId();
                            int oldIndex = change.getOldIndex();
                            int newIndex = change.getNewIndex();

                            switch (change.getType()) {
                                case MODIFIED:
                                    try {
                                        //Toast.makeText(getActivity(),"資料修改更新"+id+"\nold:"+oldIndex+"\nnew:"+newIndex, Toast.LENGTH_SHORT).show();
                                        //刷新refresh Activity
                                        Intent intent = new Intent(ParkinglotActivity.this,
                                                ParkinglotActivity.class);
                                        Bundle b = new Bundle();    //資訊放入Bundle
                                        b.putString("string_parkinglot_name",parkinglot_name.getText().toString());
                                        intent.putExtras(b);
                                        startActivity(intent);

                                    }catch (Exception e1){
                                        e1.printStackTrace();
                                    }

                                    break;
                                /*
                            case ADDED:
                                Toast.makeText(getActivity(),"資料加入"+id+"\nold:"+oldIndex+"\nnew:"+newIndex, Toast.LENGTH_SHORT).show();
                                break;
                            case REMOVED:
                                Toast.makeText(getActivity(),"資料移除"+id+"\nold:"+oldIndex+"\nnew:"+newIndex, Toast.LENGTH_SHORT).show();
                                break;
                                 */
                            }
                        }
                    }
                });


    }
    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new java.text.SimpleDateFormat("ddHHmmss", Locale.FRENCH).format(now));
        return id;
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


