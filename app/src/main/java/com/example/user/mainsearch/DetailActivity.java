package com.example.user.mainsearch;





import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailActivity extends Activity {
    String gymId;
    TextView nameText, addressText, phoneText, desText, parkText;
    ListView lvComment;
    ImageButton btnHome,btnKeyword,btnMap,btnSpinner,btnFavorite,btnEver;
    ArrayList<HashMap<String, String>> comlist = new ArrayList<HashMap<String, String>>();
    private static final String TAG_COM = "data";
    private static final String TAG_GYMID = "gymid";
    private static final String TAG_SCORE = "score";
    private static final String TAG_COMMENT = "comment";
    private static final String TAG_NICKNAME = "nickname";
    public InputStream is = null;
    public JSONObject jObj = null;
    public String json = "";
    private static final String TAG_OS = "value";
    private static final String TAG_count = "Name";
    private static final String TAG_area = "Address";
    private static final String TAG_ID = "GymID";
    private static final String TAG_PHOTO = "Photo1";
    private static final String TAG_DES = "Description";
    private static final String TAG_PHONE = "Phone";
    private static final String TAG_PARK = "Park";
    JSONArray value = null;
    String url;
    String url2;
    ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        photoView = (ImageView) findViewById(R.id.photoView);
        lvComment = (ListView)findViewById(R.id.lvComment);
        btnHome = (ImageButton)findViewById(R.id.btnHome);
        btnKeyword = (ImageButton)findViewById(R.id.btnKeyword);
        btnMap = (ImageButton)findViewById(R.id.btnMap);
        btnSpinner = (ImageButton)findViewById(R.id.btnSpinner);
        btnFavorite = (ImageButton)findViewById(R.id.btnFavorite);
        btnEver = (ImageButton)findViewById(R.id.btnEver);
        Intent intent = this.getIntent();
        gymId = intent.getStringExtra("GymId");
        btnHome.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(DetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnKeyword.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(DetailActivity.this, MainSearch.class);
                startActivity(intent);
                finish();
            }
        });
        btnMap.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(DetailActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSpinner.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(DetailActivity.this, SearchSpinner.class);
                startActivity(intent);
            }
        });
        btnFavorite.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(DetailActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        btnEver.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(DetailActivity.this, EverActivity.class);
                startActivity(intent);
            }
        });
        //Toast.makeText(DetailActivity.this, gymId, Toast.LENGTH_LONG).show();
        url = "http://iplay.sa.gov.tw/odata/Gym(" + gymId + ")?$format=application/json;odata.metadata=none";
        url2 = "http://52.198.27.85/overmove/SelectOne/"+gymId;
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        JSONParse jsonparse = new JSONParse();
        jsonparse.execute();
        JSONParse2 jsonparse2 = new JSONParse2();
        jsonparse2.execute();
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String gymId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nameText = (TextView) findViewById(R.id.nameText);
            addressText = (TextView) findViewById(R.id.addressText);
            phoneText = (TextView) findViewById(R.id.phoneText);
            desText = (TextView) findViewById(R.id.desText);
            parkText = (TextView) findViewById(R.id.parkText);
            pDialog = new ProgressDialog(DetailActivity.this);
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
            ImageSize mImageSize = new ImageSize(500, 500);
            try {
                // Getting JSON Array from URL
                value = json.getJSONArray(TAG_OS);
                for (int i = 0; i < value.length(); i++) {
                    JSONObject c = value.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String ecountiesString = c.getString(TAG_count);
                    String areaString = c.getString(TAG_area);
                    final String MapAddress = areaString;
                    String photo1 = c.getString(TAG_PHOTO);
                    String phone = c.getString(TAG_PHONE);
                    String des = c.getString(TAG_DES);
                    String park = c.getString(TAG_PARK);
                    gymId = c.getString(TAG_ID);
                    nameText.setText(ecountiesString);
                    addressText.setText(areaString);
                    phoneText.setText(phone);
                    desText.setText(des);
                    parkText.setText(park);
                    ImageLoader.getInstance().displayImage(photo1, photoView, mImageSize);
                    addressText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+MapAddress);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    });
                    phoneText.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            String phone = phoneText.getText().toString().replaceAll("-", "");
                            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                                    "tel", phone, null));
                            startActivity(phoneIntent);
                        }
                    });
                    // Adding value HashMap key => value

//                    HashMap<String, String> map = new HashMap<String, String>();
//
//                    map.put(TAG_count, ecountiesString);
//                    map.put(TAG_area, areaString);
//
//                    oslist.add(map);
//                    list=(ListView)findViewById(R.id.listView);
//
//                    ListAdapter adapter = new SimpleAdapter(Main2Activity.this, oslist,
//                            R.layout.list_data,
//                            new String[] {TAG_count,TAG_area}, new int[] {
//                            R.id.vers,R.id.name});
//
//                    list.setAdapter(adapter);
//                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view,
//                                                int position, long id) {
//                            Intent intent =new Intent();
//                            intent.setClass(Main2Activity.this, Main3Activity.class);
//                            intent.putExtra("GymId", gymId);
//                            startActivity(intent);
//                            //finish();
//                            //Toast.makeText(Main2Activity.this, "You Clicked at "+oslist.get(+position).get("ecounties"), Toast.LENGTH_SHORT).show();
//
//                        }
//                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private Drawable loadImageFromURL(String url) {

        InputStream is = null;
        try {
            is = (InputStream) new URL(url).getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable draw = Drawable.createFromStream(is, "src");
        return draw;

    }
    private class JSONParse2 extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String gymId;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DetailActivity.this);
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
                value = json.getJSONArray(TAG_COM);

                //skipValue = new JSONObject(String.valueOf(json)).getString("@odata.nextLink");

                for(int i = 0; i < value.length(); i++){
                    JSONObject c = value.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String GymidString = c.getString(TAG_GYMID);
                    String ScoreString = c.getString(TAG_SCORE);
                    String CommentString = c.getString(TAG_COMMENT);

                    // Adding value HashMap key => value

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_GYMID, GymidString);
                    map.put(TAG_SCORE, ScoreString);
                    map.put(TAG_COMMENT, CommentString);

                    comlist.add(map);

                    //titleA[countNum]=oslist.get(i).get(TAG_count);
                    //countNum=countNum+1;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*ListAdapter adapter = new SimpleAdapter(
                    Main3Activity.this, comlist,
                    R.layout.list_comment, new String[]{TAG_SCORE,TAG_COMMENT,TAG_NICKNAME}, new int[]{R.id
                    .rbComment,
                    R.id.tvComment,R.id.tvNickname});

            lvComment.setAdapter(adapter);*/
            ListAdapter adapter = new SimpleAdapter(
                    DetailActivity.this, comlist,
                    R.layout.list_comment, new String[] { TAG_SCORE, TAG_COMMENT},
                    new int[] { R.id.rbComment, R.id.tvComment});
            ((SimpleAdapter) adapter).setViewBinder(new MyBinder());
            // updating listview
            lvComment.setAdapter(adapter);
        }
    }

    class MyBinder implements SimpleAdapter.ViewBinder {
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if (view.getId() == R.id.rbComment) {
                String stringval = (String) data;
                float ratingValue = Float.parseFloat(stringval);
                RatingBar ratingBar = (RatingBar) view;
                ratingBar.setRating(ratingValue);
                return true;
            }
            return false;
        }
    }

}






