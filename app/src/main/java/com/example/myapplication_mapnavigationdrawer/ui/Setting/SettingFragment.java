package com.example.myapplication_mapnavigationdrawer.ui.Setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication_mapnavigationdrawer.R;

public class SettingFragment extends Fragment {

    private ReservateViewModel reservateViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reservateViewModel =
                ViewModelProviders.of(this).get(ReservateViewModel.class);
        View root = inflater.inflate(R.layout.settings, container, false);
        /*
        final TextView textView = root.findViewById(R.id.text_historical);
        reservateViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        return root;
    }
}