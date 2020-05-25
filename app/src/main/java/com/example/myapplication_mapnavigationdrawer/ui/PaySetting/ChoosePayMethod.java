package com.example.myapplication_mapnavigationdrawer.ui.PaySetting;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication_mapnavigationdrawer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.Transaction;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;
import static com.google.firebase.firestore.DocumentChange.Type.MODIFIED;

public class ChoosePayMethod extends AppCompatActivity {
    private RadioButton radio_btb_my_wallet ;
    private Button btn_wallet_deposit;
    private TextView choose_pay_method_show_wallet_remaining;
    private String string_uid,  wallet_remaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pay_method);
        radio_btb_my_wallet = findViewById(R.id.radio_btb_my_wallet);
        btn_wallet_deposit = findViewById(R.id.btn_wallet_deposit);
        choose_pay_method_show_wallet_remaining = findViewById(R.id.choose_pay_method_show_wallet_remaining);

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
                                    choose_pay_method_show_wallet_remaining.setText(wallet_remaining);
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


            //資料更新時，刷新頁面
            user_db.collection("users").document(string_uid).collection("record")
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

                                switch (change.getType()){
                                    case MODIFIED:
                                        //Toast.makeText(getActivity(),"資料修改更新"+id+"\nold:"+oldIndex+"\nnew:"+newIndex, Toast.LENGTH_SHORT).show();

                                        //刷新refresh Activity
                                        Intent intent = new Intent(ChoosePayMethod.this,
                                                ChoosePayMethod.class);
                                        startActivity(intent);

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

        btn_wallet_deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoosePayMethod.this,
                        DepositWallet.class);
                startActivity(intent);
            }
        });

        radio_btb_my_wallet.setChecked(true);
        RadioGroup rg1 = (RadioGroup) findViewById(R.id.radioGroup_choose_pay_method);
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_btb_my_wallet:

                        break;
                }
            }
        });
    }
}
