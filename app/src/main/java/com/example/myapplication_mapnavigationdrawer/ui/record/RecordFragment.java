package com.example.myapplication_mapnavigationdrawer.ui.record;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.adapter.RecordAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication_mapnavigationdrawer.RatingDialogFragment.TAG;

public class RecordFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private ListView listview_record;

    public String string_email, string_uid, record_parkinglot_reservating, record_parkinglot_time, txt_is_finish, txt_is_using, record_parkinglot_name,
            record_parkinglot_space, parking_total_time, record_parkinglot_address, current_price, should_pay, record_license;
    private ArrayList<String> items_record_parkinglot_time = new ArrayList<>();
    private ArrayList<String> items_txt_is_finish = new ArrayList<>();
    private ArrayList<String> items_txt_is_using = new ArrayList<>();
    private ArrayList<String> items_record_parkinglot_name = new ArrayList<>();
    private ArrayList<String> items_record_parkinglot_space = new ArrayList<>();
    private ArrayList<String> items_parking_total_time = new ArrayList<>();
    private ArrayList<String> items_record_parkinglot_address = new ArrayList<>();
    private ArrayList<String> items_current_price = new ArrayList<>();
    private ArrayList<String> items_should_pay = new ArrayList<>();
    private ArrayList<String> items_record_license = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.praking_record, container, false);

        listview_record = root.findViewById(R.id.listview_record);



        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            string_email = user.getEmail();//抓取使用者email
            string_uid = user.getUid();     //抓取使用者UID

            firestore.collection("users").document(string_uid).collection("record")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int count = 0;
                                for (DocumentSnapshot document : task.getResult()) {
                                    count++;
                                    record_parkinglot_time =  document.getData().get("預約日期時間").toString();
                                    record_parkinglot_name = document.getData().get("停車場").toString();
                                    record_parkinglot_space = document.getData().get("車位").toString();
                                    record_parkinglot_address = document.getData().get("地址").toString();
                                    current_price = document.getData().get("費率").toString();
                                    record_license = document.getData().get("預約車牌").toString();
                                    //record_parkinglot_reservating = document.getData().get("預約中").toString();
/*

                                    if(document.getData().get("訂單完成").toString().matches("true")){
                                        txt_is_finish   =  "\t訂單完成\t";
                                    }else{
                                        DocumentReference docRef_is_finish = firestore.collection("reservatable parkinglot").document(record_parkinglot_name).collection("parking grid").document(record_parkinglot_space);
                                        if(docRef_is_finish != null){

                                            docRef_is_finish.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取parking grid/A1 是否使用中或已被預約
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Log.e("TAG", "DocumentSnapshot data: " + document.getData());
                                                            Boolean reserved = (Boolean) document.get("預約中");
                                                            Boolean using = (Boolean) document.get("使用中");
                                                            Boolean is_pay = (Boolean) document.get("是否繳費完成");
                                                            String string_order_number = (String) document.get("訂單編號");

                                                            if(reserved || using || !is_pay){
                                                                txt_is_finish   =  "\t預約中\t";
                                                                Map<String, Boolean> record_boolean = new HashMap<>();
                                                                Map<String, Object> record_string = new HashMap<>();
                                                                record_boolean.put("訂單完成", false);
                                                                record_boolean.put("預約中", true);

                                                                //A1info_string.put("結束時間日期","");
                                                                //A1info_string.put("應付金額","");
                                                                firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(record_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                            }else{
                                                                //txt_is_finish   =  "\t訂單完成\t";
                                                            }

                                                            if(using){
                                                                //txt_is_using = "\t車輛已進場\t\t";
                                                                Map<String, Boolean> record_boolean = new HashMap<>();
                                                                Map<String, Object> record_string = new HashMap<>();
                                                                record_boolean.put("使用中", true);

                                                                firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(record_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                            }else{
                                                                //txt_is_using = "\t車輛未進場\t\t";
                                                                Map<String, Boolean> record_boolean = new HashMap<>();
                                                                Map<String, Object> record_string = new HashMap<>();
                                                                record_boolean.put("使用中", false);

                                                                firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(record_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                            }

                                                        } else {
                                                            Log.e("TAG", "No such document");
                                                        }
                                                    } else {
                                                        Log.e("TAG", "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                        }
                                    }

 */

                                    if(document.getData().get("預約中").toString().matches("true")){
                                        txt_is_finish = "\t預約中\t";
                                    }else{
                                        txt_is_finish = "\t訂單已完成\t";
                                    }

                                    if(document.getData().get("使用中") != null){
                                        if(document.getData().get("使用中").toString().matches("true")){
                                            txt_is_using = "\t車輛已進場\t\t";
                                        }else {
                                            txt_is_using = "\t車輛未進場\t\t";
                                        }
                                    }

                                    parking_total_time = "0分鐘";
                                    should_pay = "0NT";

                                    items_record_parkinglot_time.add(record_parkinglot_time);
                                    items_txt_is_finish.add(txt_is_finish);
                                    items_record_parkinglot_name.add(record_parkinglot_name);
                                    items_record_parkinglot_space.add(record_parkinglot_space);
                                    items_record_parkinglot_address.add(record_parkinglot_address);
                                    items_current_price.add(current_price);
                                    items_txt_is_using.add(txt_is_using);
                                    items_parking_total_time.add(parking_total_time);
                                    items_should_pay.add(should_pay);
                                    items_record_license.add(record_license);

                                }
                                Toast.makeText(getActivity(),"共有"+ count+ "筆資料", Toast.LENGTH_SHORT).show();

                                RecordAdapter recordAdapter = new
                                        RecordAdapter(getActivity(), items_record_parkinglot_time, items_txt_is_finish, items_txt_is_using, items_record_parkinglot_name,
                                        items_record_parkinglot_space, items_parking_total_time, items_record_parkinglot_address, items_current_price, items_should_pay, items_record_license);

                                listview_record.setAdapter(recordAdapter);

                            } else {
                                Log.d("record", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }




        /*
        final TextView textView = root.findViewById(R.id.text_historical);
        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        return root;
    }
}
