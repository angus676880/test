
package com.example.user.mainsearch;

import android.content.Intent;
import android.content.pm.PackageManager;
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
    ImageButton btnHome;
    ImageButton btnKeyword;
    ImageButton btnMap;
    ImageButton btnSpinner;
    ImageButton btnFavorite;
    ImageButton btnEver;
    ArrayList latList = new ArrayList();
    ArrayList lngList = new ArrayList();
    ArrayList nameList = new ArrayList();
    ArrayList addressList = new ArrayList();
    ArrayList photoList = new ArrayList();
    ArrayList gymIDList = new ArrayList();
    private Spinner spinner;
    String nextLink;
    int countlatlng=0;
    int countmarker=0;
    boolean flag=true;
    private ArrayAdapter<String> lunchList;
    String[] lunch = {"5", "10", "20", "30"};
    int selectNum;
    int oldnum=0;
    String photoUrl="";
    SQLiteDatabase db= null;
    //SQL語法
    String createTable = "CREATE TABLE if not exists FavoriteListFinal"+"(_id INTEGER PRIMARY " +
            "KEY autoincrement,gymID TEXT,title TEXT,subtitle TEXT,LatLng TEXT,PhotoUrl TEXT)";

    LatLng latLng;
    char[] urlChar;
    String temp;
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
        db.execSQL(createTable); //建立資料表
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        spinner = (Spinner)findViewById(R.id.mySpinner);
        lunchList = new ArrayAdapter(MapsActivity.this, android.R.layout.simple_spinner_item,
                lunch);
        spinner.setAdapter(lunchList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                selectNum = Integer.parseInt(lunch[position].trim());

                flag=false;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //??????
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                latList.clear();
                lngList.clear();
                nameList.clear();
                addressList.clear();
                countlatlng = 0;
                countmarker = 0;
                nextLink="";
                final double latitude = gps.getLatitude();
                final double longitude = gps.getLongitude();
                try {
                    //API串接的uri路徑

                    do {
                        if(flag==false){
                            throw new IllegalArgumentException("GO CATCH");
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

                            for (int i = 0; i < 10; i++) {

                                String latLng = new JSONArray(new JSONObject(mJsonText).getString
                                        ("value"))
                                        .getJSONObject(i).getString("LatLng");

                                String[] latLng2 = latLng.split(" ");
                                char[] a = latLng2[0].toCharArray();
                                char[] b = latLng2[1].toCharArray();
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
                                double lat = Double.parseDouble(a2.trim());
                                double lng = Double.parseDouble(b2.trim());

                                final double earthRadius = 6378137.0;
                                double radLat1 = lat * Math.PI / 180.0;
                                double radLat2 = latitude * Math.PI / 180.0;
                                double adistance = radLat1 - radLat2;
                                double bdistance = (lng - longitude) * Math.PI / 180.0;
                                double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(adistance / 2), 2)
                                        + Math.cos(radLat1) * Math.cos(radLat2)
                                        * Math.pow(Math.sin(bdistance / 2), 2)));
                                s = s * earthRadius / 1000;
                                if (s > selectNum) {
                                    continue;
                                }
                                nameList.add(new JSONArray(new JSONObject(mJsonText).getString("value"))
                                        .getJSONObject(i).getString("Name"));
                                photoList.add(new JSONArray(new JSONObject(mJsonText)
                                        .getString("value"))
                                        .getJSONObject(i).getString("Photo1"));
                                addressList.add(new JSONArray(new JSONObject(mJsonText)
                                        .getString("value"))
                                        .getJSONObject(i).getString("Address"));
                                gymIDList.add(new JSONArray(new JSONObject(mJsonText)
                                        .getString("value"))
                                        .getJSONObject(i).getString("GymID"));

                                latList.add(lat);
                                lngList.add(lng);
                                countlatlng = countlatlng + 1;
                            }

                            nextLink = new JSONObject(mJsonText).getString("@odata" +
                                    ".nextLink");
                        }

                    } while (nextLink != "" && nextLink != null);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }).start();

    }

    public void setUpMap(double x, double y, Object title, Object snippet) {

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

        gps = new GPSTracker(this);

        if(countlatlng>countmarker) {
            for (int i =countmarker; i < countlatlng; i++) {
                photoUrl = (String) photoList.get(i);
                setUpMap((Double) latList.get(i), (Double) lngList.get(i),nameList.get(i),
                        addressList.get(i) + "@"+ photoUrl + "@" + gymIDList.get(i));
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
                Action.TYPE_VIEW,
                "Maps Page",
                Uri.parse("http://host/path"),
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
                Action.TYPE_VIEW,
                "Maps Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.example.user.mainsearch/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private MapsActivity context;
        boolean notFirstTimeShowingInfoWindow;
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
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }
                    if(urlChar[i]==32)
                    {
                        temp="%20";
                    }
                }
                encodeResult=encodeResult + temp;
            }
            if (notFirstTimeShowingInfoWindow) {
                Picasso.with(MapsActivity.this).load(encodeResult).into(imageUrl);
            } else { // if it's the first time, load the image with the callback set
                notFirstTimeShowingInfoWindow=true;
                Picasso.with(MapsActivity.this).load(encodeResult).into(imageUrl,new InfoWindowRefresher(marker));
            }
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
        public void onError() {
            //???
        }
    }
}

