package com.example.servicesample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.servicesample.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "debug";
    ServiceSample.TimerBinder binder;

    private ServiceConnection conn = new ServiceConnection() {

        // Service接続に成功した際に呼ばれる
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("debug", "onServiceConnected");
            binder = (ServiceSample.TimerBinder) service;

            Log.d("debug", String.valueOf(binder.getRunningApp()));
        }

        // Service異常切断時に呼ばれる
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("debug", "onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // アプリが起動してからの時間の記録を開始する(既に開始していたら何もしない)
        Intent intent = new Intent(getBaseContext(), ServiceSample.class);
        // サービスが起動していなければ自動的にサービスを作成する
        bindService(intent, conn, Service.BIND_AUTO_CREATE);
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        // アプリが終了処理を開始したとき、bindを解除する
        unbindService(conn);
    }

    public ServiceSample.TimerBinder getBinder() {
        return binder;
    }
}