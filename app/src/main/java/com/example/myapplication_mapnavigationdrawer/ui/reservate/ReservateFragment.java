package com.example.myapplication_mapnavigationdrawer.ui.reservate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.ui.gallery.GalleryViewModel;

public class ReservateFragment extends Fragment {

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