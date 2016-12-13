package com.example.makeze.dbmeter;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class UpdateServerService extends Service {

    private final IBinder binder = new UpdateServerBinder();
    String baseUrl = "http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?"; // http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?x=-XXX.XXXXXX&y=YYY.YYYYYY

    public UpdateServerService() {
    }

    public class UpdateServerBinder extends Binder {
        UpdateServerService updateTrigger() {
            return UpdateServerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate(){

    }

    public void updateServer(){
 
    }
}
