package com.example.user.mainsearch;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

public class CaloriesActivity extends Activity {

    String SelectMonth,SelectYear;
    SQLiteDatabase db= null;
    Cursor cursor;  //和TABLE溝通
    //SQL語法
    String CREATE_TABLE = "CREATE TABLE if not exists EverListFinal"+"(_id INTEGER PRIMARY KEY " +
            "autoincrement,Name TEXT,Start TEXT,End TEXT,Total TEXT,Month TEXT,Day TEXT,Year " +
            "TEXT,LatLng TEXT,PhotoUrl TEXT)";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories);
        db = openOrCreateDatabase("database.db",MODE_WORLD_WRITEABLE,null);
        db.execSQL(CREATE_TABLE); //建立資料表

        Intent intent = this.getIntent();
        SelectMonth = intent.getStringExtra("SelectMonth");
        SelectYear = intent.getStringExtra("SelectYear");

        cursor = getans();


        String[] titles = new String[] {"單位：大卡"}; // 定義折線的名稱
        List<Integer> x = new ArrayList(); // 點的x坐標
        List<Double> y = new ArrayList(); // 點的y坐標


        if (cursor.getCount()>0){    // 若有資料

            cursor.moveToFirst();    // 移到第 1 筆資料
            do{        // 逐筆讀出資料
                String aa = cursor.getString(6);
                x.add(Integer.valueOf(aa));
            } while(cursor.moveToNext());    // 有一下筆就繼續迴圈
        }


        if (cursor.getCount()>0){    // 若有資料

            cursor.moveToFirst();    // 移到第 1 筆資料
            do{        // 逐筆讀出資料
                y.add(cursor.getDouble(4));
            } while(cursor.moveToNext());    // 有一下筆就繼續迴圈
        }
        XYMultipleSeriesDataset dataset = buildDatset(titles, x, y); // 儲存座標值

        int[] colors = new int[] { Color.RED};// 折線的顏色
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE}; // 折線點的形狀
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);

        setChartSettings(renderer, "本月運動消耗熱量", "日期", "消耗熱量", 0, 31, 0, 100, Color.BLACK);// 定義折線圖
        View chart = ChartFactory.getLineChartView(this, dataset, renderer);
        setContentView(chart);
    }

    public Cursor getans(){
        //設定查詢id
        //查詢指令
        String[] Selectdate = {SelectMonth,SelectYear};

        cursor = db.rawQuery("SELECT * FROM EverListFinal WHERE Month = ? AND Year = ?",
                Selectdate);


        return cursor;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
                                    String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor) {
        renderer.setChartTitle(title); // 折線圖名稱
        renderer.setChartTitleTextSize(30); // 折線圖名稱字形大小
        renderer.setAxisTitleTextSize(20);// 設置坐標軸標題文本大小
        renderer.setLegendTextSize(18);// 設置圖例文本大小
        renderer.setXTitle(xTitle); // X軸名稱
        renderer.setYTitle(yTitle); // Y軸名稱
        renderer.setLabelsTextSize(20); // 設置軸標籤文本大小
        renderer.setXAxisMin(xMin); // X軸顯示最小值
        renderer.setXAxisMax(xMax); // X軸顯示最大值
        renderer.setXLabelsColor(Color.BLACK); // X軸線顏色
        renderer.setYAxisMin(yMin); // Y軸顯示最小值
        renderer.setYAxisMax(yMax); // Y軸顯示最大值
        renderer.setAxesColor(axesColor); // 設定坐標軸顏色
        renderer.setYLabelsColor(0, Color.BLACK); // Y軸線顏色
        renderer.setLabelsColor(Color.BLACK); // 設定標籤顏色
        renderer.setMarginsColor(Color.WHITE); // 設定背景顏色
        renderer.setShowGrid(true); // 設定格線
        renderer.setDisplayChartValues(true);//設定是否顯示數值
        renderer.setClickEnabled(false);//設置是否可以滑動及放大縮小
        renderer.setPanEnabled(false);
    }

    // 定義折線圖的格式
    private XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            r.setFillPoints(fill);
            renderer.addSeriesRenderer(r); //將座標變成線加入圖中顯示
        }
        return renderer;
    }

    // 資料處理
    private XYMultipleSeriesDataset buildDatset(String[] titles, List<Integer> xValues,
                                                List<Double> yValues) {
        List<Integer> xV = new ArrayList(); // 點的x坐標
        List<Double> yV = new ArrayList(); // 點的y坐標
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        int length = titles.length; // 折線數量
        for (int i = 0; i < length; i++) {
            // XYseries對象,用於提供繪製的點集合的資料
            XYSeries series = new XYSeries(titles[i]); // 依據每條線的名稱新增
            for(int a=0;a<xValues.size();a++) {
                xV.add(xValues.get(a));
                yV.add(yValues.get(a));
            }
            int seriesLength = xV.size(); // 有幾個點

            for (int k = 0; k < seriesLength; k++) // 每條線裡有幾個點
            {
                series.add(xV.get(k),yV.get(k));
            }
            dataset.addSeries(series);
        }
        return dataset;
    }
}
