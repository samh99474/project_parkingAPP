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


public class HomeFragment extends Fragment implements
        OnMapReadyCallback
{

    private HomeViewModel homeViewModel;
    private GoogleMap mymap;
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyGsonData mygsondata = new Gson().fromJson(intent.getExtras().getString("json"), MyGsonData.class);

            for (int i=0; i<mygsondata.data.parkinglots.length-1; i++) {
                Log.e("res", mygsondata.data.parkinglots[i].id+"");

                MarkerOptions m1 = new MarkerOptions ();

                int height = 100;
                int width = 100;
                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.loticon);
                Bitmap b=bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                m1.position ( new LatLng( mygsondata.data.parkinglots[i].lat  , mygsondata.data.parkinglots[i].lng ) );
                m1.title( mygsondata.data.parkinglots[i].name );
                m1.draggable ( true );
                //m1.icon(BitmapDescriptorFactory.fromResource(smallMarker));
                m1.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                if (mymap != null){
                    mymap.addMarker ( m1 );
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Request req = new Request.Builder().url("https://api.parkinglotapp.com/v2/pois/nearest?lat=25.047924%20&lng=121.517081").build();
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
    public void onMapReady(GoogleMap map){
        if (ActivityCompat.checkSelfPermission ( getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission ( getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)!=
                        PackageManager.PERMISSION_GRANTED)
            return;
        mymap = map;
        mymap.setMyLocationEnabled ( true );
        MarkerOptions m1 = new MarkerOptions ();

        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.loticon);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


        m1.position ( new LatLng( 25.033611, 121.565000 ) );
        m1.title("台北101");
        m1.draggable ( true );
        //m1.icon(BitmapDescriptorFactory.fromResource(smallMarker));
        m1.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        mymap.addMarker ( m1 );

        MarkerOptions m2 = new MarkerOptions ();
        m2.position ( new LatLng ( 25.047924, 121.517081 ) );
        m2.title ( "台北車站" );
        m2.draggable ( true );
        m2.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        mymap.addMarker ( m2 );

        mymap.moveCamera ( CameraUpdateFactory.newLatLngZoom (
                new LatLng ( 25.034,121.545 ), 13 ) );

        //Toast
        mymap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Toast.makeText(getActivity(),"這裡的緯度是:"+marker.getPosition().latitude+"",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //點marker跳itude+"",Toast.LENGTH_SHORT).show();
        //                return false;infowindo
        mymap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(),
                        infowindow.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }
}