package com.example.user.mainsearch;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//這邊把原本繼承的 Activity 改為 ListActivity
//然後 implements ListView.OnItemClickListener，方便等下捕捉 ListView item 的 click

public class ArenaActivity extends Activity {

    //傳API解JSON
    ImageButton btnHome;
    ImageButton btnKeyword;
    ImageButton btnMap;
    ImageButton btnSpinner;
    ImageButton btnFavorite;
    ImageButton btnEver;
    ListView lvArena;
    String ecounties;
    String area;
    String encodeResult="";
    InputStream is;
    JSONObject jObj;
    String json ;
    InputStream is2 ;
    JSONObject jObj2 ;
    String json2 ;
    char[] urlChar;
    ArrayList<HashMap<String, String>> oslist = new ArrayList();
    private static final String TAG_OS = "value";
    private static final String TAG_COUNT = "Name";
    private static final String TAG_AREA = "Address";
    private static final String TAG_ID = "GymID";
    private static final String TAG_PHOTO = "Photo1";
    private static final String TAG_LATLNG = "LatLng";
    ArrayList<HashMap<String, String>> gymstarlist = new ArrayList();
    private static final String TAG_COM = "data";
    private static final String TAG_SCORE = "AVG(score)";
    JSONArray value = null;
    JSONArray value2 = null;
    String skipValue = null;
    String url;
    String url2;
    String url3;
    //連資料庫
    SQLiteDatabase db= null;
    //SQL語法
    String createTable = "CREATE TABLE if not exists FavoriteListFinal"+"(_id INTEGER PRIMARY " +
            "KEY autoincrement,gymID TEXT,title TEXT,subtitle TEXT,LatLng TEXT,PhotoUrl TEXT)";

    List<Map<String, Object>> list = new ArrayList();
    ArrayList gymIDArr = new ArrayList();
    ArrayList titleArr = new ArrayList();
    ArrayList latLngArr = new ArrayList();
    ArrayList subtitleArr = new ArrayList();
    ArrayList image = new ArrayList();
    ArrayList gymstarArr = new ArrayList();
    com.gc.materialdesign.views.ButtonRectangle btnNextPage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena);
        lvArena = (ListView)findViewById(R.id.lvArena);
        btnHome = (ImageButton)findViewById(R.id.btnHome);
        btnKeyword = (ImageButton)findViewById(R.id.btnKeyword);
        btnMap = (ImageButton)findViewById(R.id.btnMap);
        btnSpinner = (ImageButton)findViewById(R.id.btnSpinner);
        btnFavorite = (ImageButton)findViewById(R.id.btnFavorite);
        btnEver = (ImageButton)findViewById(R.id.btnEver);
        oslist = new ArrayList();
        Intent intent = this.getIntent();
        ecounties = intent.getStringExtra("ecounties");
        area = intent.getStringExtra("area");
        btnHome.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(ArenaActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnKeyword.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(ArenaActivity.this, MainSearch.class);
                startActivity(intent);
                finish();
            }
        });
        btnMap.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(ArenaActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSpinner.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(ArenaActivity.this, SearchSpinner.class);
                startActivity(intent);
            }
        });
        btnFavorite.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(ArenaActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        btnEver.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(ArenaActivity.this, EverActivity.class);
                startActivity(intent);
            }
        });

        url = "http://iplay.sa.gov.tw/odata/GymSearch?$format=application/json;odata.metadata=none&City="+ecounties+"&Country="+area;
        JSONParse jsonparse = new JSONParse();
        jsonparse.execute();

        //建立資料庫，若存在則開啟資料庫
        db = openOrCreateDatabase("database.db",MODE_WORLD_WRITEABLE,null);
        db.execSQL(createTable); //建立資料表
    }

    // ListView 的 item click

    public final class MyView {
        TextView title;
        TextView subtitle;
        com.gc.materialdesign.views.Button bt;
        ImageView iv;
        RatingBar ratingBar;
    }

    // 實作一個 Adapter 繼承 BaseAdapter
    public class MyAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    public MyAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        //回傳這個 List 有幾個 item
        return list.size();

    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyView myviews ;
        myviews = new MyView();
        convertView = inflater.inflate(R.layout.list_data, null);
        myviews.title = (TextView) convertView.findViewById(R.id.textView1);
        myviews.subtitle = (TextView) convertView.findViewById(R.id.textView2);
        myviews.bt = (com.gc.materialdesign.views.Button) convertView.findViewById(R.id.button1);
        myviews.iv = (ImageView) convertView.findViewById(R.id.imageView);
        myviews.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
        convertView.setClickable(true);
        convertView.setFocusable(true);
        urlChar = ((String) list.get(position).get("image")).toCharArray();
        encodeResult="";
        String temp;
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
        myviews.title.setText((String) list.get(position).get("title"));
        myviews.subtitle.setText((String) list.get(position).get("subtitle"));
        String aaa = (String) list.get(position).get("GymStar");
        if (aaa=="null"){
            aaa="0";
        }
        float f = Float.parseFloat(aaa);
        myviews.ratingBar.setRating(f);
        new DownloadImageTask(myviews.iv).execute(encodeResult);

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String gymId = (String) list.get(position).get("GYMID");
                Intent intent =new Intent();
                intent.setClass(ArenaActivity.this, DetailActivity.class);
                intent.putExtra("GymId", gymId);
                startActivity(intent);
            }

        });

        myviews.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tt = titleArr.get(position).toString();
                String stt = subtitleArr.get(position).toString();
                String gymidtt = gymIDArr.get(position).toString();
                String latLng = latLngArr.get(position).toString();
                String photoUrl = image.get(position).toString();
                Toast.makeText(ArenaActivity.this, "已成功加入收藏場館清單", Toast.LENGTH_SHORT).show();
                create(gymidtt,tt,stt,latLng,photoUrl);
                url3 = "http://52.198.27.85/overmove/Hot/"+gymidtt;
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
//API串接的uri路徑 (小黑人目前範例先取用YouTube API)
                            String uri = url3;
                            HttpClient mHttpClient = new DefaultHttpClient();
                            HttpGet mHttpGet = new HttpGet(uri);
                            mHttpClient.execute(mHttpGet);
                        }
                        catch(Exception e)
                        {
                            throw new IllegalArgumentException(e);
                        }
                    }
                }).start();
            }
        });
        return convertView;
    }
}

    public long create(String gymidtt, String tt, String stt,String latLng,String photoUrl) {
        ContentValues args = new ContentValues();
        args.put("gymID", gymidtt);
        args.put("title", tt);
        args.put("subtitle", stt);
        args.put("LatLng", latLng);
        args.put("PhotoUrl", photoUrl);

        return db.insert("FavoriteListFinal", null, args);
    }

    //顯示圖片
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 ;
            try {

                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            bmImage.setImageBitmap(result);
        }
    }
    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String gymId;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ArenaActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line ;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
            return jObj;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                value = json.getJSONArray(TAG_OS);

                try {
                    skipValue = new JSONObject(String.valueOf(json)).getString("@odata.nextLink");
                    btnNextPage = (com.gc.materialdesign.views.ButtonRectangle)findViewById(R.id.btnNextpage);
                    btnNextPage.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            oslist.clear();
                            gymstarlist.clear();
                            gymIDArr.clear();
                            titleArr.clear();
                            subtitleArr.clear();
                            image.clear();
                            latLngArr.clear();
                            gymstarArr.clear();
                            list.clear();
                            url = skipValue;
                            JSONParse jsonparse = new JSONParse();
                            jsonparse.execute();
                        }
                    });

                }
                catch(JSONException e) {
                    throw new IllegalArgumentException(e);
                };

                for(int i = 0; i < value.length(); i++){
                    JSONObject c = value.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String ecountiesString = c.getString(TAG_COUNT);
                    String areaString = c.getString(TAG_AREA);
                    String imageurl = c.getString(TAG_PHOTO);
                    gymId = c.getString(TAG_ID);
                    String latLng = c.getString(TAG_LATLNG);

                    // Adding value HashMap key => value

                    HashMap<String, String> map = new HashMap();

                    map.put(TAG_COUNT, ecountiesString);
                    map.put(TAG_AREA, areaString);
                    map.put(TAG_PHOTO, imageurl);
                    map.put(TAG_ID,gymId);
                    map.put(TAG_LATLNG,latLng);


                    oslist.add(map);

                    gymIDArr.add(oslist.get(i).get(TAG_ID));
                    titleArr.add(oslist.get(i).get(TAG_COUNT));
                    subtitleArr.add(oslist.get(i).get(TAG_AREA));
                    image.add(oslist.get(i).get(TAG_PHOTO));
                    latLngArr.add(oslist.get(i).get(TAG_LATLNG));

                }


            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
            url2 = "http://52.198.27.85/overmove/AvgScore/";
            for(int i = 0; i < gymIDArr.size() ; i++)
            {
               url2= url2 + gymIDArr.get(i)+"&";
            }
            url2=url2.substring(0,url2.length()-1);
            JSONParse2 jsonparse2 = new JSONParse2();
            jsonparse2.execute();
        }
    }
    private class JSONParse2 extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ArenaActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url2);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is2 = httpEntity.getContent();

            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is2, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is2.close();
                json2 = sb.toString();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            try {
                jObj2 = new JSONObject(json2);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
            return jObj2;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                value2 = json.getJSONArray(TAG_COM);

                for(int i = 0; i < value2.length(); i++){
                    JSONObject c = value2.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String starString = c.getString(TAG_SCORE);

                    HashMap<String, String> map = new HashMap();

                    map.put(TAG_SCORE, starString);


                    gymstarlist.add(map);

                    gymstarArr.add(gymstarlist.get(i).get(TAG_SCORE));
                    
                }


            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }

            //將需要的資料塞到 List 裡面

            List<Map<String, Object>> list2 = new ArrayList();
            for(int i=0;i<titleArr.size();i++) {
                HashMap<String,Object> item = new HashMap();
                item.put("GYMID", gymIDArr.get(i));
                item.put("title", titleArr.get(i));
                item.put("subtitle", subtitleArr.get(i));
                item.put("image", image.get(i));
                item.put("GymStar", gymstarArr.get(i));
                list2.add(item);
            }
            MyAdapter adapter = new MyAdapter(ArenaActivity.this);
            lvArena.setAdapter(adapter);
        }
    }
}







