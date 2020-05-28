package com.example.myapplication_mapnavigationdrawer.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myapplication_mapnavigationdrawer.R;

import java.util.ArrayList;

public class RecordAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private ArrayList<String> web_record_order_number,web_record_parkinglot_time, web_txt_is_finish, web_txt_is_using, web_record_parkinglot_name,
            web_record_parkinglot_address, web_parkinglot_phone, web_current_price,  web_record_license, web_record_txt_press_finish, web_record_accumulated_price,
            web_record_accumulated_time = new ArrayList<>();


    public RecordAdapter(Activity context,
                         ArrayList<String> web_record_order_number, ArrayList<String> web_record_parkinglot_time, ArrayList<String> web_txt_is_finish, ArrayList<String> web_txt_is_using,
                         ArrayList<String> web_record_parkinglot_name, ArrayList<String> web_record_parkinglot_address, ArrayList<String> web_parkinglot_phone,
                         ArrayList<String> web_current_price, ArrayList<String> web_record_license, ArrayList<String> web_record_txt_press_finish,
                         ArrayList<String> web_record_accumulated_price, ArrayList<String> web_record_accumulated_time) {
        super(context, R.layout.record_adapterlist, web_record_parkinglot_time);
        this.context = context;
        this.web_record_order_number = web_record_order_number;
        this.web_record_parkinglot_time = web_record_parkinglot_time;
        this.web_txt_is_finish = web_txt_is_finish;
        this.web_txt_is_using = web_txt_is_using;
        this.web_record_parkinglot_name = web_record_parkinglot_name;
        this.web_record_parkinglot_address = web_record_parkinglot_address;
        this.web_parkinglot_phone = web_parkinglot_phone;
        this.web_current_price = web_current_price;
        this.web_record_license = web_record_license;
        this.web_record_txt_press_finish = web_record_txt_press_finish;
        this.web_record_accumulated_price = web_record_accumulated_price;
        this.web_record_accumulated_time = web_record_accumulated_time;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.record_adapterlist, null, true);

        TextView record_order_number = (TextView) rowView.findViewById(R.id.order_number);
        TextView record_parkinglot_time = (TextView) rowView.findViewById(R.id.record_parkinglot_time);
        TextView txt_is_finish = (TextView) rowView.findViewById(R.id.txt_is_finish);
        TextView txt_is_using = (TextView) rowView.findViewById(R.id.txt_is_using);
        TextView record_parkinglot_name = (TextView) rowView.findViewById(R.id.record_parkinglot_name);
        TextView record_parkinglot_address = (TextView) rowView.findViewById(R.id.record_parkinglot_address);
        TextView txt_web_parkinglot_phone = (TextView) rowView.findViewById(R.id.record_parkinglot_phone);
        TextView current_price = (TextView) rowView.findViewById(R.id.current_price);
        TextView record_license = (TextView) rowView.findViewById(R.id.record_license);
        TextView txt_press_finish = (TextView) rowView.findViewById(R.id.txt_press_finish);
        TextView txt_record_accumulated_price = (TextView) rowView.findViewById(R.id.record_accumulated_price);
        TextView txt_record_accumulated_time = (TextView) rowView.findViewById(R.id.record_accumulated_time);

        record_order_number.setText(web_record_order_number.get(position));
        record_parkinglot_time.setText(web_record_parkinglot_time.get(position));
        txt_is_finish.setText(web_txt_is_finish.get(position));
        txt_is_using.setText(web_txt_is_using.get(position));
        record_parkinglot_name.setText(web_record_parkinglot_name.get(position));
        record_parkinglot_address.setText(web_record_parkinglot_address.get(position));
        txt_web_parkinglot_phone.setText(web_parkinglot_phone.get(position));
        current_price.setText(web_current_price.get(position));
        record_license.setText(web_record_license.get(position));
        txt_press_finish.setText(web_record_txt_press_finish.get(position));
        txt_record_accumulated_price.setText(web_record_accumulated_price.get(position));
        txt_record_accumulated_time.setText(web_record_accumulated_time.get(position));
        return rowView;
    }

}
