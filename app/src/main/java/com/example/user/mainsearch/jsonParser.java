package com.example.user.mainsearch;

import android.util.Log;

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

/**
 * Created by ASUS on 2016/11/27.
 */

public class jsonParser {

    public static InputStream is ;
    public static JSONObject jObj = null;
    public  static String json = "";

    // constructor
    public jsonParser() {

    }

    public JSONObject getJSONFromUrl(String url) {

        //System.out.println("url getJSONfromUrl " + url);
        //url = "http://api.worldbank.org/countries/CA/indicators/SP.POP.TOTL?date=1980:1981&format=json";

        // Making HTTP request
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
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            System.out.println("JSONParser string: " + json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }


        if (json.startsWith("[")) {
            // We have a JSONArray
            try {
                jObj = new JSONObject();
                jObj.put("data", new JSONArray(json));
            } catch (JSONException e) {
                Log.d("JSON Parser", "Error parsing JSONArray " + e.toString());
            }
            return jObj;
        }

        // try parse the string to a JSON object
    /*try {
        jObj = new JSONObject(json);
    } catch (JSONException e) {
        Log.e("JSON Parser", "Error parsing data " + e.toString());
    }*/

        // return JSON String
        return jObj;



    }
}