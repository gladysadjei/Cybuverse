package com.gladys.cybuverse.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Models.Avatar;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AddAvatarActivity extends AppCompatActivity {

    private final int GALLERY_PICK = 20;
    private final int GALLERY_PICK_PERMISSIONS = 140;

    private Uri imageUri;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_avatar);
        initializeViews();
    }

    private void initializeViews() {

        imageView = findViewById(R.id.image);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(AddAvatarActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddAvatarActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker();
                    }
                } else {
                    openGalleryPicker();
                }
            }
        });

        findViewById(R.id.proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageUri != null) {
                    if (Helper.isNetworkAvailable(getApplicationContext())) {
                        proceedWithUpload();
                    } else {
                        Helper.shortToast(getApplicationContext(), "internet connection not available!...");
                    }
                } else {
                    Helper.shortToast(getApplicationContext(), "please add an image to proceed. click on the add icon to add image");
                }
            }
        });

    }

    private void proceedWithUpload() {
        disableViews();

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        final String gender = findViewById(((RadioGroup) findViewById(R.id.gender)).getCheckedRadioButtonId()).getTag().toString();
        final Avatar avatar = new Avatar();

        String mediaName = Helper.generateRandomString(15, null);

        final StorageReference mediaReference = FirebaseStorage.getInstance().getReference("avatars/data").child(mediaName);
        final StorageReference thumbnailReference = FirebaseStorage.getInstance().getReference("avatars/thumbnail").child(mediaName);

        UploadTask mediaUploadTask = mediaReference.putFile(imageUri);
        mediaUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mediaReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    avatar.setImageUri(task.getResult().toString());
                    Bitmap bitmap = Helper.getThumbnailFromImageAsBitmap(AddAvatarActivity.this, imageUri, 200, 200);
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        UploadTask thumbnailUploadTask = thumbnailReference.putBytes(outStream.toByteArray());
                        thumbnailUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                return thumbnailReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    avatar.setThumbnailUri(task.getResult().toString());
                                    avatar.setGender(gender);
                                    FirebaseFirestore.getInstance().collection("Avatars").add(avatar)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                                                    if (task.isSuccessful()) {
                                                        Helper.shortToast(getApplicationContext(), "avatar uploaded successfully");
                                                        finish();
                                                    } else {
                                                        enableViews();
                                                        mediaReference.delete();
                                                        thumbnailReference.delete();
                                                        Helper.longToast(getApplicationContext(), "failed to upload avatar: " + task.getException().getMessage());
                                                    }
                                                }
                                            });
                                } else {
                                    enableViews();
                                    mediaReference.delete();
                                    thumbnailReference.delete();
                                    Helper.longToast(getApplicationContext(), "failed to upload avatar: " + task.getException().getMessage());
                                }
                            }
                        });
                    } else {
                        avatar.setThumbnailUri(avatar.getImageUri());
                        avatar.setGender(gender);
                        FirebaseFirestore.getInstance().collection("Avatars").add(avatar)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                                        if (task.isSuccessful()) {
                                            Helper.shortToast(getApplicationContext(), "avatar uploaded successfully");
                                            finish();
                                        } else {
                                            enableViews();
                                            mediaReference.delete();
                                            thumbnailReference.delete();
                                            Helper.longToast(getApplicationContext(), "failed to upload avatar: " + task.getException().getMessage());
                                        }
                                    }
                                });
                    }
                } else {
                    enableViews();
                    findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                    Helper.shortToast(getApplicationContext(), "failed to upload avatar: " + task.getException().getMessage());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(AddAvatarActivity.this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Helper.log("Error: Crop Error.\nMessage: " + result.getError().getMessage());
                Helper.longToast(getApplicationContext(), result.getError().getMessage());
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == GALLERY_PICK_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryPicker();
            } else {
                Helper.longToast(getApplicationContext(), "Gallery permissions denied!");
            }
        }

    }

    private void disableViews() {
        imageView.setFocusable(false);
        findViewById(R.id.male).setClickable(false);
        findViewById(R.id.female).setClickable(false);
        findViewById(R.id.other).setClickable(false);
        findViewById(R.id.back).setClickable(false);
        findViewById(R.id.image).setClickable(false);
        findViewById(R.id.proceed).setClickable(false);
    }

    private void enableViews() {
        imageView.setFocusable(true);
        findViewById(R.id.male).setClickable(true);
        findViewById(R.id.female).setClickable(true);
        findViewById(R.id.other).setClickable(true);
        findViewById(R.id.back).setClickable(true);
        findViewById(R.id.image).setClickable(true);
        findViewById(R.id.proceed).setClickable(true);
    }

    private void openGalleryPicker() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        supportFinishAfterTransition();
    }
}
