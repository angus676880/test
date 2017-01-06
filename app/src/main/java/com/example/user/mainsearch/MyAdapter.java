

package com.example.user.mainsearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

    private LayoutInflater myInflater;

    CharSequence[] icon = null;
    CharSequence[] title = null;
    CharSequence[] info = null;
    CharSequence[] latLng = null;
    public MyAdapter(Context ctxt ){
        myInflater = LayoutInflater.from( ctxt);
        this.icon=icon;
        this.title = title;
        this.info = info;
        this.latLng=latLng;
    }




    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return title[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //自訂類別，表達個別listItem中的view物件集合。
        ViewTag viewTag;

        if(convertView == null){
            //取得listItem容器 view
            convertView = myInflater.inflate(R.layout.list_raw, null);

            //建構listItem內容view
            viewTag = new ViewTag(
                    (ImageView)convertView.findViewById(R.id.list_image),
                    (TextView) convertView.findViewById(R.id.list_Name),
                    (TextView) convertView.findViewById(R.id.list_Address),
                    (TextView) convertView.findViewById(R.id.list_LatLng)
            );

            //設置容器內容
            convertView.setTag(viewTag);
        }
        else{
            viewTag = (ViewTag) convertView.getTag();
        }

        //設定內容圖案


        //設定標題文字
        viewTag.title.setText("title"+position);
        //設定內容文字
        viewTag.info.setText("info"+position);

        viewTag.latLng.setText("latLng"+position);

        return convertView;
    }

    //自訂類別，表達個別listItem中的view物件集合。
    class ViewTag{
        ImageView icon;
        TextView title;
        TextView info;
        TextView latLng;

        public ViewTag(ImageView icon, TextView title, TextView info, TextView latLng){
            this.icon = icon;
            this.title = title;
            this.info = info;
            this.latLng = latLng;

        }
    }
}
