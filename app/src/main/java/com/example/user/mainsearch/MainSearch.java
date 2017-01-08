package com.example.user.mainsearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainSearch extends Activity {

    private EditText searchText;
    com.gc.materialdesign.views.Button searchBtn;
    ImageButton btnHome;
    ImageButton btnKeyword;
    ImageButton btnMap;
    ImageButton btnSpinner;
    ImageButton btnFavorite;
    ImageButton btnEver;
    Spinner spinner;

    public ArrayList nameArr = new ArrayList();

    private ArrayAdapter<String> countiesList;
    private String[] counties = {"臺北市","高雄市","基隆市","新北市","桃園市","新竹縣","新竹市","苗栗縣",
            "臺中市","彰化縣","南投縣","雲林縣","嘉義縣","嘉義市","臺南市","屏東縣",
            "宜蘭縣","花蓮縣","臺東縣","澎湖縣","金門縣","連江縣"};
    private String countiesSelect;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);
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
                intent.setClass(MainSearch.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnKeyword.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainSearch.this, MainSearch.class);
                startActivity(intent);
                finish();
            }
        });
        btnMap.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainSearch.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSpinner.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainSearch.this, SearchSpinner.class);
                startActivity(intent);
            }
        });
        btnFavorite.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainSearch.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        btnEver.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainSearch.this, EverActivity.class);
                startActivity(intent);
            }
        });

        searchText = (EditText) findViewById(R.id.searchText);
        searchBtn= (com.gc.materialdesign.views.Button) findViewById(R.id.searchBtn);

        spinner = (Spinner)findViewById(R.id.spinner);

        countiesList = new ArrayAdapter(this,R.layout.spinner_layout,R.id.txt, counties);
        spinner.setAdapter(countiesList);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        searchBtn.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                                try {
                    EditText searchText = MainSearch.this.searchText;
                    String result = DBConnector.executeQuery("SELECT distinct Venue FROM information WHERE " +
                            "Counties LIKE '%" + countiesSelect + "%' AND (" +
                            "Address LIKE '%" + searchText.getText().toString() + "%'" +
                            "OR Area LIKE '%" + searchText.getText().toString() + "%' " +
                            "OR Venue LIKE '%" + searchText.getText().toString() + "%' " +
                            "OR Address LIKE '%" + searchText.getText().toString() + "%' " +
                            "OR ArenaProject LIKE '%" + searchText.getText().toString() + "%'" +
                            "OR FacilityProjects LIKE '%" + searchText.getText().toString() + "%')");

                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */

                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonData = jsonArray.getJSONObject(i);

                            final TextView user_acc = new TextView(MainSearch.this);
                            user_acc.setText(jsonData.getString("Venue"));

                            String name = user_acc.getText().toString();

                            nameArr.add(name);

                            //TextView user_pwd = new TextView(MainSearch.this);
                            //user_pwd.setText(jsonData.getString("Address"));
                            //user_pwd.setLayoutParams(view_layout);

                            //建立多執行緒進行網路Server API串接的資料傳輸與讀取


                        }

                        Intent intent = new Intent();
                        intent.setClass(MainSearch.this, ArenaActivity2.class);
                        intent.putExtra("nameArr", nameArr);
                        startActivity(intent);
                        finish();

                } catch (Exception e) {
                    Toast.makeText(MainSearch.this, "查無資料", Toast.LENGTH_SHORT).show();
                    Log.e("log_tag", e.toString());
                }
            }
        });

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countiesSelect = counties[position].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            //??????
            }
        });

    }
}
