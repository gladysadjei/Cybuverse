package com.gladys.cybuverse.Fragments;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.gladys.cybuverse.Adapters.QuickGameChatAdapter;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Models.ChatMessage;
import com.gladys.cybuverse.Models.Post;
import com.gladys.cybuverse.R;
import com.gladys.cybuverse.Utils.FileUtils.FileExplorer;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.Variable;

import java.io.ByteArrayInputStream;
import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import static android.content.Context.DOWNLOAD_SERVICE;

public class MediaPlayerDialogFragment extends DialogFragment {

    private Post post;
    private ChatMessage chatMessage;
    private View view;
    private int[] colorList;

    public MediaPlayerDialogFragment() {
    }

    public MediaPlayerDialogFragment(Post post) {
        this.post = post;
    }

    public MediaPlayerDialogFragment(ChatMessage item) {
        this.chatMessage = item;
        this.post = new Post();
        post.setMediaUri(item.getMediaUriList().get(0));
        post.setType(item.getMediaType());
    }

    public Post getItem() {
        return post;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources resources = getActivity().getResources();
        colorList = new int[]{
                resources.getColor(R.color.colorPrimaryDark),
                resources.getColor(R.color.colorAccent),
                resources.getColor(R.color.colorPrimary),
                resources.getColor(R.color.t_blue_light),
                resources.getColor(R.color.t_reddish_brown),
                resources.getColor(R.color.t_reddish_orange)
        };
        setStyle(DialogFragment.STYLE_NORMAL, R.style.VideoDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.popup_video_audio_player, null);
        initializeView();
        return view;
    }

    private void initializeView() {

        if (!(getItem().hasProperty("isNewPost") && (boolean) getItem().getProperty("isNewPost"))) {


            if (getItem().getType().equals(Post.TYPE_AUDIO) || getItem().getType().equals(Post.TYPE_VIDEO)) {
                final VideoView videoView = (VideoView) findViewById(R.id.video);

                Uri uri = Uri.parse(getItem().getMediaUri());
                if (chatMessage != null) {
                    File file = QuickGameChatAdapter.getLocalFile(chatMessage);
                    if (file.exists()) {
                        uri = getSharableUriFromUri(Uri.fromFile(file));
                    }
                }

                videoView.setVideoURI(uri);

                videoView.setKeepScreenOn(true);
                videoView.seekTo(1);
                videoView.requestFocus();

                if (getItem().getType().equals(Post.TYPE_AUDIO)) {

                    ImageView imageView = (ImageView) findViewById(R.id.image);
                    imageView.setVisibility(View.VISIBLE);
                    if (getItem().hasProperty("isNewPost") &&
                            (boolean) getItem().getProperty("isNewPost") &&
                            getItem().hasProperty("thumbnailByteArray")) {
                        //post to upload
                        Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(
                                (byte[]) getItem().getProperty("thumbnailByteArray")));
                        imageView.setImageBitmap(bitmap);
                    } else if (getItem().hasProperty("thumbnailUri")) {
                        //downloaded post
                        Glide.with(getView().getContext())
                                .load(getItem().getProperty("thumbnailUri"))
                                .into(imageView);
                    } else {
                        imageView.setBackgroundResource(R.drawable.bg_audio);
                    }

                }

                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setIndeterminate(true);

                final Variable<Boolean> isPlaying = new Variable<>(false);

                final Runnable progressColorChangerRunnable = new Runnable() {

                    int i = 0;

                    @Override
                    public void run() {
                        if (i == colorList.length)
                            i = 0;
                        progressBar.getIndeterminateDrawable().setColorFilter(colorList[i], PorterDuff.Mode.SRC_IN);
                        i++;

                        if (!isPlaying.getValue())
                            progressBar.postDelayed(this, 1000);
                    }
                };

                progressBar.postDelayed(progressColorChangerRunnable, 1000);

                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        isPlaying.setValue(false);
                        progressBar.setVisibility(View.GONE);
                        videoView.start();
                    }
                });

                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        findViewById(R.id.btn_play).setVisibility(View.VISIBLE);
                    }
                });


                findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findViewById(R.id.btn_play).setVisibility(View.GONE);
                        videoView.seekTo(1);
                        videoView.start();
                    }
                });

                findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoView.stopPlayback();
                        getDialog().cancel();
                    }
                });

            } else if (getItem().getType().equals(Post.TYPE_IMAGE)) {
                findViewById(R.id.video).setVisibility(View.GONE);
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.image_media).setVisibility(View.VISIBLE);

                findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDialog().cancel();
                    }
                });

                if (chatMessage != null) {
                    File file = QuickGameChatAdapter.getLocalFile(chatMessage);
                    if (file.exists()) {
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), file.getPath());
                        ((ImageView) findViewById(R.id.image_media)).setImageDrawable(bitmapDrawable);
                    } else {
                        Glide.with(getActivity().getApplicationContext())
                                .load(Uri.parse(post.getMediaUri()))
                                .into((ImageView) findViewById(R.id.image_media));
                    }
                }


            }

            if (this.chatMessage != null && !QuickGameChatAdapter.getLocalFile(chatMessage).exists()) {
                findViewById(R.id.btn_download).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveChatItemMedia(chatMessage);
                    }
                });
            }

        }

    }

    private Uri getSharableUriFromUri(Uri uri) {
        String path = Helper.getPathFromUri(getActivity(), uri);
        if (path == null) path = uri.getPath();
        return FileProvider.getUriForFile(getActivity().getApplicationContext(),
                getActivity().getPackageName() + ".provider", new File(path));
    }

    private void saveChatItemMedia(ChatMessage item) {
        String type = item.getMediaType();

        Object fileName = item.getProperty("mediaName");
        if (fileName == null || fileName.toString().isEmpty()) {
            fileName = QuickGameChatAdapter.getFileNameFromUrl(item.getMediaUriList().get(0));
        }

        String saveFolder = (type.equals("file")) ? "Documents" :
                (type.equals("image") || type.equals("camera")) ? "Images" :
                        (type.equals("audio")) ? "Audio" :
                                (type.equals("video")) ? "Videos" : "VoiceNotes";

        File file = new FileExplorer(Environment.getExternalStorageDirectory())
                .createNewFolder("Cybuverse").openFolder("Cybuverse")
                .createNewFolder("Media").openFolder("Media")
                .createNewFolder(saveFolder).openFolder(saveFolder)
                .getFile(fileName.toString());

        if (!file.exists()) {

            DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.getMediaUriList().get(0)));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle(fileName.toString());
            request.setDescription(item.getMediaType() + " from cybuverse");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setDestinationUri(Uri.fromFile(file));
            downloadManager.enqueue(request);
            Helper.shortToast(getContext(), "download started...");
        } else {
            Helper.shortToast(getContext(), "already saved...");
        }
    }


    @Nullable
    @Override
    public View getView() {
        return view;
    }

    private View findViewById(int id) {
        return getView().findViewById(id);
    }
}
