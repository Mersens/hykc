package com.tuoying.hykc.processprotection;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tuoying.hykc.R;

public class PlayerMusicService extends Service {

    private MediaPlayer mMediaPlayer;
    private boolean normalExit;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground();
        normalExit = false;
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.silent);
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(true);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand","onStartCommand");
        new Thread(new Runnable() {
            @Override
            public void run() {
                startPlayMusic();
            }
        }).start();
        return START_STICKY;
    }


    private void startPlayMusic() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.silent);

            if (mMediaPlayer != null) {
                mMediaPlayer.setLooping(true);
                Log.d("PlayerMusicService","开启后台播放音乐");
                mMediaPlayer.start();
            }
        }
    }


    private void stopPlayMusic() {
        Log.d("PlayerMusicService","关闭后台播放音乐");
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayMusic();
        // 重启
        if (!normalExit) {
            Log.e("PlayerMusicService","重新启动PlayerMusic服务！");
            Intent intent = new Intent(getApplicationContext(), PlayerMusicService.class);
            startService(intent);
        }
    }

    private void startForeground(){
        String CHANNEL_ONE_ID = "com.tuoying.hykc";
        String CHANNEL_ONE_NAME = "货运快车";
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(notificationChannel);
            startForeground(1, new NotificationCompat.Builder(this, CHANNEL_ONE_ID).build());
        }

    }
}