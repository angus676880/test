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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
    public InputStream is;
    public JSONObject jObj;
    public String json ;
    public InputStream is2 ;
    public JSONObject jObj2 ;
    public String json2 ;
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
    String CREATE_TABLE = "CREATE TABLE if not exists FavoriteListFinal"+"(_id INTEGER PRIMARY " +
            "KEY autoincrement,gymID TEXT,title TEXT,subtitle TEXT,LatLng TEXT,PhotoUrl TEXT)";

    List<Map<String, Object>> list = new ArrayList();
    public ArrayList GYMIDArr = new ArrayList();
    public ArrayList titleArr = new ArrayList();
    public ArrayList LatLngArr = new ArrayList();
    public ArrayList subtitleArr = new ArrayList();
    public ArrayList image = new ArrayList();
    public ArrayList gymstarArr = new ArrayList();
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
        db.execSQL(CREATE_TABLE); //建立資料表
    }

    //將需要的資料塞到 List 裡面

    private List<Map<String,Object>> getData2() {
        List<Map<String, Object>> list2 = new ArrayList();
        for(int i=0;i<titleArr.size();i++) {
            HashMap<String,Object> item = new HashMap();
            item.put("GYMID", GYMIDArr.get(i));
            item.put("title", titleArr.get(i));
            item.put("subtitle", subtitleArr.get(i));
            item.put("image", image.get(i));
            item.put("GymStar", gymstarArr.get(i));
            list2.add(item);
        }
        return list2;
    }

    // ListView 的 item click

    public final class MyView {
        public TextView title;
        public TextView subtitle;
        public com.gc.materialdesign.views.Button bt;
        public ImageView iv;
        public RatingBar ratingBar;
    }

    // 實作一個 Adapter 繼承 BaseAdapter
    public class MyAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    public MyAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub

        //回傳這個 List 有幾個 item
        return list.size();

    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
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
                    throw new RuntimeException(e);
                }
                if(urlChar[i]==32)temp="%20";
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
                String gymidtt = GYMIDArr.get(position).toString();
                String LatLng = LatLngArr.get(position).toString();
                String PhotoUrl = image.get(position).toString();
                Toast.makeText(ArenaActivity.this, "已成功加入收藏場館清單", Toast.LENGTH_SHORT).show();
                create(gymidtt,tt,stt,LatLng,PhotoUrl);
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
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        });
        return convertView;
    }
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

    //顯示圖片
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 ;
            try {

                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                throw new RuntimeException(e);
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

            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
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
                throw new RuntimeException(e);
            }
            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                throw new RuntimeException(e);
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
                            GYMIDArr.clear();
                            titleArr.clear();
                            subtitleArr.clear();
                            image.clear();
                            LatLngArr.clear();
                            gymstarArr.clear();
                            list.clear();
                            url = skipValue;
                            JSONParse jsonparse = new JSONParse();
                            jsonparse.execute();
                        }
                    });

                }
                catch(JSONException e) {
                    throw new RuntimeException(e);
                };

                for(int i = 0; i < value.length(); i++){
                    JSONObject c = value.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String ecountiesString = c.getString(TAG_COUNT);
                    String areaString = c.getString(TAG_AREA);
                    String imageurl = c.getString(TAG_PHOTO);
                    gymId = c.getString(TAG_ID);
                    String LatLng = c.getString(TAG_LATLNG);

                    // Adding value HashMap key => value

                    HashMap<String, String> map = new HashMap();

                    map.put(TAG_COUNT, ecountiesString);
                    map.put(TAG_AREA, areaString);
                    map.put(TAG_PHOTO, imageurl);
                    map.put(TAG_ID,gymId);
                    map.put(TAG_LATLNG,LatLng);


                    oslist.add(map);

                    GYMIDArr.add(oslist.get(i).get(TAG_ID));
                    titleArr.add(oslist.get(i).get(TAG_COUNT));
                    subtitleArr.add(oslist.get(i).get(TAG_AREA));
                    image.add(oslist.get(i).get(TAG_PHOTO));
                    LatLngArr.add(oslist.get(i).get(TAG_LATLNG));

                }


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            url2 = "http://52.198.27.85/overmove/AvgScore/";
            for(int i = 0; i < GYMIDArr.size() ; i++)
            {
               url2= url2 + GYMIDArr.get(i)+"&";
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

            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
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
                throw new RuntimeException(e);
            }
            try {
                jObj2 = new JSONObject(json2);
            } catch (JSONException e) {
                throw new RuntimeException(e);
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
                throw new RuntimeException(e);
            }
            list = getData2();
            MyAdapter adapter = new MyAdapter(ArenaActivity.this);
            lvArena.setAdapter(adapter);
        }
    }
}







