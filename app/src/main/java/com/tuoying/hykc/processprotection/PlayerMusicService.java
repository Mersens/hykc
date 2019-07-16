package com.tuoying.hykc.processprotection;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
}