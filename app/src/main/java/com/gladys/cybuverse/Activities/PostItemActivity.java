package com.gladys.cybuverse.Activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.gladys.cybuverse.Adapters.LearningPostsAdapter;
import com.gladys.cybuverse.Adapters.QuickGameChatAdapter;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Models.Post;
import com.gladys.cybuverse.R;
import com.gladys.cybuverse.Utils.FileUtils.FileExplorer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

public class PostItemActivity extends AppCompatActivity {

    public TextView type, timestamp, content;
    public ImageView image;

    public View mediaContent;
    public View linkView;
    public ImageView linkImage;
    public TextView linkContent;
    public VideoView videoView;

    private Post post;
    private Uri mediaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);
        initializeViews();
    }

    private void initializeViews() {

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        post = new Post();
        post.setType(getIntent().getStringExtra("fileType"));
        post.setMediaUri(getIntent().getStringExtra("fileUri"));
        post.setLink(getIntent().getStringExtra("linkUri"));
        post.setMessage(getIntent().getStringExtra("message"));
        post.addProperty("timestamp", getIntent().getExtras().get("timestamp"));
        post.addProperty("thumbnailByteArray", getIntent().getExtras().get("thumbnailByteArray"));
        post.addProperty("thumbnailUri", getIntent().getExtras().get("thumbnailUri"));

        type = findViewById(R.id.type);
        timestamp = findViewById(R.id.timestamp);
        content = findViewById(R.id.content);
        image = findViewById(R.id.image);

        mediaContent = findViewById(R.id.media_content);
        linkView = findViewById(R.id.link_overview);
        linkImage = findViewById(R.id.link_photo);
        linkContent = findViewById(R.id.link_content);
        videoView = findViewById(R.id.video);

        type.setText(getType());
        timestamp.setText(post.getProperty("timestamp").toString());
        content.setText(post.getMessage());

        if (!post.getType().equals(Post.TYPE_NO_MEDIA)) {
            mediaContent.setVisibility(View.VISIBLE);
            mediaUri = Uri.parse(post.getMediaUri());

            if (getLocalFile().exists())
                mediaUri = Uri.fromFile(getLocalFile());
        }
        useLinkContent();

        switch (post.getType()) {
            case Post.TYPE_NO_MEDIA:
                if (post.hasProperty("thumbnailUri"))
                    Glide.with(getApplicationContext()).load(Uri.parse(post.getProperty("thumbnailUri").toString())).into(image);
                else if (post.hasProperty("thumbnailByteArray")) {
                    Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(
                            (byte[]) getItem().getProperty("thumbnailByteArray")));
                    image.setImageBitmap(bitmap);
                } else
                    image.setVisibility(View.GONE);
                break;
            case Post.TYPE_IMAGE:
                Glide.with(getApplicationContext()).load(mediaUri).into(image);
                break;
            case Post.TYPE_FILE:
                useFile();
                break;
            default:
                useVideoOrAudio();
                break;
        }

        useSave();

    }

    public Post getItem() {
        return post;
    }

    private String getType() {

        String type = "";

        if (!getItem().getType().equals(Post.TYPE_NO_MEDIA)) {
            type += String.valueOf(getItem().getType().charAt(0)).toUpperCase() +
                    getItem().getType().substring(1);
        } else {
            if (getItem().getLink() != null && !getItem().getMessage().isEmpty()) {
                type = "Feed";
            } else if (getItem().getLink() != null) {
                type = "Link";
            } else if (!getItem().getMessage().isEmpty()) {
                type = "News";
            }
        }

        return type;
    }

    private void useLinkContent() {
        if (getItem().getLink() != null) {
            linkView.setVisibility(View.VISIBLE);
            //TODO: get link photo | content
            linkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getItem().getLink()));
                    startActivity(i);
                }
            });

            linkContent.setText(getItem().getLink());
            new LearningPostsAdapter.LinkScrapper(linkContent, linkImage).execute(getItem().getLink());
        } else {
            linkView.setVisibility(View.GONE);
        }
    }

    private void useFile() {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getSharableIntent(mediaUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Helper.shortToast(getApplicationContext(), "failed to open file");
                    }
                }
            }
        });
    }

    public void useSave() {
        if (!getItem().getType().equals(Post.TYPE_NO_MEDIA) && !getLocalFile().exists()) {
            findViewById(R.id.save).setVisibility(View.VISIBLE);
            findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attemptSaveMedia();
                }
            });
        }
    }

    private void useVideoOrAudio() {

        image.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(mediaUri);
        videoView.setKeepScreenOn(true);
        videoView.seekTo(1);
        videoView.requestFocus();

        if (getItem().getType().equals(Post.TYPE_AUDIO)) {
            if (post.getProperty("thumbnailByteArray") != null) {
                image.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(
                        (byte[]) getItem().getProperty("thumbnailByteArray")));
                image.setImageBitmap(bitmap);
                videoView.setBackgroundResource(R.color.transparent);
            } else if (post.getProperty("thumbnailUri") != null) {
                image.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext())
                        .load(Uri.parse(getItem().getProperty("thumbnailUri").toString()))
                        .into(image);
                videoView.setBackgroundResource(R.color.transparent);
            } else {
                videoView.setBackgroundResource(R.drawable.bg_audio);
            }
        }

        MediaController controller = new MediaController(PostItemActivity.this);
        videoView.setMediaController(controller);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });

    }


    public Intent getSharableIntent(Uri uri) {

        Uri contentUri = uri;
        if (uri.toString().contains("file://"))
            contentUri = getSharableUriFromUri(uri);

        Intent intent = ShareCompat.IntentBuilder.from(this)
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
        String path = Helper.getPathFromUri(this, uri);
        if (path == null) path = uri.getPath();
        return FileProvider.getUriForFile(getApplicationContext(),
                getPackageName() + ".provider", new File(path));
    }

    private File getLocalFile() {
        String type = post.getType();

        String fileName = QuickGameChatAdapter.getFileNameFromUrl(post.getMediaUri());

        String saveFolder = (type.equals("file")) ? "Documents" :
                (type.equals("image") || type.equals("camera")) ? "Images" :
                        (type.equals("audio")) ? "Audio" :
                                (type.equals("video")) ? "Videos" : "VoiceNotes";

        return new FileExplorer(Environment.getExternalStorageDirectory())
                .createNewFolder("Cybuverse").openFolder("Cybuverse")
                .createNewFolder("Media").openFolder("Media")
                .createNewFolder(saveFolder).openFolder(saveFolder)
                .getFile(fileName);
    }

    private void attemptSaveMedia() {

        File file = getLocalFile();

        if (!file.exists()) {

            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(post.getMediaUri()));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle(file.getName());
            request.setDescription(post.getType() + " from cybuverse");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setDestinationUri(Uri.fromFile(file));
            downloadManager.enqueue(request);
            Helper.shortToast(getApplicationContext(), "download started...");
        } else {
            Helper.shortToast(getApplicationContext(), "already saved...");
        }
    }


}
