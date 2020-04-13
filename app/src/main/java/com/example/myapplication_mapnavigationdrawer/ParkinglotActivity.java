package com.example.myapplication_mapnavigationdrawer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ParkinglotActivity extends AppCompatActivity {


    //if(預約||使用==0){ButtonA1可按}else{ButtonA1不可按 顏色改暗}
    private String choose;
    private RadioGroup radiogroup_parkinglot;
    private RadioButton A1;
    private RadioButton A2;
    private RadioButton A3;
    final private String string_A1 = "A1";
    final private String string_A2 = "A2";
    final private String string_A3 = "A3";
    private String string_choose_grid;
    private TextView parkinglot_name;
    private TextView parkinglot_price;
    private TextView parkinglot_address;
    private TextView pay;
    private TextView choose_grid;

    private String string_name, string_phone, string_email, string_license, string_reservate_time;
    private EditText editText_name, editText_phone, editText_email, editText_license, editText_reservate_time;

    private Button gotoreservation;
    private Button returntomap;
    private FirebaseFirestore mFirestore;
    private boolean using = false;
    private boolean reserved = false;
    private Query mQuery;
    private static final String TAG = "ParkinglotActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkinglot);

        parkinglot_name = findViewById(R.id.parkinglot_name);
        parkinglot_price = findViewById(R.id.parkinglot_price);
        parkinglot_address = findViewById(R.id.parkinglot_address);
        pay = findViewById(R.id.pay);
        editText_name = findViewById(R.id.editText_name);
        editText_phone = findViewById(R.id.editText_phone);
        editText_email = findViewById(R.id.editText_email);
        editText_license = findViewById(R.id.editText_license);
        editText_reservate_time = findViewById(R.id.editText_reservate_time);
        radiogroup_parkinglot = findViewById(R.id.radiogroup_parkinglot);
        A1 = findViewById(R.id.A1);
        A2 = findViewById(R.id.A2);
        A3 = findViewById(R.id.A3);

        choose_grid = findViewById(R.id.choose_grid);
        gotoreservation = findViewById(R.id.gotoreservation);
        returntomap = findViewById(R.id.returntomap);

        Bundle b = getIntent().getExtras();
        String string_parkinglot_name = b.getString("string_parkinglot_name");
        String string_parkinglot_simple_description = b.getString("parkinglot_simple_description");//price
        String string_parkinglot_address = b.getString("parkinglot_address");

        parkinglot_name.setText(string_parkinglot_name);
        parkinglot_price.setText(string_parkinglot_simple_description);
        pay.setText(string_parkinglot_simple_description);
        parkinglot_address.setText(string_parkinglot_address);

//寫case
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final DocumentReference docRef_user = firestore.collection("users").document("i");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            string_email = user.getEmail(); //抓取使用者email
        }


        final DocumentReference docRef_A1 = firestore.collection("parking grid").document("A1");
        final DocumentReference docRef_A2 = firestore.collection("parking grid").document("A2");
        final DocumentReference docRef_A3 = firestore.collection("parking grid").document("A3");

        docRef_A1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取parking grid/A1 是否使用中或已被預約
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                        reserved = (Boolean) document.get("預約中");
                        using = (Boolean) document.get("使用中");

                        if(reserved || using){  //假如A1車位已經被預約或使用中，使A1不能選取
                            A1.setClickable(false);
                            A1.setText(A1.getText()+"已被預約或使用中");
                            A1.setTextColor(Color.parseColor("#FF4B5A"));
                        }
                    } else {
                        Log.e(TAG, "No such document");
                    }
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });

        docRef_A2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取parking grid/A2 是否使用中或已被預約
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                        reserved = (Boolean) document.get("預約中");
                        using = (Boolean) document.get("使用中");

                        if(reserved || using){  //假如A1車位已經被預約或使用中，使A2不能選取
                            A2.setClickable(false);
                            A2.setText(A2.getText()+"已被預約或使用中");
                            A2.setTextColor(Color.parseColor("#FF4B5A"));
                        }
                    } else {
                        Log.e(TAG, "No such document");
                    }
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });

        docRef_A3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取parking grid/A3 是否使用中或已被預約
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                        reserved = (Boolean) document.get("預約中");
                        using = (Boolean) document.get("使用中");

                        if(reserved || using){  //假如A1車位已經被預約或使用中，使A3不能選取
                            A3.setClickable(false);
                            A3.setText(A3.getText()+"已被預約或使用中");
                            A3.setTextColor(Color.parseColor("#FF4B5A"));
                        }
                    } else {
                        Log.e(TAG, "No such document");
                    }
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });

        ///////////////////////////////////Step 1，選擇停車格/////////////////////////////////////////////
                radiogroup_parkinglot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                  public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId){
                            case R.id.A1:
                                string_choose_grid = "A1";
                                choose_grid.setText(string_choose_grid);
                                break;
                            case R.id.A2:
                                string_choose_grid = "A2";
                                choose_grid.setText(string_choose_grid);
                                break;
                            case R.id.A3:
                                string_choose_grid = "A3";
                                choose_grid.setText(string_choose_grid);
                                break;
                        }
                   }
              });


        docRef_user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取user
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                        string_name = document.getData().get("名子").toString();
                        editText_name.setText(string_name);
                        string_phone = document.getData().get("手機號碼").toString();
                        editText_phone.setText(string_phone);
                        string_license = document.getData().get("車牌號碼").toString();
                        editText_license.setText(string_license);

                        editText_email.setText(string_email);
                    } else {
                        Log.e(TAG, "No such document");
                    }
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });

        gotoreservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choose_grid.getText().toString().matches("") || editText_name.getText().toString().matches("") || editText_phone.getText().toString().matches("") ||
                        editText_email.getText().toString().matches("") || editText_license.getText().toString().matches("") || editText_reservate_time.getText().toString().matches("")){
                    Toast.makeText(ParkinglotActivity.this,"資料尚未填寫完畢", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder dialog_reservate_send = new AlertDialog.Builder(ParkinglotActivity.this);
                    dialog_reservate_send.setTitle("確定預約");
                    dialog_reservate_send.setMessage("確定送出後則無法取消");

                    dialog_reservate_send.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (choose_grid.getText().toString()){
                                case string_A1:
                                    Map<String, Boolean> A1info_boolean = new HashMap<>();
                                    Map<String, Object> A1info_string = new HashMap<>();
                                    A1info_boolean.put("預約中", true);
                                    A1info_string.put("姓名",editText_name.getText().toString());
                                    A1info_string.put("手機", editText_phone.getText().toString());
                                    A1info_string.put("預約車牌",editText_license.getText().toString());
                                    A1info_string.put("預約日期",editText_reservate_time.getText().toString());
                                    A1info_string.put("預約時間",editText_reservate_time.getText().toString());

                                    firestore.collection("parking grid").document(string_A1).set(A1info_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                    firestore.collection("parking grid").document(string_A1).set(A1info_string, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                    break;

                                case string_A2:
                                    Map<String, Boolean> A2info_boolean = new HashMap<>();
                                    Map<String, Object> A2info_string = new HashMap<>();
                                    A2info_boolean.put("預約中", true);
                                    A2info_string.put("姓名",editText_name.getText().toString());
                                    A2info_string.put("手機", editText_phone.getText().toString());
                                    A2info_string.put("預約車牌",editText_license.getText().toString());
                                    A2info_string.put("預約日期",editText_reservate_time.getText().toString());
                                    A2info_string.put("預約時間",editText_reservate_time.getText().toString());

                                    firestore.collection("parking grid").document(string_A2).set(A2info_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                    firestore.collection("parking grid").document(string_A2).set(A2info_string, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                    break;

                                case string_A3:
                                    Map<String, Boolean> A3info_boolean = new HashMap<>();
                                    Map<String, Object> A3info_string = new HashMap<>();
                                    A3info_boolean.put("預約中", true);
                                    A3info_string.put("姓名",editText_name.getText().toString());
                                    A3info_string.put("手機", editText_phone.getText().toString());
                                    A3info_string.put("預約車牌",editText_license.getText().toString());
                                    A3info_string.put("預約日期",editText_reservate_time.getText().toString());
                                    A3info_string.put("預約時間",editText_reservate_time.getText().toString());

                                    firestore.collection("parking grid").document(string_A3).set(A3info_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                    firestore.collection("parking grid").document(string_A3).set(A3info_string, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                    break;
                            }

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


