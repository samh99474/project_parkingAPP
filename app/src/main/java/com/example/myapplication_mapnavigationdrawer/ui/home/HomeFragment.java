package com.example.myapplication_mapnavigationdrawer.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;

import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication_mapnavigationdrawer.MainActivity;
import com.example.myapplication_mapnavigationdrawer.R;
import com.example.myapplication_mapnavigationdrawer.ui.record.RecordFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.SEARCH_SERVICE;
import static com.example.myapplication_mapnavigationdrawer.FilterDialogFragment.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private HomeViewModel homeViewModel;
    private GoogleMap mymap;
    private SupportMapFragment map;
    private FusedLocationProviderClient client;
    private String string_parkinglot_name;
    private String string_parkinglot_snippet;
    private String string_favorite_lat;
    private String string_favorite_lng;
    private String title_parkinglot_name, full_description, simple_description, parkinglot_total_space,
            parkinglot_price_number, API_id, more_detail_info, today_service_time, parkinglot_address, parkinglot_phone;
    private Boolean is_opening, reservatable;
    private Double parkinglot_lat, parkinglot_lng;
    private Long parkinglot_remain_space;
    public float zoomLevel;
    private int number;

    private EditText mSearchText;
    private ImageButton btn_mylocation;

    // private AppCompatActivity;
    private final static int REQUEST_PERMISSIONS = 1;


    @Override
    public void onRequestPermissionsResult(int requsetCode, String
            permissions[], int[] grantResults) {
        switch (requsetCode) {
            case REQUEST_PERMISSIONS:
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        getActivity().finish();
                    } else {
                        SupportMapFragment map =
                                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                        map.getMapAsync(this);
                        getCurrentLocation();
                    }
                }
                break;
        }
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        private ClusterManager<MyItem> mClusterManager;

        @Override
        public void onReceive(Context context, Intent intent) {
            final MyGsonData mygsondata = new Gson().fromJson(intent.getExtras().getString("json"), MyGsonData.class);

            for (int i = 0; i < mygsondata.data.parkinglots.length - 1; i++) {
                //Log.e("res", mygsondata.data.parkinglots[i].id+"");
                //MyItem offsetItem = new MyItem(mygsondata.data.parkinglots[i].lat, mygsondata.data.parkinglots[i].lng);
                //mClusterManager.addItem(offsetItem);

                final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                final DocumentReference docRef_parkinglot_info = firestore.collection("reservatable parkinglot").document("APP特約停車場").collection("info").document("detail_info");

                try {
                    if (docRef_parkinglot_info != null) {
                        docRef_parkinglot_info.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //抓取parking grid/A1 是否使用中或已被預約
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                                        title_parkinglot_name = (String) document.get("停車場名稱");
                                        full_description = (String) document.get("full_description");
                                        simple_description = (String) document.get("simple_description");
                                        parkinglot_remain_space = (Long) document.get("剩餘車位");
                                        parkinglot_total_space = (String) document.get("總車位");
                                        parkinglot_price_number = (String) document.get("費率");
                                        API_id = (String) document.get("API_id");
                                        more_detail_info = (String) document.get("more_detail_info");
                                        today_service_time = (String) document.get("today_service_time");
                                        parkinglot_address = (String) document.get("地址");
                                        parkinglot_phone = (String) document.get("電話");
                                        is_opening = (Boolean) document.get("is_opening");
                                        reservatable = (Boolean) document.get("reservatable");
                                        parkinglot_lat = (Double) document.get("lat");
                                        parkinglot_lng = (Double) document.get("lng");


                                        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.mipmap.loticon_reservatable);
                                        Bitmap b2 = bitmapdraw2.getBitmap();
                                        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, 115, 115, false);

                                        MarkerOptions m2 = new MarkerOptions();
                                        m2.position(new LatLng(parkinglot_lat, parkinglot_lng));
                                        m2.title(title_parkinglot_name);
                                        m2.snippet(parkinglot_total_space + "," +
                                                parkinglot_remain_space + "," +
                                                simple_description + "," +
                                                full_description + "," +
                                                String.valueOf(is_opening) + "," +
                                                today_service_time + "," +
                                                parkinglot_address + "," +
                                                parkinglot_phone + "," +
                                                String.valueOf(parkinglot_lat) + "," +
                                                String.valueOf(parkinglot_lng) + "," +
                                                API_id + "," +
                                                more_detail_info + "," +
                                                String.valueOf(reservatable) + ",");
                                        m2.draggable(true);
                                        m2.icon(BitmapDescriptorFactory.fromBitmap(smallMarker2));
                                        mymap.addMarker(m2);

                                        if (zoomLevel > 13) {
                                            mymap.addMarker(m2);
                                        }

                                    } else {
                                        Log.e(TAG, "No such document");
                                    }
                                } else {
                                    Log.e(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                MarkerOptions m1 = new MarkerOptions();

                int height = 100;
                int width = 100;
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.loticon);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                m1.position(new LatLng(mygsondata.data.parkinglots[i].lat, mygsondata.data.parkinglots[i].lng));
                m1.title(mygsondata.data.parkinglots[i].name);

                if (mygsondata.data.parkinglots[i].current_price_text != null) {
                    //m1.snippet()  0總車位,1剩餘車位,    2費率simple_description,  3費率full_description,    4 is_opening,
                    // 5 today_service_time,   6  地址, 7電話tel, 8 lat,  9 lng,  10 API id , 11 停車場detail info, 12 是否可預約
                    m1.snippet(mygsondata.data.parkinglots[i].total_lots + ","
                            + mygsondata.data.parkinglots[i].available_lots + ","
                            + mygsondata.data.parkinglots[i].current_price_text.simple_description + ","
                            + mygsondata.data.parkinglots[i].detail_info[2][1] + ","
                            + mygsondata.data.parkinglots[i].is_opening + ","
                            + mygsondata.data.parkinglots[i].today_service_time + ","
                            + mygsondata.data.parkinglots[i].address + ","
                            + mygsondata.data.parkinglots[i].tel + ","
                            + mygsondata.data.parkinglots[i].lat + ","
                            + mygsondata.data.parkinglots[i].lng + ","
                            + mygsondata.data.parkinglots[i].id + ","
                            + mygsondata.data.parkinglots[i].detail_info[3][0]
                            + mygsondata.data.parkinglots[i].detail_info[3][1] + "\n" + ","
                            + "false" + ",");
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

            try {
                if (zoomLevel > 13) {
                    mymap.addMarker(m1);
                }
                if (zoomLevel <= 13) {
                    mymap.clear();
                }

                if (number <= 400) {  //載入太多API的話，清除mymap
                    number = number + 1;
                } else {
                    number = 0;
                    mymap.clear();
                    mymap.addMarker(m1);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            }
            //mClusterManager.cluster();
        }

        private void setUpClusterer() {
            // Position the map.
            //mymap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

            // Initialize the manager with the context and the map.
            // (Activity extends context, so we can pass 'this' in the constructor.)
            mClusterManager = new ClusterManager<MyItem>(getActivity(), mymap);

            // Point the map's listeners at the listeners implemented by the cluster
            // manager.
            mymap.setOnCameraIdleListener(mClusterManager);
            mymap.setOnMarkerClickListener(mClusterManager);

            // Add cluster items (markers) to the cluster manager.

        }

    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        zoomLevel = 7;

    }

    private void init() {
        Log.d("TAG", "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geoLocate();
                    return true;
                }

                return false;
            }
        });
    }

    private void geoLocate() {           //搜尋地點
        Log.d("TAG", "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e("TAG", "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Toast.makeText(getActivity(), "正在搜尋:\t" + address.getAddressLine(0), Toast.LENGTH_SHORT).show();
            mSearchText.setText(null);

            Log.d("TAG", "geoLocate: found a location: " + address.toString());
            /*
            Toast.makeText(getActivity(),address.getFeatureName()+",\t"+address.getAdminArea()+",\t"+address.getLocality()+
                    "\n"+ address.getLatitude()+"\n"+address.getLongitude(),Toast.LENGTH_SHORT).show();

             */

            mymap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(address.getLatitude(), address.getLongitude()), 13));

            //演算出適當zoomlevel的大小
            if (address.getSubAdminArea() == null && address.getAdminArea() == null) {
                mymap.animateCamera(CameraUpdateFactory.zoomTo(5), 2000, null);

            } else {
                if (address.getThoroughfare() != null) {
                    mymap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                } else {
                    mymap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                }
            }
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "搜尋無結果", Toast.LENGTH_SHORT).show();
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() != null)
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                    new IntentFilter("MyMessage"));
        homeViewModel =
                ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.activity_main2, container, false);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new
                            String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS);
            Toast.makeText(getActivity(), "無法讀取您的位置，請檢查是否開啟定位，或者稍後再試", Toast.LENGTH_SHORT).show();
        }
        else {
            map =
                    (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            map.getMapAsync(this);

            client = LocationServices.getFusedLocationProviderClient(getActivity());

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }else {
                //when permission denied
                //Request permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            }
        }

        mSearchText = (EditText) root.findViewById(R.id.input_search);
        btn_mylocation = (ImageButton) root.findViewById(R.id.btn_mylocation);


        btn_mylocation.setOnClickListener(new View.OnClickListener() {      //my current location button
            @Override
            public void onClick(View view) {
                getCurrentLocation();
/*
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                //設定抓取使用者當前位置
                LocationManager locationManager =
                        (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);
//        locationManager.requestLocationUpdates(provider, 10000, 10, (LocationListener) this);

                locationManager.requestLocationUpdates
                        (LocationManager.NETWORK_PROVIDER, 1, 0.1f, LocationChange);

                final Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                if (location != null) {
                    mymap.animateCamera(CameraUpdateFactory.newLatLngZoom(new   //畫面移動並放大到使用者當前位置
                            LatLng(location.getLatitude(),
                            location.getLongitude()), 17));
                } else {

                    Toast.makeText(getActivity(), "無法讀取您的位置，請檢查是否開啟定位，或者稍後再試", Toast.LENGTH_SHORT).show();
                }

 */

            }
        });




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


    public void onActivityCreated(@Nullable Bundle savedInstanceState, Double lat, Double lng) {
        super.onActivityCreated(savedInstanceState);
        String str = null;
        Log.e("GET", lat.toString());
        Log.e("GET", lng.toString());

        str = String.format("https://api.parkinglotapp.com/v2/pois/nearest?lat=%s&lng=%s", lat, lng);
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
    public void onMapReady(final GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }

        init();//search搜尋地點功能

        //Google語音搜尋功能
        ImageButton btn_google_speak = (ImageButton) getActivity().findViewById(R.id.btn_google_speak);
        btn_google_speak.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說您要尋找的地點");
                try {
                    startActivityForResult(intent, 200);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Intent problem", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Marker mCenterMarker = null;

        mymap = map;
        mymap.setMyLocationEnabled(true);
        mymap.getUiSettings().setMyLocationButtonEnabled(false);    // delete default button(my location button)
        //setUpClusterer();
        //mClusterManager.setRenderer(new MarkerClusterRenderer(getActivity(), mymap, mClusterManager));

        final MarkerOptions m1 = new MarkerOptions();

        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.loticon);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        Bundle bundle_favorite = getArguments();//當使用者點擊到我的最愛list，取得Favorite的Bundle
        if (bundle_favorite != null)            //假使有取得到，則Map ZOOM到此地點
        {
            string_favorite_lat = bundle_favorite.getString("string_favorite_lat");
            string_favorite_lng = bundle_favorite.getString("string_favorite_lng");

            mymap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Double.parseDouble(string_favorite_lat), Double.parseDouble(string_favorite_lng)), 13));
            mymap.animateCamera(CameraUpdateFactory.zoomTo(19), 10, null);

        } else {
            getCurrentLocation();
/*
            //設定抓取使用者當前位置
            LocationManager locationManager =
                    (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
//        locationManager.requestLocationUpdates(provider, 10000, 10, (LocationListener) this);

            locationManager.requestLocationUpdates
                    (LocationManager.NETWORK_PROVIDER, 1, 0.1f, LocationChange);

            final Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

            if (location != null) {
                mymap.animateCamera(CameraUpdateFactory.newLatLngZoom(new   //畫面移動並放大到使用者當前位置
                        LatLng(location.getLatitude(),
                        location.getLongitude()), 17));
            } else {
                mymap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(25.047702, 121.516193), 13));
            }

 */
        }

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
                onActivityCreated(null, test.target.latitude, test.target.longitude);
                zoomLevel = map.getCameraPosition().zoom;

                if (zoomLevel <= 13) {
                    Toast.makeText(getActivity(), "放大地圖顯示停車場", Toast.LENGTH_SHORT).show();
                }
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
                //Toast.makeText(getActivity(),"停車場名稱:"+string_parkinglot_name,Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(),
                        infowindow.class);

                Bundle b = new Bundle();    //資訊放入Bundle
                b.putString("string_parkinglot_name", string_parkinglot_name);
                b.putString("string_parkinglot_snippet", string_parkinglot_snippet);
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

                View view = getLayoutInflater().inflate(R.layout.activity_marker_window_adapter, null);
                TextView windowadapter_parkinglot_name = (TextView) view.findViewById(R.id.windowadapter_parkinglot_name);
                TextView windowadapter_parkinglot_lot = (TextView) view.findViewById(R.id.windowadapter_parkinglot_lot);
                windowadapter_parkinglot_name.setText(string_parkinglot_name);

                String[] split_string_parkinglot_snippet = string_parkinglot_snippet.split(",");//split(指定符號) ，可依指定符號把字串分開成陣列

                if (split_string_parkinglot_snippet[0].equals("-1") == true) {
                    windowadapter_parkinglot_lot.setText("●總車位:  無資訊");
                } else if (split_string_parkinglot_snippet[1].equals("-1") == true) {
                    windowadapter_parkinglot_lot.setText("●總車位: " + split_string_parkinglot_snippet[0]);
                } else {
                    windowadapter_parkinglot_lot.setText("●總車位: " + split_string_parkinglot_snippet[0] + "  ●剩餘車位: " + split_string_parkinglot_snippet[1]);
                }

                windowadapter_parkinglot_lot.setText(windowadapter_parkinglot_lot.getText() + "\n●費率: " + split_string_parkinglot_snippet[2]);


                if (split_string_parkinglot_snippet[4].matches("true")) {
                    windowadapter_parkinglot_lot.setText(windowadapter_parkinglot_lot.getText() + "\n●營業中");
                } else {
                    windowadapter_parkinglot_lot.setText(windowadapter_parkinglot_lot.getText() + "\n●休息中");
                }
                return view;    //どちらかに処理を記述
            }
        });
    }

    private void getCurrentLocation() {
        //Initialize task Location
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        try{
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {
                    //when success
                    if(location != null){
                        //sync map
                        map.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                //Initialize lat lng
                                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                            }
                        });
                    }else {
                        Toast.makeText(getActivity(), "無法讀取您的位置，請檢查是否開啟定位，或者稍後再試", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e) {
            reflash_HomeFragment();
            e.printStackTrace();
        }
    }

    //Google語音讀取Intent resultcode requestcode 來執行，將語音轉文字到mSearchText
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mSearchText.setText(result.get(0)); //語音轉文字結果 讀到mSearchText
                geoLocate();                        //搜尋
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private String mTitle;
        private String mSnippet;

        public MyItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        public MyItem(double lat, double lng, String title, String snippet) {
            mPosition = new LatLng(lat, lng);
            mTitle = title;
            mSnippet = snippet;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public String getTitle() {
            return mTitle;
        }

        @Override
        public String getSnippet() {
            return mSnippet;
        }
    }
    //刷新Fragment頁面
    private void reflash_HomeFragment() {
        try {
            HomeFragment homeFragment = new HomeFragment();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, homeFragment)
                    .addToBackStack("TAG_TO_FRAGMENT").commit();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

/*
    public class MarkerClusterRenderer extends DefaultClusterRenderer<MyItem> {

        public MarkerClusterRenderer(Context context, GoogleMap map,
                                     ClusterManager<MyItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
            // use this to make your change to the marker option
            // for the marker before it gets render on the map
            int height = 120;
            int width = 120;
            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.loticon);
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        }
    }

 */


}