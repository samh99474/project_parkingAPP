package com.example.myapplication_mapnavigationdrawer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class FirebaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        FirebaseApp.initializeApp(this);
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
                );

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                1);

        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName("com.example.myapplication_mapnavigationdrawer", true, null)
        .setHandleCodeInApp(true) // This must be set to true
                .setUrl("https://google.com") // This URL needs to be whitelisted
                .build();



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(FirebaseActivity.this,"Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(FirebaseActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                // ...
            } else {

                Toast.makeText(FirebaseActivity.this,"Sign in failed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(FirebaseActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                //Sign in failed . If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

}



