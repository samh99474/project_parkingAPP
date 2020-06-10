package com.example.myapplication_mapnavigationdrawer.ui.record;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication_mapnavigationdrawer.MainActivity;
import com.example.myapplication_mapnavigationdrawer.MessageFirebaseService;
import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.adapter.RecordAdapter;
import com.example.myapplication_mapnavigationdrawer.ten_min_ReminderBroadcast;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.myapplication_mapnavigationdrawer.RatingDialogFragment.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class RecordFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private ListView listview_record;

    public String string_email, string_uid, string_order_number, record_string_parkinglot_time, txt_is_finish, txt_is_using, record_parkinglot_name,
            parking_total_time, record_parkinglot_address, current_price, firebase_current_price, record_license, show_diff_time,txt_press_finish,
            parkinglot_phone;
    private Timestamp record_timestamp_parkinglot_time;
    private ArrayList<String> items_order_number = new ArrayList<>();
    private ArrayList<String> items_record_parkinglot_time = new ArrayList<>();
    private ArrayList<String> items_txt_is_finish = new ArrayList<>();
    private ArrayList<String> items_txt_is_using = new ArrayList<>();
    private ArrayList<String> items_record_parkinglot_name = new ArrayList<>();
    private ArrayList<String> items_parking_total_time = new ArrayList<>();
    private ArrayList<String> items_record_parkinglot_address = new ArrayList<>();
    private ArrayList<String> items_parkinglot_phone = new ArrayList<>();
    private ArrayList<String> items_current_price = new ArrayList<>();
    private ArrayList<String> items_should_pay = new ArrayList<>();
    private ArrayList<String> items_record_license = new ArrayList<>();
    private ArrayList<String> items_txt_press_finish = new ArrayList<>();
    private ArrayList<String> items_show_diff_time = new ArrayList<>();

    private Timestamp timestamp_resevate_time, timestamp_pay_time;
    private Long should_pay, parking_total_sec;
    private String string_should_pay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        final View root = inflater.inflate(R.layout.praking_record, container, false);

        listview_record = root.findViewById(R.id.listview_record);

        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        try{
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
                                        if(document.getTimestamp("預約日期時間") != null){
                                            record_timestamp_parkinglot_time = document.getTimestamp("預約日期時間");
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            record_string_parkinglot_time = sdf.format(record_timestamp_parkinglot_time.toDate());
                                        }

                                        record_parkinglot_name = document.getData().get("停車場").toString();
                                        record_parkinglot_address = document.getData().get("地址").toString();
                                        parkinglot_phone = document.getData().get("聯絡電話").toString();
                                        current_price = document.getData().get("費率").toString();
                                        record_license = document.getData().get("預約車牌").toString();
                                        string_order_number = document.getData().get("訂單編號").toString();
                                        string_should_pay = document.getData().get("應付金額").toString();

                                        SimpleDateFormat sdf_now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    //取得現在日期時間
                                        String currentDateandTime = sdf_now.format(new Date());

                                        timestamp_resevate_time = new Timestamp(record_timestamp_parkinglot_time.toDate());
                                        timestamp_pay_time = new Timestamp(java.sql.Timestamp.valueOf( currentDateandTime ));
                                        Long diff_time_d = (timestamp_pay_time.toDate().getTime() - timestamp_resevate_time.toDate().getTime())/(1000*60*60*24);//總共相差天數
                                        Long diff_time_h = (timestamp_pay_time.toDate().getTime() - timestamp_resevate_time.toDate().getTime())/(1000*60*60);//總共相差小時
                                        Long diff_time_m = (timestamp_pay_time.toDate().getTime() - timestamp_resevate_time.toDate().getTime())/(1000*60);//總共相差分鐘
                                        Long diff_time_s = (timestamp_pay_time.toDate().getTime() - timestamp_resevate_time.toDate().getTime())/(1000);//總共相差秒

                                        long day = diff_time_s / 86400;             //86400為一天的秒數，總秒數除以一天的秒數取整為天數
                                        long hour = (diff_time_s % 86400) / 3600;   //總秒數除以一天的秒數後的餘數除以3600s為小時數
                                        long mins = (diff_time_s % 86400) % 3600;   //總秒數除以一天的秒數後的餘數除以3600s後的餘數為剩下的總分鐘數
                                        long min = mins / 60;                   //總分鐘數除以60s的取整為分鐘數
                                        //long second = mins % 60;                //總分鐘數除以60s的餘數為剩下的秒數

                                        if(document.getData().get("訂單取消").toString().matches("true")){
                                            if(document.getData().get("overtime_thirty").toString().matches("true")){
                                                txt_is_using = "\t30分內未進場\t\t";
                                                txt_press_finish = "\t訂單已取消\t";
                                                txt_is_finish = "\t自動取消預約\t\t";
                                                show_diff_time = "30分內未進場";
                                            }else {
                                                txt_press_finish = "\t訂單已取消\t";
                                                txt_is_using = "\t車輛未進場\t\t";
                                                txt_is_finish = "\t預約結束\t\t";
                                                show_diff_time = "未進場";
                                            }

                                        }else {
                                            if(document.getData().get("訂單完成").toString().matches("false")){
                                                if(document.getData().get("reservating").toString().matches("true")){
                                                    txt_is_finish = "\t預約中\t";
                                                }

                                                if(document.getData().get("using") != null) {
                                                    if(document.getData().get("using").toString().matches("true")){
                                                        txt_is_using = "\t車輛已進場\t\t";
                                                        txt_press_finish = "\t車輛出場時將自動扣款完成訂單\t";
                                                        show_diff_time = day + "天" + hour + "小時" + min + "分鐘";

                                                        //曾經沒接收過進場通知
                                                        if(document.getData().get("had_notified_car_in").toString().matches("false")){
                                                            //更改值firebase
                                                            Map<String, Boolean> user_record_notify_car_in_boolean = new HashMap<>();
                                                            user_record_notify_car_in_boolean.put("had_notified_car_in", true);

                                                            firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(user_record_notify_car_in_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                                            //跳通知已進場
                                                            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
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

                                                            String[] events = {"車輛已進場，車輛出場時將自動扣款完成訂單"};
                                                            // Sets a title for the Inbox in expanded layout
                                                            inboxStyle.setBigContentTitle("車輛已進場:");
                                                            // Moves events into the expanded layout
                                                            for (int i=0; i < events.length; i++) {
                                                                inboxStyle.addLine(events[i]);
                                                            }

                                                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(root.getContext(), channelId)
                                                                    .setSmallIcon(R.mipmap.loticon)//R.mipmap.ic_launcher
                                                                    .setContentTitle("車輛已進場:")
                                                                    .setContentText("車輛已進場，車輛出場時將自動扣款完成訂單")
                                                                    .setStyle(inboxStyle)
                                                                    .setVibrate(new long[]{100, 250})
                                                                    .setLights(Color.YELLOW, 500, 5000)
                                                                    .setAutoCancel(true)
                                                                    .setColor(ContextCompat.getColor(root.getContext(), R.color.colorPrimary));

                                                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(root.getContext());
                                                            stackBuilder.addNextIntent(new Intent(getActivity(), MainActivity.class));
                                                            Intent myintent = new Intent(getActivity(), MainActivity.class);
                                                            PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0,
                                                                    myintent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                            mBuilder.setContentIntent(contentIntent);

                                                            notificationManager.notify(notificationId, mBuilder.build());
                                                        }
                                                    }else {
                                                        txt_is_using = "\t車輛未進場\t\t";
                                                        txt_press_finish = "\t請於預約時間後30分內進場\t\n否則將自動取消訂單";
                                                        show_diff_time = "進場後，將開始計時";
                                                    }
                                                }


/*
                                                if(diff_time_m <= 15){
                                                    if(document.getData().get("using") != null) {
                                                        if(document.getData().get("using").toString().matches("true")){
                                                            txt_is_using = "\t車輛已進場\t\t";
                                                            txt_press_finish = "\t車輛出場時將自動扣款完成訂單\t";
                                                            show_diff_time = day + "天" + hour + "小時" + min + "分鐘";
                                                        }else {
                                                            txt_is_using = "\t車輛未進場\t\t";
                                                            txt_press_finish = "\t請於預約時間後15分內進場\t";
                                                            show_diff_time = "0";
                                                        }
                                                    }
                                                }else if(diff_time_m > 15 && diff_time_m <= 30){
                                                    if(document.getData().get("using") != null) {
                                                        if(document.getData().get("using").toString().matches("true")){
                                                            txt_is_using = "\t車輛已進場\t\t";
                                                            txt_press_finish = "\t車輛出場時將自動扣款完成訂單\t";
                                                            show_diff_time = day + "天" + hour + "小時" + min + "分鐘";
                                                        }else {
                                                            txt_is_using = "\t車輛未進場\t\t";
                                                            txt_press_finish = "\t請於預約時間後30分內進場\t\n否則將自動取消訂單";
                                                            show_diff_time = "因15分內未進場，\n已向您收取最低費用";
                                                        }
                                                    }
                                                }else {
                                                    if(document.getData().get("using") != null) {
                                                        if(document.getData().get("using").toString().matches("true")){
                                                            txt_is_using = "\t車輛已進場\t\t";
                                                            txt_press_finish = "\t車輛出場時將自動扣款完成訂單\t";
                                                            show_diff_time = day + "天" + hour + "小時" + min + "分鐘";
                                                        }else {
                                                            txt_press_finish = "\t訂單已取消\t";
                                                            txt_is_using = "\t30分內未進場\t\t";
                                                            txt_is_finish = "\t自動取消預約\t\t";
                                                            show_diff_time = "未進場";
                                                        }
                                                    }
                                                }

 */
/*
                                            if(document.getData().get("使用中") != null){
                                                if(document.getData().get("使用中").toString().matches("true")){
                                                    txt_is_using = "\t車輛已進場\t\t";
                                                    txt_press_finish = "\t車輛出場時將自動扣款完成訂單\t";
                                                    show_diff_time = day + "天" + hour + "小時" + min + "分鐘";
                                                }else {
                                                    if(diff_time_m >= 15){

                                                        txt_press_finish = "\t訂單已取消\t";
                                                        txt_is_using = "\t15分內未進場\t\t";
                                                        txt_is_finish = "\t自動取消預約\t\t";
                                                        show_diff_time = "未進場";

                                                    }else {
                                                        txt_is_using = "\t車輛未進場\t\t";
                                                        txt_press_finish = "\t請於預約時間後15分內進場\t";
                                                        show_diff_time = day + "天" + hour + "小時" + min + "分鐘";
                                                    }
                                                }
                                            }

 */
                                            }else {
                                                if (document.getData().get("parking_total_sec") != null) {
                                                    parking_total_sec = document.getLong("parking_total_sec");
                                                    long parking_day = parking_total_sec / 86400;             //86400為一天的秒數，總秒數除以一天的秒數取整為天數
                                                    long parking_hour = (parking_total_sec % 86400) / 3600;   //總秒數除以一天的秒數後的餘數除以3600s為小時數
                                                    long parking_mins = (parking_total_sec % 86400) % 3600;   //總秒數除以一天的秒數後的餘數除以3600s後的餘數為剩下的總分鐘數
                                                    long parking_min = parking_mins / 60;                   //總分鐘數除以60s的取整為分鐘數
                                                    show_diff_time = parking_day + "天" + parking_hour + "小時" + parking_min + "分鐘";
                                                }
                                                txt_press_finish = "\t訂單已完成\t";
                                                txt_is_using = "\t車輛已出場\t\t";
                                                txt_is_finish = "\t預約結束\t\t";

                                                //曾經沒接收過出場通知
                                                if(document.getData().get("had_notified_car_out").toString().matches("false")){
                                                    //更改值firebase
                                                    Map<String, Boolean> user_record_notify_car_out_boolean = new HashMap<>();
                                                    user_record_notify_car_out_boolean.put("had_notified_car_out", true);

                                                    firestore.collection("users").document(string_uid).collection("record").document(string_order_number).set(user_record_notify_car_out_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                                    //跳通知已出場
                                                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
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

                                                    String[] events = {"車輛已出場，已自動扣款完成訂單"};
                                                    // Sets a title for the Inbox in expanded layout
                                                    inboxStyle.setBigContentTitle("車輛已出場:");
                                                    // Moves events into the expanded layout
                                                    for (int i=0; i < events.length; i++) {
                                                        inboxStyle.addLine(events[i]);
                                                    }

                                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(root.getContext(), channelId)
                                                            .setSmallIcon(R.mipmap.loticon)//R.mipmap.ic_launcher
                                                            .setContentTitle("車輛已出場:")
                                                            .setContentText("車輛已出場，已自動扣款完成訂單")
                                                            .setStyle(inboxStyle)
                                                            .setVibrate(new long[]{100, 250})
                                                            .setLights(Color.YELLOW, 500, 5000)
                                                            .setAutoCancel(true)
                                                            .setColor(ContextCompat.getColor(root.getContext(), R.color.colorPrimary));

                                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(root.getContext());
                                                    stackBuilder.addNextIntent(new Intent(getActivity(), MainActivity.class));
                                                    Intent myintent = new Intent(getActivity(), MainActivity.class);
                                                    PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0,
                                                            myintent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                    mBuilder.setContentIntent(contentIntent);

                                                    notificationManager.notify(notificationId, mBuilder.build());
                                            }
                                            }
                                        }



                                        items_order_number.add(string_order_number);
                                        items_record_parkinglot_time.add(record_string_parkinglot_time);
                                        items_txt_is_finish.add(txt_is_finish);
                                        items_record_parkinglot_name.add(record_parkinglot_name);
                                        items_record_parkinglot_address.add(record_parkinglot_address);
                                        items_parkinglot_phone.add(parkinglot_phone);
                                        items_current_price.add(current_price);
                                        items_txt_is_using.add(txt_is_using);
                                        items_record_license.add(record_license);
                                        items_txt_press_finish.add(txt_press_finish);
                                        items_should_pay.add(string_should_pay);
                                        items_show_diff_time.add(show_diff_time);

                                    }
                                    //Toast.makeText(getActivity(),"共有"+ count+ "筆資料", Toast.LENGTH_SHORT).show();

                                    RecordAdapter recordAdapter = new
                                            RecordAdapter(getActivity(), items_order_number, items_record_parkinglot_time, items_txt_is_finish, items_txt_is_using, items_record_parkinglot_name,
                                            items_record_parkinglot_address, items_parkinglot_phone, items_current_price, items_record_license, items_txt_press_finish, items_should_pay, items_show_diff_time);

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
                                    if (change.getType() == DocumentChange.Type.MODIFIED) {
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

                                            //刷新refresh Fragment
                                            reflash_rocord_fragment();
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

            //ListView加入Head list 頭標
            View headlist = getLayoutInflater().inflate(R.layout.record_head_list, null);
            listview_record.addHeaderView(headlist);
        }catch (Exception e){
            e.printStackTrace();
        }


        listview_record.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= listview_record.getHeaderViewsCount();//position 扣掉是因為有head list

                if(position!=-1) {

                    if((items_txt_is_using.get(position).matches("\t車輛未進場\t\t")) &&
                            !(items_txt_press_finish.get(position).matches("\t訂單已完成\t")) &&
                            !(items_txt_press_finish.get(position).matches("\t訂單已取消\t"))){

                        Toast.makeText(getActivity(), items_record_parkinglot_name.get(position), Toast.LENGTH_SHORT).show();

                        /*
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

                        should_pay = diff_time_h*30;

                         */


                        ////////////AlertDialog 付費資訊////////////
                        AlertDialog.Builder dialog_finish_pay = new AlertDialog.Builder(getActivity());
                        dialog_finish_pay.setTitle("結束預約");
                        dialog_finish_pay.setMessage("付費資訊：\n\n"+
                                items_record_parkinglot_name.get(position) +
                                "\n車牌：" + items_record_license.get(position) +
                                "\n\n預約時間：" + items_record_parkinglot_time.get(position) +
                                "\n停車時數：" + items_show_diff_time.get(position) +
                                "\n應付金額：" + "$" + items_should_pay.get(position));
                        dialog_finish_pay.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(),"取消", Toast.LENGTH_SHORT).show();
                            }
                        });

                        final String order_number = items_order_number.get(position);
                        final String parkinglot_name = items_record_parkinglot_name.get(position);
                        final String car_license = items_record_license.get(position);
                        final String user_uid = string_uid;
                        final String final_string_should_pay = items_should_pay.get(position);

                        dialog_finish_pay.setPositiveButton("確定結束預約", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                /*
                                //法二，Alarm calendar

                                //calendar.add(calendar.DATE,1); //把日期往后增加一天,整数  往后推,负数往前移动
                                //date=calendar.getTime(); //这个时间就是日期往后推一天的结果

                                Date date= new Date(); //取时间
                                Calendar   calendar = new GregorianCalendar();
                                calendar.setTime(date);
                                //Calendar c = Calendar.getInstance();
                                calendar.add(calendar.SECOND,15);//10秒後的calendar


                                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                                Intent intent = new Intent(getActivity(), ten_min_ReminderBroadcast.class);

                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);
                                if (calendar.before(Calendar.getInstance())) {
                                    calendar.add(Calendar.SECOND, 15);
                                }
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                                 */

                                //跳通知，告知10分鐘內離場
                                NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
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

                                String[] events = {"您已結束預約，若有疑問可聯絡該停車場客服電話，尋求協助。"};
                                // Sets a title for the Inbox in expanded layout
                                inboxStyle.setBigContentTitle("您已結束預約:");
                                // Moves events into the expanded layout
                                for (int i=0; i < events.length; i++) {
                                    inboxStyle.addLine(events[i]);
                                }

                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(root.getContext(), channelId)
                                        .setSmallIcon(R.mipmap.loticon)//R.mipmap.ic_launcher
                                        .setContentTitle("您已結束預約:")
                                        .setContentText("您已結束預約！\t若有疑問可聯絡該停車場客服電話，尋求協助。")
                                        .setStyle(inboxStyle)
                                        .setVibrate(new long[]{100, 250})
                                        .setLights(Color.YELLOW, 500, 5000)
                                        .setAutoCancel(true)
                                        .setColor(ContextCompat.getColor(root.getContext(), R.color.colorPrimary));

                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(root.getContext());
                                stackBuilder.addNextIntent(new Intent(getActivity(), MainActivity.class));
                                Intent myintent = new Intent(getActivity(), MainActivity.class);
                                PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0,
                                        myintent, PendingIntent.FLAG_UPDATE_CURRENT);
                                mBuilder.setContentIntent(contentIntent);

                                notificationManager.notify(notificationId, mBuilder.build());

                                //傳送給firebase 按下按鈕的時間
                                Map<String, Timestamp> user_record = new HashMap<>();
                                Map<String, Boolean> user_record_boolean = new HashMap<>();
                                Map<String, String> user_record_string = new HashMap<>();
                                user_record.put("付費時間", timestamp_pay_time);
                                user_record_boolean.put("按下結束按鈕", true);
                                //user_record_boolean.put("是否重新計費", false);
                                user_record_boolean.put("訂單完成", true);
                                user_record_boolean.put("訂單取消", true);
                                user_record_string.put("應付金額",final_string_should_pay);
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

                                firestore.collection("users").document(string_uid).collection("record").document(order_number).set(user_record_string, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                firestore.collection("users").document(string_uid).collection("record").document(order_number).set(user_record_boolean, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                firestore.collection("reservatable parkinglot").document(parkinglot_name).collection("parking grid").document(car_license)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });
                                /*
                                firestore.collection("reservatable parkinglot").document(parkinglot_name).collection("parking grid").document(car_license).set(user_record, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                 */
                            }
                        });
                        dialog_finish_pay.show();
                    }
                }
            }
        });


        return root;
    }

    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.FRENCH).format(now));
        return id;
    }

    private void reflash_rocord_fragment() {
        RecordFragment recordFragment = new RecordFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, recordFragment)
                .addToBackStack("TAG_TO_FRAGMENT").commit();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "LemubitReminderChannel";
            String description = "Channel for Lemubit Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyLemubit", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

    }
}
