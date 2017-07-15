package com.gavin.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ClickService extends Service
{
    private MyBinder myBinder = new MyBinder();
    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d("MyService", "onBind");
        return myBinder;
    }
    @Override
    public void onCreate()
    {
        Log.d("MyService", "onCreate");
        super.onCreate();


    }

    @Override
    public void onDestroy()
    {
        Log.d("MyService", "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final String x = intent.getStringExtra("x");
        final String y = intent.getStringExtra("y");
        new Thread(new Runnable() {
            @Override
            public void run() {
                    AutoTool.execShellCmd("input tap " + x + " " +y);
                    Log.d("TEST","onclick");
            }
        }).start();
//        onDestroy();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRebind(Intent intent)
    {
        Log.d("MyService", "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        Log.d("MyService", "onStart");
        super.onStart(intent, startId);
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        Log.d("MyService", "onUnbind");

        return super.onUnbind(intent);
    }
    public class MyBinder extends Binder
    {
        ClickService getService()
        {
            return ClickService.this;
        }
    }
}