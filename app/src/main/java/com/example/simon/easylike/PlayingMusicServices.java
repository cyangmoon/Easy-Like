package com.example.simon.easylike;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Switch;


public class PlayingMusicServices extends Service {

    private static MediaPlayer mediaPlayer;
    public static MediaPlayer getMediaPlayer() {return mediaPlayer;}
    public static Handler handlerMusic;


    /**
     * onBind，返回一个IBinder，可以与Activity交互
     * 这是Bind Service的生命周期方法
     *
     * @param intent
     * @return IBinder
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        handlerMusic = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        isStopMusicThread = true;
                        break;
                    default:
                }
            }
        };
    }

    boolean isPause = false;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (MainActivity.songs == null) return START_NOT_STICKY;
        switch (intent.getIntExtra("type", -1)) {
            case MainActivity.PLAT_MUSIC:
                if (isPause) {
                    mediaPlayer.start();
                    isPause = false;
                }
                else
                    playMusic();
                break;
            case MainActivity.PAUSE_MUSIC:
                if (mediaPlayer != null && mediaPlayer.isPlaying())
                    isPause = true;
                    mediaPlayer.pause();
                break;
            case MainActivity.PREVIOUS_MUSIC:
                // TODO: 5/16/2018
                if (MainActivity.currentPlaySongOrder > 0)
                    MainActivity.currentPlaySongOrder--;
                playMusic();
                break;
            case MainActivity.NEXT_MUSIC:
                // TODO: 5/16/2018
                if (MainActivity.currentPlaySongOrder < MainActivity.songs.size()-1)
                    MainActivity.currentPlaySongOrder++;
                playMusic();
                break;
            case MainActivity.LOOP_MUSIC:
                if (MainActivity.isLoopOne) {
                    mediaPlayer.setLooping(true);
                    MainActivity.isLoopOne = false;
                } else {
                    mediaPlayer.setLooping(false);
                    MainActivity.isLoopOne = true;
                }
                break;
            default:
        }
        return START_REDELIVER_INTENT;
    }
    public boolean isStopMusicThread=false;

    private void playMusic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(MainActivity.getInstance(), Uri.parse("http://music.163.com/song/media/outer/url?id="
                        + MainActivity.songs.get(MainActivity.currentPlaySongOrder).getId() + ".mp3"));
                MainActivity.getInstance().mHandler.sendEmptyMessage(0);
                mediaPlayer.start();
                isPause = false;
                while (!isStopMusicThread) {
                    MainActivity.getInstance().mHandler.sendEmptyMessage(1);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isStopMusicThread = false;

            }
        }).start();

    }


}