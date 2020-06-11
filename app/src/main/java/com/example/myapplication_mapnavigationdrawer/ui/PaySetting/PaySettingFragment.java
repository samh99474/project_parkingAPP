package com.example.myapplication_mapnavigationdrawer.ui.PaySetting;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.ui.PersonalInfo.SettingsPersonalInfoActivity;
import com.example.myapplication_mapnavigationdrawer.ui.record.RecordFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;

public class PaySettingFragment extends Fragment {

    private ReservateViewModel reservateViewModel;
    private TextView show_wallet_remaining;
    private Button btn_setting_wallet;
    private String string_uid;
    private Long wallet_remaining;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reservateViewModel =
                ViewModelProviders.of(this).get(ReservateViewModel.class);
        View root = inflater.inflate(R.layout.settings, container, false);
        show_wallet_remaining = root.findViewById(R.id.show_wallet_remaining);
        btn_setting_wallet = root.findViewById(R.id.btn_setting_wallet);

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
                                if (document.getData().get("錢包") != null) {
                                    wallet_remaining = document.getLong("錢包");
                                }else {
                                    wallet_remaining = Long.valueOf(0);
                                    Map<String, Object> user_wallet = new HashMap<>();
                                    user_wallet.put("錢包",0);
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
                                }
                                show_wallet_remaining.setText(String.valueOf(wallet_remaining));
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
            user_db.collection("users")
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
                                        try {
                                            //Toast.makeText(getActivity(),"資料修改更新"+id+"\nold:"+oldIndex+"\nnew:"+newIndex, Toast.LENGTH_SHORT).show();

                                            //刷新refresh Fragment
                                            PaySettingFragment paySettingFragment = new PaySettingFragment();
                                            getFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.nav_host_fragment, paySettingFragment)
                                                    .addToBackStack("TAG_TO_FRAGMENT").commit();
                                        }catch (Exception e0){
                                            e0.printStackTrace();
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

        btn_setting_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        ChoosePayMethod.class);
                startActivity(intent);
            }
        });

        /*
        final TextView textView = root.findViewById(R.id.text_historical);
        reservateViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        return root;
    }
}