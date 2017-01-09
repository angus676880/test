package com.example.user.mainsearch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    ImageButton btnHome;
    ImageButton btnKeyword;
    ImageButton btnMap;
    ImageButton btnSpinner;
    ImageButton btnFavorite;
    ImageButton btnEver;
    ListView lv3;
    InputStream is = null;
    JSONObject jObj = null;
    String json = "";
    ArrayList<HashMap<String, String>> toplist = new ArrayList();
    private static final String TAG_TOP = "data";
    private static final String TAG_GYMID = "gymid";
    ArrayList<HashMap<String, String>> infolist = new ArrayList();
    private static final String TAG_COM = "value";
    private static final String TAG_NAME = "Name";
    private static final String TAG_ADDRESS = "Address";
    private static final String TAG_IMAGE = "Photo1";
    List<Map<String, Object>> list = new ArrayList();
    String encodeResult="";
    char[] urlChar;
    JSONArray value = null;
    JSONArray value2 = null;
    JSONArray value3 = null;
    JSONArray value4 = null;
    String temp;
    String url;
    String url1;
    String url2;
    String url3;
    ArrayList gymidArr = new ArrayList();
    ArrayList nameArr = new ArrayList();
    ArrayList addressArr = new ArrayList();
    ArrayList imageArr = new ArrayList();
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
    public final class MyView {
        TextView tvHomeName;
        TextView tvHomeAddress;
        TextView tvHomeID;
        ImageView iv;
    }
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
            convertView = inflater.inflate(R.layout.list_home, null);
            myviews.tvHomeName = (TextView) convertView.findViewById(R.id.tvHomeName);
            myviews.tvHomeAddress = (TextView) convertView.findViewById(R.id.tvHomeAddress);
            myviews.tvHomeID = (TextView) convertView.findViewById(R.id.tvHomeID);
            myviews.iv = (ImageView) convertView.findViewById(R.id.imageView2);
            convertView.setClickable(true);
            convertView.setFocusable(true);
            urlChar = ((String) list.get(position).get("imageArr")).toCharArray();
            encodeResult="";
            for(int i = 0; i <urlChar.length; i++) {
                temp = String.valueOf(urlChar[i]);
                if( urlChar[i]<33 ||  urlChar[i]>126)
                {
                    try {
                        temp = URLEncoder.encode(String.valueOf(urlChar[i]),"utf-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new IllegalArgumentException(e);
                    }
                    if(urlChar[i]==32)temp="%20";
                }
                encodeResult=encodeResult + temp;
            }
            myviews.tvHomeName.setText((String) list.get(position).get("nameArr"));
            myviews.tvHomeAddress.setText((String) list.get(position).get("addressArr"));
            myviews.tvHomeID.setText((String) list.get(position).get("gymidArr"));
            new DownloadImageTask(myviews.iv).execute(encodeResult);
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
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
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
                value = json.getJSONArray(TAG_TOP);

                for(int i = 0; i < value.length(); i++){
                    JSONObject c = value.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String gymidString = c.getString(TAG_GYMID);
                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap();

                    map.put(TAG_GYMID, gymidString);

                    toplist.add(map);

                    gymidArr.add(toplist.get(i).get(TAG_GYMID));
                }
            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
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
                    String nameString = c.getString(TAG_NAME);
                    String addressString = c.getString(TAG_ADDRESS);
                    String imageString = c.getString(TAG_IMAGE);

                    HashMap<String, String> map = new HashMap();

                    map.put(TAG_NAME, nameString);
                    map.put(TAG_ADDRESS, addressString);
                    map.put(TAG_IMAGE, imageString);
                    infolist.add(map);

                    nameArr.add(infolist.get(i).get(TAG_NAME));
                    addressArr.add(infolist.get(i).get(TAG_ADDRESS));
                    imageArr.add(infolist.get(i).get(TAG_IMAGE));
                    infolist.clear();
                }


            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
            try {
                // Getting JSON Array from URL
                value3 = json2.getJSONArray(TAG_COM);

                for(int i = 0; i < value3.length(); i++){
                    JSONObject c = value3.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String nameString = c.getString(TAG_NAME);
                    String addressString = c.getString(TAG_ADDRESS);
                    String imageString = c.getString(TAG_IMAGE);

                    HashMap<String, String> map = new HashMap();

                    map.put(TAG_NAME, nameString);
                    map.put(TAG_ADDRESS, addressString);
                    map.put(TAG_IMAGE, imageString);
                    infolist.add(map);

                    nameArr.add(infolist.get(i).get(TAG_NAME));
                    addressArr.add(infolist.get(i).get(TAG_ADDRESS));
                    imageArr.add(infolist.get(i).get(TAG_IMAGE));
                    infolist.clear();
                }


            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
            try {
                // Getting JSON Array from URL
                value4 = json3.getJSONArray(TAG_COM);

                for(int i = 0; i < value4.length(); i++){
                    JSONObject c = value4.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String nameString = c.getString(TAG_NAME);
                    String addressString = c.getString(TAG_ADDRESS);
                    String imageString = c.getString(TAG_IMAGE);

                    HashMap<String, String> map = new HashMap();

                    map.put(TAG_NAME, nameString);
                    map.put(TAG_ADDRESS, addressString);
                    map.put(TAG_IMAGE, imageString);
                    infolist.add(map);

                    nameArr.add(infolist.get(i).get(TAG_NAME));
                    addressArr.add(infolist.get(i).get(TAG_ADDRESS));
                    imageArr.add(infolist.get(i).get(TAG_IMAGE));
                    infolist.clear();
                }


            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }

            List<Map<String, Object>> list2 = new ArrayList();
            for(int i=0;i<gymidArr.size();i++) {
                HashMap<String,Object> item = new HashMap();
                item.put("nameArr", nameArr.get(i));
                item.put("addressArr", addressArr.get(i));
                item.put("gymidArr", gymidArr.get(i));
                item.put("imageArr", imageArr.get(i));
                list2.add(item);
            }

            MyAdapter adapter = new MyAdapter(MainActivity.this);
            lv3.setAdapter(adapter);

        }
    }
}
