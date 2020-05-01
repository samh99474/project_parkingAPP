package com.example.myapplication_mapnavigationdrawer.ui.PersonalInfo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication_mapnavigationdrawer.MainActivity;
import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.viewmodel.MainActivityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private ImageView img_btn_select_head;
    private ImageButton btn_back;
    private String string_uid;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private MainActivityViewModel mViewModel;
    private static final String TAG = "PersonalInfoActivity";
    private String string_head_name, name, phone, license;

    private boolean lockAspectRatio = false, setBitmapMaxWidthHeight = false;
    private int ASPECT_RATIO_X = 16, ASPECT_RATIO_Y = 9, bitmapMaxWidth = 1000, bitmapMaxHeight = 1000;
    private int IMAGE_COMPRESSION = 80;
    public static final String INTENT_ASPECT_RATIO_X = "aspect_ratio_x";
    public static final String INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y";
    public static final String INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio";
    public static final String INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality";
    public static final String INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height";
    public static final String INTENT_BITMAP_MAX_WIDTH = "max_width";
    public static final String INTENT_BITMAP_MAX_HEIGHT = "max_height";
    public static final String INTENT_IMAGE_PICKER_OPTION = "image_picker_option";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        img_btn_select_head = findViewById(R.id.img_btn_select_head);
        btn_back = findViewById(R.id.btn_goback);

        set_name = findViewById(R.id.input_username);
        set_phone_number = findViewById(R.id.input_phone_number);
        set_plate_number = findViewById(R.id.input_plate_number);


        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(getApplicationContext(), "getString(R.string.toast_image_intent_null)", Toast.LENGTH_LONG).show();
            return;
        }

        ASPECT_RATIO_X = intent.getIntExtra(INTENT_ASPECT_RATIO_X, ASPECT_RATIO_X);
        ASPECT_RATIO_Y = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, ASPECT_RATIO_Y);
        IMAGE_COMPRESSION = intent.getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION);
        lockAspectRatio = intent.getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, false);
        setBitmapMaxWidthHeight = intent.getBooleanExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false);
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth);
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight);

        Bundle b = getIntent().getExtras();
        if(b != null){
        String string_user_name = b.getString("string_user_name");
        String string_user_phone = b.getString("string_user_phone");
        String string_user_license = b.getString("string_user_license");

            set_name.setText(string_user_name);
            set_phone_number.setText(string_user_phone);
            set_plate_number.setText(string_user_license);
        }


        final FirebaseFirestore user_db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            string_uid = user.getUid();     //抓取使用者UID

            DocumentReference docRef = user_db.collection("users").document(string_uid);
            if(docRef != null){
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.e(TAG, "DocumentSnapshot data: " + document.getData());

                                if (document.exists()) {
                                    Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                                    if (document.getData().get("名子") != null) {
                                        name = document.getData().get("名子").toString();
                                    }
                                    if (document.getData().get("手機號碼") != null) {
                                        phone = document.getData().get("手機號碼").toString();
                                    }
                                    if (document.getData().get("車牌號碼") != null) {
                                        license = document.getData().get("車牌號碼").toString();
                                    }

                                    set_name.setText(name);
                                    set_phone_number.setText(phone);
                                    set_plate_number.setText(license);

                                    if (document.getData().get("大頭貼") != null) {
                                        string_head_name = document.getData().get("大頭貼").toString();
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                        StorageReference dateRef = storageRef.child("profile_pic_" + string_uid + "/" + string_head_name);
                                        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri downloadUrl) {
                                                Glide.with(SettingsPersonalInfoActivity.this)
                                                        .load(downloadUrl)
                                                        .into(img_btn_select_head);
                                            }
                                        });
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
        }

        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                Intent intent = new Intent(SettingsPersonalInfoActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsPersonalInfoActivity.this, MainActivity.class);
                startActivity(intent);

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