package com.gladys.cybuverse.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AddAttachmentActivity extends AppCompatActivity {

    private static final int VIDEO_GALLERY_PICK = 20;
    private static final int IMAGE_GALLERY_PICK = 40;
    private static final int AUDIO_GALLERY_PICK = 80;
    private static final int FILE_GALLERY_PICK = 120;
    private static final int VIDEO_GALLERY_PICK_PERMISSIONS = 140;
    private static final int IMAGE_GALLERY_PICK_PERMISSIONS = 160;
    private static final int AUDIO_GALLERY_PICK_PERMISSIONS = 180;
    private static final int FILE_GALLERY_PICK_PERMISSIONS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attachment);
        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (ContextCompat.checkSelfPermission(AddAttachmentActivity.this, Manifest.permission.MANAGE_DOCUMENTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddAttachmentActivity.this, new String[]{Manifest.permission.MANAGE_DOCUMENTS}, 0);
        }

        findViewById(R.id.add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start gallery item picker

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(AddAttachmentActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddAttachmentActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker("image");
                    }
                } else {
                    openGalleryPicker("image");
                }

            }
        });

        findViewById(R.id.add_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start gallery item picker

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(AddAttachmentActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddAttachmentActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, VIDEO_GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker("video");
                    }
                } else {
                    openGalleryPicker("video");
                }
            }
        });

        findViewById(R.id.add_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start gallery item picker

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(AddAttachmentActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddAttachmentActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, AUDIO_GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker("audio");
                    }
                } else {
                    openGalleryPicker("audio");
                }

            }
        });

        findViewById(R.id.add_document).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start gallery item picker

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(AddAttachmentActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddAttachmentActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker("*");
                    }
                } else {
                    openGalleryPicker("*");
                }

            }
        });

        findViewById(R.id.add_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result-info", "link");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = new Intent();

        if (requestCode == IMAGE_GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            intent.putExtra("result-info", "image");
            intent.setData(imageUri);
            setResult(RESULT_OK, intent);
            finish();
        }

        if (requestCode == VIDEO_GALLERY_PICK && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            intent.putExtra("result-info", "video");
            intent.setData(videoUri);
            setResult(RESULT_OK, intent);
            finish();
        }

        if (requestCode == AUDIO_GALLERY_PICK && resultCode == RESULT_OK) {
            Uri audioUri = data.getData();
            intent.putExtra("result-info", "audio");
            intent.setData(audioUri);
            setResult(RESULT_OK, intent);
            finish();
        }

        if (requestCode == FILE_GALLERY_PICK && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            intent.putExtra("result-info", "file");
            intent.setData(fileUri);
            setResult(RESULT_OK, intent);
            finish();
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
        if (requestCode == IMAGE_GALLERY_PICK_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryPicker("image");
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
        if (requestCode == FILE_GALLERY_PICK_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryPicker("*");
            } else {
                Helper.longToast(getApplicationContext(), "Gallery permissions denied!");
            }
        }

    }

    private void openGalleryPicker(String type) {
        Intent galleryIntent = new Intent();
        galleryIntent.setType(type + "/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT " + type.toUpperCase()),
                (type.equals("video")) ? VIDEO_GALLERY_PICK :
                        (type.equals("image")) ? IMAGE_GALLERY_PICK :
                                (type.equals("audio")) ? AUDIO_GALLERY_PICK :
                                        FILE_GALLERY_PICK);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }
}
