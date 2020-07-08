package com.gladys.cybuverse.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.MediaManager;
import com.gladys.cybuverse.R;
import com.gladys.cybuverse.Utils.FileUtils.FileExplorer;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.Variable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionManager;

import static android.app.Activity.RESULT_OK;

public class MediaInputManagerFragment extends Fragment {

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_VOICE = "voice";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_CAMERA = "camera";
    public static final String TYPE_FILE = "file";
    private final int CAMERA_PICTURE = 10;
    private final int VIDEO_GALLERY_PICK = 20;
    private final int IMAGE_GALLERY_PICK = 40;
    private final int AUDIO_GALLERY_PICK = 80;
    private final int FILE_GALLERY_PICK = 120;
    private final int CAMERA_PERMISSIONS = 130;
    private final int VIDEO_GALLERY_PICK_PERMISSIONS = 140;
    private final int IMAGE_GALLERY_PICK_PERMISSIONS = 160;
    private final int AUDIO_GALLERY_PICK_PERMISSIONS = 180;
    private final int FILE_GALLERY_PICK_PERMISSIONS = 200;
    Handler mHandler = new Handler();
    private View view;
    private View mediaTypeSelector;
    private View voiceRecorderView;
    private View selectedMediaPreview;
    private Uri mediaUri;
    private String mediaType;
    private String mediaName;
    private String recordingPath;
    private Bitmap mediaBitmap;
    private String recordingState;
    private OnVisibilityChangeListener defaultVisibilityChangeListener;
    private MediaManager mediaManager;
    private MediaPlayer recordedMediaPlayer;
    private MediaController controller;


    public MediaInputManagerFragment() {
    }

    public MediaInputManagerFragment(OnVisibilityChangeListener defaultVisibilityChangeListener) {
        this.defaultVisibilityChangeListener = defaultVisibilityChangeListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_media_input_manager, container, false);
        initializeView();
        return view;
    }

    public Uri getMediaUri() {
        return mediaUri;
    }

    public void setMediaUri(Uri mediaUri) {
        this.mediaUri = mediaUri;
    }

    public Bitmap getMediaBitmap() {
        return mediaBitmap;
    }

    public void setMediaBitmap(Bitmap mediaBitmap) {
        this.mediaBitmap = mediaBitmap;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public OnVisibilityChangeListener getDefaultVisibilityChangeListener() {
        return defaultVisibilityChangeListener;
    }

    public void setDefaultVisibilityChangeListener(OnVisibilityChangeListener onDefaultVisibilityChangeListener) {
        this.defaultVisibilityChangeListener = onDefaultVisibilityChangeListener;
    }

    public boolean hasMedia() {
        return getMediaType() != null;
    }

    public boolean isMediaInputVisible() {
        return getView().getVisibility() == View.VISIBLE;
    }

    private void setMediaMessage(Uri uri, String mediaName, String mediaType) {
        setMediaUri(uri);
        setMediaName(mediaName);
        setMediaType(mediaType);
        setupMediaPreview();
        showSelectedMediaPreview();
    }

    private void setMediaMessage(Bitmap bitmap, String mediaName) {
        setMediaBitmap(bitmap);
        setMediaName(mediaName);
        setMediaType(TYPE_CAMERA);
        setupMediaPreview();
        showSelectedMediaPreview();
    }

    private void setMediaMessage(File file) {
        setMediaUri(Uri.fromFile(file));
        setMediaName(file.getName());
        setMediaType(TYPE_VOICE);
    }

    public void reset() {
        setMediaType(null);
        setMediaName(null);
        setMediaUri(null);
        resetRecorder();
        resetVideoPlayer();
        showMediaTypeSelector();
    }

    public void show(Animation animation, final OnVisibilityChangeListener listener) {

        if (!isMediaInputVisible()) {

            if (animation == null) {
                animation = new TranslateAnimation(0, 0, 200, 0);
                animation.setInterpolator(new LinearInterpolator());
                animation.setDuration(200);
            }

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (hasMedia()) {
                        if (mediaType.equals(TYPE_VOICE)) {
                            showVoiceRecorderView();
                        } else {
                            showSelectedMediaPreview();
                        }
                    }

                    if (defaultVisibilityChangeListener != null)
                        defaultVisibilityChangeListener.onStart(isMediaInputVisible(), MediaInputManagerFragment.this);
                    if (listener != null)
                        listener.onStart(isMediaInputVisible(), MediaInputManagerFragment.this);
                    getView().setVisibility(View.VISIBLE);

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (defaultVisibilityChangeListener != null)
                        defaultVisibilityChangeListener.onEnd(isMediaInputVisible(), MediaInputManagerFragment.this);
                    if (listener != null)
                        listener.onEnd(isMediaInputVisible(), MediaInputManagerFragment.this);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            getView().startAnimation(animation);
        }
    }

    public void hide(Animation animation, final OnVisibilityChangeListener listener) {

        if (isMediaInputVisible()) {
            if (animation == null) {
                animation = new TranslateAnimation(0, 0, 0, 600);
                animation.setInterpolator(new LinearInterpolator());
                animation.setDuration(200);
            }

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    VideoView videoView = (VideoView) findViewById(R.id.streamPreview);
                    if (controller.isShowing()) {
                        controller.hide();
                    }
                    if (videoView.isPlaying()) {
                        videoView.pause();
                    }
                    if (recordedMediaPlayer != null && recordedMediaPlayer.isPlaying()) {
                        findViewById(R.id.recorderButtonMain).callOnClick();
                    }
                    if (defaultVisibilityChangeListener != null)
                        defaultVisibilityChangeListener.onStart(isMediaInputVisible(), MediaInputManagerFragment.this);
                    if (listener != null)
                        listener.onStart(isMediaInputVisible(), MediaInputManagerFragment.this);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    getView().setVisibility(View.GONE);
                    if (defaultVisibilityChangeListener != null)
                        defaultVisibilityChangeListener.onEnd(isMediaInputVisible(), MediaInputManagerFragment.this);
                    if (listener != null)
                        listener.onEnd(isMediaInputVisible(), MediaInputManagerFragment.this);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            getView().startAnimation(animation);
        }
    }

    public void show(final OnVisibilityChangeListener listener) {
        show(null, listener);
    }

    public void hide(final OnVisibilityChangeListener listener) {
        hide(null, listener);
    }

    public void show(Animation animation) {
        show(animation, null);
    }

    public void hide(Animation animation) {
        hide(animation, null);
    }

    public void show() {
        show(null, null);
    }

    public void hide() {
        hide(null, null);
    }

    public void showMediaTypeSelector() {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        mediaTypeSelector.setVisibility(View.VISIBLE);
        if (selectedMediaPreview.getVisibility() == View.VISIBLE)
            selectedMediaPreview.setVisibility(View.GONE);
        if (voiceRecorderView.getVisibility() == View.VISIBLE)
            voiceRecorderView.setVisibility(View.GONE);
    }

    public void showSelectedMediaPreview() {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        selectedMediaPreview.setVisibility(View.VISIBLE);
        if (voiceRecorderView.getVisibility() == View.VISIBLE)
            voiceRecorderView.setVisibility(View.GONE);
        if (mediaTypeSelector.getVisibility() == View.VISIBLE)
            mediaTypeSelector.setVisibility(View.GONE);
    }

    public void showVoiceRecorderView() {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        voiceRecorderView.setVisibility(View.VISIBLE);
        if (selectedMediaPreview.getVisibility() == View.VISIBLE)
            selectedMediaPreview.setVisibility(View.GONE);
        if (mediaTypeSelector.getVisibility() == View.VISIBLE)
            mediaTypeSelector.setVisibility(View.GONE);
    }

    private void setupMediaPreview() {

        TextView textView = (TextView) findViewById(R.id.mediaName);
        VideoView videoView = (VideoView) findViewById(R.id.streamPreview);
        ImageView imageView = (ImageView) findViewById(R.id.imagePreview);
        View imageHolder = findViewById(R.id.image_holder);
        View videoHolder = findViewById(R.id.video_holder);

        textView.setText(mediaName);

        switch (getMediaType()) {
            case TYPE_IMAGE:
                imageHolder.setVisibility(View.VISIBLE);
                videoHolder.setVisibility(View.GONE);
                imageView.setBackground(new ColorDrawable(Color.BLACK));
                imageView.setImageURI(getMediaUri());
                break;
            case "camera":
                imageHolder.setVisibility(View.VISIBLE);
                videoHolder.setVisibility(View.GONE);
                imageView.setImageBitmap(getMediaBitmap());
                break;
            case TYPE_VIDEO:
                videoHolder.setVisibility(View.VISIBLE);
                imageHolder.setVisibility(View.GONE);
                videoView.setVideoURI(getMediaUri());
                videoView.seekTo(1);
                videoView.setKeepScreenOn(true);
                videoView.setMediaController(controller);
                controller.show(1000);
                break;
            case TYPE_AUDIO:
                videoHolder.setVisibility(View.VISIBLE);
                imageHolder.setVisibility(View.GONE);
                videoView.setBackgroundResource(R.drawable.bg_audio);
                videoView.setVideoURI(getMediaUri());
                videoView.setKeepScreenOn(true);
                videoView.setMediaController(controller);
                controller.show(1000);
                break;
            case TYPE_FILE:
                videoHolder.setVisibility(View.GONE);
                imageHolder.setVisibility(View.VISIBLE);
                imageView.setBackground(null);
                imageView.setImageResource(R.drawable.bg_document);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getSharableIntent(getMediaUri());
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            try {
                                getActivity().startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Helper.shortToast(getActivity().getApplicationContext(), "failed to open file");
                            }
                        }
                    }
                });
                break;
        }
    }

    private void setupVoiceRecorder() {
        final ImageButton buttonMain = (ImageButton) findViewById(R.id.recorderButtonMain);
        final ImageButton buttonSec = (ImageButton) findViewById(R.id.recorderButtonSec);
        TextView textView = (TextView) findViewById(R.id.counter);
        buttonMain.setImageResource(R.drawable.ic_fiber_manual_record);
        buttonSec.setVisibility(View.GONE);
        textView.setText("00:00");

        buttonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 0);
                } else {
                    TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.recorderButtons));
                    startRecorder();
                    usePause();
                    useStop();
                }
            }

            private void useStop() {
                buttonSec.setImageResource(R.drawable.ic_stop);
                buttonSec.setVisibility(View.VISIBLE);
                buttonSec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopRecorder();
                        usePlay();
                        useRecordAgain();
                    }
                });
            }

            private void useRecordAgain() {
                buttonSec.setImageResource(R.drawable.ic_record_voice_over);
                buttonSec.setVisibility(View.VISIBLE);
                buttonSec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetRecorder();
                        setupVoiceRecorder();
                    }
                });
            }

            private void usePlay() {
                buttonMain.setImageResource(R.drawable.ic_play_arrow);
                buttonMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recordedMediaPlayer != null) {
                            recordedMediaPlayer.start();
                        } else {
                            recordedMediaPlayer = mediaManager.makeMediaPlayer(getRecording().getPath());
                            recordedMediaPlayer.start();
                            recordedMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    recordedMediaPlayer.stop();
                                    recordedMediaPlayer.release();
                                    recordedMediaPlayer = null;
                                    usePlay();
                                }
                            });
                        }
                        useStopPlay();
                    }
                });
            }

            private void useStopPlay() {
                buttonMain.setImageResource(R.drawable.ic_pause);
                buttonMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recordedMediaPlayer != null)
                            recordedMediaPlayer.pause();
                        usePlay();
                    }
                });
            }

            private void useResume() {
                buttonMain.setImageResource(R.drawable.ic_play_arrow);
                buttonMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resumeRecorder();
                        usePause();
                    }
                });
            }

            private void usePause() {
                buttonMain.setImageResource(R.drawable.ic_pause);
                buttonMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pauseRecorder();
                        useResume();
                    }
                });
            }
        });


        buttonMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startRecorder();
                buttonMain.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            stopRecorder();
                            useStop();
                            buttonMain.setOnTouchListener(null);
                        }
                        return false;
                    }
                });
                return false;
            }

            private void useStop() {
                usePlay();
                useRecordAgain();
            }

            private void usePlay() {
                buttonMain.setImageResource(R.drawable.ic_play_arrow);
                buttonMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recordedMediaPlayer != null) {
                            recordedMediaPlayer.start();
                        } else {
                            recordedMediaPlayer = mediaManager.makeMediaPlayer(getRecording().getPath());
                            recordedMediaPlayer.start();
                            recordedMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    recordedMediaPlayer.stop();
                                    recordedMediaPlayer.release();
                                    recordedMediaPlayer = null;
                                    usePlay();
                                }
                            });
                        }
                        useStopPlay();
                    }
                });
            }

            private void useRecordAgain() {
                buttonSec.setImageResource(R.drawable.ic_record_voice_over);
                buttonSec.setVisibility(View.VISIBLE);
                buttonSec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetRecorder();
                        setupVoiceRecorder();
                    }
                });
            }

            private void useStopPlay() {
                buttonMain.setImageResource(R.drawable.ic_pause);
                buttonMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recordedMediaPlayer != null)
                            recordedMediaPlayer.pause();
                        usePlay();
                    }
                });
            }

        });
    }

    private void resetRecorder() {
        final TextView textView = (TextView) findViewById(R.id.counter);
        textView.setText("00:00");
        if (recordedMediaPlayer != null && recordedMediaPlayer.isPlaying()) {
            recordedMediaPlayer.stop();
            recordedMediaPlayer.release();
            recordedMediaPlayer = null;
        }
        if (recordingState != null && (recordingState.equals("started") || recordingState.equals("resumed"))) {
            recordingState = "stopped";
            mediaManager.stopRecording();
        }
        recordingPath = null;
    }

    private void resetVideoPlayer() {
        VideoView videoView = (VideoView) findViewById(R.id.streamPreview);
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        if (controller.isShowing()) {
            controller.hide();
        }
    }

    private void startRecorder() {
        recordingState = "started";

        recordingPath = mediaManager.startRecording();

        final Variable<Integer> tick = new Variable<>(0);

        Runnable counter = new Runnable() {

            private String getTimeString() {
                int seconds = tick.getValue() % 60;
                int minutes = (tick.getValue() - seconds) / 60;
                return ((minutes > 0) ? ((String.valueOf(minutes).length() == 1) ? "0" + minutes : minutes) : "00")
                        + ":" +
                        ((String.valueOf(seconds).length() == 1) ? ("0" + seconds) : seconds);
            }

            @Override
            public void run() {
                if (recordingState.equals("paused")) {
                    mHandler.postDelayed(this, 1000);
                } else if (recordingState.equals("started") || recordingState.equals("resumed")) {
                    TextView textView = (TextView) findViewById(R.id.counter);
                    textView.setText(getTimeString());
                    tick.setValue(tick.getValue() + 1);
                    mHandler.postDelayed(this, 1000);
                }
            }

        };

        mHandler.postDelayed(counter, 1000);
    }

    private void stopRecorder() {
        recordingState = "stopped";
        mediaManager.stopRecording();
        File file = getRecording();
        setMediaMessage(file);
    }

    private void pauseRecorder() {
        recordingState = "paused";
        mediaManager.pauseRecording();
    }

    private void resumeRecorder() {
        recordingState = "resumed";
        mediaManager.resumeRecording();
    }

    private File getRecording() {
        return new File(recordingPath);
    }

    private void initializeView() {

        mediaTypeSelector = findViewById(R.id.mediaTypeSelector);
        selectedMediaPreview = findViewById(R.id.selectedMediaPreview);
        voiceRecorderView = findViewById(R.id.voiceRecorderView);

        mediaManager = new MediaManager(getActivity());
        mediaManager.setAppHomePath(new FileExplorer(Environment.getExternalStorageDirectory())
                .createNewFolder("Cybuverse").openFolder("Cybuverse")
                .createNewFolder("Media").openFolder("Media")
                .createNewFolder("VoiceNotes").openFolder("VoiceNotes")
                .getCurrentPath());
        mediaManager.setRecordedFilesName("voice_note_", "cvn");
        controller = new MediaController(getActivity());

        setupVoiceRecorder();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.MANAGE_DOCUMENTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.MANAGE_DOCUMENTS}, 0);
        }

        findViewById(R.id.keyboard_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_GALLERY_PICK_PERMISSIONS);
                } else {
                    openGalleryPicker(TYPE_IMAGE);
                }
            }
        });

        findViewById(R.id.keyboard_camera_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS);
                } else {
                    openCamera();
                }
            }
        });

        findViewById(R.id.keyboard_video_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, VIDEO_GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker(TYPE_VIDEO);
                    }
                } else {
                    openGalleryPicker(TYPE_VIDEO);
                }
            }
        });

        findViewById(R.id.keyboard_voice_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVoiceRecorderView();
            }
        });

        findViewById(R.id.keyboard_audio_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, AUDIO_GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker(TYPE_AUDIO);
                    }
                } else {
                    openGalleryPicker(TYPE_AUDIO);
                }

            }
        });

        findViewById(R.id.keyboard_document_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker("*");
                    }
                } else {
                    openGalleryPicker("*");
                }

            }
        });

        findViewById(R.id.closeRecorder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                setupVoiceRecorder();
            }
        });

        findViewById(R.id.closePreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

    }

    private View findViewById(int id) {
        return getView().findViewById(id);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PICTURE);
    }

    private void openGalleryPicker(String type) {
        Intent galleryIntent = new Intent();
        galleryIntent.setType(type + "/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT " + type.toUpperCase()),
                (type.equals(TYPE_VIDEO)) ? VIDEO_GALLERY_PICK :
                        (type.equals(TYPE_IMAGE)) ? IMAGE_GALLERY_PICK :
                                (type.equals(TYPE_AUDIO)) ? AUDIO_GALLERY_PICK :
                                        FILE_GALLERY_PICK);
    }

    private Intent getSharableIntent(Uri uri) {

        Uri contentUri = uri;
        if (uri.toString().contains("file://"))
            contentUri = getSharableUriFromUri(uri);

        Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                .setStream(contentUri) // uri from FileProvider
                .getIntent()
                .setAction(Intent.ACTION_VIEW); //Change if needed

        if (contentUri.toString().contains(".doc") || contentUri.toString().contains(".docx")) {
            intent.setDataAndType(contentUri, "application/msword");
        } else if (contentUri.toString().contains(".pdf")) {
            intent.setDataAndType(contentUri, "application/pdf");
        } else if (contentUri.toString().contains(".ppt") || contentUri.toString().contains(".pptx")) {
            intent.setDataAndType(contentUri, "application/vnd.ms-powerpoint");
        } else if (contentUri.toString().contains(".xls") || contentUri.toString().contains(".xlsx")) {
            intent.setDataAndType(contentUri, "application/vnd.ms-excel");
        } else if (contentUri.toString().contains(".zip") || contentUri.toString().contains(".rar")) {
            intent.setDataAndType(contentUri, "application/zip");
        } else if (contentUri.toString().contains(".rtf")) {
            intent.setDataAndType(contentUri, "application/rtf");
        } else if (contentUri.toString().contains(".wav") || contentUri.toString().contains(".mp3")) {
            intent.setDataAndType(contentUri, "audio/x-wav");
        } else if (contentUri.toString().contains(".gif")) {
            intent.setDataAndType(contentUri, "image/gif");
        } else if (contentUri.toString().contains(".jpg") || contentUri.toString().contains(".jpeg") || contentUri.toString().contains(".png")) {
            intent.setDataAndType(contentUri, "image/jpeg");
        } else if (contentUri.toString().contains(".txt")) {
            intent.setDataAndType(contentUri, "text/plain");
        } else if (contentUri.toString().contains(".3gp") || contentUri.toString().contains(".mpg") || contentUri.toString().contains(".mpeg") || contentUri.toString().contains(".mpe") || contentUri.toString().contains(".mp4") || contentUri.toString().contains(".avi")) {
            intent.setDataAndType(contentUri, "video/*");
        } else {
            intent.setDataAndType(contentUri, "*/*");
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    private Uri getSharableUriFromUri(Uri uri) {
        String path = Helper.getPathFromUri(getActivity(), uri);
        if (path == null) path = uri.getPath();
        return FileProvider.getUriForFile(getActivity().getApplicationContext(),
                getActivity().getPackageName() + ".provider", new File(path));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_PICTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            setMediaMessage(bitmap, "IMAGE_" + getCurrentDateString() + ".jpg");
        }

        if (requestCode == IMAGE_GALLERY_PICK && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            setMediaMessage(uri, "IMAGE_" + getCurrentDateString() + ".jpg", TYPE_IMAGE);
        }

        if (requestCode == VIDEO_GALLERY_PICK && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            setMediaMessage(uri, "VIDEO_" + getCurrentDateString() + ".mp4", TYPE_VIDEO);
        }

        if (requestCode == AUDIO_GALLERY_PICK && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            setMediaMessage(uri, "AUDIO_" + getCurrentDateString() + ".mp3", TYPE_AUDIO);
        }

        if (requestCode == FILE_GALLERY_PICK && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String mimetype = Helper.getMimeType(getActivity(), data.getData());
            if (mimetype.contains(TYPE_IMAGE))
                onActivityResult(IMAGE_GALLERY_PICK, RESULT_OK, data);
            else if (mimetype.contains(TYPE_VIDEO))
                onActivityResult(VIDEO_GALLERY_PICK, RESULT_OK, data);
            else if (mimetype.contains(TYPE_AUDIO))
                onActivityResult(AUDIO_GALLERY_PICK, RESULT_OK, data);
            else {
                File file = new File(uri.getPath());
                setMediaMessage(uri, file.getName(), TYPE_FILE);
            }
        }

    }

    private String getCurrentDateString() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Helper.longToast(getContext(), "Camera permissions denied!");
            }
        }

        if (requestCode == VIDEO_GALLERY_PICK_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryPicker(TYPE_VIDEO);
            } else {
                Helper.longToast(getContext(), "Gallery permissions denied!");
            }
        }
        if (requestCode == IMAGE_GALLERY_PICK_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryPicker(TYPE_IMAGE);
            } else {
                Helper.longToast(getContext(), "Gallery permissions denied!");
            }
        }
        if (requestCode == AUDIO_GALLERY_PICK_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryPicker(TYPE_AUDIO);
            } else {
                Helper.longToast(getContext(), "Gallery permissions denied!");
            }
        }
        if (requestCode == FILE_GALLERY_PICK_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryPicker("*");
            } else {
                Helper.longToast(getContext(), "Gallery permissions denied!");
            }
        }

    }

    public interface OnVisibilityChangeListener {
        void onStart(boolean isVisible, MediaInputManagerFragment fragment);

        void onEnd(boolean isVisible, MediaInputManagerFragment fragment);
    }

}
