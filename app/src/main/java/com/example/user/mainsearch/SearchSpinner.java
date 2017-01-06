package com.example.user.mainsearch;





import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchSpinner extends Activity {
    ImageButton btnHome,btnKeyword,btnMap,btnSpinner,btnFavorite,btnEver;
    com.gc.materialdesign.views.Button searchButton;
    ArrayList<String> listItems=new ArrayList<>();
    ArrayList<String> listItems2=new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;
    Spinner sp,sp2;
    HttpURLConnection urlConnection = null;
    HttpURLConnection urlConnection2 = null;
    String ecounties;
    BufferedReader in = null;
    String aa;
    String area;
    String firstArea;
    boolean noarea = true;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_spinner);
        sp=(Spinner)findViewById(R.id.spinner);
        sp2=(Spinner)findViewById(R.id.spinner2);
        adapter=new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.txt,listItems);
        adapter2=new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.txt,listItems2);
        searchButton= (com.gc.materialdesign.views.Button) findViewById(R.id.searchButton);
        btnHome = (ImageButton)findViewById(R.id.btnHome);
        btnKeyword = (ImageButton)findViewById(R.id.btnKeyword);
        btnMap = (ImageButton)findViewById(R.id.btnMap);
        btnSpinner = (ImageButton)findViewById(R.id.btnSpinner);
        btnFavorite = (ImageButton)findViewById(R.id.btnFavorite);
        btnEver = (ImageButton)findViewById(R.id.btnEver);
        sp.setAdapter(adapter);
        sp2.setAdapter(adapter2);

        btnHome.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(SearchSpinner.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnKeyword.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(SearchSpinner.this, MainSearch.class);
                startActivity(intent);
                finish();
            }
        });
        btnMap.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(SearchSpinner.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSpinner.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(SearchSpinner.this, SearchSpinner.class);
                startActivity(intent);
            }
        });
        btnFavorite.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(SearchSpinner.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        btnEver.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(SearchSpinner.this, EverActivity.class);
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (noarea == true) {
                    Toast.makeText(SearchSpinner.this, "請選擇行政區", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent =new Intent();
                    intent.setClass(SearchSpinner.this, ArenaActivity.class);
                    intent.putExtra("ecounties",aa);
                    intent.putExtra("area",area);
                    startActivity(intent);
                    finish();
                }
            }
        });

        sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView adapterView, View view, int position, long id){

                aa = adapterView.getSelectedItem().toString();
                switch(aa) {
                    case "臺北市":
                        ecounties = "taipei";
                        break;
                    case "高雄市":
                        ecounties = "kaohsiung";
                        break;
                    case "基隆市":
                        ecounties = "keelung";
                        break;
                    case "新北市":
                        ecounties = "newtaipei";
                        break;
                    case "桃園市":
                        ecounties = "taoyuan";
                        break;
                    case "新竹縣":
                        ecounties = "hsinchu1";
                        break;
                    case "新竹市":
                        ecounties = "hsinchu2";
                        break;
                    case "苗栗縣":
                        ecounties = "miaoli";
                        break;
                    case "臺中市":
                        ecounties = "taichung";
                        break;
                    case "彰化縣":
                        ecounties = "changhua";
                        break;
                    case "南投縣":
                        ecounties = "nantou";
                        break;
                    case "雲林縣":
                        ecounties = "yunlin";
                        break;
                    case "嘉義縣":
                        ecounties = "chiayi1";
                        break;
                    case "嘉義市":
                        ecounties = "chiayi2";
                        break;
                    case "臺南市":
                        ecounties = "tainan";
                        break;
                    case "屏東縣":
                        ecounties = "pingtung";
                        break;
                    case "宜蘭縣":
                        ecounties = "yilan";
                        break;
                    case "花蓮縣":
                        ecounties = "hualien";
                        break;
                    case "臺東縣":
                        ecounties = "taitung";
                        break;
                    case "澎湖縣":
                        ecounties = "penghu";
                        break;
                    case "金門縣":
                        ecounties = "kinmen";
                        break;
                    case "連江縣(馬祖)":
                        ecounties = "lienchiang";
                        break;
                    default:
                        break;
                }

                select se=new select();
                se.execute();

            }
            public void onNothingSelected(AdapterView arg0) {
                Toast.makeText(SearchSpinner.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });

        sp2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                area = adapterView.getSelectedItem().toString();
                firstArea = adapterView.getItemAtPosition(0).toString();
                if (firstArea == area) {
                    noarea = true;
                } else {
                    noarea = false;
                }

            }
        });


        //程式剛啟始時載入第一個下拉選單

    }

    public void onStart(){
        super.onStart();
        BackTask bt=new BackTask();
        bt.execute();

    }
    private class BackTask extends AsyncTask<Void,Void,Void> {
        ArrayList<String> list;
        protected void onPreExecute(){
            super.onPreExecute();

            list=new ArrayList<>();
        }
        protected Void doInBackground(Void...params){
            InputStream is=null;
            String result="";
            try{
                URL url = new URL("http://fs.mis.kuas.edu.tw/~s1102137119/counties.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                is = urlConnection.getInputStream();
            }catch(IOException e){
                e.printStackTrace();
            }

            //convert response to string
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result+=line;
                }
                is.close();
                //result=sb.toString();
            }catch(Exception e){
                e.printStackTrace();
            }
            // parse json data
            try{
                JSONArray jArray =new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    JSONObject jsonObject=jArray.getJSONObject(i);
                    // add interviewee name to arraylist
                    list.add(jsonObject.getString("name"));
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result){
            listItems.addAll(list);

            adapter.notifyDataSetChanged();
        }
    }

    /*private Void sendPostDataToInternet(String ecounties) {



        return null;
    }*/
    private class select extends AsyncTask<Void,Void,Void> {

        ArrayList<String> list2;
        protected void onPreExecute(){
            super.onPreExecute();

            list2=new ArrayList<>();
        }
        protected Void doInBackground(Void...params){

            HttpPost myPost = new HttpPost("http://fs.mis.kuas.edu.tw/~s1102137119/area.php");
            try {
                List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                params2.add(new BasicNameValuePair("ecounties",ecounties));
                myPost.setEntity(new UrlEncodedFormEntity(params2, HTTP.UTF_8));
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                //HttpEntity entity = response.getEntity();
                //is2 = entity.getContent();


            } catch (Exception e) {
                e.printStackTrace();
            }
            String result="";

            //convert response to string
            try{
                //BufferedReader reader = new BufferedReader(new InputStreamReader(is2,"utf-8"));
                String line = "";
                while ((line = in.readLine()) != null) {
                    result+=line;
                }
                //is2.close();
                //result=sb.toString();
            }catch(Exception e){
                e.printStackTrace();
            }
            // parse json data
            try{
                JSONArray jArray =new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    JSONObject jsonObject=jArray.getJSONObject(i);
                    // add interviewee name to arraylist
                    list2.add(jsonObject.getString("name"));
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result){
            //sp2.setAdapter(null);
            listItems2.clear();
            listItems2.addAll(list2);
            adapter2.notifyDataSetChanged();

        }
    }
}
