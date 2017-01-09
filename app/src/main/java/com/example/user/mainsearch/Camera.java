package com.example.user.mainsearch;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.UUID;

import static com.example.user.mainsearch.R.id.img;

public class Camera extends Activity {
    //宣告
    private ImageView mImg;
    private DisplayMetrics mPhone;
    int cam = 66 ;
    private Uri file;
    Uri uri;
    EditText etCusCom;
    RatingBar rbCusCom;
    Float cusComment;
    String photoUrl;
    char[] urlChar;
    String encodeResult="";
    String temp;
    String comments;
    String gymid;
    String cusComments;
    String latLng;
    String name;
    String start;
    String end;
    String total;
    String month;
    String day;
    String year;
    String photo_url2;
    String url;
    FTPClient ftpClient;
    String createTable = "CREATE TABLE if not exists EverListFinal"+"(_id INTEGER PRIMARY KEY " +
            "autoincrement,Name TEXT,Start TEXT,End TEXT,Total TEXT,Month TEXT,Day TEXT,Year " +
            "TEXT,LatLng TEXT,PhotoUrl TEXT)";

    long cursor2;  //和TABLE溝通
    SQLiteDatabase db= null;
    com.gc.materialdesign.views.ButtonRectangle btn4;
    com.gc.materialdesign.views.ButtonRectangle photo;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //讀取手機解析度
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);
        rbCusCom = (RatingBar)findViewById(R.id.rbCusCom);
        rbCusCom.setRating(0);
        etCusCom = (EditText)findViewById(R.id.etCusCom);
        mImg = (ImageView) findViewById(img);
        photo = (ButtonRectangle) findViewById(R.id.photo);
        btn4 = (ButtonRectangle) findViewById(R.id.button4);
        Intent intent = this.getIntent();
        gymid = intent.getStringExtra("gymid");
        latLng = intent.getStringExtra("LatLng");
        name = intent.getStringExtra("Name");
        start = intent.getStringExtra("Start");
        end = intent.getStringExtra("End");
        total = intent.getStringExtra("Total");
        month = intent.getStringExtra("Month");
        day = intent.getStringExtra("Day");
        year = intent.getStringExtra("Year");
        photo_url2 = intent.getStringExtra("PhotoUrl");


        db = openOrCreateDatabase("database.db",MODE_WORLD_WRITEABLE,null);
        db.execSQL(createTable); //建立資料表
        //db.execSQL("INSERT INTO table02 (loc,date,time,oth) values ('台北','2016/7/23','1:41 AM','喔噎')");  //新增資料
        rbCusCom.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar,
                                        float rating, boolean paramBoolean) {
                cusComment = rating;
                cusComments = cusComment.toString();

            }
        });

        urlChar = photo_url2.toCharArray();
        encodeResult="";
        for(int i = 0; i <urlChar.length; i++) {
            temp = String.valueOf(urlChar[i]);
            if( urlChar[i]<33 ||  urlChar[i]>126)
            {
                try {
                    temp = URLEncoder.encode(String.valueOf(urlChar[i]),"utf-8");
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
                if(urlChar[i]==32)
                {
                    temp="%20";
                }
            }
            encodeResult=encodeResult + temp;
        }
        new DownloadImageTask(mImg).execute(encodeResult);


        photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因
                //為點選相片後返回程式呼叫onActivityResult

                // 建立 "選擇檔案 Action" 的 Intent
                Intent intent = new Intent(Intent.ACTION_PICK);
                // 過濾檔案格式
                intent.setType("image/*");
                // 建立 "檔案選擇器" 的 Intent (第二個參數: 選擇器的標題)
                Intent destIntent = Intent.createChooser(intent, "選擇圖片");
                // 切換到檔案選擇器 (它的處理結果, 會觸發 onActivityResult 事件)
                startActivityForResult(destIntent, 0);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                comments = etCusCom.getText().toString();
                url = "http://52.198.27.85/overmove/InsertOne/"+gymid+"&"+cusComments+"&"+comments;

                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
//API串接的uri路徑 (小黑人目前範例先取用YouTube API)
                            String uri = url;
                            HttpClient mHttpClient = new DefaultHttpClient();
                            HttpGet mHttpGet = new HttpGet(uri);
                            mHttpClient.execute(mHttpGet);
                        }
                        catch(Exception e)
                        {
                            throw new IllegalArgumentException(e);
                        }
                    }
                }).start();
                imageUpload();
                // Intent code for open new activity through intent.
                Intent intent = new Intent(Camera.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }

    //拍照完畢或選取圖片後呼叫此函式
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            mImg.setImageURI(file);
        }
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if ((requestCode == cam || resultCode == RESULT_OK ) && data != null)
        {
            //取得照片路徑uri
            uri = data.getData() ;

            ContentResolver cr = this.getContentResolver();

            try
            {
                //讀取照片，型態為Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

                //判斷照片為橫向或者為直向，並進入ScalePic判斷圖片是否要進行縮放
                if(bitmap.getWidth()>bitmap.getHeight())
                {
                    ScalePic(bitmap,mPhone.heightPixels);
                }
                else ScalePic(bitmap,mPhone.widthPixels);
            }
            catch (FileNotFoundException e)
            {
                throw new IllegalArgumentException(e);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void ScalePic(Bitmap bitmap, int phone)
    {
        //縮放比例預設為1
        float mScale ;

        //如果圖片寬度大於手機寬度則進行縮放，否則直接將圖片放入ImageView內
        if(bitmap.getWidth() > phone )
        {
            //判斷縮放比例
            mScale = (float)phone/(float)bitmap.getWidth();

            Matrix mMat = new Matrix() ;
            mMat.setScale(mScale, mScale);

            Bitmap mScaleBitmap = Bitmap.createBitmap(bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    mMat,
                    false);
            mImg.setImageBitmap(mScaleBitmap);
        }
        else mImg.setImageBitmap(bitmap);
    }

    private void imageUpload()
    {
        StrictMode
                .setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()   // or .detectAll() for all detectable problems
                        .penaltyLog()
                        .build());
        StrictMode
                .setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .penaltyLog()
                        .penaltyDeath()
                        .build());
        if(uri != null) {
            //抓取檔案真實路徑
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

            //真實路徑
            String name = cursor.getString(idx);

            Log.e("file", name);
            cursor.close();
            String uuid = UUID.randomUUID().toString();
            //檔案名稱
            String filename = name.substring(name.lastIndexOf("/") + 1, name.length()).toLowerCase() + uuid;

            final String ftpHost = "fs.mis.kuas.edu.tw";
            final String ftpUser = "s1102137127";
            final String ftpPass = "H124676880";

            try {
                ftpClient = new FTPClient();

                ftpClient.setControlEncoding("UTF-8");

                //連接FTP
                try {ftpClient.connect(InetAddress.getByName(ftpHost));}
                catch (Exception ex) {
                    throw new IOException("不能找到FTP服務:" + InetAddress.getByName(ftpHost) + "'");
                }

                //在?接???查??.
                int reply = ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    throw new IOException("不能連接到伺服器':" + InetAddress.getByName(ftpHost) + "'");
                }


                //登陸
                if (!ftpClient.login(ftpUser, ftpPass)) {
                    ftpClient.disconnect();
                    throw new IOException("不能登入到FTP伺服器:'" + InetAddress.getByName(ftpHost) + "'");
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }

            try {
                //轉到指定上傳目錄

                ftpClient.changeWorkingDirectory("www/");

                //創建server端資料夾

                ftpClient.makeDirectory("www/test/");//restaurantId

                //設定檔案類型
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                //設定傳輸模式為被動
                ftpClient.enterLocalPassiveMode();


                BufferedInputStream buffIn = null;
                //上傳檔案間重新命名
                try {
                    //開啟資料流
                    buffIn = new BufferedInputStream(new FileInputStream(name));//路徑

                    ftpClient.changeWorkingDirectory("/");
                    boolean ftpupload = ftpClient.storeFile("www/test/" + filename, buffIn);
                    //            ftpClient.storeFile("/gymupload/"+restaurantId+"/"+restaurantId+"."+menuId+filename.substring(filename.lastIndexOf("."),filename.length()), buffIn);

                    if (ftpupload) {
                        Toast.makeText(this, "回報圖片上傳成功!!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }finally {
                    if(buffIn != null){
                    buffIn.close();
                    }
                }

                ftpClient.logout();
                ftpClient.disconnect();

            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            photoUrl = "http://fs.mis.kuas.edu.tw/~s1102137127/test/"+filename;
        }else {
            photoUrl = photo_url2;
        }

        cursor2 = create(name,start,end,total,month,day,year,latLng,photoUrl);
    }


    public long create(String name, String start, String end, String total2, String month ,
                       String day, String year, String latLng,String photo_url2) {
        ContentValues args = new ContentValues();
        args.put("Name", name);
        args.put("Start", start);
        args.put("End", end);
        args.put("total", total2);
        args.put("Month", month);
        args.put("Day", day);
        args.put("Year", year);
        args.put("LatLng", latLng);
        args.put("PhotoUrl", photo_url2);


        return db.insert("EverListFinal", null, args);
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
            Bitmap mIcon11;
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

}
