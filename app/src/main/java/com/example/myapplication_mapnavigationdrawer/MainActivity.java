package com.example.myapplication_mapnavigationdrawer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.myapplication_mapnavigationdrawer.ui.PersonalInfo.SettingsPersonalInfoActivity;
import com.example.myapplication_mapnavigationdrawer.ui.bar_action;
import com.example.myapplication_mapnavigationdrawer.ui.home.HomeFragment;
import com.example.myapplication_mapnavigationdrawer.viewmodel.MainActivityViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = "MainActivity";
    private Button logout;
    private ImageView nav_header_pic;
    private TextView nav_header_name,nav_header_email;
    private String string_uid, string_name, string_email, string_head_name;
    private  NavigationView nav_view;
    private static final int RC_SIGN_IN = 9001;
    private MainActivityViewModel mViewModel;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private static final int LIMIT = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav_view = (NavigationView) findViewById(R.id.nav_view);
        View v = nav_view.getHeaderView(0);
        logout = v.findViewById(R.id.signin);
        nav_header_pic = v.findViewById(R.id.nav_header_pic);
        nav_header_name = v.findViewById(R.id.nav_header_name);
        nav_header_email = v.findViewById(R.id.nav_header_email);


        Firbase_GetUserData();

        Log.e("view", ""+(nav_view.getHeaderCount()));

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        /////////////////////////////////////////////////Drawer//////////////////////////////////////////////////////
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_historical, R.id.nav_personalinfo,
                R.id.nav_favorite, R.id.nav_help, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        FirebaseFirestore.setLoggingEnabled(true);
        // View model
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        // Initialize Firestore and the main RecyclerView
        initFirestore();
        initRecyclerView();

        if(!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null){
            logout.setText("登入");
        }else{
            logout.setText("登出");
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ddddddddddddddd","ddddddddddddd");
                if (shouldStartSignIn()) {      //未登入狀態，應該要登入
                    startSignIn();

                    if(mViewModel.getIsSigningIn()){
                        logout.setText("登出");
                        Firbase_GetUserData();
                        Log.e("signin_OK","signin_OK");return;
                    }

                    Log.e("jaja","haha");return;

                }else{      //登入狀態，應該要登出
                    AlertDialog.Builder dialog_isSignout = new AlertDialog.Builder(MainActivity.this);
                    dialog_isSignout.setTitle("登出");
                    dialog_isSignout.setMessage("您是否要登出呢?");
                    dialog_isSignout.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this,"登出失敗", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog_isSignout.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AuthUI.getInstance().signOut(MainActivity.this);
                            if(!mViewModel.getIsSigningIn()){
                                logout.setText("登入");
                                nav_header_name.setText("立即登入預約");
                                nav_header_email.setText("");
                                nav_header_pic.setImageResource(R.mipmap.loticon);
                                Toast.makeText(MainActivity.this,"您已登出，請重新登入", Toast.LENGTH_SHORT).show();
                                Log.e("signout_OK","signout_OK");return;
                            }
                        }
                    });

                    dialog_isSignout.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this,"您已取消", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog_isSignout.show();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void initFirestore() {
        // TODO(developer): Implement
        mFirestore = FirebaseFirestore.getInstance();

        // Get the 50 highest rated restaurants
        mQuery = mFirestore.collection("restaurants")
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .limit(LIMIT);
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.e("jaja","haha");
        // Start sign in if necessary


    }
    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }
    private void startSignIn() {
        // 登入FirebaseUI，若該帳號第一次登入就會執行註冊
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);//設定成已登入
    }

    public void Firbase_GetUserData(){
        final FirebaseFirestore user_db = FirebaseFirestore.getInstance();
        if(user_db != null){

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null){

                string_uid = user.getUid();     //抓取使用者UID
                string_email =user.getEmail();

                nav_header_email.setText(string_email);

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
                                    if(document.getData().get("名子") != null){
                                        string_name = document.getData().get("名子").toString();
                                    }else {
                                        string_name ="個人資料尚未填寫完畢";
                                    }
                                } else {
                                    Log.e(TAG, "No such document");
                                }
                                nav_header_name.setText(string_name);
                            } else {
                                Log.e(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                }

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.e(TAG, "DocumentSnapshot data: " + document.getData());

                                if(document.getData().get("大頭貼") != null){
                                    string_head_name = document.getData().get("大頭貼").toString();
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                    StorageReference dateRef = storageRef.child("profile_pic_" + string_uid +"/"+ string_head_name);
                                    dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                    {
                                        @Override
                                        public void onSuccess(Uri downloadUrl)
                                        {
                                            Glide.with(MainActivity.this)
                                                    .load(downloadUrl)
                                                    .into(nav_header_pic);
                                        }
                                    });
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
    }


}

