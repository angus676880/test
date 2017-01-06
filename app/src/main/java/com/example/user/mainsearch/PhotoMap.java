package com.example.user.mainsearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class PhotoMap extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback {

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    ImageButton btnHome,btnKeyword,btnMap,btnSpinner,btnFavorite,btnEver;
    public double LAT[]= new double[10000] ;
    public  ArrayList NAMEList = new ArrayList();
    public  ArrayList ADDRESSList = new ArrayList();
    public  ArrayList uriList = new ArrayList();
    public  ArrayList titleList = new ArrayList();
    public  ArrayList latList = new ArrayList();
    public  ArrayList lngList = new ArrayList();

    public double LNG[]= new double[10000] ;
    public String NAME[]= new String[10000];
    public String ADDRESS[]= new String[10000];
    private Spinner spinner;
    public String nextLink;
    public int countlatlng=0,runTime=0,countmarker=0;
    public boolean checkApi=false,flag=true;
    private GoogleMap mMap;
    private ArrayAdapter<String> lunchList;
    private String[] lunch = {"5", "10", "20", "30"};
    ProgressDialog mProgressDialog;
    SQLiteDatabase db= null;
    LatLng latLng;
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
        setContentView(R.layout.activity_photo_map);
        btnHome = (ImageButton)findViewById(R.id.btnHome);
        btnKeyword = (ImageButton)findViewById(R.id.btnKeyword);
        btnMap = (ImageButton)findViewById(R.id.btnMap);
        btnSpinner = (ImageButton)findViewById(R.id.btnSpinner);
        btnFavorite = (ImageButton)findViewById(R.id.btnFavorite);
        btnEver = (ImageButton)findViewById(R.id.btnEver);
        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);
        btnHome.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(PhotoMap.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnKeyword.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(PhotoMap.this, MainSearch.class);
                startActivity(intent);
                finish();
            }
        });
        btnMap.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(PhotoMap.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSpinner.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(PhotoMap.this, SearchSpinner.class);
                startActivity(intent);
            }
        });
        btnFavorite.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(PhotoMap.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        btnEver.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(PhotoMap.this, EverActivity.class);
                startActivity(intent);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        db = openOrCreateDatabase("database.db",MODE_WORLD_WRITEABLE,null);

    }

    public void GetApi(View view){
        callMap(5);
    }

    public void callMap(final double selectNum){
    }

    public void setUpMap(double x, double y, Object title, Object snippet) throws IOException {

        LatLng place = new LatLng(23.07973176, 120.34423828);
        // 刪除原來預設的內容
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        for(int i = 0;i<uriList.size();i++) {
            // 建立位置的座標物件

                new DownloadImage().execute((String) uriList.get(i), (String)titleList.get
                        (i),(String)latList.get(i),(String)lngList.get(i));
            // 加入地圖標記
            //addMarker(place, title, snippet, (String) uriList.get(0));
        }
    }

    public void addMarker(LatLng place, Object title, Object snippet,String uri) throws
            IOException {

        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.connect();
        InputStream is = conn.getInputStream();
        Bitmap bmImg = BitmapFactory.decodeStream(is);
        // TODO Auto-generated method stub


        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
        Canvas canvas1 = new Canvas(bmp);

        // paint defines the text color, stroke width and size
        Paint color = new Paint();
        color.setTextSize(35);
        color.setColor(Color.BLACK);

        // modify canvas
        canvas1.drawBitmap(bmImg, 0,0, color);
        canvas1.drawText("User Name!", 30, 40, color);

        // add marker to Map
        mMap.addMarker(new MarkerOptions().position(place)
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                // Specifies the anchor to be at a particular point in the marker image.
                .anchor(0.5f, 1));
/*
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place)
                .title((String) title)
                .snippet((String) snippet);

        mGoogleMap.addMarker(markerOptions);*/
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
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Cursor cursor = getAll();
        try {
            setUpMap(0, 0, 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Cursor getAll(){
        Cursor cursor = db.rawQuery("SELECT Name, PhotoUrl,LatLng FROM EverListFinal",null);
        TextView textview = (TextView) findViewById(R.id.textView14) ;
        for(int i = 0 ; i < cursor.getCount() ; i++ )
        {
            //利用for迴圈切換指標位置
            cursor.moveToPosition(i);
            //每筆姓名、年齡、性別、電話、地址資訊
            char[] urlChar=cursor.getString(cursor.getColumnIndex("PhotoUrl")).toCharArray();

            String encodeResult="";
            for(int j = 0; j <urlChar.length; j++) {
                String temp = String.valueOf(urlChar[j]);
                if( urlChar[j]<33 ||  urlChar[j]>126)
                {
                    try {
                        temp = URLEncoder.encode(String.valueOf(urlChar[j]),"utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(urlChar[j]==32)temp="%20";
                }
                encodeResult=encodeResult + temp;
            }

            uriList.add(encodeResult);
            titleList.add(cursor.getString(cursor.getColumnIndex("Name")));
            String latlngString=cursor.getString(cursor.getColumnIndex("LatLng"));

            String[] LatLng2 = latlngString.split(" ");
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
            latList.add(a2);
            lngList.add(b2);
        }
        return cursor;
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



    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        public String photoImageURL,photoTitle;
        public double photoLat,photoLng;
        @Override
        protected void onPreExecute() {
            /*super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(PhotoMap.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Download Image ");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();*/
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            photoImageURL = URL[0];
            photoTitle = URL[1];
            photoLat = Double.parseDouble(URL[2]);
            photoLng = Double.parseDouble(URL[3]);

            Bitmap bmImg = null;
            try {
                // Download Image from URL
                InputStream input = new URL(photoImageURL).openStream();
                // Decode Bitmap
                bmImg = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return bmImg;
    }

        @Override
        protected void onPostExecute(Bitmap result) {

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bmp = Bitmap.createBitmap(result.getWidth(), result.getHeight(), conf);
            Canvas canvas1 = new Canvas(bmp);



            // paint defines the text color, stroke width and size
            Paint color = new Paint();
            color.setTextSize(35);
            color.setColor(Color.BLUE);

            canvas1.drawBitmap(result,0,0, color);

            LatLng place = new LatLng(photoLat, photoLng);
            // add marker to Map

            mGoogleMap.addMarker(new MarkerOptions().position(place)
                    .icon(BitmapDescriptorFactory.fromBitmap(zoomImage(bmp,160,160)))
                    // Specifies the anchor to be at a particular point in the marker image.
                    .anchor(0.5f, 1)
                    .title(photoTitle));

            // Close progressdialog
            //ProgressDialog.dismiss();
        }
    }
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}
