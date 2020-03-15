package com.example.myapplication_mapnavigationdrawer.ui.PersonalInfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication_mapnavigationdrawer.R;

public class PersonalInfoFragment extends Fragment {

    private PersonalInfoViewModel personalInfoViewModel;
    private TextView user_name;
    private TextView phon_number;
    private TextView plate_number;
    private Button btn_edit;
    private Context context;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        personalInfoViewModel =
                ViewModelProviders.of(this).get(PersonalInfoViewModel.class);
        View root = inflater.inflate(R.layout.personalinfo, container, false);
        setHasOptionsMenu(true);
        context = root.getContext();
        user_name = root.findViewById(R.id.show_name);
        phon_number = root.findViewById(R.id.phone_number);
        plate_number = root.findViewWithTag(R.id.show_plate);
        btn_edit = root.findViewById(R.id.btn_eidt_personalinfo);


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