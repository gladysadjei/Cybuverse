package com.gladys.cybuverse.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Models.Post;
import com.gladys.cybuverse.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


public class AddLearningMaterialActivity extends AppCompatActivity {

    public static final int THUMBNAIL_REQUEST_CODE = 210;
    public static final int ATTACHMENT_REQUEST_CODE = 300;
    private static final int VIDEO_GALLERY_PICK = 20;
    private static final int AUDIO_GALLERY_PICK = 40;
    private static final int IMAGE_GALLERY_PICK = 60;
    private static final int FILE_GALLERY_PICK = 80;
    private static final int LINK_GALLERY_PICK = 100;
    private static final int VIDEO_GALLERY_PICK_PERMISSIONS = 140;
    private static final int AUDIO_GALLERY_PICK_PERMISSIONS = 160;
    private Uri fileUri, linkUri;
    private String fileType;
    private Bitmap thumbnailBitmap;
    private boolean isThumbnailImage;


    private VideoView videoView;
    private ImageView imageView, thumbnailImage;
    private EditText postMessageInput, linkUrlInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_learning_material);

        fileType = Post.TYPE_NO_MEDIA;

        isThumbnailImage = false;

        videoView = findViewById(R.id.video);
        imageView = findViewById(R.id.image);
        thumbnailImage = findViewById(R.id.thumbnail_image);

        postMessageInput = findViewById(R.id.post_message);
        linkUrlInput = findViewById(R.id.link_uri);

        MediaController controller = new MediaController(AddLearningMaterialActivity.this);
//        controller.setMediaPlayer(videoView);
        videoView.setMediaController(controller);

        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.add_attachment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (videoView.isPlaying()) {
                    videoView.pause();
                    videoView.stopPlayback();
                }

                startActivityForResult(
                        new Intent(AddLearningMaterialActivity.this, AddAttachmentActivity.class),
                        ATTACHMENT_REQUEST_CODE);
            }
        });

        findViewById(R.id.add_link).setOnClickListener(new View.OnClickListener() {

            boolean open = false;

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                if (open) {
                    findViewById(R.id.link_view).setVisibility(View.GONE);
                    findViewById(R.id.link_overview).setVisibility(View.GONE);
                    ((ImageButton) v).setImageResource(R.drawable.ic_link_white_24dp);
                    linkUri = null;
                    linkUrlInput.setText("");
                    imm.hideSoftInputFromWindow(linkUrlInput.getWindowToken(), 0);
                    open = false;
                } else {
                    findViewById(R.id.link_view).setVisibility(View.VISIBLE);
                    ((ImageButton) v).setImageResource(R.drawable.ic_close_white_24dp);
                    linkUrlInput.requestFocus();
                    linkUrlInput.setFocusableInTouchMode(true);
                    imm.showSoftInput(linkUrlInput, InputMethodManager.SHOW_IMPLICIT);
                    open = true;
                }
            }
        });

        findViewById(R.id.add_post_thumbnail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(AddLearningMaterialActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddLearningMaterialActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, AUDIO_GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker("thumbnail-image");
                    }
                } else {
                    openGalleryPicker("thumbnail-image");
                }
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost();
            }
        });

        findViewById(R.id.remove_media).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUri = null;
                thumbnailBitmap = null;
                thumbnailImage.setVisibility(View.GONE);
                if (fileType.equals("video")) {
                    videoView.pause();
                    videoView.stopPlayback();
                }
                fileType = Post.TYPE_NO_MEDIA;
                findViewById(R.id.media_holder).setVisibility(View.INVISIBLE);
                refreshSaveButtonState();
            }
        });

        findViewById(R.id.hint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                postMessageInput.setVisibility(View.VISIBLE);
                postMessageInput.requestFocus();
                postMessageInput.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(postMessageInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        postMessageInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (postMessageInput.getText().toString().trim().isEmpty()) {
                        findViewById(R.id.hint).setVisibility(View.VISIBLE);
                        postMessageInput.setVisibility(View.GONE);
                        postMessageInput.clearFocus();
                    }
                }
            }
        });


        findViewById(R.id.add_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start gallery item picker
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(AddLearningMaterialActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddLearningMaterialActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, AUDIO_GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker("audio");
                    }
                } else {
                    openGalleryPicker("audio");
                }
            }
        });

        findViewById(R.id.add_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start gallery item picker
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(AddLearningMaterialActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddLearningMaterialActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, VIDEO_GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker("video");
                    }
                } else {
                    openGalleryPicker("video");
                }
            }
        });

        findViewById(R.id.link_overview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linkUri != null) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(linkUri);
                    startActivity(i);
                }
            }
        });

        postMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    ((FloatingActionButton) findViewById(R.id.save)).setImageResource(R.drawable.ic_check_white_24dp);
                } else {
                    refreshSaveButtonState();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        linkUrlInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findViewById(R.id.link_overview).setVisibility(View.GONE);

                refreshSaveButtonState();

                if (!s.toString().trim().isEmpty()) {
                    new LinkScrapper().execute(s.toString().trim());
                } else {
                    linkUri = null;
                    Helper.log("invalid url: null");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void refreshSaveButtonState() {
        if (fileUri != null || linkUri != null || !postMessageInput.getText().toString().trim().isEmpty()) {
            ((FloatingActionButton) findViewById(R.id.save)).setImageResource(R.drawable.ic_check_white_24dp);
        } else {
            ((FloatingActionButton) findViewById(R.id.save)).setImageResource(R.drawable.ic_close_white_24dp);
        }
    }

    private void savePost() {
        if (fileUri != null || linkUri != null || !postMessageInput.getText().toString().trim().isEmpty()) {
            Intent intent = new Intent();
            intent.putExtra("fileType", fileType);
            intent.putExtra("fileUri", fileUri == null ? null : fileUri.toString());
            intent.putExtra("linkUri", linkUri == null ? null : linkUri.toString());
            intent.putExtra("message", postMessageInput.getText().toString().trim());
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            if (thumbnailBitmap != null) {
                thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                intent.putExtra("thumbnailBitmap", outStream.toByteArray());
            }
            Helper.shortToast(getApplicationContext(), "posting...");
            setResult(RESULT_OK, intent);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void setFileUri(String fileType, final Uri uri) {
        this.fileType = fileType;
        this.fileUri = uri;

        if (fileType.equals("file")) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Uri contentUri = uri;
                    if (uri.toString().contains("file://"))
                        contentUri = getSharableUriFromUri(uri);

                    Intent intent = ShareCompat.IntentBuilder.from(AddLearningMaterialActivity.this)
                            .setStream(uri) // uri from FileProvider
                            .getIntent()
                            .setAction(Intent.ACTION_VIEW); //Change if needed

                    if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
                        intent.setDataAndType(contentUri, "application/msword");
                    } else if (uri.toString().contains(".pdf")) {
                        intent.setDataAndType(contentUri, "application/pdf");
                    } else if (uri.toString().contains(".ppt") || uri.toString().contains(".pptx")) {
                        intent.setDataAndType(contentUri, "application/vnd.ms-powerpoint");
                    } else if (uri.toString().contains(".xls") || uri.toString().contains(".xlsx")) {
                        intent.setDataAndType(contentUri, "application/vnd.ms-excel");
                    } else if (uri.toString().contains(".zip") || uri.toString().contains(".rar")) {
                        intent.setDataAndType(contentUri, "application/zip");
                    } else if (uri.toString().contains(".rtf")) {
                        intent.setDataAndType(contentUri, "application/rtf");
                    } else if (uri.toString().contains(".wav") || uri.toString().contains(".mp3")) {
                        intent.setDataAndType(contentUri, "audio/x-wav");
                    } else if (uri.toString().contains(".gif")) {
                        intent.setDataAndType(contentUri, "image/gif");
                    } else if (uri.toString().contains(".jpg") || uri.toString().contains(".jpeg") || uri.toString().contains(".png")) {
                        intent.setDataAndType(contentUri, "image/jpeg");
                    } else if (uri.toString().contains(".txt")) {
                        intent.setDataAndType(contentUri, "text/plain");
                    } else if (uri.toString().contains(".3gp") || uri.toString().contains(".mpg") || uri.toString().contains(".mpeg") || uri.toString().contains(".mpe") || uri.toString().contains(".mp4") || uri.toString().contains(".avi")) {
                        intent.setDataAndType(contentUri, "video/*");
                    } else {
                        intent.setDataAndType(contentUri, "*/*");
                    }

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
        } else {
            imageView.setOnClickListener(null);
        }
    }

    private Uri getSharableUriFromUri(Uri uri) {
        String path = Helper.getPathFromUri(this, uri);
        Helper.log("Sharable Path: " + path);
        if (path == null) path = uri.getPath();
        Helper.log("Final Sharable Path: " + path);
        return FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", new File(path));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ATTACHMENT_REQUEST_CODE && resultCode == RESULT_OK) {

            String type = data.getStringExtra("result-info");

            if (type != null && type.equals("file")) {
                String mimetype = Helper.getMimeType(AddLearningMaterialActivity.this, data.getData());
                if (mimetype.contains("image"))
                    type = "image";
                else if (mimetype.contains("video"))
                    type = "video";
                else if (mimetype.contains("audio"))
                    type = "audio";
            }

            switch (type) {
                case "image":
                    onActivityResult(IMAGE_GALLERY_PICK, RESULT_OK, data);
                    break;
                case "audio":
                    onActivityResult(AUDIO_GALLERY_PICK, RESULT_OK, data);
                    break;
                case "video":
                    onActivityResult(VIDEO_GALLERY_PICK, RESULT_OK, data);
                    break;
                case "file":
                    onActivityResult(FILE_GALLERY_PICK, RESULT_OK, data);
                    break;
                case "link":
                    onActivityResult(LINK_GALLERY_PICK, RESULT_OK, data);
                    break;
            }
        } else if (requestCode == THUMBNAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            isThumbnailImage = true;
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(AddLearningMaterialActivity.this);
        } else if (requestCode == VIDEO_GALLERY_PICK && resultCode == RESULT_OK) {
            setFileUri(Post.TYPE_VIDEO, data.getData());
            videoView.setVideoURI(fileUri);
            videoView.seekTo(1);
            videoView.setKeepScreenOn(true);
            videoView.setBackground(null);
            thumbnailBitmap = Helper.getThumbnailFromVideoAsBitmap(AddLearningMaterialActivity.this, fileUri);
            thumbnailImage.setImageBitmap(Helper.getThumbnailFromVideoAsBitmap(AddLearningMaterialActivity.this, fileUri));
            thumbnailImage.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            findViewById(R.id.media_holder).setVisibility(View.VISIBLE);
            ((FloatingActionButton) findViewById(R.id.save)).setImageResource(R.drawable.ic_check_white_24dp);
        } else if (requestCode == IMAGE_GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).start(AddLearningMaterialActivity.this);
        } else if (requestCode == AUDIO_GALLERY_PICK && resultCode == RESULT_OK) {
            setFileUri(Post.TYPE_AUDIO, data.getData());
            videoView.setVideoURI(fileUri);
            videoView.setBackground(getResources().getDrawable(R.drawable.bg_audio));
            thumbnailBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_audio);
            thumbnailImage.setImageBitmap(thumbnailBitmap);
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            findViewById(R.id.media_holder).setVisibility(View.VISIBLE);
            ((FloatingActionButton) findViewById(R.id.save)).setImageResource(R.drawable.ic_check_white_24dp);
        } else if (requestCode == FILE_GALLERY_PICK && resultCode == RESULT_OK) {
            setFileUri(Post.TYPE_FILE, data.getData());
            imageView.setImageResource(R.drawable.bg_document);
            thumbnailBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_document);
            thumbnailImage.setImageBitmap(thumbnailBitmap);
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            thumbnailImage.setVisibility(View.VISIBLE);
            findViewById(R.id.media_holder).setVisibility(View.VISIBLE);
            ((FloatingActionButton) findViewById(R.id.save)).setImageResource(R.drawable.ic_check_white_24dp);
        } else if (requestCode == LINK_GALLERY_PICK && resultCode == RESULT_OK) {
            if (findViewById(R.id.link_view).getVisibility() == View.GONE) {
                findViewById(R.id.add_link).callOnClick();
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (isThumbnailImage) {
                    //TODO: add check to thumbnail.
                    thumbnailBitmap = Helper.getThumbnailFromImageAsBitmap(AddLearningMaterialActivity.this, result.getUri(), 300, 300);
                    thumbnailImage.setImageBitmap(thumbnailBitmap);
                    thumbnailImage.setVisibility(View.VISIBLE);
                    isThumbnailImage = false;
                } else {
                    setFileUri(Post.TYPE_IMAGE, result.getUri());
                    imageView.setImageURI(fileUri);
                    imageView.setVisibility(View.VISIBLE);
                    thumbnailBitmap = Helper.getThumbnailFromImageAsBitmap(AddLearningMaterialActivity.this, fileUri, 300, 300);
                    thumbnailImage.setImageBitmap(thumbnailBitmap);
                    thumbnailImage.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(null);
                    videoView.setVisibility(View.GONE);
                    findViewById(R.id.media_holder).setVisibility(View.VISIBLE);
                    ((FloatingActionButton) findViewById(R.id.save)).setImageResource(R.drawable.ic_check_white_24dp);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Helper.log("Error: Crop Error.\nMessage: " + result.getError().getMessage());
                Helper.longToast(getApplicationContext(), result.getError().getMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == VIDEO_GALLERY_PICK_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryPicker("video");
            } else {
                Helper.longToast(getApplicationContext(), "Gallery permissions denied!");
            }
        }
        if (requestCode == AUDIO_GALLERY_PICK_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryPicker("audio");
            } else {
                Helper.longToast(getApplicationContext(), "Gallery permissions denied!");
            }
        }

    }

    private void openGalleryPicker(String type) {
        Intent galleryIntent = new Intent();
        if (type.equals("thumbnail-image")) {
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), THUMBNAIL_REQUEST_CODE);
        } else {
            galleryIntent.setType(type + "/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT " + type.toUpperCase()),
                    (type.equals("video")) ? VIDEO_GALLERY_PICK : AUDIO_GALLERY_PICK);
        }
    }

    @Override
    protected void onResume() {

        if (!postMessageInput.getText().toString().isEmpty()) {
            findViewById(R.id.hint).callOnClick();
        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        supportFinishAfterTransition();
    }

    private class LinkScrapper extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(final String... strings) {

            linkUri = null;

            String text = strings[0];

            if (text.contains(".") && text.indexOf(".") == text.lastIndexOf(".") && !text.contains("www"))
                if (!text.contains("://"))
                    text = "www." + text;
                else {
                    int index = text.indexOf("://");
                    text = text.substring(0, index + 3) + "www." + text.substring(index + 3);
                }

            if (!text.contains("://"))
                text = "http://" + text;

            final String url = text;

            Helper.log("processing overview for url: " + url);

            try {
                Helper.log("getting overview for link: " + url);

                Document document = Jsoup.connect(url).get();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        linkUri = Uri.parse(url);
                        ((FloatingActionButton) findViewById(R.id.save)).setImageResource(R.drawable.ic_check_white_24dp);
                    }
                });

                final String title = document.title() + "\n" + strings[0];

                Element imgElement = document.select("img").first();

                if (imgElement != null) {
                    final String imgUrl = imgElement.absUrl("src");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(getApplicationContext())
                                    .load(Uri.parse(imgUrl))
                                    .into((ImageView) findViewById(R.id.link_photo));
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) findViewById(R.id.link_photo))
                                    .setImageResource(R.drawable.ic_link_white_24dp);
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.link_content)).setText(title);
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (strings[0].equals(linkUrlInput.getText().toString().trim())) {
                            findViewById(R.id.link_overview).setVisibility(View.VISIBLE);
                        }
                    }
                });

            } catch (final Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        e.printStackTrace();
                        findViewById(R.id.link_overview).setVisibility(View.GONE);
                        Helper.log("error: " + e.getMessage());
                    }
                });
            }
            return null;
        }
    }
}
