package com.example.myapplication_mapnavigationdrawer.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication_mapnavigationdrawer.ParkinglotActivity;
import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.MyDBHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class infowindow extends AppCompatActivity {

    private Button back ;
    private Button reservation;
    private Button btn_favorite;
    private Button parkinglot_street_view_image_url;
    private TextView parkinglot_lot;
    private TextView parkinglot_is_opening;
    private TextView parkinglot_name;
    private TextView parkinglot_today_service_time;
    private TextView parkinglot_simple_description;
    private TextView parkinglot_full_description;
    private TextView parkinglot_address;
    private TextView parkinglot_tel;
    private TextView parkinglot_detail_info;
    private RecyclerView mParkinggrid;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private static final int LIMIT = 50;
    private static final String TAG = "infowindow";
    private CollectionReference mParkingGrid;

    private SQLiteDatabase dari;

    private String string_email, string_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infowindow);

        back = findViewById(R.id.back);
        btn_favorite = findViewById(R.id.btn_favorite);
        reservation = findViewById(R.id.reservation);
        parkinglot_name = findViewById(R.id.parkinglot_name);
        parkinglot_lot = findViewById(R.id.parkinglot_lot);
        parkinglot_is_opening = findViewById(R.id.parkinglot_is_opening);
        parkinglot_today_service_time = findViewById(R.id.parkinglot_today_service_time);
        parkinglot_simple_description = findViewById(R.id.parkinglot_simple_description);
        parkinglot_full_description = findViewById(R.id.detail_price);
        parkinglot_address = findViewById(R.id.parkinglot_address);
        parkinglot_tel = findViewById(R.id.parkinglot_tel);
        parkinglot_detail_info = findViewById(R.id.parkinglot_detail_info);

        Bundle b = getIntent().getExtras();
        String string_parkinglot_name = b.getString("string_parkinglot_name");
        String string_parkinglot_snippet = b.getString("string_parkinglot_snippet");

        parkinglot_name.setText(String.format("%s",string_parkinglot_name));//停車場名 Title

        String[] split_string_parkinglot_snippet = string_parkinglot_snippet.split(",");//split(指定符號) ，可依指定符號把字串分開成陣列

        dari = new MyDBHelper(this).getWritableDatabase();

        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            string_email = user.getEmail();//抓取使用者email
            string_uid = user.getUid();     //抓取使用者UID

            final DocumentReference docRef_user = firestore.collection("users").document(string_uid);
            if(docRef_user != null){
                if(split_string_parkinglot_snippet[12].matches("true")){     //假如是可預約的停車場

                    reservation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                /*
                Intent intent = new Intent();
                startActivity(new Intent(infowindow.this, ParkinglotActivity.class));
                 */

                            Intent intent = new Intent(infowindow.this,
                                    ParkinglotActivity.class);

                            Bundle b = new Bundle();    //資訊放入Bundle
                            b.putString("string_parkinglot_name",parkinglot_name.getText().toString());
                            b.putString("parkinglot_simple_description",parkinglot_simple_description.getText().toString());
                            b.putString("parkinglot_address",parkinglot_address.getText().toString());
                            intent.putExtras(b);
                            startActivity(intent);

                        }
                    });
                }else{
                    //讓reservation預約按鈕失效且消失
                    reservation.setEnabled(false);
                    reservation.setVisibility(View.GONE);
                }
            }else {
                Toast.makeText(infowindow.this,"請先填寫個人資料完畢，即可預約!", Toast.LENGTH_LONG).show();
            }
        }else{
            reservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(infowindow.this,"尚未登入，請先登入，即可預約。", Toast.LENGTH_LONG).show();
                }
            });
        }

        if(split_string_parkinglot_snippet[0].equals("-1") == true){
            parkinglot_lot.setText(String.format("無資訊"));
        }
        else if (split_string_parkinglot_snippet[1].equals("-1") == true){
            parkinglot_lot.setText(String.format(split_string_parkinglot_snippet[0]));
        }
        else{
            parkinglot_lot.setText(String.format(split_string_parkinglot_snippet[0]+"  /  剩餘車位："+split_string_parkinglot_snippet[1]));
        }
/*
        windowadapter_parkinglot_lot.setText(windowadapter_parkinglot_lot.getText()+"\n●費率: "+split_string_parkinglot_snippet[2]);
*/

        if(split_string_parkinglot_snippet[4].matches("true")){
            parkinglot_is_opening.setText(String.format("   營業中   "));
        }else {
            parkinglot_is_opening.setText(String.format("   休息中   "));
        }

        parkinglot_today_service_time.setText(String.format(split_string_parkinglot_snippet[5]));
        parkinglot_simple_description.setText(String.format(split_string_parkinglot_snippet[2]));
        parkinglot_full_description.setText(String.format(split_string_parkinglot_snippet[3])); //之後改成detail_info
        parkinglot_address.setText(String.format(split_string_parkinglot_snippet[6]));

        if(split_string_parkinglot_snippet[7].matches("")){
            parkinglot_tel.setText(String.format("無資訊"));   //仍然有BUG，tel若為空值，點進去依然閃退
        }else {
            parkinglot_tel.setText(String.format(split_string_parkinglot_snippet[7]));
        }

        parkinglot_detail_info.setText(String.format(split_string_parkinglot_snippet[11]));
        parkinglot_detail_info.setMovementMethod(new ScrollingMovementMethod());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //SQL我的最愛
        final String string_id = split_string_parkinglot_snippet[10];
        final String string_lat = split_string_parkinglot_snippet[8];
        final String string_lng = split_string_parkinglot_snippet[9];
        final String string_price = split_string_parkinglot_snippet[2];
        final String string_address = split_string_parkinglot_snippet[6];

        Cursor cursor;
        cursor = dari.rawQuery("SELECT * FROM myTable WHERE favorite_id LIKE '" + string_id + "'",null);

        if(cursor.getCount() != 0){
            btn_favorite.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_favorite_black_24dp),null,null);
            btn_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        dari.execSQL("DELETE FROM myTable WHERE favorite_id LIKE '" + string_id + "'");
                        btn_favorite.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp),null,null);
                        onBackPressed();
                        Toast.makeText(infowindow.this,"移除我的最愛"+
                                string_id + "\n停車場:"+ parkinglot_name.getText().toString(), Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        Toast.makeText(infowindow.this,"刪除失敗"+
                                e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }else{
            btn_favorite.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp),null,null);
            btn_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        //新增一筆book與price資料進入 myTable 資料表
                        dari.execSQL("INSERT INTO myTable(favorite_id,favorite_name, " +
                                        "favorite_lat, favorite_lng, favorite_price, favorite_address) VALUES(?,?,?,?,?,?)",
                                new  Object[]{string_id,
                                        parkinglot_name.getText().toString(),
                                        string_lat,
                                        string_lng,
                                        string_price,
                                        string_address});

                        onBackPressed();
                        Toast.makeText(infowindow.this,
                                "已加入到我的最愛\nID:"+string_id
                                        +"\n停車場"+parkinglot_name.getText().toString(), Toast.LENGTH_SHORT).show();
                        btn_favorite.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_favorite_black_24dp),null,null);
                    }catch(Exception e){
                        Toast.makeText(infowindow.this,"最愛新增失敗 :" +
                                e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }




        //街景URL
        parkinglot_street_view_image_url = findViewById(R.id.parkinglot_street_view_image_url);

        String strUri = "google.streetview:cbll="+
                String.valueOf(split_string_parkinglot_snippet[8])+","+//讀取lat
                String.valueOf(split_string_parkinglot_snippet[9]);     //讀取lng
        final String string_strUri1 = strUri;
        parkinglot_street_view_image_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下面放街景url即可
                Intent intent_street_view = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(string_strUri1));
                startActivity(intent_street_view);
            }
        });

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        mParkingGrid = mFirestore.collection("parking grid");

        getParkingGrid(mParkingGrid);

        getParkingGridData();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    /**
    讀取停車格資料用的函式
   */
    private void getParkingGridData()  {

        final DocumentReference parkingGridData= mParkingGrid.document("A1");

        parkingGridData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot document =  task.getResult();
                    if (document.exists()){
                        Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                    }else{
                        Log.e(TAG, "No such document");
                    }
                }else {
                    Log.e(TAG, "get failed with ", task.getException());

                }
            }
        });
    }

    /**
     * 透過尋找ID來找到停車格的ID
     */
    private void getParkingGrid(CollectionReference mParkingGrid){

       mParkingGrid.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if (task.isSuccessful()){
                   List<String> list = new ArrayList<>();
                   for (QueryDocumentSnapshot document : task.getResult()) {
                       list.add(document.getId());
                   }
                   Log.e(TAG, list.toString());
               }else{
                   Log.e(TAG, "Error getting documents: ", task.getException());
               }
           }
       });
    }
    private void getAvailableParkingGrid (CollectionReference mAvailableParkingGrid) {

    }

}
