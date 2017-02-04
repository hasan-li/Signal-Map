package com.example.makeze.dbmeter;

/**
 * Created by makeze on 2/4/17.
 */

public class ConfigurationManager {
    private int serverUploadFreq;
    private int serverDownloadFreq;

    public ConfigurationManager(){
        this.serverUploadFreq = 180*1000; // 180 seconds
        this.serverDownloadFreq = 30*1000; // 30 seconds
    }

    public void setUploadFreq(int serverUploadFreq){
        this.serverUploadFreq = serverUploadFreq;
    }

    public int getUploadFreq(){
        return serverUploadFreq;
    }

    public void setDownloadFreq(int serverDownloadFreq){
        this.serverDownloadFreq = serverDownloadFreq;
    }

    public int getDownloadFreq(){
        return serverDownloadFreq;
    }
}
