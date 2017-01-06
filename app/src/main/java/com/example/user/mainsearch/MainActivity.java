package com.example.user.mainsearch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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

public class MainActivity extends Activity {

    ImageButton btnHome,btnKeyword,btnMap,btnSpinner,btnFavorite,btnEver;
    ListView lv3;
    public InputStream is = null;
    public JSONObject jObj = null;
    public String json = "";
    ArrayList<HashMap<String, String>> toplist = new ArrayList<HashMap<String, String>>();
    private static final String TAG_top = "data";
    private static final String TAG_gymid = "gymid";
    ArrayList<HashMap<String, String>> infolist = new ArrayList<HashMap<String, String>>();
    private static final String TAG_COM = "value";
    private static final String TAG_Name = "Name";
    private static final String TAG_Address = "Address";
    private static final String TAG_Image = "Photo1";
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    String urlEncode;
    String encodeResult="";
    char[] urlChar;
    JSONArray value = null;
    JSONArray value2 = null;
    JSONArray value3 = null;
    JSONArray value4 = null;
    public String temp;
    String url,url1,url2,url3;
    public ArrayList gymidArr = new ArrayList();
    public ArrayList nameArr = new ArrayList();
    public ArrayList addressArr = new ArrayList();
    public ArrayList imageArr = new ArrayList();
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv3 = (ListView)findViewById(R.id.lvHome) ;

        url = "http://52.198.27.85/overmove/Top";

        JSONParse jsonparse = new JSONParse();
        jsonparse.execute();
        btnHome = (ImageButton)findViewById(R.id.btnHome);
        btnKeyword = (ImageButton)findViewById(R.id.btnKeyword);
        btnMap = (ImageButton)findViewById(R.id.btnMap);
        btnSpinner = (ImageButton)findViewById(R.id.btnSpinner);
        btnFavorite = (ImageButton)findViewById(R.id.btnFavorite);
        btnEver = (ImageButton)findViewById(R.id.btnEver);

        btnHome.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnKeyword.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainActivity.this, MainSearch.class);
                startActivity(intent);
                finish();
            }
        });
        btnMap.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSpinner.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainActivity.this, SearchSpinner.class);
                startActivity(intent);
            }
        });
        btnFavorite.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        btnEver.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainActivity.this, EverActivity.class);
                startActivity(intent);
            }
        });



    }
    private List<Map<String,Object>> getData() {
        List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
        for(int i=0;i<gymidArr.size();i++) {
            HashMap<String,Object> item = new HashMap<String,Object>();
            item.put("nameArr", nameArr.get(i));
            item.put("addressArr", addressArr.get(i));
            item.put("gymidArr", gymidArr.get(i));
            item.put("imageArr", imageArr.get(i));
            list2.add(item);
        }
        return list2;
    }
    /*public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        String gymId = toplist.get(arg2).get(TAG_gymid);
        Intent intent =new Intent();
        intent.setClass(MainActivity.this, DetailActivity.class);
        intent.putExtra("GymId", gymId);
        startActivity(intent);
    }*/

    public final class MyView {
        public TextView tvHomeName;
        public TextView tvHomeAddress;
        public TextView tvHomeID;
        public ImageView iv;
    }
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
            convertView = inflater.inflate(R.layout.list_home, null);
            myviews.tvHomeName = (TextView) convertView.findViewById(R.id.tvHomeName);
            myviews.tvHomeAddress = (TextView) convertView.findViewById(R.id.tvHomeAddress);
            myviews.tvHomeID = (TextView) convertView.findViewById(R.id.tvHomeID);
            myviews.iv = (ImageView) convertView.findViewById(R.id.imageView2);
            convertView.setClickable(true);
            convertView.setFocusable(true);
            //urlEncode = (String) list.get(position).get("image");
            urlChar = ((String) list.get(position).get("imageArr")).toCharArray();
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
            myviews.tvHomeName.setText((String) list.get(position).get("nameArr"));
            myviews.tvHomeAddress.setText((String) list.get(position).get("addressArr"));
            myviews.tvHomeID.setText((String) list.get(position).get("gymidArr"));
            new DownloadImageTask(myviews.iv).execute(encodeResult);
            /*myviews.bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(MainActivity.this, titleArr[position], Toast.LENGTH_SHORT).show();
                    String tt = titleArr.get(position).toString();
                    String stt = subtitleArr.get(position).toString();
                    String gymidtt = GYMIDArr.get(position).toString();
                    Toast.makeText(ArenaActivity.this, "已成功加入我的運動清單", Toast.LENGTH_SHORT).show();
                    create(gymidtt,tt,stt);
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
                    }).tart();

                    Intent intent = new Intent(ArenaActivity.this, FavoriteActivity.class);
                    startActivity(intent);
                }
            });*/
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String gymId = (String) list.get(position).get("gymidArr");
                    Intent intent =new Intent();
                    intent.setClass(MainActivity.this, DetailActivity.class);
                    intent.putExtra("GymId", gymId);
                    startActivity(intent);
                }

            });
            return convertView;
        }
    }
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
    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String gymId;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
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
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jObj;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                value = json.getJSONArray(TAG_top);

                for(int i = 0; i < value.length(); i++){
                    JSONObject c = value.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String gymidString = c.getString(TAG_gymid);
                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_gymid, gymidString);

                    toplist.add(map);

                    //titleA[countNum]=oslist.get(i).get(TAG_count);
                    //countNum=countNum+1;
                    gymidArr.add(toplist.get(i).get(TAG_gymid));

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            url1 = "http://iplay.sa.gov.tw/odata/Gym(" + gymidArr.get(0) + ")?$format=application/json;odata.metadata=none";
            url2 = "http://iplay.sa.gov.tw/odata/Gym(" + gymidArr.get(1) + ")?$format=application/json;odata.metadata=none";
            url3 = "http://iplay.sa.gov.tw/odata/Gym(" + gymidArr.get(2) + ")?$format=application/json;odata.metadata=none";
            new GetJSONTask().execute();

        }
    }
    class GetJSONTask extends AsyncTask<String, Void, JSONObject[]> {


        protected JSONObject[] doInBackground(String... urls) {
            // Creating new JSON Parser
            jsonParser jParser = new jsonParser();

            // Getting JSON from URL
            JSONObject[] jsons = new JSONObject[3];
            jsons[0] = jParser.getJSONFromUrl(url1);
            jsons[1] = jParser.getJSONFromUrl(url2);
            jsons[2] = jParser.getJSONFromUrl(url3);

            return jsons;
        }
        protected void onPostExecute(JSONObject[] jsons) {
            JSONObject json1 = jsons[0];
            JSONObject json2 = jsons[1];
            JSONObject json3 = jsons[2];
            // do you work after this
            try {
                // Getting JSON Array from URL
                value2 = json1.getJSONArray(TAG_COM);

                for(int i = 0; i < value2.length(); i++){
                    JSONObject c = value2.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String nameString = c.getString(TAG_Name);
                    String addressString = c.getString(TAG_Address);
                    String imageString = c.getString(TAG_Image);

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_Name, nameString);
                    map.put(TAG_Address, addressString);
                    map.put(TAG_Image, imageString);
                    infolist.add(map);

                    nameArr.add(infolist.get(i).get(TAG_Name));
                    addressArr.add(infolist.get(i).get(TAG_Address));
                    imageArr.add(infolist.get(i).get(TAG_Image));
                    infolist.clear();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                // Getting JSON Array from URL
                value3 = json2.getJSONArray(TAG_COM);

                for(int i = 0; i < value3.length(); i++){
                    JSONObject c = value3.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String nameString = c.getString(TAG_Name);
                    String addressString = c.getString(TAG_Address);
                    String imageString = c.getString(TAG_Image);

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_Name, nameString);
                    map.put(TAG_Address, addressString);
                    map.put(TAG_Image, imageString);
                    infolist.add(map);

                    nameArr.add(infolist.get(i).get(TAG_Name));
                    addressArr.add(infolist.get(i).get(TAG_Address));
                    imageArr.add(infolist.get(i).get(TAG_Image));
                    infolist.clear();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                // Getting JSON Array from URL
                value4 = json3.getJSONArray(TAG_COM);

                for(int i = 0; i < value4.length(); i++){
                    JSONObject c = value4.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String nameString = c.getString(TAG_Name);
                    String addressString = c.getString(TAG_Address);
                    String imageString = c.getString(TAG_Image);

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_Name, nameString);
                    map.put(TAG_Address, addressString);
                    map.put(TAG_Image, imageString);
                    infolist.add(map);

                    nameArr.add(infolist.get(i).get(TAG_Name));
                    addressArr.add(infolist.get(i).get(TAG_Address));
                    imageArr.add(infolist.get(i).get(TAG_Image));
                    infolist.clear();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            list = getData();
            MyAdapter adapter = new MyAdapter(MainActivity.this);
            lv3.setAdapter(adapter);

        }
    }



}
