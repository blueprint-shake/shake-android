package com.stevenwood.com.shake.Util;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Created by Temp on 2/21/2015.
 */
public class HTTPUtil {

    public static void post(String url, List<NameValuePair> vals) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(vals));
        HttpResponse response = httpClient.execute(httpPost);

    }

    public static JSONObject getJSON(String urlParam) throws IOException, JSONException {
        HttpClient httpClient = new DefaultHttpClient();
        StringBuilder url = new StringBuilder(urlParam);

        HttpGet get = new HttpGet(url.toString());
        HttpResponse r = httpClient.execute(get);
        int status = r.getStatusLine().getStatusCode();
        if(status == 200){
            HttpEntity e = r.getEntity();
            String data = EntityUtils.toString(e);
            JSONArray array = new JSONArray();
            JSONObject last = array.getJSONObject(0);
            return last;
        }
        return null;
    }

    public class Read extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject json = getJSON("");
                return json.getString(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //httpStuff.setText(s);
        }

    }
   }
