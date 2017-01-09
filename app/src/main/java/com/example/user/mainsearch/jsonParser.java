package com.example.user.mainsearch;

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


    // constructor
    public jsonParser() {

    }

    public JSONObject getJSONFromUrl(String url) {
        InputStream is ;
        JSONObject jObj = null;
        String json = "";

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        } catch (ClientProtocolException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line ;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            System.out.println("JSONParser string: " + json);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }


        if (json.startsWith("[")) {
            // We have a JSONArray
            try {
                jObj = new JSONObject();
                jObj.put("data", new JSONArray(json));
            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
            return jObj;
        }

        // return JSON String
        return jObj;
    }
}