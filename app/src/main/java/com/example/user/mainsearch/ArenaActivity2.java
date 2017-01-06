package com.example.user.mainsearch;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Timer;
import java.util.TimerTask;

//這邊把原本繼承的 Activity 改為 ListActivity
//然後 implements ListView.OnItemClickListener，方便等下捕捉 ListView item 的 click

public class ArenaActivity2 extends Activity {

    //傳API解JSON
    ImageButton btnHome,btnKeyword,btnMap,btnSpinner,btnFavorite,btnEver;
    ListView lvArena2;
    String urlEncode;
    String encodeResult="";
    String GYMID;
    int CountCode=0,CountSec=0;
    public InputStream is = null;
    public JSONObject jObj = null;
    public String json = "";
    public InputStream is2 = null;
    public JSONObject jObj2 = null;
    public String json2 = "";
    char[] urlChar;
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    private static final String TAG_OS = "value";
    private static final String TAG_Skip = "@odata.nextLink";
    private static final String TAG_count = "Name";
    private static final String TAG_area = "Address";
    private static final String TAG_ID = "GymID";
    private static final String TAG_PHOTO = "Photo1";
    private static final String TAG_LatLng = "LatLng";
    ArrayList<HashMap<String, String>> gymstarlist = new ArrayList<HashMap<String, String>>();
    private static final String TAG_COM = "data";
    private static final String TAG_SCORE = "AVG(score)";
    JSONArray value = null;
    JSONArray value2 = null;
    String skipValue = null;
    String url,url2,url3;
    //連資料庫
    SQLiteDatabase db= null;
    Cursor cursor;  //和TABLE溝通
    //SQL語法
    String CREATE_TABLE = "CREATE TABLE if not exists FavoriteListFinal"+"(_id INTEGER PRIMARY " +
            "KEY autoincrement,gymID TEXT,title TEXT,subtitle TEXT,LatLng TEXT,PhotoUrl TEXT)";
    private Bitmap bitmap;
    public String temp;
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    List<Map<String, Object>> starlist = new ArrayList<Map<String, Object>>();
    public ArrayList intentArr = new ArrayList();
    public ArrayList urlArr = new ArrayList();
    public ArrayList GYMIDArr = new ArrayList();
    public ArrayList titleArr = new ArrayList();
    public ArrayList LatLngArr = new ArrayList();
    public String titleA[]= new String[100];
    public int countNum = 0;
    public ArrayList subtitleArr = new ArrayList();
    public ArrayList image = new ArrayList();
    public ArrayList gymstarArr = new ArrayList();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena2);
        lvArena2 = (ListView)findViewById(R.id.lvArena2);
        btnHome = (ImageButton)findViewById(R.id.btnHome);
        btnKeyword = (ImageButton)findViewById(R.id.btnKeyword);
        btnMap = (ImageButton)findViewById(R.id.btnMap);
        btnSpinner = (ImageButton)findViewById(R.id.btnSpinner);
        btnFavorite = (ImageButton)findViewById(R.id.btnFavorite);
        btnEver = (ImageButton)findViewById(R.id.btnEver);
        oslist = new ArrayList<HashMap<String, String>>();
        Intent intent = this.getIntent();
        intentArr = (ArrayList) getIntent().getSerializableExtra("nameArr");
        btnHome.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(ArenaActivity2.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnKeyword.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(ArenaActivity2.this, MainSearch.class);
                startActivity(intent);
                finish();
            }
        });
        btnMap.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(ArenaActivity2.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSpinner.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(ArenaActivity2.this, SearchSpinner.class);
                startActivity(intent);
            }
        });
        btnFavorite.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(ArenaActivity2.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        btnEver.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(ArenaActivity2.this, EverActivity.class);
                startActivity(intent);
            }
        });

        for(int i=0;i<intentArr.size();i++){
            url = "http://iplay.sa.gov.tw/odata/GymSearch?$format=application/json;odata.metadata=none&Name="+intentArr.get(i);
            urlArr.add(url);
        }

        new GetJSONTask().execute();



        //建立資料庫，若存在則開啟資料庫
        db = openOrCreateDatabase("database.db",MODE_WORLD_WRITEABLE,null);
        db.execSQL(CREATE_TABLE); //建立資料表

        //db.execSQL("INSERT INTO table02 (loc,date,time,oth) values ('台北','2016/7/23','1:41 AM','喔噎')");  //新增資料

    }

    //將需要的資料塞到 List 裡面


    private List<Map<String,Object>> getData2() {
        List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
        for(int i=0;i<gymstarArr.size();i++) {
            HashMap<String,Object> item = new HashMap<String,Object>();
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
    /*public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        String gymId = oslist.get(arg2).get(TAG_ID);
        Intent intent =new Intent();
        intent.setClass(ArenaActivity2.this, DetailActivity.class);
        intent.putExtra("GymId", gymId);
        startActivity(intent);
    }*/

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
            MyView myviews = null;
            myviews = new MyView();
            convertView = inflater.inflate(R.layout.list_data, null);
            myviews.title = (TextView) convertView.findViewById(R.id.textView1);
            myviews.subtitle = (TextView) convertView.findViewById(R.id.textView2);
            myviews.bt = (com.gc.materialdesign.views.Button) convertView.findViewById(R.id.button1);
            myviews.iv = (ImageView) convertView.findViewById(R.id.imageView);
            myviews.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            convertView.setClickable(true);
            convertView.setFocusable(true);
            //urlEncode = (String) list.get(position).get("image");
            urlChar = ((String) list.get(position).get("image")).toCharArray();
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
                    intent.setClass(ArenaActivity2.this, DetailActivity.class);
                    intent.putExtra("GymId", gymId);
                    startActivity(intent);
                }

            });

            myviews.bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(MainActivity.this, titleArr[position], Toast.LENGTH_SHORT).show();
                    String tt = titleArr.get(position).toString();
                    String stt = subtitleArr.get(position).toString();
                    String gymidtt = GYMIDArr.get(position).toString();
                    String LatLng = LatLngArr.get(position).toString();
                    String PhotoUrl = image.get(position).toString();
                    Toast.makeText(ArenaActivity2.this, "已成功加入收藏場館清單", Toast.LENGTH_SHORT).show();
                    create(gymidtt,tt,stt,LatLng,PhotoUrl);
                    url3 = "http://52.198.27.85/overmove/Hot/"+gymidtt;
                    new Thread(new Runnable()
                    {
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

    /*public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/

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
            Bitmap mIcon11 = null;
            try {

                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            bmImage.setImageBitmap(result);
        }
    }
    class GetJSONTask extends AsyncTask<String, Void, JSONObject[]> {
        private ProgressDialog mProgressDialog;
        protected void onPreExecute() {

            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ArenaActivity2.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Loading");
            // Set progressdialog message
            mProgressDialog.setMessage("讀取場館中...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        protected JSONObject[] doInBackground(String... urls) {
            // Creating new JSON Parser
            jsonParser jParser = new jsonParser();

            // Getting JSON from URL
            JSONObject[] jsons = new JSONObject[intentArr.size()];
            for(int i=0;i<intentArr.size();i++){
                jsons[i] = jParser.getJSONFromUrl(urlArr.get(i).toString());
            }

            return jsons;
        }
        protected void onPostExecute(JSONObject[] jsons) {
            mProgressDialog.dismiss();
            JSONObject[] json1 = new JSONObject[intentArr.size()];
            for(int j=0;j<intentArr.size();j++) {
                json1[j] = jsons[j];
                // do you work after this
                    try {
                        // Getting JSON Array from URL
                        value = json1[j].getJSONArray(TAG_OS);


                            JSONObject c = value.getJSONObject(0);
                            // Storing  JSON item in a Variable
                            String nameString = c.getString(TAG_count);
                            String addressString = c.getString(TAG_area);
                            String imageString = c.getString(TAG_PHOTO);
                            String gymidString = c.getString(TAG_ID);
                            String LatLngString = c.getString(TAG_LatLng);

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put(TAG_count, nameString);
                            map.put(TAG_area, addressString);
                            map.put(TAG_PHOTO, imageString);
                            map.put(TAG_ID, gymidString);
                            map.put(TAG_LatLng, LatLngString);

                            oslist.add(map);

                            titleArr.add(oslist.get(0).get(TAG_count));
                            subtitleArr.add(oslist.get(0).get(TAG_area));
                            image.add(oslist.get(0).get(TAG_PHOTO));
                            GYMIDArr.add(oslist.get(0).get(TAG_ID));
                            LatLngArr.add(oslist.get(0).get(TAG_LatLng));

                            oslist.clear();



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }
            url2 = "http://52.198.27.85/overmove/AvgScore/";
            for(int i = 0; i < GYMIDArr.size() ; i++)
            {
                url2= url2 + GYMIDArr.get(i)+"&";
            }
            url2=url2.substring(0,url2.length()-1);
            JSONParse2 jsonparse2 = new JSONParse2();
            jsonparse2.execute();
            counttimer();

        }
    }
    private class JSONParse2 extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String gymId;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ArenaActivity2.this);
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
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is2, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is2.close();
                json2 = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            try {
                jObj2 = new JSONObject(json2);
            } catch (JSONException e) {
                e.printStackTrace();
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

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_SCORE, starString);


                    gymstarlist.add(map);

                    gymstarArr.add(gymstarlist.get(i).get(TAG_SCORE));

                    CountCode = CountCode+1;

                    if(CountCode>CountSec) {
                        list = getData2();
                        MyAdapter adapter = new MyAdapter(ArenaActivity2.this);
                        lvArena2.setAdapter(adapter);
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public void counttimer () {
        Timer T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.i("tag", "A Kiss every 1 seconds");
                        CountSec = CountSec+1;
                    }
                });
            }
        }, 1000, 1000);

    }
}







