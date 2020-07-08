package com.gladys.cybuverse.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gladys.cybuverse.Helpers.AlertTechupDialog;
import com.gladys.cybuverse.Helpers.TechupApplication;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.PlaySoundOnClickListener;
import com.gladys.cybuverse.Helpers.ProgressTechupDialog;
import com.gladys.cybuverse.Helpers.TechupAnimationUtils;
import com.gladys.cybuverse.Helpers.TechupDialog;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;
//import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {
    private static final int AVATAR_PICK = 40;
    private static final int GALLERY_PICK = 20;
    private static final int CAMERA_PICTURE = 80;
    private static final int CAMERA_PERMISSIONS = 120;
    private static final int GALLERY_PICK_PERMISSIONS = 140;

    private User user;
    private TextView username, email, points;
    private View delete_account, camera, gallery, avatar;
    private TechupApplication preference;
    private TechupDialog techupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeViews();
        setupAnimations();
    }

    private void initializeViews() {
        gallery = findViewById(R.id.gallery);
        camera = findViewById(R.id.camera);
        avatar = findViewById(R.id.avatar);
        username = findViewById(R.id.name);
        email = findViewById(R.id.email);
        points = findViewById(R.id.points);
        preference = ((TechupApplication) getApplicationContext());
        user = preference.getUser();
        delete_account = findViewById(R.id.delete_account);

        findViewById(R.id.back).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                onBackPressed();
            }
        });

        if (user.hasInfoKey("profile-drawable")) {
            ((CircleImageView) findViewById(R.id.image))
                    .setImageDrawable((Drawable) user.getInfo("profile-drawable"));
        } else {
            Glide.with(getApplicationContext())
                    .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_user))
                    .load(user.getProfileUri())
                    .into((CircleImageView) findViewById(R.id.image));
        }

        username.setText(user.getName());
        email.setText(user.getEmail());
        points.setText(user.hasInfoKey("points") && user.getInfo("points") != null ?
                user.getInfo("points").toString() + " points" : "0 points");

        findViewById(R.id.home).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                supportFinishAfterTransition();
            }
        });

        setupDeleteAccountClick();

        setupProfilePicGetters();
    }


    private void setupProfilePicGetters() {
        camera.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                //TODO: start camera activity
                if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS);
                } else {
                    openCamera();
                }
            }
        });

        gallery.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                //start gallery item picker

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker();
                    }
                } else {
                    openGalleryPicker();
                }
            }
        });

        avatar.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, com.gladys.cybuverse.Activities.AvatarPickerActivity.class);
                intent.putExtra("gender", user.getGender());
                startActivityForResult(intent, AVATAR_PICK);
            }
        });
    }

    private void openGalleryPicker() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, CAMERA_PICTURE);
    }

    private void setupDeleteAccountClick() {
        delete_account.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                techupDialog = new AlertTechupDialog(ProfileActivity.this);
                techupDialog.setTitleText("Confirm Delete");
                techupDialog.setMessageText("Are you sure you want to delete you account? We will loose your information permanently.");
                ((AlertTechupDialog) techupDialog).setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        techupDialog.dismiss();
                        if ((boolean) user.getInfo("is-admin")) {
                            techupDialog = new AlertTechupDialog(ProfileActivity.this);
                            techupDialog.setTitleText("Delete Admin Account");
                            techupDialog.setMessageText("You are an admin. Are you absolutely sure you want to delete your account?");
                            ((AlertTechupDialog) techupDialog).setPositiveButton("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    techupDialog.dismiss();
                                    deleteUserPermanently();
                                }
                            });
                            ((AlertTechupDialog) techupDialog).setNegativeButton("No", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    techupDialog.dismiss();
                                }
                            });

                            techupDialog.setCancelable(false);
                            techupDialog.show();
                        } else {
                            deleteUserPermanently();
                        }
                    }
                });
                ((AlertTechupDialog) techupDialog).setNegativeButton("No", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        techupDialog.dismiss();
                    }
                });

                techupDialog.setCancelable(false);
                techupDialog.show();
            }
        });
    }


    private void deleteUserPermanently() {
        techupDialog = new ProgressTechupDialog(ProfileActivity.this);
        techupDialog.setMessageText("deleting user account...");
        techupDialog.setCancelable(false);
        techupDialog.show();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseAuth.getInstance().getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore.getInstance().collection("Users")
                                    .document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    techupDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        User user = task.getResult().toObject(User.class);
                                        FirebaseStorage.getInstance().getReferenceFromUrl(user.getProfileUri()).delete();
                                        task.getResult().getReference().delete();
                                        FirebaseFirestore.getInstance().collection("StoryModeChats").document(userId).delete();
                                        FirebaseAuth.getInstance().signOut();
                                        startActivity(new Intent(ProfileActivity.this, StartUpActivity.class));
                                        finish();
                                    } else {
                                        showError(task.getException().getMessage());
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void showError(String message) {
        techupDialog = new AlertTechupDialog(ProfileActivity.this);
        techupDialog.setTitleText("Error Deleting Account");
        techupDialog.setMessageText(message);
        ((AlertTechupDialog) techupDialog).setPositiveButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                techupDialog.dismiss();
            }
        });
        ((AlertTechupDialog) techupDialog).setNegativeButton("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                techupDialog.dismiss();
                deleteUserPermanently();
            }
        });

        techupDialog.setCancelable(false);
        techupDialog.show();
    }


    private void setupAnimations() {
        TechupAnimationUtils.bounceY(delete_account, 8f, 500);

        TechupAnimationUtils.scaleInOut(findViewById(R.id.image), 0.9f, 1000);
        TechupAnimationUtils.bounceY(findViewById(R.id.camera), 4f, 400);
        TechupAnimationUtils.bounceY(findViewById(R.id.gallery), 2f, 600);
        TechupAnimationUtils.bounceY(findViewById(R.id.avatar), 2f, 600);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == AVATAR_PICK && resultCode == RESULT_OK) {
            Glide.with(getApplicationContext()).applyDefaultRequestOptions(new RequestOptions()
                    .placeholder(((ImageView) findViewById(R.id.image)).getDrawable()))
                    .load(data.getData()).into((ImageView) findViewById(R.id.image));
            tryUploadingAvatar(data.getData().toString());
        }


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(ProfileActivity.this);

        }

        if (requestCode == CAMERA_PICTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap != null) ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
            tryUploadingProfileImage(null, bitmap);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //TODO: upload image
                //show upload progress in profile image
                Bitmap bitmap = Helper.getThumbnailFromImageAsBitmap(ProfileActivity.this, result.getUri(), 100, 100);
                if (bitmap != null) ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
                tryUploadingProfileImage(result.getUri(), null);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Helper.log("Error: Crop Error.\nMessage: " + result.getError().getMessage());
                Helper.longToast(getApplicationContext(), result.getError().getMessage());
            }
        }

    }

    private void tryUploadingAvatar(final String uri) {

        final View progressBar = findViewById(R.id.progressBar);
        final ImageView progressImage = findViewById(R.id.progressImage);

        //TODO: disableViews
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update("profileUri", uri)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task1) {
                        progressBar.setVisibility(View.GONE);
                        if (task1.isSuccessful()) {
                            user.setProfileUri(uri);
                            preference.setUser(user);
                            progressImage.setVisibility(View.VISIBLE);
                            progressImage.setImageResource(R.drawable.ic_thumb_up_gold_24dp);
                            Helper.shortToast(getApplicationContext(), "profile image changed successfully.");
                            progressImage.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressImage.setVisibility(View.GONE);
                                }
                            }, 1000);
                        } else {
                            progressImage.setVisibility(View.VISIBLE);
                            progressImage.setImageResource(R.drawable.ic_retry_gold_24dp);
                            progressImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    v.setVisibility(View.GONE);
                                    tryUploadingAvatar(uri);
                                }
                            });
                        }
                    }
                });
    }

    private void tryUploadingProfileImage(final Uri result, final Bitmap bitmap) {
        final View progressBar = findViewById(R.id.progressBar);
        final ImageView progressImage = findViewById(R.id.progressImage);

        //TODO: disableViews

        progressBar.setVisibility(View.VISIBLE);

        UploadTask uploadTask;

        if (result != null) {
            uploadTask = FirebaseStorage.getInstance().getReference()
                    .child("profile_images")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg")
                    .putFile(result);
        } else {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            uploadTask = FirebaseStorage.getInstance().getReference()
                    .child("profile_images")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg")
                    .putBytes(outputStream.toByteArray());
        }


        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult()
                            .getStorage()
                            .getDownloadUrl()
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull final Task<Uri> task) {

                                    FirebaseFirestore.getInstance().collection("Users")
                                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .update("profileUri", task.getResult().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task1) {
                                                    progressBar.setVisibility(View.GONE);
                                                    if (task1.isSuccessful()) {
                                                        user.setProfileUri(task.getResult().toString());
                                                        preference.setUser(user);
                                                        ((ImageView) findViewById(R.id.image)).setImageURI(result);
                                                        progressImage.setVisibility(View.VISIBLE);
                                                        progressImage.setImageResource(R.drawable.ic_thumb_up_gold_24dp);
                                                        Helper.shortToast(getApplicationContext(), "profile image changed successfully.");
                                                        progressImage.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressImage.setVisibility(View.GONE);
                                                            }
                                                        }, 1000);
                                                    } else {
                                                        progressImage.setVisibility(View.VISIBLE);
                                                        progressImage.setImageResource(R.drawable.ic_retry_gold_24dp);
                                                        progressImage.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                v.setVisibility(View.GONE);
                                                                tryUploadingProfileImage(result, bitmap);
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            });
                } else {
                    Helper.log("Error: Firestorage Error.\nMessage: " + task.getException().getMessage());
                    progressBar.setVisibility(View.GONE);
                    progressImage.setVisibility(View.VISIBLE);
                    progressImage.setImageResource(R.drawable.ic_retry_gold_24dp);
                    progressImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setVisibility(View.GONE);
                            tryUploadingProfileImage(result, bitmap);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Helper.longToast(getApplicationContext(), "Camera permissions denied!");
            }
        }

        if (requestCode == GALLERY_PICK_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryPicker();
            } else {
                Helper.longToast(getApplicationContext(), "Gallery permissions denied!");
            }
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.bounce_right, R.anim.slide_out_left_anim);
    }

}
