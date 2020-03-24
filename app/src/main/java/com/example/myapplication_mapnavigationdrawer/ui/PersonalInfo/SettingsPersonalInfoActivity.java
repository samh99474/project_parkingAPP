package com.example.myapplication_mapnavigationdrawer.ui.PersonalInfo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication_mapnavigationdrawer.MainActivity;
import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.viewmodel.MainActivityViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SettingsPersonalInfoActivity extends AppCompatActivity {

    private EditText set_name;
    private EditText set_phone_number;
    private EditText set_plate_number;
    private Button btn_info;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private MainActivityViewModel mViewModel;
    private static final String TAG = "PersonalInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        final FirebaseFirestore user_db = FirebaseFirestore.getInstance();

        btn_info = findViewById(R.id.btn_info);
        btn_info.setOnClickListener(new View.OnClickListener() {
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
                user_db.collection("users").document("i").set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {//固定文件ID

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
                btn_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SettingsPersonalInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

    }
}