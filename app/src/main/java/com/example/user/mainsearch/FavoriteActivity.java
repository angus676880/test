package com.example.user.mainsearch;



import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class FavoriteActivity extends Activity {

    ImageButton btnHome;
    ImageButton btnKeyword;
    ImageButton btnMap;
    ImageButton btnSpinner;
    ImageButton btnFavorite;
    ImageButton btnEver;
    ListView listView2;
    com.gc.materialdesign.views.Button btnDel;
    private long myid = 1;
    SQLiteDatabase db= null;
    Cursor cursor;  //和TABLE溝通
    //SQL語法
    String createTable = "CREATE TABLE if not exists FavoriteListFinal"+"(_id INTEGER PRIMARY " +
            "KEY autoincrement,gymID TEXT,title TEXT,subtitle TEXT,LatLng TEXT,PhotoUrl TEXT)";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        listView2 = (ListView)findViewById(R.id.listView2);
        btnDel = (com.gc.materialdesign.views.Button) findViewById(R.id.btnDel);
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
        listView2.setOnItemClickListener(new ListClickHandler());

        btnHome.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(FavoriteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnKeyword.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(FavoriteActivity.this, MainSearch.class);
                startActivity(intent);
                finish();
            }
        });
        btnMap.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(FavoriteActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSpinner.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(FavoriteActivity.this, SearchSpinner.class);
                startActivity(intent);
            }
        });
        btnFavorite.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(FavoriteActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        btnEver.setOnClickListener (new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(FavoriteActivity.this, EverActivity.class);
                startActivity(intent);
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                dropTable();
                refresh();
            }
        });
    }

    public class ListClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            myid = arg3;
            String a = String.valueOf(myid);
            Intent intent = new Intent(FavoriteActivity.this, StartActivity.class);
            intent.putExtra("selected-item", a);
            startActivity(intent);
        }

    }

    public void dropTable () {
        db.execSQL("DROP TABLE FavoriteListFinal");
        db.close();
    }

    public void refresh(){

        onCreate(null);

    }

    public void UpdataAdapter(Cursor cursor){
        if (cursor != null && cursor.getCount() >=0){
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.list_love, //包含兩個資料項
                    cursor, //資料庫的Cursors物件
                    new String[] {"gymID","title","subtitle"},
                    new int[] {R.id.textView17,R.id.textView3,R.id.textView4});
            listView2.setAdapter(adapter);
        }
    }

    public Cursor getAll(){
        return db.rawQuery("SELECT _id, gymID, title, subtitle FROM FavoriteListFinal",null);
    }
}
