package com.example.user.mainsearch;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class EverActivity extends Activity {

    ImageButton btnHome;
    ImageButton btnKeyword;
    ImageButton btnMap;
    ImageButton btnSpinner;
    ImageButton btnFavorite;
    ImageButton btnEver;
    ListView lvEver;
    com.gc.materialdesign.views.Button btnDelEver;
    com.gc.materialdesign.views.Button btnCheck;
    Spinner sp4;
    Spinner sp5;

    private ArrayAdapter<String> monthList;
    private String[] month = {"一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"};
    private String months;
    String selectMonth;
    private ArrayAdapter<String> yearList;
    private String[] year = {"2016","2017","2018","2019","2020"};
    private String selectYear;

    SQLiteDatabase db= null;
    Cursor cursor;  //和TABLE溝通
    //SQL語法
    String createTable = "CREATE TABLE if not exists EverListFinal"+"(_id INTEGER PRIMARY KEY " +
            "autoincrement,Name TEXT,Start TEXT,End TEXT,Total TEXT,Month TEXT,Day TEXT,Year " +
            "TEXT,LatLng TEXT,PhotoUrl TEXT)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ever);
        lvEver = (ListView)findViewById(R.id.lvEver);
        btnDelEver = (com.gc.materialdesign.views.Button) findViewById(R.id.btnDelEver);
        btnCheck = (com.gc.materialdesign.views.Button) findViewById(R.id.btnCheck);
        btnHome = (ImageButton)findViewById(R.id.btnHome);
        btnKeyword = (ImageButton)findViewById(R.id.btnKeyword);
        btnMap = (ImageButton)findViewById(R.id.btnMap);
        btnSpinner = (ImageButton)findViewById(R.id.btnSpinner);
        btnFavorite = (ImageButton)findViewById(R.id.btnFavorite);
        btnEver = (ImageButton)findViewById(R.id.btnEver);

        db = openOrCreateDatabase("database.db",MODE_WORLD_WRITEABLE,null);
        db.execSQL(createTable); //建立資料表
        cursor = getAll(); //查詢所有資料
        UpdataAdapter(cursor);  //載入資料表至listview

        btnHome.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(EverActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnKeyword.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(EverActivity.this, MainSearch.class);
                startActivity(intent);
                finish();
            }
        });
        btnMap.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(EverActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSpinner.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(EverActivity.this, SearchSpinner.class);
                startActivity(intent);
            }
        });
        btnFavorite.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(EverActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        btnEver.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(EverActivity.this, EverActivity.class);
                startActivity(intent);
            }
        });

        sp4 = (Spinner)findViewById(R.id.spinner4);
        monthList = new ArrayAdapter(this,R.layout.spinner_layout,R.id.txt, month);
        sp4.setAdapter(monthList);
        sp4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                months = month[position];
                switch (months)
                {
                    case "一月":
                        selectMonth="1";
                        break;
                    case "二月":
                        selectMonth="2";
                        break;
                    case "三月":
                        selectMonth="3";
                        break;
                    case "四月":
                        selectMonth="4";
                        break;
                    case "五月":
                        selectMonth="5";
                        break;
                    case "六月":
                        selectMonth="6";
                        break;
                    case "七月":
                        selectMonth="7";
                        break;
                    case "八月":
                        selectMonth="8";
                        break;
                    case "九月":
                        selectMonth="9";
                        break;
                    case "十月":
                        selectMonth="10";
                        break;
                    case "十一月":
                        selectMonth="11";
                        break;
                    case "十二月":
                        selectMonth="12";
                        break;
                    default:
                        break;
                }
                cursor = getans(); //查詢所有資料
                UpdataAdapter(cursor);  //載入資料表至listview
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        sp5 = (Spinner)findViewById(R.id.spinner5);
        yearList = new ArrayAdapter(this,R.layout.spinner_layout,R.id.txt, year);
        sp5.setAdapter(yearList);
        sp5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                selectYear = year[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //沒選擇資料
            }
        });



        btnDelEver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.setClass(EverActivity.this, PhotoMap.class);
                startActivity(intent);


            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent =new Intent();
                intent.setClass(EverActivity.this, CaloriesActivity.class);
                intent.putExtra("SelectMonth",selectMonth);
                intent.putExtra("SelectYear",selectYear);
                startActivity(intent);
            }
        });
    }

    public void UpdataAdapter(Cursor cursor){
        if (cursor != null && cursor.getCount() >=0){
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.list_ever, //包含兩個資料項
                    cursor, //資料庫的Cursors物件
                    new String[] {"Name","Start","End","Total"},
                    new int[] {R.id.tvName,R.id.tvEVStart,R.id.tvEVEnd,R.id.tvTotal});
            lvEver.setAdapter(adapter);
        }
    }

    public Cursor getAll(){
        return db.rawQuery("SELECT * FROM EverListFinal",null);
    }

    public Cursor getans(){
        //設定查詢id
        //查詢指令
        String[] selectDate = {selectMonth,selectYear};

        cursor = db.rawQuery("SELECT * FROM EverListFinal WHERE Month = ? AND Year = ?",
                selectDate);
        return cursor;
    }
}
