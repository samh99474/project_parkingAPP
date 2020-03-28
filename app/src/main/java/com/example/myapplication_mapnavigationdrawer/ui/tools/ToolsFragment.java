package com.example.myapplication_mapnavigationdrawer.ui.tools;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication_mapnavigationdrawer.MainActivity;
import com.example.myapplication_mapnavigationdrawer.MyDBHelper;
import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.ui.home.HomeFragment;

import java.util.ArrayList;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<String> items_lat_lng = new ArrayList<>();

    private SQLiteDatabase dari;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.favorite, container, false);
        //listView = (ListView)listView.findViewById(R.id.listView);

    /*    final TextView textView = root.findViewById(R.id.text_favorite);
        toolsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

     */
        listView = root.findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, items);

        listView.setAdapter(adapter);
        dari = new MyDBHelper(getActivity()).getWritableDatabase();

        Cursor c;
        c = dari.rawQuery("SELECT * FROM myTable",null);
        Toast.makeText(getActivity(),"共有" + c.getCount() +
                "筆資料", Toast.LENGTH_SHORT).show();

        c.moveToFirst();//指標一開始是指向-1的位置，所以源碼裡有一行moveToFirst()，把cursor指到第一個位置，否則會出現錯誤
        for(int i=0;i< c.getCount();i++){

            items.add("\t" + c.getString(1) +
                    "\n\t地址：" + c.getString(5) +
                    "\n\t費率：" + c.getString(4));
            items_lat_lng.add(","+c.getString(2) + "," + c.getString(3)+",");
            c.moveToNext();
        }


        //更新listView內容
        adapter.notifyDataSetChanged();
        //關閉cursor
        c.close();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),items.get(position), Toast.LENGTH_SHORT).show();
                String[] strings_items_lat_lng = items_lat_lng.get(position).toString().split(",");//split(指定符號) ，可依指定符號把字串分開成陣列

                Bundle b_favorite_lat_lng = new Bundle();    //資訊放入Bundle
                b_favorite_lat_lng.putString("string_favorite_lat",strings_items_lat_lng[1]);//lat
                b_favorite_lat_lng.putString("string_favorite_lng",strings_items_lat_lng[2]);//lng

                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(b_favorite_lat_lng);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, homeFragment)
                        .addToBackStack("TAG_TO_FRAGMENT").commit();
            }
        });
        return root;
    }
}