package com.gladys.cybuverse.Helpers;

import android.media.MediaPlayer;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public abstract class PlaySoundOnClickListener implements View.OnClickListener {

    public final static int FLAG_STOP_AND_PLAY = 0;
    public final static int FLAG_PLAY_FULL_ADD_NEW = 1;
    public final static int FLAG_PLAY_FULL_NO_NEW = 2;
    private static Map<View, MediaPlayer> viewMediaPlayerMap;
    private int playFlag;
    private int playDelay;
    private int soundResource;

    public PlaySoundOnClickListener(int soundResource) {
        this.playDelay = -1;
        this.soundResource = soundResource;
        this.playFlag = FLAG_STOP_AND_PLAY;
        if (viewMediaPlayerMap == null) {
            viewMediaPlayerMap = new HashMap<>();
        }
    }

    public PlaySoundOnClickListener(int soundResource, int flag) {
        this.playDelay = -1;
        this.playFlag = flag;
        this.soundResource = soundResource;
        if (viewMediaPlayerMap == null) {
            viewMediaPlayerMap = new HashMap<>();
        }
    }

    public PlaySoundOnClickListener(int soundResource, int flag, int delay) {
        this.soundResource = soundResource;
        this.playFlag = flag;
        this.playDelay = delay;
        if (viewMediaPlayerMap == null) {
            viewMediaPlayerMap = new HashMap<>();
        }
    }

    public int getPlayFlag() {
        return playFlag;
    }

    public void setPlayFlag(int playFlag) {
        this.playFlag = playFlag;
    }

    public int getSoundResource() {
        return soundResource;
    }

    public void setSoundResource(int soundResource) {
        this.soundResource = soundResource;
    }

    @Override
    public void onClick(final View v) {

        if (isProceedPlaySound(v)) {
            if (playDelay > 0) {
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playSound(v);
                    }
                }, playDelay);
            } else {
                playSound(v);
            }
        }

        runOnClick(v);
    }

    private void playSound(final View v) {
        switch (playFlag) {
            case FLAG_STOP_AND_PLAY:
                MediaPlayer player1;
                if (viewMediaPlayerMap.containsKey(v)) {
                    player1 = viewMediaPlayerMap.get(v);
                    if (player1 != null && player1.isPlaying()) {
                        player1.reset();
                        player1.release();
                    }
                }
                player1 = MediaPlayer.create(v.getContext(), soundResource);
                player1.start();
                player1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        mp.reset();
                        mp.release();
                        viewMediaPlayerMap.put(v, null);
                    }
                });
                viewMediaPlayerMap.put(v, player1);
                break;
            case FLAG_PLAY_FULL_NO_NEW:
                MediaPlayer player2 = null;
                if (viewMediaPlayerMap.containsKey(v)) {
                    player2 = viewMediaPlayerMap.get(v);
                }
                if (player2 == null || !player2.isPlaying()) {
                    player2 = MediaPlayer.create(v.getContext(), soundResource);
                    player2.start();
                    player2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.reset();
                            mp.release();
                            viewMediaPlayerMap.put(v, null);
                        }
                    });
                }
                break;
            case FLAG_PLAY_FULL_ADD_NEW:
                MediaPlayer player3 = MediaPlayer.create(v.getContext(), soundResource);
                player3.start();
                player3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        mp.reset();
                        mp.release();
                    }
                });
                break;
        }
    }

    public boolean isProceedPlaySound(View v) {
        return ((TechupApplication) v.getContext().getApplicationContext()).getUsesSoundPreference();
    }

    public abstract void runOnClick(View v);
}