package com.example.myapplication_mapnavigationdrawer.ui.PersonalInfo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.viewmodel.MainActivityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class PersonalInfoFragment extends Fragment {

    private PersonalInfoViewModel personalInfoViewModel;
    private TextView user_name;
    private TextView phon_number;
    private TextView plate_number;
    private Button btn_edit;
    private Context context;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private MainActivityViewModel mViewModel;
    private static final String TAG = "PersonalInfoFragment";
    private int i = 0;
    private String name,phone,plate;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        personalInfoViewModel =
                ViewModelProviders.of(this).get(PersonalInfoViewModel.class);
        View root = inflater.inflate(R.layout.personalinfo, container, false);
        setHasOptionsMenu(true);
        context = root.getContext();
        user_name = root.findViewById(R.id.show_name);
        phon_number = root.findViewById(R.id.show_phone_number);
        plate_number = root.findViewById(R.id.show_plate);
        btn_edit = root.findViewById(R.id.btn_eidt_personalinfo);

        final FirebaseFirestore user_db = FirebaseFirestore.getInstance();

        user_db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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


                        } else {
                            Log.e(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SettingsPersonalInfoActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }


}