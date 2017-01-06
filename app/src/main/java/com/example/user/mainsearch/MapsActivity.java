
package com.example.user.mainsearch;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.example.user.mainsearch.R.id.map;

public class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback {

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    String url3;
    ImageButton btnHome,btnKeyword,btnMap,btnSpinner,btnFavorite,btnEver;
    public double LAT[]= new double[10000] ;
    public  ArrayList LATList = new ArrayList();
    public  ArrayList LNGList = new ArrayList();
    public  ArrayList NAMEList = new ArrayList();
    public  ArrayList ADDRESSList = new ArrayList();
    public  ArrayList PHOTOList = new ArrayList();
    public  ArrayList GYMIDList = new ArrayList();
    public double LNG[]= new double[10000] ;
    public String NAME[]= new String[10000];
    public String ADDRESS[]= new String[10000];
    private Spinner spinner;
    public String nextLink;
    public int countlatlng=0,runTime=0,countmarker=0;
    public boolean checkApi=false,flag=true;
    private GoogleMap mMap;
    private ArrayAdapter<String> lunchList;
    public String[] lunch = {"5", "10", "20", "30"};
    public double selectNum,oldnum=0;
    public String photoUrl="";
    SQLiteDatabase db= null;
    Cursor cursor;  //和TABLE溝通
    //SQL語法
    String CREATE_TABLE = "CREATE TABLE if not exists FavoriteListFinal"+"(_id INTEGER PRIMARY " +
            "KEY autoincrement,gymID TEXT,title TEXT,subtitle TEXT,LatLng TEXT,PhotoUrl TEXT)";

    LatLng latLng;
    char[] urlChar;
    public String temp;
    String encodeResult="";
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker mCurrLocation;
    GPSTracker gps;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btnHome = (ImageButton)findViewById(R.id.btnHome);
        btnKeyword = (ImageButton)findViewById(R.id.btnKeyword);
        btnMap = (ImageButton)findViewById(R.id.btnMap);
        btnSpinner = (ImageButton)findViewById(R.id.btnSpinner);
        btnFavorite = (ImageButton)findViewById(R.id.btnFavorite);
        btnEver = (ImageButton)findViewById(R.id.btnEver);
        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mFragment.getMapAsync(this);
        Thread t;
        btnHome.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnKeyword.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MapsActivity.this, MainSearch.class);
                startActivity(intent);
                finish();
            }
        });
        btnMap.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MapsActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSpinner.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MapsActivity.this, SearchSpinner.class);
                startActivity(intent);
            }
        });
        btnFavorite.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MapsActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        btnEver.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MapsActivity.this, EverActivity.class);
                startActivity(intent);
            }
        });
        db = openOrCreateDatabase("database.db",MODE_WORLD_WRITEABLE,null);
        db.execSQL(CREATE_TABLE); //建立資料表
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        spinner = (Spinner)findViewById(R.id.mySpinner);
        lunchList = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_spinner_item,
                lunch);
        spinner.setAdapter(lunchList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //Toast.makeText(mContext, "你選的是"+lunch[position], Toast.LENGTH_SHORT).show();
                selectNum = Double.valueOf(lunch[position].trim()).doubleValue();

                flag=false;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


    }

    public void GetApi(View view){

        if(oldnum != selectNum){
            flag=true;
            oldnum=selectNum;
            mGoogleMap.clear();
            callMap(selectNum);
        }
    }

    public void callMap(final double selectNum){


        //建立多執行緒進行網路Server API串接的資料傳輸與讀取
        gps = new GPSTracker(this);
        final double latitude = gps.getLatitude();
        final double longitude = gps.getLongitude();


        new Thread(new Runnable() {
            @Override
            public void run() {
                LATList.clear();
                LNGList.clear();
                NAMEList.clear();
                ADDRESSList.clear();
                countlatlng = 0;
                countmarker = 0;
                nextLink="";
                final double latitude = gps.getLatitude();
                final double longitude = gps.getLongitude();
                try {
                    //API串接的uri路徑

                    do {
                        if(flag==false){
                            throw new Exception("go catch");
                        }

                        String uri = "http://iplay.sa.gov" +
                                ".tw/odata/GymSearch?$format=application/json;" +
                                "odata.metadata=none&Latitude=" + latitude + "&Longitude=" + longitude;


                        if (nextLink != null && nextLink != "") {
                            uri = nextLink;
                        }

                        HttpClient mHttpClient = new DefaultHttpClient();
                        HttpGet mHttpGet = new HttpGet(uri);
                        HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);

                        // HttpURLConnection.HTTP_OK為200，200代表串接溝通成功
                        if (mHttpResponse.getStatusLine().getStatusCode() ==
                                HttpURLConnection
                                        .HTTP_OK) {
                            //API的回傳文字，格式為json格式
                            String mJsonText = EntityUtils.toString(mHttpResponse
                                    .getEntity());
                            //將json格式解開並取出名稱
                            String value = new JSONObject(mJsonText).getString("value");

                            for (int i = 0; i < 10; i++) {
                                //  NAME[countlatlng] = new JSONArray(new JSONObject(mJsonText)
                                //         .getString("value"))
                                //         .getJSONObject(i).getString("Name");


                                //     ADDRESS[countlatlng]  = new JSONArray(new JSONObject
                                // (mJsonText)
                                //             .getString
                                //             ("value"))
                                //             .getJSONObject(i).getString("Address");
                                String Photo1 = new JSONArray(new JSONObject(mJsonText)
                                        .getString("value"))
                                        .getJSONObject(i).getString("Photo1");
                                String LatLng = new JSONArray(new JSONObject(mJsonText).getString("value"))
                                        .getJSONObject(i).getString("LatLng");
                                String OperationName = new JSONArray(new JSONObject(mJsonText).getString("value"))
                                        .getJSONObject(i).getString("OperationName");
                                String OperationTel = new JSONArray(new JSONObject(mJsonText).getString("value"))
                                        .getJSONObject(i).getString("OperationTel");

                                String[] LatLng2 = LatLng.split(" ");
                                char[] a = LatLng2[0].toCharArray();
                                char[] b = LatLng2[1].toCharArray();
                                String a2 = "";
                                String b2 = "";

                                for (int j = 0; j < a.length; j++) {
                                    if (a[j] >= 48 && a[j] <= 57 || a[j] == 46) {
                                        a2 = a2 + a[j];
                                    }
                                }
                                for (int j = 0; j < b.length; j++) {
                                    if (b[j] >= 48 && b[j] <= 57 || b[j] == 46) {
                                        b2 = b2 + b[j];
                                    }
                                }
                                double lat = Double.valueOf(a2.trim()).doubleValue();
                                double lng = Double.valueOf(b2.trim()).doubleValue();

                                final double EARTH_RADIUS = 6378137.0;
                                double radLat1 = (lat * Math.PI / 180.0);
                                double radLat2 = (latitude * Math.PI / 180.0);
                                double adistance = radLat1 - radLat2;
                                double bdistance = (lng - longitude) * Math.PI / 180.0;
                                double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(adistance / 2), 2)
                                        + Math.cos(radLat1) * Math.cos(radLat2)
                                        * Math.pow(Math.sin(bdistance / 2), 2)));
                                s = s * EARTH_RADIUS / 1000;
                                if (s > selectNum) {
                                    continue;
                                }
                                NAMEList.add(new JSONArray(new JSONObject(mJsonText).getString("value"))
                                        .getJSONObject(i).getString("Name"));
                                PHOTOList.add(new JSONArray(new JSONObject(mJsonText)
                                        .getString("value"))
                                        .getJSONObject(i).getString("Photo1"));
                                ADDRESSList.add(new JSONArray(new JSONObject(mJsonText)
                                        .getString("value"))
                                        .getJSONObject(i).getString("Address"));
                                GYMIDList.add(new JSONArray(new JSONObject(mJsonText)
                                        .getString("value"))
                                        .getJSONObject(i).getString("GymID"));

                                LATList.add(lat);
                                LNGList.add(lng);
                                //LAT[countlatlng] = lat;
                                //LNG[countlatlng] = lng;
                                countlatlng = countlatlng + 1;
                            }

                            nextLink = new JSONObject(mJsonText).getString("@odata" +
                                    ".nextLink");
                        }

                    } while (nextLink != "" && nextLink != null);
                } catch (Exception e) {
                }
            }
        }).start();

    }

    public void setUpMap(double x, double y, Object title, Object snippet) {


        // 刪除原來預設的內容
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));


        // 建立位置的座標物件
        LatLng place = new LatLng(x, y);

        // 加入地圖標記
        addMarker(place, title, snippet);
    }

    public void addMarker(LatLng place, Object title, Object snippet) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place)
                .title((String) title)
                .snippet((String) snippet);

        //mGoogleMap.addMarker(markerOptions);

        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MapsActivity.this);
        mGoogleMap.setInfoWindowAdapter(adapter);
        mGoogleMap.addMarker(markerOptions);


        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //new一個intent物件，並指定Activity切換的class
                String adressPhotoId=marker.getSnippet();
                String[] markerInfos = adressPhotoId.split("@");

                Intent intent = new Intent();
                intent.setClass(MapsActivity.this,DetailActivity.class);
                intent .putExtra("GymId",markerInfos[2]);//可放所有基本類別
                //切換Activity
                startActivity(intent);
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        buildGoogleApiClient();

        mGoogleApiClient.connect();


    }

    @Override
    public void onPause() {
        super.onPause();
        //Unregister for location callbacks:
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocation = mGoogleMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //5 seconds
        mLocationRequest.setFastestInterval(1000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //remove previous current location marker and add new one at current position
        if (mCurrLocation != null) {
            mCurrLocation.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocation = mGoogleMap.addMarker(markerOptions);

        //  Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();
        gps = new GPSTracker(this);
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude +
            //        "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
        }
        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        if(countlatlng>countmarker) {
            for (int i =countmarker; i < countlatlng; i++) {
                photoUrl = (String) PHOTOList.get(i);
                setUpMap((Double) LATList.get(i), (Double) LNGList.get(i),NAMEList.get(i),
                        ADDRESSList.get(i) + "@"+ photoUrl + "@" + GYMIDList.get(i));
                countmarker=countmarker+1;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.user.mainsearch/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.user.mainsearch/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private MapsActivity context;
        boolean not_first_time_showing_info_window;
        public CustomInfoWindowAdapter(MapsActivity context){
            this.context = context;
        }
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
        @Override
        public View getInfoContents(final Marker marker) {
            View view = context.getLayoutInflater().inflate(R.layout.customwindow, null);
            ImageView imageUrl = (ImageView) view.findViewById(R.id.imageUrl);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
            TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);
            String adressPhotoId=marker.getSnippet();
            final String[] markerInfos = adressPhotoId.split("@");

            tvTitle.setText(marker.getTitle());
            tvSubTitle.setText(markerInfos[0]);
            urlChar=markerInfos[1].toCharArray();

            encodeResult="";
            for(int i = 0; i <urlChar.length; i++) {
                temp = String.valueOf(urlChar[i]);
                if( urlChar[i]<33 ||  urlChar[i]>126)
                {
                    try {
                        temp = URLEncoder.encode(String.valueOf(urlChar[i]),"utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(urlChar[i]==32)temp="%20";
                }
                encodeResult=encodeResult + temp;
            }
            if (not_first_time_showing_info_window) {
                Picasso.with(MapsActivity.this).load(encodeResult).into(imageUrl);
            } else { // if it's the first time, load the image with the callback set
                not_first_time_showing_info_window=true;
                Picasso.with(MapsActivity.this).load(encodeResult).into(imageUrl,new InfoWindowRefresher(marker));
            }

            //new ImageDownloader(imageUrl).execute(encodeResult);
            return view;
        }


    }

    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError() {}
    }
    public long create(String gymidtt, String tt, String stt,String LatLng,String PhotoUrl) {
        ContentValues args = new ContentValues();
        args.put("gymID", gymidtt);
        args.put("title", tt);
        args.put("subtitle", stt);
        args.put("LatLng", LatLng);
        args.put("PhotoUrl", PhotoUrl);

        return db.insert("FavoriteListFinal", null, args);
    }

}

