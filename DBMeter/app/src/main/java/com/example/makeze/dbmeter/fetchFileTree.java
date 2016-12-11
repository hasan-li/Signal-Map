package com.example.makeze.dbmeter;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class fetchFileTree extends AppCompatActivity {

    private int READ_EXTERNAL_STORAGE_CODE = 5;
    LocationCoordinates locationCoordinates;
    double latitude;
    double longitude;
    String[] fileNameExtracted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_file_tree);

        locationCoordinates = new LocationCoordinates(fetchFileTree.this);
        latitude = locationCoordinates.getLatitude();
        longitude = locationCoordinates.getLongitude();


        //File dir = new File("/storage/emulated/0/DBMeter/"); // create your own directory name and read it instead
        File dir = new File(Environment.getExternalStorageDirectory()+"/DBMeter");

        // have the object build the directory structure, if needed.
        if(!dir.exists()) {
           //directory is created
            dir.mkdirs();
        }

        showTreePerm(dir);
        String path = dir.getAbsolutePath(); // get absolute path
        //System.out.println("PATH: "+path);
    }

    public void showTree(File dir){
        File files[] = dir.listFiles();

        for(File f : files){
            String filePath= f.getPath();
            System.out.println("FILENAMES: "+f.getPath());

            String filename=filePath.substring(filePath.lastIndexOf("/")+1);
            System.out.println("filename: "+filename);

            Pattern p = Pattern.compile("(.*?).png");
            Matcher m = p.matcher(filename);

            while (m.find()) {

                //System.out.println("FILE "+m.group(1));
                String tempName = m.group(1);
                fileNameExtracted = tempName.split("_");
                System.out.println("fileNameExtracted: "+ Arrays.toString(fileNameExtracted));
            }

            if(latitude >= Double.parseDouble(fileNameExtracted[0]) && latitude <= Double.parseDouble(fileNameExtracted[2])
                    && longitude >= Double.parseDouble(fileNameExtracted[1]) && longitude <= Double.parseDouble(fileNameExtracted[3])){

                    System.out.println("OVERLAY FOUND FOR IMAGE "+ Arrays.toString(fileNameExtracted) + ". DRAW THIS IMAGE");

            }
            else{
                System.out.println("OVERLAY NOT FOUND FOR IMAGE: "+ Arrays.toString(fileNameExtracted));

            }

        }
    }

    public void downloadImage(){

    }

    public void showTreePerm(File dir){
        Log.i("PERMISSION LOG", "Requesting telephony permissions.");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, READ_EXTERNAL_STORAGE_CODE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, true);
        } else {
            Log.i("PERMISSION LOG", "Readstuff permission been granted.");
            showTree(dir);

        }
    }
}
