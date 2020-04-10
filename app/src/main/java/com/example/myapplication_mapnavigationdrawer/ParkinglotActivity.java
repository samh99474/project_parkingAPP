package com.example.myapplication_mapnavigationdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication_mapnavigationdrawer.model.Rating;
import com.example.myapplication_mapnavigationdrawer.ui.PersonalInfo.SettingsPersonalInfoActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ParkinglotActivity extends AppCompatActivity {


    //if(預約||使用==0){ButtonA1可按}else{ButtonA1不可按 顏色改暗}
    private String choose;
    private Button A1;
    private Button A2;
    private Button A3;
    private Button gotoreservation;
    private Button returntomap;
    private FirebaseFirestore mFirestore;
    private boolean using = false;
    private boolean reserved = false;
    private Query mQuery;
    private static final String TAG = "ParkinglotActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkinglot);

//寫case

                final FirebaseFirestore parkingA1 = FirebaseFirestore.getInstance();
                final DocumentReference docRef = parkingA1.collection("parking grid").document("A1");
                A1 = findViewById(R.id.A1);
                A1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Boolean> A1info = new HashMap<>();
                        A1info.put("預約中", true);
                        parkingA1.collection("parking grid").document("A1").set(A1info, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e(TAG, "A1 successfully reserved!");
                                A1.setBackgroundColor(0xffff00ff);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error writing document", e);
                            }
                        });
                    }
                });

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取parking grid/A1
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                                reserved = (Boolean) document.get("預約中");
                                using = (Boolean) document.get("使用中");
                            } else {
                                Log.e(TAG, "No such document");
                            }
                            if (reserved == false) {
                                A1.setBackgroundColor(0xffff0000);
                            } else {
                                A1.setBackgroundColor(0xffff00ff);
                            }
                        } else {
                            Log.e(TAG, "get failed with ", task.getException());
                        }
                    }

                });



                final FirebaseFirestore parkingA2 = FirebaseFirestore.getInstance();
                final DocumentReference docRef2 = parkingA2.collection("parking grid").document("A2");
                A2 = findViewById(R.id.A2);
                A2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Map<String, Boolean> A2info = new HashMap<>();
                        A2info.put("預約中", true);
                        parkingA2.collection("parking grid").document("A2").set(A2info, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e(TAG, "A2 successfully reserved!");
                                A2.setBackgroundColor(0xffff00ff);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error writing document", e);
                            }
                        });
                    }
                });

                docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取parking grid/A2
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                                reserved = (Boolean) document.get("預約中");
                                using = (Boolean) document.get("使用中");
                            } else {
                                Log.e(TAG, "No such document");
                            }
                            if (reserved == false) {
                                A2.setBackgroundColor(0xffff0000);
                            } else {
                                A2.setBackgroundColor(0xffff00ff);
                            }
                        } else {
                            Log.e(TAG, "get failed with ", task.getException());
                        }
                    }

                });


                final FirebaseFirestore parkingA3 = FirebaseFirestore.getInstance();
                final DocumentReference docRef3 = parkingA3.collection("parking grid").document("A3");
                A3 = findViewById(R.id.A3);
                A3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Map<String, Boolean> A3info = new HashMap<>();
                        A3info.put("預約中", true);
                        parkingA3.collection("parking grid").document("A3").set(A3info, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e(TAG, "A3 successfully reserved!");
                                A3.setBackgroundColor(0xffff00ff);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error writing document", e);
                            }
                        });
                    }
                });

                docRef3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取parking grid/A3
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                                reserved = (Boolean) document.get("預約中");
                                using = (Boolean) document.get("使用中");
                            } else {
                                Log.e(TAG, "No such document");
                            }
                            if (reserved == false) {
                                A3.setBackgroundColor(0xffff0000);
                            } else {
                                A3.setBackgroundColor(0xffff00ff);
                            }
                        } else {
                            Log.e(TAG, "get failed with ", task.getException());
                        }
                    }

                });
        gotoreservation = findViewById(R.id.gotoreservation);
        returntomap = findViewById(R.id.returntomap);
        gotoreservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParkinglotActivity.this, ReservationActivity.class);
                startActivity(intent);
            }
        });
        returntomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });





    }
        }









           /* public void onChildChanged(DataSnapshot A1info, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Comment newComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();
            }
        }



}

                        /*@Override
                        public void onClick(View v) {
                                switch (v.getId()) {
                                        case R.id.gotoreservation:
                                                onSubmitClicked(v);
                                                break;
                                        case R.id.returntomap:
                                                onCancelClicked(v);
                                                break;
                                }
                        }
*/
                       /* public void onSubmitClicked(View view) {




                                dismiss();
                        }

                        public void onCancelClicked(View view) {

                                dismiss();
                        }
*/


