package com.example.makeze.dbmeter;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by makeze on 12/15/16.
 */

public class UploaderClass extends AsyncTask<Void, Void, Integer>{

    String baseUrl = "http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?"; // http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?x=XXX.XXXXXX&y=YYY.YYYYYY
    String params;
    HttpURLConnection connection;
    Integer res;

    UploaderClass(String params){
        this.params = params;
    }

    @Override
    protected Integer doInBackground(Void... urls) {
            try {
                //String link = baseUrl+params[0]+params[1]+params[2];
                String baseUrl = "http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?";


                URL url = new URL(baseUrl+params);
                Log.i("chat", baseUrl+params);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();

                res = connection.getResponseCode();
                Log.i("UpdaterLog", "+ server response (200 - everything ok): "
                        + res.toString());

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
                }
            } catch (Exception e) {
                Log.i("UpdaterLog", " - transmission error:\n"
                        + e.getMessage());
            } finally {

            }
        }

}
