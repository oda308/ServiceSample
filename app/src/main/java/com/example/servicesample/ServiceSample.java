package com.example.servicesample;


import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class ServiceSample extends Service {

    final static String TAG = "debug";
    private TimerBinder binder = new TimerBinder();

    static long timeRunningApp = 0;

    public class TimerBinder extends Binder {
        public long getRunningApp() {
            return timeRunningApp;
        }
    }

    final Handler timerHandler = new Handler();
    final Runnable runnable = new Runnable() {
        // バックグラウンドに移行させる時間を指定(秒)
        final long timeShutdownApp = 10;
        boolean isTimeUp = false;

        public void run() {

            if (!isTimeUp) {
                timerHandler.postDelayed(this, 1000);
                timeRunningApp++;
                Log.d(TAG, "timeRunningApp : " + timeRunningApp);

                // アプリが起動してから指定の時間が経過したらアプリをバックグラウンドに移行
                if (timeRunningApp > timeShutdownApp) {
                    isTimeUp = true;
                    stopSelf();

                    // Home画面に遷移させる
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(intent);
                }
            }
        }
    };

    // 最初のbindService呼び出し時のみ実行
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return binder;
    }

    // サービスインスタンスが生成される時だけ実行
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        // シャットダウンタイマーの開始(初回のstartServiceをコールした時だけ実行する)
        timerHandler.post(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    // バインドしているクライアントが"全て"いなくなったときだけ呼ばれる
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return true;
    }

    // onUnbindが呼ばれた後に実行
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
