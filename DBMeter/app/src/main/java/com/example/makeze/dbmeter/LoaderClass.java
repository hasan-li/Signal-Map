package com.example.makeze.dbmeter;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by makeze on 12/15/16.
 */

public class LoaderClass extends AsyncTask<Void, Void, Integer>{

    String baseUrl = "http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?"; // http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?x=XXX.XXXXXX&y=YYY.YYYYYY
    String link;
    HttpURLConnection connection;
    Integer res;

    LoaderClass(String link){
        this.link = link;
    }

    @Override
    protected Integer doInBackground(Void... params) {
            try {
                //String link = baseUrl+params[0]+params[1]+params[2];
                String baseUrl = "http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?";

                URL url = new URL(baseUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();

                res = connection.getResponseCode();
                Log.i("chat", "+ ChatActivity - ответ сервера (200 - все ОК): "
                        + res.toString());

            } catch (Exception e) {
                Log.i("chat",
                        "+ ChatActivity - ошибка соединения: " + e.getMessage());

            } finally {
                connection.disconnect();
            }
            return res;
        }

        protected void onPostExecute(Integer result) {

            try {
                if (result == 200) {
                    Log.i("chat", "+ ChatActivity - сообщение успешно ушло.");
                }
            } catch (Exception e) {
                Log.i("chat", "+ ChatActivity - ошибка передачи сообщения:\n"
                        + e.getMessage());
            } finally {

            }
        }

}
