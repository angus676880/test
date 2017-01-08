package com.example.user.mainsearch;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
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
    TextView title,subtitle,tvStart,tvEnd;
    String gymid,LatLng,PhotoUrl;
    com.gc.materialdesign.views.Button btnStart,btnEnd,btnFinish,btnDel;
    Spinner sp3;
    public String sh;
    public String sm;
    public String eh;
    public String em;
    public String month;
    public String day;
    public String year;
    public int shh,smm,ehh,emm;
    public int cal;
    public double totalcal;

    private ArrayAdapter<String> sportList;
    private Context mContext;
    private String[] sport = {"健走","跑步","排球","棒壘球","籃球","羽毛球","桌球","網球","其他運動","游泳","健身房運動","有氧舞蹈"};
    private String Sport;

    private Calendar mCalendar;
    private String str;
    private SimpleDateFormat df;
    private long startTime;
    private Handler handler = new Handler();
    SQLiteDatabase db= null;
    Cursor cursor;  //和TABLE溝通
    //SQL語法
    String CREATE_TABLE = "CREATE TABLE if not exists FavoriteListFinal"+"(_id INTEGER PRIMARY " +
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

        mContext = this.getApplicationContext();
        sp3 = (Spinner)findViewById(R.id.spinner3);

        sportList = new ArrayAdapter(this,R.layout.spinner_layout,R.id.txt, sport);
        sp3.setAdapter(sportList);

        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Sport = sport[position];
                switch (Sport)
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
            }
        });

        //將場館名稱傳進來
        Intent intent = getIntent();
        ansid = intent.getStringExtra("selected-item");
        getid = Integer.parseInt(ansid);

        db = openOrCreateDatabase("database.db",MODE_WORLD_WRITEABLE,null);
        db.execSQL(CREATE_TABLE);
        cursor = getans();
        cursor.moveToFirst();
        showans();
        operation();

        gymid = cursor.getString(cursor.getColumnIndex("gymID"));
        LatLng = cursor.getString(cursor.getColumnIndex("LatLng"));
        PhotoUrl = cursor.getString(cursor.getColumnIndex("PhotoUrl"));

        btnFinish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(StartActivity.this);
                dialog.setTitle("前往評論");
                dialog.setMessage("請問您是否要前往評論");
                dialog.setNegativeButton("No,完成運動",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String Name = title.getText().toString();
                        String Start = tvStart.getText().toString();
                        String End = tvEnd.getText().toString();
                        calories();
                        String total2 = Double.toString(totalcal);
                        String Month = month;
                        String Day = day;
                        String Year = year;
                        create(Name,Start,End,total2,Month,Day,Year,LatLng,PhotoUrl);
                        dbdelete(getid);
                        Intent intent = new Intent(StartActivity.this, EverActivity.class);
                        startActivity(intent);
                    }

                });
                dialog.setPositiveButton("Yes,我要評論",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String Name = title.getText().toString();
                        String Start = tvStart.getText().toString();
                        String End = tvEnd.getText().toString();
                        calories();
                        String total2 = Double.toString(totalcal);
                        String Month = month;
                        String Day = day;
                        String Year = year;
                        dbdelete(getid);
                        Intent intent =new Intent();
                        intent.setClass(StartActivity.this, Camera.class);
                        intent.putExtra("gymid",gymid);
                        intent.putExtra("LatLng",LatLng);
                        intent.putExtra("Name",Name);
                        intent.putExtra("Start",Start);
                        intent.putExtra("End",End);
                        intent.putExtra("Total",total2);
                        intent.putExtra("Month",Month);
                        intent.putExtra("Day",Day);
                        intent.putExtra("Year",Year);
                        intent.putExtra("PhotoUrl",PhotoUrl);
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

        startTime = System.currentTimeMillis(); //取得目前時間
        handler.removeCallbacks(updateTimer);//設定定時要執行的方法
        handler.postDelayed(updateTimer, 500);//設定Delay的時間
    }

    //固定要執行的方法
    private Runnable updateTimer = new Runnable() {
        public void run() {
            handler.postDelayed(this,500);
            mCalendar = Calendar.getInstance();
            df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            str = df.format(mCalendar.getTime());
        }
    };

    public Cursor getans(){
        //設定查詢id
        String[] Ans = {ansid};
        //查詢指令
        cursor = db.rawQuery("SELECT * FROM FavoriteListFinal WHERE _id = ?",Ans);
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

    public long create(String Name, String Start, String End, String total2, String Month ,
                       String Day, String Year, String LatLng,String photourl) {
        ContentValues args = new ContentValues();
        args.put("Name", Name);
        args.put("Start", Start);
        args.put("End", End);
        args.put("total", total2);
        args.put("Month", Month);
        args.put("Day", Day);
        args.put("Year", Year);
        args.put("LatLng", LatLng);
        args.put("PhotoUrl", photourl);


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