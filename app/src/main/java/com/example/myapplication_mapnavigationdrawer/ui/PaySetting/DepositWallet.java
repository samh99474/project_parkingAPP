package com.example.myapplication_mapnavigationdrawer.ui.PaySetting;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication_mapnavigationdrawer.MainActivity;
import com.example.myapplication_mapnavigationdrawer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;

public class DepositWallet extends AppCompatActivity {
    private RadioButton radioButton_method, price_100, price_250, price_500, price_1000;
    private RadioGroup radioGroup_wallet_deposit_price;
    private Button btb_deposit;
    private String choose_method="", choose_price="", string_uid, wallet_remaining;
    private Integer wallet_after_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_wallet);
        radioButton_method = findViewById(R.id.radioButton_method);
        price_100 = findViewById(R.id.price_100);
        price_250 = findViewById(R.id.price_250);
        price_500 = findViewById(R.id.price_500);
        price_1000 = findViewById(R.id.price_1000);
        radioGroup_wallet_deposit_price = findViewById(R.id.radioGroup_wallet_deposit_price);
        btb_deposit = findViewById(R.id.btb_deposit);


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
                                    if (document.getData().get("錢包") != null) {
                                        wallet_remaining = document.getData().get("錢包").toString();
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

        price_100.setClickable(false);
        price_250.setClickable(false);
        price_500.setClickable(false);
        price_1000.setClickable(false);
        btb_deposit.setBackgroundColor(Color.GRAY);

        RadioGroup rg_method = (RadioGroup) findViewById(R.id.radioGroup_wallet_deposit_method);
        rg_method.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton_method:
                        choose_method = radioButton_method.getText().toString();

                        price_100.setClickable(true);
                        price_250.setClickable(true);
                        price_500.setClickable(true);
                        price_1000.setClickable(true);

                        RadioGroup rg_price = (RadioGroup) findViewById(R.id.radioGroup_wallet_deposit_price);
                        rg_price.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId){
                                    case R.id.price_100:
                                        choose_price = "100";
                                        break;
                                    case R.id.price_250:
                                        choose_price = "250";
                                        break;
                                    case R.id.price_500:
                                        choose_price = "500";
                                        break;
                                    case R.id.price_1000:
                                        choose_price = "1000";
                                        break;
                                }
                                if(choose_method.matches("")){
                                    btb_deposit.setClickable(false);
                                    btb_deposit.setFocusable(false);
                                    btb_deposit.setBackgroundColor(Color.GRAY);
                                }
                                else {
                                    btb_deposit.setClickable(true);
                                    btb_deposit.setFocusable(true);
                                    btb_deposit.setBackgroundResource(R.drawable.about_top_background);

                                    btb_deposit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            wallet_after_add = Integer.parseInt(wallet_remaining)+Integer.parseInt(choose_price);
                                            Map<String, Object> user_wallet = new HashMap<>();
                                            user_wallet.put("錢包",wallet_after_add);
                                            user_db.collection("users").document(string_uid).set(user_wallet, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {//固定文件ID

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

                                            Toast.makeText(DepositWallet.this, "您已成功加值"+choose_price+"\n"+choose_method, Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent(DepositWallet.this,
                                                    MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });
                        break;
                }
            }
        });
    }

}
