package com.example.myapplication_mapnavigationdrawer.ui.PersonalInfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.viewmodel.MainActivityViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SettingsPersonalInfoActivity extends AppCompatActivity {

    private EditText set_name;
    private EditText set_phone_number;
    private EditText set_plate_number;
    private Button btn_save;
    private ImageButton img_btn_select_head;
    private ImageButton btn_back;
    private String string_uid;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private MainActivityViewModel mViewModel;
    private static final String TAG = "PersonalInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        img_btn_select_head = findViewById(R.id.img_btn_select_head);
        btn_back = findViewById(R.id.btn_goback);

        final FirebaseFirestore user_db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            string_uid = user.getUid();     //抓取使用者UID
        }


        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                set_name = findViewById(R.id.input_username);
                set_phone_number = findViewById(R.id.input_phone_number);
                set_plate_number = findViewById(R.id.input_plate_number);
                String name = set_name.getText().toString();
                String phone_number = set_phone_number.getText().toString();
                String plate_number = set_plate_number.getText().toString();

                Map<String, Object> user = new HashMap<>();
                user.put("名子",name);
                user.put("手機號碼",phone_number);
                user.put("車牌號碼",plate_number);
                user_db.collection("users").document(string_uid).set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {//固定文件ID

                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "DocumentSnapshot successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error writing document", e);
                    }
                });

                       /*亂數文件ID user_db.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Snapshot added with ID:",documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error adding document", e);
                    }
                });*/

               /* 偷吃貓版本user_db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Snapshot added with ID:",documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Error adding document", e);
                            }
                        });
                Intent i = new Intent();
                setResult(1,i);
                finish();*/
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_btn_select_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_pic = new Intent(SettingsPersonalInfoActivity.this, select_upload_profile_head.class);
                startActivity(intent_pic);
            }
        });

    }
}