package com.gladys.cybuverse.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;

import com.gladys.cybuverse.Utils.FileUtils.FileExplorer;

import java.io.IOException;

public class MediaManager {

    private MediaPlayer mMediaPlayer;
    private MediaRecorder mMediaRecorder;
    private Context mContext;
    private Handler mHandler;

    private String mMediaSource;
    private String mHomePath;
    private String mExtension;

    private int recCount;

    public MediaManager(Context context) {
        mContext = context;
        recCount = getRecCount();
        mHandler = new Handler();
    }

    MediaManager(Context context, String mediaSource) {
        mContext = context;
        mMediaSource = mediaSource;
        recCount = getRecCount();
        mHandler = new Handler();
    }

    public void setAppHomePath(String path) {
        mHomePath = path;
    }

    public void setRecordedFilesName(String name) {
        mMediaSource = name;
    }

    public void setRecordedFilesName(String name, String extension) {
        mMediaSource = name;
        mExtension = extension;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mMediaPlayer) {
        this.mMediaPlayer = mMediaPlayer;
    }

    public MediaRecorder getMediaRecorder() {
        return mMediaRecorder;
    }

    public void setMediaRecorder(MediaRecorder mMediaRecorder) {
        this.mMediaRecorder = mMediaRecorder;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public String getMediaSource() {
        return mMediaSource;
    }

    public void setMediaSource(String mMediaSource) {
        this.mMediaSource = mMediaSource;
    }

    public String getHomePath() {
        return mHomePath;
    }

    public void setHomePath(String mHomePath) {
        this.mHomePath = mHomePath;
    }

    public String getExtension() {
        return mExtension;
    }

    public void setExtension(String mExtension) {
        this.mExtension = mExtension;
    }

    public String getRecOutputName() {
        return (mMediaSource == null) ? "" : mMediaSource;
    }

    public String startRecording() {

        String recordingName = buildRecSavePath(generateNextRecName());

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setOutputFile(recordingName);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recordingName;
    }

    public void stopRecording() {
        try {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        } catch (Exception e) {
            com.gladys.cybuverse.Helpers.Helper.log("MediaManager :: Error Stopping Recorder");
        }
    }

    @SuppressLint("NewApi")
    public void pauseRecording() {
        mMediaRecorder.pause();
    }

    @SuppressLint("NewApi")
    public void resumeRecording() {
        mMediaRecorder.resume();
    }

    public void play() {

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        });

        if (mMediaSource != null) {
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mMediaPlayer.start();
                    changeSeekPosition();
                }
            });
        } else {
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        }
    }

    private void changeSeekPosition() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                changeSeekPosition();
            }
        };

        try {
            if (mMediaPlayer.isPlaying()) {
                mHandler.postDelayed(runnable, 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            mMediaPlayer.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        if (mMediaPlayer == null) {
            Helper.log("mMediaPlayer is null");
        } else {
            mMediaPlayer.start();
        }
        ;
    }

    public boolean playStarted() {
        if (mMediaPlayer == null && mMediaPlayer.getCurrentPosition() > 0) {
            return true;
        }
        return false;
    }

    public MediaPlayer makeMediaPlayer(String mediaSource) {
        final MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(mediaSource);
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateNextRecName() {
        recCount++;
        setRecCount();
        return getRecOutputName() + "_" + Helper.generateRandomString(10, null) + "." + getExtension();
    }

    private void setRecCount() {
        SharedPreferences recCounterPreference = getContext().getSharedPreferences("voiceRecordsCounter", 0);
        SharedPreferences.Editor userEditor = recCounterPreference.edit();
        userEditor.putInt("counter", recCount);
        userEditor.apply();
    }

    private int getRecCount() {
        SharedPreferences recCounterPreference = getContext().getSharedPreferences("current_user", 0);
        return recCounterPreference.getInt("counter", 0);
    }

    public void setRecCount(int recCount) {
        this.recCount = recCount;
    }

    private String buildRecSavePath(String currentRecName) {
        if (mHomePath == null) {
            return new FileExplorer(Environment.getExternalStorageDirectory())
                    .createNewFolder("Cybuverse").openFolder("Cybuverse")
                    .createNewFolder("Media").openFolder("Media")
                    .createNewFolder("VoiceNotes").openFolder("VoiceNotes").getCurrentPath() + "/" + currentRecName;
        }
        return mHomePath + "/" + currentRecName;
    }

    public void stop() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

}
