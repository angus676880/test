package com.example.user.mainsearch;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class StartActivity extends Activity {

    String ansid;
    Integer getid;
    TextView title;
    TextView subtitle;
    TextView tvStart;
    TextView tvEnd;
    String gymid;
    String latLng;
    String photoUrl;
    com.gc.materialdesign.views.Button btnStart;
    com.gc.materialdesign.views.Button btnEnd;
    com.gc.materialdesign.views.Button btnFinish;
    com.gc.materialdesign.views.Button btnDel;
    Spinner sp3;
    String sh;
    String sm;
    String eh;
    String em;
    String month;
    String day;
    String year;
    int shh;
    int smm;
    int ehh;
    int emm;
    int cal;
    int totalcal;
    private ArrayAdapter<String> sportList;
    private String[] sport = {"健走","跑步","排球","棒壘球","籃球","羽毛球","桌球","網球","其他運動","游泳","健身房運動","有氧舞蹈"};
    private String sports;

    private Calendar mCalendar;
    private String str;
    private SimpleDateFormat df;
    private Handler handler = new Handler();
    SQLiteDatabase db= null;
    Cursor cursor;  //和TABLE溝通
    //SQL語法
    String createTable = "CREATE TABLE if not exists FavoriteListFinal"+"(_id INTEGER PRIMARY " +
            "KEY autoincrement,gymID TEXT,title TEXT,subtitle TEXT,LatLng TEXT,PhotoUrl TEXT)";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        title = (TextView)findViewById(R.id.textView7);
        subtitle = (TextView)findViewById(R.id.textView8);
        tvStart = (TextView)findViewById(R.id.tvStart);
        tvEnd = (TextView)findViewById(R.id.tvEnd);
        btnStart = (com.gc.materialdesign.views.Button) findViewById(R.id.btnStart);
        btnEnd = (com.gc.materialdesign.views.Button) findViewById(R.id.btnEnd);
        btnEnd.setEnabled(false);
        btnFinish = (com.gc.materialdesign.views.Button) findViewById(R.id.btnFinish);
        btnFinish.setEnabled(false);
        btnDel = (com.gc.materialdesign.views.Button) findViewById(R.id.button3);

        sp3 = (Spinner)findViewById(R.id.spinner3);

        sportList = new ArrayAdapter(this,R.layout.spinner_layout,R.id.txt, sport);
        sp3.setAdapter(sportList);

        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                sports = sport[position];
                switch (sports)
                {
                    case "健走":
                        cal = 80;
                        break;
                    case "跑步":
                        cal = 165;
                        break;
                    case "排球":
                        cal = 60;
                        break;
                    case "棒壘球":
                        cal = 78;
                        break;
                    case "籃球":
                        cal = 122;
                        break;
                    case "羽毛球":
                        cal = 85;
                        break;
                    case "桌球":
                        cal = 70;
                        break;
                    case "網球":
                        cal = 110;
                        break;
                    case "其他運動":
                        cal = 87;
                        break;
                    case "游泳":
                        cal = 131;
                        break;
                    case "健身房運動":
                        cal = 112;
                        break;
                    case "有氧舞蹈":
                        cal = 74;
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //???
            }
        });

        //將場館名稱傳進來
        Intent intent = getIntent();
        ansid = intent.getStringExtra("selected-item");
        getid = Integer.parseInt(ansid);

        db = openOrCreateDatabase("database.db",MODE_WORLD_WRITEABLE,null);
        db.execSQL(createTable);
        cursor = getans();
        cursor.moveToFirst();
        showans();
        operation();

        gymid = cursor.getString(cursor.getColumnIndex("gymID"));
        latLng = cursor.getString(cursor.getColumnIndex("LatLng"));
        photoUrl = cursor.getString(cursor.getColumnIndex("PhotoUrl"));

        btnFinish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(StartActivity.this);
                dialog.setTitle("前往評論");
                dialog.setMessage("請問您是否要前往評論");
                dialog.setNegativeButton("No,完成運動",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String name = title.getText().toString();
                        String start = tvStart.getText().toString();
                        String end = tvEnd.getText().toString();
                        calories();
                        String total2 = Double.toString(totalcal);
                        String month2 = month;
                        String day2 = day;
                        String year2 = year;
                        create(name,start,end,total2,month2,day2,year2,latLng,photoUrl);
                        dbdelete(getid);
                        Intent intent = new Intent(StartActivity.this, EverActivity.class);
                        startActivity(intent);
                    }

                });
                dialog.setPositiveButton("Yes,我要評論",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String name = title.getText().toString();
                        String start = tvStart.getText().toString();
                        String end = tvEnd.getText().toString();
                        calories();
                        String total2 = Double.toString(totalcal);
                        String month2 = month;
                        String day2 = day;
                        String year2 = year;
                        dbdelete(getid);
                        Intent intent =new Intent();
                        intent.setClass(StartActivity.this, Camera.class);
                        intent.putExtra("gymid",gymid);
                        intent.putExtra("LatLng",latLng);
                        intent.putExtra("Name",name);
                        intent.putExtra("Start",start);
                        intent.putExtra("End",end);
                        intent.putExtra("Total",total2);
                        intent.putExtra("Month",month2);
                        intent.putExtra("Day",day2);
                        intent.putExtra("Year",year2);
                        intent.putExtra("PhotoUrl",photoUrl);
                        startActivity(intent);
                        finish();
                    }

                });
                dialog.show();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                dbdelete(getid);
                // Intent code for open new activity through intent.
                Intent intent = new Intent(StartActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
    }

    public void operation(){
        btnStart.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                tvStart.setText(""+ str);
                btnStart.setEnabled(false);
                btnEnd.setEnabled(true);
                sh = str.substring(11,13);
                sm = str.substring(14,16);
                month = str.substring(5,7);
                day = str.substring(8,10);
                year = str.substring(0,4);
                shh = Integer.parseInt(sh);
                smm = Integer.parseInt(sm);
            }
        });
        btnEnd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                tvEnd.setText(""+ str);
                btnEnd.setEnabled(false);
                btnFinish.setEnabled(true);
                eh = str.substring(11,13);
                em = str.substring(14,16);
                ehh = Integer.parseInt(eh);
                emm = Integer.parseInt(em);
            }
        });
        handler.removeCallbacks(updateTimer);//設定定時要執行的方法
        handler.postDelayed(updateTimer, 500);//設定Delay的時間
    }

    //固定要執行的方法
    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this,500);
            mCalendar = Calendar.getInstance();
            df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            str = df.format(mCalendar.getTime());
        }
    };

    public Cursor getans(){
        //設定查詢id
        String[] ans = {ansid};
        //查詢指令
        cursor = db.rawQuery("SELECT * FROM FavoriteListFinal WHERE _id = ?",ans);
        return cursor;
    }

    public int dbdelete(long rowId) {
        return db.delete("FavoriteListFinal",	//資料表名稱
                "_ID=" + rowId,			//WHERE
                null				//WHERE的參數
        );
    }

    public void showans(){

        String anstt = cursor.getString(cursor.getColumnIndex("title"));
        String ansstt = cursor.getString(cursor.getColumnIndex("subtitle"));

        title.setText(anstt);
        subtitle.setText(ansstt);

    }

    public long create(String name, String start, String end, String total2, String month ,
                       String day, String year, String latLng,String photoUrl) {
        ContentValues args = new ContentValues();
        args.put("Name", name);
        args.put("Start", start);
        args.put("End", end);
        args.put("total", total2);
        args.put("Month", month);
        args.put("Day", day);
        args.put("Year", year);
        args.put("LatLng", latLng);
        args.put("PhotoUrl", photoUrl);


        return db.insert("EverListFinal", null, args);
    }

    public double calories(){

        int starttime = (shh*60)+smm;
        int endtime = (ehh*60)+emm;
        int totaltime = endtime-starttime;
        totalcal = (totaltime*cal*60)/1000;

        return totalcal;
    }



}