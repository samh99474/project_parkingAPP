package com.example.myapplication_mapnavigationdrawer.ui.PersonalInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.myapplication_mapnavigationdrawer.MainActivity;
import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.viewmodel.MainActivityViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.Collections;

import static android.app.Activity.RESULT_OK;

public class PersonalInfoFragment extends Fragment {

    private PersonalInfoViewModel personalInfoViewModel;
    private TextView user_name;
    private TextView phon_number;
    private TextView license_number;
    private Button btn_edit;
    private ImageView profile_head;
    private Context context;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private MainActivityViewModel mViewModel;
    public int i;
    private static final String TAG = "PersonalInfoFragment";
    private static final int RC_SIGN_IN = 9001;
    public String string_uid ,name ,phone ,license, string_head_name;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        personalInfoViewModel =
                ViewModelProviders.of(this).get(PersonalInfoViewModel.class);
        View root = inflater.inflate(R.layout.personalinfo, container, false);
        setHasOptionsMenu(true);
        context = root.getContext();
        user_name = root.findViewById(R.id.show_name);
        phon_number = root.findViewById(R.id.show_phone_number);
        license_number = root.findViewById(R.id.show_license);
        btn_edit = root.findViewById(R.id.btn_eidt_personalinfo);
        profile_head = root.findViewById(R.id.profile_head);


        final FirebaseFirestore user_db = FirebaseFirestore.getInstance();
        if(user_db != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                string_uid = user.getUid();     //抓取使用者UID

                DocumentReference docRef = user_db.collection("users").document(string_uid);
                if (docRef != null) {
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                                    name = document.getData().get("名子").toString();
                                    phone = document.getData().get("手機號碼").toString();
                                    license = document.getData().get("車牌號碼").toString();

                                } else {
                                    Log.e(TAG, "No such document");
                                }
                                user_name.setText(name);
                                phon_number.setText(phone);
                                license_number.setText(license);
                            } else {
                                Log.e(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                                    string_head_name = document.getData().get("大頭貼").toString();

                                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                    StorageReference dateRef = storageRef.child("profile_pic_" + string_uid + "/" + string_head_name);
                                    dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri downloadUrl) {
                                            Glide.with(getActivity())
                                                    .load(downloadUrl)
                                                    .into(profile_head);
                                        }
                                    });

                                } else {
                                    Log.e(TAG, "No such document");
                                }

                            } else {
                                Log.e(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

                    btn_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(),
                                    SettingsPersonalInfoActivity.class);

                            Bundle b = new Bundle();    //資訊放入Bundle
                            b.putString("string_user_name", user_name.getText().toString());
                            b.putString("string_user_phone", phon_number.getText().toString());
                            b.putString("string_user_license", license_number.getText().toString());
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    });

                }
            } else {
                btn_edit.setClickable(false);
                btn_edit.setPressed(false);
                Toast.makeText(getActivity(), "尚未登入，請先登入", Toast.LENGTH_SHORT).show();
            }
        }

        FirebaseFirestore.setLoggingEnabled(true);
        // View model
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        // Initialize Firestore and the main RecyclerView



                /*.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> user = new HashMap<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                i++;
                                Log.e(TAG, document.getId() + " => " + document.getData() + i+"個id");
                                name = document.getData().get("名子").toString();
                                phone = document.getData().get("手機號碼").toString();
                                plate = document.getData().get("車牌號碼").toString();
                            }
                            user_name.setText(name);
                            phon_number.setText(phone);
                            plate_number.setText(plate);
Log.e(TAG, "Error getting documents.", task.getException());

                        } else {

                        }
                    }
                });

*/

        return root;
    }
}