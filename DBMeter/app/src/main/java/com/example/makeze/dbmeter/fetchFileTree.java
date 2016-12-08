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

public class fetchFileTree extends AppCompatActivity {
    private int READ_EXTERNAL_STORAGE_CODE = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_file_tree);
        File dir = new File("/storage/emulated/0/DCIM"); // create your own directory name and read it instead
        showTreePerm(dir);
    }

    public void showTree(File dir){
        File files[] = dir.listFiles();

        for(File f : files){
            System.out.println("FILENAMES: "+f.getPath());
        }
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
