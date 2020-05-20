package com.example.myapplication_mapnavigationdrawer.ui.record;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ParseException;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.adapter.RecordAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.example.myapplication_mapnavigationdrawer.RatingDialogFragment.TAG;

public class RecordFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private ListView listview_record;

    public String string_email, string_uid, string_order_number, record_string_parkinglot_time, txt_is_finish, txt_is_using, record_parkinglot_name,
            parking_total_time, record_parkinglot_address, current_price, firebase_current_price, record_license, show_diff_time;
    private Timestamp record_timestamp_parkinglot_time;
    private ArrayList<String> items_order_number = new ArrayList<>();
    private ArrayList<String> items_record_parkinglot_time = new ArrayList<>();
    private ArrayList<String> items_txt_is_finish = new ArrayList<>();
    private ArrayList<String> items_txt_is_using = new ArrayList<>();
    private ArrayList<String> items_record_parkinglot_name = new ArrayList<>();
    private ArrayList<String> items_parking_total_time = new ArrayList<>();
    private ArrayList<String> items_record_parkinglot_address = new ArrayList<>();
    private ArrayList<String> items_current_price = new ArrayList<>();
    private ArrayList<String> items_should_pay = new ArrayList<>();
    private ArrayList<String> items_record_license = new ArrayList<>();

    private Timestamp timestamp_resevate_time, timestamp_pay_time;

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


                                    //將timestamp轉成Date(Date格式整理過)，最後轉成String
                                    record_timestamp_parkinglot_time = document.getTimestamp("預約日期時間");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    record_string_parkinglot_time = sdf.format(record_timestamp_parkinglot_time.toDate());


                                    record_parkinglot_name = document.getData().get("停車場").toString();
                                    record_parkinglot_address = document.getData().get("地址").toString();
                                    current_price = document.getData().get("費率").toString();
                                    record_license = document.getData().get("預約車牌").toString();
                                    string_order_number = document.getData().get("訂單編號").toString();



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

                                    items_order_number.add(string_order_number);
                                    items_record_parkinglot_time.add(record_string_parkinglot_time);
                                    items_txt_is_finish.add(txt_is_finish);
                                    items_record_parkinglot_name.add(record_parkinglot_name);
                                    items_record_parkinglot_address.add(record_parkinglot_address);
                                    items_current_price.add(current_price);
                                    items_txt_is_using.add(txt_is_using);
                                    items_record_license.add(record_license);


                                }
                                Toast.makeText(getActivity(),"共有"+ count+ "筆資料", Toast.LENGTH_SHORT).show();

                                RecordAdapter recordAdapter = new
                                        RecordAdapter(getActivity(), items_order_number, items_record_parkinglot_time, items_txt_is_finish, items_txt_is_using, items_record_parkinglot_name,
                                        items_record_parkinglot_address, items_current_price, items_record_license);

                                listview_record.setAdapter(recordAdapter);

                            } else {
                                Log.d("record", "Error getting documents: ", task.getException());
                            }
                        }
                    });

            firestore.collection("users").document(string_uid).collection("record")
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
                                        Toast.makeText(getActivity(),"資料修改更新"+id+"\nold:"+oldIndex+"\nnew:"+newIndex, Toast.LENGTH_SHORT).show();

                                        //刷新refresh Fragment
                                        RecordFragment recordFragment = new RecordFragment();
                                        getFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.nav_host_fragment, recordFragment)
                                                .addToBackStack("TAG_TO_FRAGMENT").commit();
                                        break;
                                    case ADDED:
                                        Toast.makeText(getActivity(),"資料加入"+id+"\nold:"+oldIndex+"\nnew:"+newIndex, Toast.LENGTH_SHORT).show();
                                        break;
                                    case REMOVED:
                                        Toast.makeText(getActivity(),"資料移除"+id+"\nold:"+oldIndex+"\nnew:"+newIndex, Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }
                    });
        }

        //ListView加入Head list 頭標
        View headlist = getLayoutInflater().inflate(R.layout.record_head_list, null);
        listview_record.addHeaderView(headlist);

        listview_record.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= listview_record.getHeaderViewsCount();//position 扣掉是因為有head list

                if(position!=-1) {

                    Toast.makeText(getActivity(), items_record_parkinglot_name.get(position), Toast.LENGTH_SHORT).show();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    //取得現在日期時間
                    String currentDateandTime = sdf.format(new Date());

                    timestamp_resevate_time = new Timestamp(java.sql.Timestamp.valueOf( items_record_parkinglot_time.get(position) ));
                    timestamp_pay_time = new Timestamp(java.sql.Timestamp.valueOf( currentDateandTime ));
                    Long diff_time_d = (timestamp_pay_time.toDate().getTime() - timestamp_resevate_time.toDate().getTime())/(1000*60*60);//相差天數
                    Long diff_time_h = (timestamp_pay_time.toDate().getTime() - timestamp_resevate_time.toDate().getTime())/(1000*60*60);//相差小時
                    Long diff_time_m = (timestamp_pay_time.toDate().getTime() - timestamp_resevate_time.toDate().getTime())/(1000*60);//相差分鐘

                    if(diff_time_h<1){
                        show_diff_time = diff_time_m + "分鐘";
                    }else {
                        diff_time_m = diff_time_m - diff_time_h * 60;
                        show_diff_time = diff_time_h + "小時又" + diff_time_m + "分鐘";
                    }

                    Long should_pay = diff_time_h*30;


                    ////////////AlertDialog 付費資訊////////////
                    AlertDialog.Builder dialog_finish_pay = new AlertDialog.Builder(getActivity());
                    dialog_finish_pay.setTitle("結束預約");
                    dialog_finish_pay.setMessage("付費資訊：\n\n"+
                            items_record_parkinglot_name.get(position) +
                            "\n車牌：" + items_record_license.get(position) +
                            "\n\n預約時間：" + items_record_parkinglot_time.get(position) +
                            "\n停車時數：" + show_diff_time +
                            "\n應付金額：" + "$" + should_pay +
                            "\n\n確定結束預約後，車輛請於 10 分鐘內離場！\n若車輛於10分鐘內離場，將會自動扣款，訂單即完成; 若車輛未於10分鐘內離場，訂單將自動繼續計費，" +
                            "若有疑問可聯絡該停車場客服電話，尋求協助。");
                    dialog_finish_pay.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(),"取消", Toast.LENGTH_SHORT).show();
                        }
                    });

                    final String order_number = items_order_number.get(position);
                    dialog_finish_pay.setPositiveButton("確定結束預約", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Map<String, Timestamp> user_record = new HashMap<>();
                            user_record.put("付費時間", timestamp_pay_time);
                            firestore.collection("users").document(string_uid).collection("record").document(order_number).set(user_record, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    });
                    dialog_finish_pay.show();

                }
            }
        });



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

    public static String getTimestampPoor(Timestamp endDate, Timestamp nowDate) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 獲得兩個時間的毫秒時間差異
        long diff = endDate.toDate().getTime() - nowDate.toDate().getTime();

        // 計算差多少天
        long day = diff / nd;
        // 計算差多少小時
        long hour = diff % nd / nh;
        // 計算差多少分鐘
        long min = diff % nd % nh / nm;
        // 計算差多少秒//輸出結果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小時" + min + "分鐘";
    }

    public static String getDatePoor(Date endDate, Date nowDate) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 獲得兩個時間的毫秒時間差異
        long diff = endDate.getTime() - nowDate.getTime();
        // 計算差多少天
        long day = diff / nd;
        // 計算差多少小時
        long hour = diff % nd / nh;
        // 計算差多少分鐘
        long min = diff % nd % nh / nm;
        // 計算差多少秒//輸出結果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小時" + min + "分鐘";
    }
}


/*
    public static void main(String[] args) throws ParseException {
        // TODO Auto-generated method stub
        android.icu.text.SimpleDateFormat sdf=new android.icu.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1=sdf.parse("2016-07-14 10:52:10");
        Date d2=sdf.parse(sdf.format(new Date()));
        System.out.println(getDatePoor(d1,d2));
    }

 */
