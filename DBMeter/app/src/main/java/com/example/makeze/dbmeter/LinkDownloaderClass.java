package com.example.makeze.dbmeter;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by makeze on 12/25/16.
 */

public class LinkDownloaderClass extends AsyncTask<Void, Void, Integer>  {

        String baseUrl = "http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?"; // http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?x=XXX.XXXXXX&y=YYY.YYYYYY
        String params;
        HttpURLConnection connection;
        Integer res;
        URL imageUrl;
        String bestSignal;

    LinkDownloaderClass(String params){
            this.params = params;
    }

    @Override
    protected Integer doInBackground(Void... urls) {
        try {
            String line;
            String html="";

            URL url = new URL(baseUrl+params);
            url = new URL("http://maksu.de/sample.html");
            Log.i("chat", baseUrl+params);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();
            res = connection.getResponseCode();

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            for(int i=0;i<2;i++){
                rd.readLine();
            }
            imageUrl=new URL(rd.readLine());
            bestSignal=rd.readLine();
            rd.close();


            Log.i("UpdaterLog", "+ server response (200 - everything ok): " + res.toString());

            } catch (Exception e) {
                Log.i("UpdaterLog", "+ connection error: " + e.getMessage());

            } finally {
                connection.disconnect();
            }
            return res;
        }

        protected void onPostExecute(Integer result) {

            try {
                if (result == 200) {
                    Log.i("UpdaterLog", " data successfully sent.");
                    new ImageDownloaderClass(imageUrl).execute();
                }
            } catch (Exception e) {
                Log.i("UpdaterLog", " - transmission error:\n"
                        + e.getMessage());
            } finally {

            }
        }

}
