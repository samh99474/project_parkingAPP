package com.example.myapplication_mapnavigationdrawer.adapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication_mapnavigationdrawer.R;

import java.util.ArrayList;

public class FavoriteAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private ArrayList<String> web = new ArrayList<>();
    private ArrayList<Integer>  imageId;
    public FavoriteAdapter(Activity context,
                           ArrayList<String> web, ArrayList<Integer> imageId) {
        super(context, R.layout.favorite_adapterlist, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.favorite_adapterlist, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web.get(position));
        imageView.setImageResource(imageId.get(position));
        return rowView;
    }

}
