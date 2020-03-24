/*package com.example.myapplication_mapnavigationdrawer;

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
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.myapplication_mapnavigationdrawer.ui.PersonalInfo.SettingsPersonalInfoActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReservationFragment extends Fragment {
    private ReservationFragment ReservationFragment;
    private TextView user_phone;
    private TextView user_license;
    private Context context;
    private Button btn_reserve;
    private static final String TAG = "ReservationFragment";
    public String phone,license;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
    ReservationFragment = ViewModelProviders.of(this).get(ReservationFragment.class);
    View root = inflater.inflate(R.layout.activity_reservation, container, false);
        setHasOptionsMenu(true);
        context = root.getContext();
        user_phone = root.findViewById(R.id.phone);
        user_license = root.findViewById(R.id.license);
        btn_reserve = root.findViewById(R.id.reserve);
        final FirebaseFirestore reservation_db = FirebaseFirestore.getInstance();
        DocumentReference docRef = reservation_db.collection("users").document("i");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e(TAG, "DocumentSnapshot data: " + document.getData());

                        phone = document.getData().get("手機號碼").toString();
                        license = document.getData().get("車牌號碼").toString();

                    } else {
                        Log.e(TAG, "No such document");
                    }

                    user_phone.setText(phone);
                    user_license.setText(license);
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });


        btn_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReservationActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }
}
*/