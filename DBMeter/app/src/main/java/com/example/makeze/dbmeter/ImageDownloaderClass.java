package com.example.makeze.dbmeter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by makeze on 12/20/16.
 */

public class ImageDownloaderClass extends AsyncTask<Void, Void, Integer> {

    String baseUrl = "http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?"; // http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?x=XXX.XXXXXX&y=YYY.YYYYYY
    String params;
    HttpURLConnection connection;
    Integer res;
    URL imgUrl;

    ImageDownloaderClass(URL imgUrl){
        this.imgUrl = imgUrl;
    }

    @Override
    protected Integer doInBackground(Void... urls) {
        try {
            //String link = baseUrl+params[0]+params[1]+params[2];
            String baseUrl = "http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?";
            String testUrl = "http://maksu.de/";

            //URL url = new URL(baseUrl+params);
            //URL url = new URL(testUrl);
            Log.i("Image Downloader: ", imgUrl.toString());
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");

            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();
            InputStream is = new BufferedInputStream(connection.getInputStream());
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            saveBmp(bitmap);
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

    private void saveBmp(Bitmap bmp){
        FileOutputStream out = null;
        try {
            String [] path = imgUrl.getFile().split("/");
            String filename = path[path.length-1];
            out = new FileOutputStream("/storage/emulated/0/DBMeter/"+filename);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
