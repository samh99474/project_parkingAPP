package com.example.myapplication_mapnavigationdrawer.ui.home;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication_mapnavigationdrawer.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeFragment extends Fragment implements OnMapReadyCallback
{

    private HomeViewModel homeViewModel;
    private GoogleMap mymap;
    private String string_parkinglot_name;
    private String string_parkinglot_snippet;
    public float zoomLevel;

    // private AppCompatActivity;
    private final static int REQUEST_PERMISSIONS = 1;

    @Override
    public void onRequestPermissionsResult ( int requsetCode, String
            permissions[],int[] grantResults){
        switch (requsetCode) {
            case REQUEST_PERMISSIONS:
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        getActivity().finish();
                    } else {
                        SupportMapFragment map =
                                (SupportMapFragment) getChildFragmentManager().findFragmentById ( R.id.map );
                        map.getMapAsync ( this );
                    }
                }
                break;
        }
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final MyGsonData mygsondata = new Gson().fromJson(intent.getExtras().getString("json"), MyGsonData.class);

            for (int i=0; i<mygsondata.data.parkinglots.length-1; i++) {
                //Log.e("res", mygsondata.data.parkinglots[i].id+"");

                MarkerOptions m1 = new MarkerOptions ();

                int height = 100;
                int width = 100;
                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.loticon);
                Bitmap b=bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.mipmap.loticon_reservatable);
                Bitmap b2=bitmapdraw2.getBitmap();
                Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, 115, 115, false);

                MarkerOptions m2 = new MarkerOptions ();
                m2.position ( new LatLng ( 25.0421794, 121.5351166 ) );
                m2.title ( "北科大APP特約停車場" );
                m2.snippet("100,100,0 / H,免費(宗演教授幫您支付),true,24H,神秘地區,0800-092-000,25.034,121.545,0001,停車場類型：室外平面,");
                m2.draggable ( true );
                m2.icon(BitmapDescriptorFactory.fromBitmap(smallMarker2));
                mymap.addMarker ( m2 );

                m1.position ( new LatLng( mygsondata.data.parkinglots[i].lat  , mygsondata.data.parkinglots[i].lng ) );
                m1.title( mygsondata.data.parkinglots[i].name );

                if(mygsondata.data.parkinglots[i].current_price_text != null){
                    //m1.snippet()  0總車位,1剩餘車位,    2費率simple_description,  3費率full_description,    4 is_opening,
                    // 5 today_service_time,   6  地址, 7電話tel, 8 lat,  9 lng,  10 API id ,
                    m1.snippet(mygsondata.data.parkinglots[i].total_lots+","
                            +mygsondata.data.parkinglots[i].available_lots+","
                            +mygsondata.data.parkinglots[i].current_price_text.simple_description+","
                            +mygsondata.data.parkinglots[i].detail_info[2][1]+","
                            +mygsondata.data.parkinglots[i].is_opening+","
                            +mygsondata.data.parkinglots[i].today_service_time+","
                            +mygsondata.data.parkinglots[i].address+","
                            +mygsondata.data.parkinglots[i].tel+","
                            +mygsondata.data.parkinglots[i].lat+","
                            +mygsondata.data.parkinglots[i].lng+","
                            +mygsondata.data.parkinglots[i].id+","
                            +mygsondata.data.parkinglots[i].detail_info[3][0]
                            +mygsondata.data.parkinglots[i].detail_info[3][1]+"\n"+",");

                }


/*
                if(mygsondata.data.parkinglots[i].total_lots == -1){
                        m1.snippet("●總車位:  無資訊");
                }
                else if (mygsondata.data.parkinglots[i].available_lots ==-1){
                    m1.snippet("●總車位: "+mygsondata.data.parkinglots[i].total_lots);
                }
                else{
                    m1.snippet("●總車位: "+mygsondata.data.parkinglots[i].total_lots+"  ●剩餘車位: " +mygsondata.data.parkinglots[i].available_lots);
                }

                m1.snippet(m1.getSnippet()+"\n●費率: "+mygsondata.data.parkinglots[i].current_price_text.simple_description);

                if(mygsondata.data.parkinglots[i].is_opening == true){
                    m1.snippet(m1.getSnippet()+"\n●營業中");
                }else {
                    m1.snippet(m1.getSnippet()+"\n●休息中");
                }

 */

                //m1.draggable ( true );


                        //m1.icon(BitmapDescriptorFactory.fromResource(smallMarker));
                m1.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                if (zoomLevel > 13) {
                    mymap.addMarker(m1);
                    mymap.addMarker(m2);

                    Log.e("HomeFragment", "shit");
                }if (zoomLevel <= 13) {
                    mymap.clear();

                    Log.e("HomeFragment", "fuck");
                }



            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null)
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                    new IntentFilter("MyMessage"));
        zoomLevel=7;

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.activity_main2, container, false);

        if (ActivityCompat.checkSelfPermission ( getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions ( getActivity(),new
                            String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS);
        else{
            SupportMapFragment map =
                    (SupportMapFragment) getChildFragmentManager().findFragmentById ( R.id.map );
            map.getMapAsync ( this );
        }

/*        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getActivity(, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
 */

        return root;
    }


    public void onActivityCreated(@Nullable Bundle savedInstanceState,Double lat,Double lng) {
        super.onActivityCreated(savedInstanceState);
        String str = null;
        Log.e("GET",lat.toString());
        Log.e("GET", lng.toString());

        str = String.format("https://api.parkinglotapp.com/v2/pois/nearest?lat=%s&lng=%s",lat,lng);
        Request req = new Request.Builder().url(str).build();
        new OkHttpClient().newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("查詢失敗", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    String json = response.body().string();
                    Intent i = new Intent("MyMessage");
                    i.putExtra("json", json);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap map){
        if (ActivityCompat.checkSelfPermission ( getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission ( getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)!=
                        PackageManager.PERMISSION_GRANTED)
            return;
        Marker mCenterMarker = null;

        mymap = map;
        mymap.setMyLocationEnabled ( true );
        final MarkerOptions m1 = new MarkerOptions ();

        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.loticon);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        mymap.moveCamera ( CameraUpdateFactory.newLatLngZoom (
                new LatLng ( 25.034,121.545 ), 13 ) );


        //Toast
        mymap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //Toast.makeText(getActivity(),"這裡的緯度是:"+marker.getPosition().latitude+"",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mymap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                CameraPosition test = mymap.getCameraPosition();
                onActivityCreated(null,test.target.latitude,test.target.longitude);
                zoomLevel = map.getCameraPosition().zoom;
            }
        });

        //點marker跳itude+"",Toast.LENGTH_SHORT).show();
        //                return false;infowindo

        mymap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker arg0) {
                // TODO Auto-generated method stub

                string_parkinglot_name = arg0.getTitle();  //點擊Marker window時觸發，取得點擊marker的title(停車場NAME)
                string_parkinglot_snippet = arg0.getSnippet();
                Toast.makeText(getActivity(),"停車場名稱:"+string_parkinglot_name,Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(),
                        infowindow.class);

                Bundle b = new Bundle();    //資訊放入Bundle
                b.putString("string_parkinglot_name",string_parkinglot_name);
                b.putString("string_parkinglot_snippet",string_parkinglot_snippet);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        //自訂義marker infowindow adapter
        mymap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;    //どちらかに処理を記述
            }

            @Override
            public View getInfoContents(Marker marker) {
                string_parkinglot_name = marker.getTitle();
                string_parkinglot_snippet = marker.getSnippet();

                View view = getLayoutInflater().inflate(R.layout.activity_marker_window_adapter,null);
                TextView windowadapter_parkinglot_name = (TextView)view.findViewById(R.id.windowadapter_parkinglot_name);
                TextView windowadapter_parkinglot_lot = (TextView)view.findViewById(R.id.windowadapter_parkinglot_lot);
                windowadapter_parkinglot_name.setText(string_parkinglot_name);

                String[] split_string_parkinglot_snippet = string_parkinglot_snippet.split(",");//split(指定符號) ，可依指定符號把字串分開成陣列

                if(split_string_parkinglot_snippet[0].equals("-1") == true){
                    windowadapter_parkinglot_lot.setText("●總車位:  無資訊");
                }
                else if (split_string_parkinglot_snippet[1].equals("-1") == true){
                    windowadapter_parkinglot_lot.setText("●總車位: "+split_string_parkinglot_snippet[0]);
                }
                else{
                    windowadapter_parkinglot_lot.setText("●總車位: "+split_string_parkinglot_snippet[0]+"  ●剩餘車位: "+split_string_parkinglot_snippet[1]);
                }

                windowadapter_parkinglot_lot.setText(windowadapter_parkinglot_lot.getText()+"\n●費率: "+split_string_parkinglot_snippet[2]);


                if(split_string_parkinglot_snippet[4].matches("true")){
                    windowadapter_parkinglot_lot.setText(windowadapter_parkinglot_lot.getText()+"\n●營業中");
                }else {
                    windowadapter_parkinglot_lot.setText(windowadapter_parkinglot_lot.getText()+"\n●休息中");
                }
                return view;    //どちらかに処理を記述
            }
        });



    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }
}