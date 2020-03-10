package com.example.myapplication_mapnavigationdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication_mapnavigationdrawer.adapter.RestaurantAdapter;
import com.example.myapplication_mapnavigationdrawer.viewmodel.MainActivityViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.Collections;

public class HomeActivity extends AppCompatActivity implements
        View.OnClickListener,
        FilterDialogFragment.FilterListener,
        RestaurantAdapter.OnRestaurantSelectedListener{

        private Button login;
        private Button signUp;
        private FirebaseAuth mAuth;
        private Text login_success;

        private MainActivityViewModel mViewModel;
        private static final int RC_SIGN_IN = 9001;
        private RestaurantAdapter mAdapter;
        private FirebaseFirestore mFirestore;
        private Query mQuery;
        private static final int LIMIT = 50;

        private static final String TAG = "MainActivity";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mAuth = FirebaseAuth.getInstance();
            setContentView(R.layout.activity_home);
            login = (Button) findViewById(R.id.login);
            //FirebaseUser user = mAuth.getCurrentUser();

            /*if(user == null){
                setContentView(R.layout.activity_home);
                login = (Button) findViewById(R.id.login);
                signUp = (Button) findViewById(R.id.sign_up);
                signUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                });
            } else{
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }*/
            FirebaseFirestore.setLoggingEnabled(true);
            // View model
            mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
            // Initialize Firestore and the main RecyclerView
            initFirestore();
            initRecyclerView();

            if(shouldStartSignIn())
            {
                startSignIn();
                Log.e("sss","success");
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //登入成功後，把登入成功後傳回
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }


    private void initFirestore() {
        // TODO(developer): Implement
        mFirestore = FirebaseFirestore.getInstance();

        // Get the 50 highest rated restaurants
        mQuery = mFirestore.collection("restaurants")
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .limit(LIMIT);
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
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);//設定成已登入
    }
    @Override
    public void onStart() {
        super.onStart();
        // Start sign in if necessary
        /*if (shouldStartSignIn()) {
            startSignIn();
            return;
        }*/
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onFilter(Filters filters) {

    }

    @Override
    public void onRestaurantSelected(DocumentSnapshot restaurant) {

    }
}
