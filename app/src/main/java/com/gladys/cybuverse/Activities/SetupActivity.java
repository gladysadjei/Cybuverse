package com.gladys.cybuverse.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gladys.cybuverse.Helpers.TechupApplication;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.PlaySoundOnClickListener;
import com.gladys.cybuverse.Helpers.TechupAnimationUtils;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SetupActivity extends AppCompatActivity {

    private static final int AVATAR_PICK = 40;
    private static final int GALLERY_PICK = 20;
    private static final int CAMERA_PICTURE = 80;
    private static final int CAMERA_PERMISSIONS = 120;
    private static final int GALLERY_PICK_PERMISSIONS = 140;

    private FirebaseUser mCurrentUser;
    private FirebaseFirestore mFireStore;
    private Uri profileImageURI;

    private User user;
    private EditText name;
    private RadioButton male, female, other;
    private ImageView profile, gallery, camera, avatar;

    private String gender;
    private Bitmap bitmap;
    private ProgressBar progressBar;

    private boolean isProfileImageChanged;
    private boolean isAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        initializeViews();
        setupAnimations();
        startViewListeners();
    }

    private void setupAnimations() {
        TechupAnimationUtils.slideY(findViewById(R.id.done), 300, 0, 500, null);
        TechupAnimationUtils.bounceY(camera, 4f, 400);
        TechupAnimationUtils.bounceY(gallery, 2f, 600);
        TechupAnimationUtils.bounceY(avatar, 2f, 600);
        TechupAnimationUtils.scaleInOut(profile, 0.9f, 1000);
        findViewById(R.id.done).postDelayed(new Runnable() {
            @Override
            public void run() {
                TechupAnimationUtils.bounceY(findViewById(R.id.done), 8f, 500);
            }
        }, 500);
    }

    private void initializeViews() {
        user = ((TechupApplication) getApplicationContext()).getUser();
        name = findViewById(R.id.name);
        profile = findViewById(R.id.image);
        gallery = findViewById(R.id.gallery);
        camera = findViewById(R.id.camera);
        avatar = findViewById(R.id.avatar);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        other = findViewById(R.id.other);

        progressBar = findViewById(R.id.progressBar);

//        progressBar = findViewById(R.id.progressBar);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFireStore = FirebaseFirestore.getInstance();
        isProfileImageChanged = false;
        isAvatar = false;
        profileImageURI = new Uri.Builder().build();
    }

    private void startViewListeners() {
        setupRadioButtons();
        setupProfilePicGetters();
        processDone();
    }

    private void setupProfilePicGetters() {
        camera.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                //TODO: start camera activity
                if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS);
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

                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PICK_PERMISSIONS);
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
                Intent intent = new Intent(SetupActivity.this, com.gladys.cybuverse.Activities.AvatarPickerActivity.class);
                intent.putExtra("gender", getGender());
                startActivityForResult(intent, AVATAR_PICK);
            }
        });
    }

    private String getGender() {
        return gender;
    }

    private void setupRadioButtons() {
        gender = "male";
        male.setChecked(true);
        female.setChecked(false);
        other.setChecked(false);

        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    female.setChecked(false);
                    other.setChecked(false);
                    gender = "male";
                }
            }
        });

        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = "female";
                    male.setChecked(false);
                    other.setChecked(false);
                }
            }
        });

        other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = "other";
                    female.setChecked(false);
                    male.setChecked(false);
                }
            }
        });

    }

    private void processDone() {
        findViewById(R.id.done).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                if (name.getText().toString().trim().isEmpty()) {
                    Helper.longToast(SetupActivity.this, "please enter a username!");
                } else {
                    disableViews();
                    processUserInfo();
                }
            }
        });

    }

    private void disableViews() {
        profile.setClickable(false);
        gallery.setClickable(false);
        camera.setClickable(false);
        avatar.setClickable(false);
        name.clearFocus();
        name.setFocusable(false);
        male.setClickable(false);
        female.setClickable(false);
        other.setClickable(false);
        findViewById(R.id.done).setClickable(false);
    }

    private void enableViews() {
        profile.setClickable(true);
        gallery.setClickable(true);
        camera.setClickable(true);
        avatar.setClickable(true);
        name.clearFocus();
        name.setFocusable(true);
        male.setClickable(true);
        female.setClickable(true);
        other.setClickable(true);
        findViewById(R.id.done).setClickable(true);
    }

    private void processUserInfo() {

        //TODO : save details in offline database and put profile image in cybuverse offline pictures.profilepictures path
        //TODO : start progress

        if (mCurrentUser != null) {

            progressBar.setVisibility(View.VISIBLE);

            user.setName(name.getText().toString());
            user.setGender(getGender());
            user.addInfo("is-new-user", false);

            if (isProfileImageChanged) {

                if (isAvatar) {
                    user.setProfileUri(profileImageURI.toString());
                    addUserToFireStore();
                } else {
                    UploadTask uploadTask;

                    if (bitmap == null) {
                        uploadTask = FirebaseStorage.getInstance().getReference()
                                .child("profile_images")
                                .child(mCurrentUser.getUid() + ".jpg")
                                .putFile(profileImageURI);

                    } else {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        uploadTask = FirebaseStorage.getInstance().getReference()
                                .child("profile_images")
                                .child(mCurrentUser.getUid() + ".jpg")
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
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                user.setProfileUri(task.getResult().toString());
                                                addUserToFireStore();
                                            }
                                        });

                            } else {
                                Helper.log("Error: Firestorage Error.\nMessage: " + task.getException().getMessage());
                                Helper.longToast(getApplicationContext(), "Error : " + task.getException().getMessage());
                            }

                        }
                    });
                }
            } else {
                addUserToFireStore();
            }
        } else {
            Helper.longToast(getApplicationContext(), "please sign in before you continue!");
            progressBar.setVisibility(View.INVISIBLE);
            enableViews();
        }

    }

    private void addUserToFireStore() {
        //adding data to fireStore
        mFireStore.collection("Users")
                .document(mCurrentUser.getUid())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Helper.log("Error: Firestore Error.\nMessage: " + task.getException().getMessage());
                            Helper.longToast(getApplicationContext(), "Error : " + task.getException());
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        enableViews();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AVATAR_PICK && resultCode == RESULT_OK) {
            //TODO: turn url into uri
            bitmap = null;
            isAvatar = true;
            isProfileImageChanged = true;
            profileImageURI = data.getData();
            Glide.with(getApplicationContext()).load(profileImageURI).into(profile);
        }
        else if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
           Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(SetupActivity.this);
        }
        else if (requestCode == CAMERA_PICTURE && resultCode == RESULT_OK) {
            profileImageURI = null;
            bitmap = (Bitmap) data.getExtras().get("data");
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
            profile.setImageDrawable(bitmapDrawable);
            isProfileImageChanged = true;
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                bitmap = null;
                isAvatar = false;
                isProfileImageChanged = true;
                profileImageURI = result.getUri();
                profile.setImageURI(profileImageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Helper.log("Error: Crop Error.\nMessage: " + result.getError().getMessage());
                Helper.longToast(getApplicationContext(), result.getError().getMessage());
            }
        }

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

    private void openGalleryPicker() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PICTURE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (user != null) {

            if (user.getName() != null) {
                name.setText(user.getName());
            }

            if (user.getGender() != null) {
                switch (user.getGender()) {
                    case "male":
                        male.setChecked(true);
                        break;
                    case "female":
                        female.setChecked(true);
                        break;
                    case "other":
                        other.setChecked(true);
                        break;
                }
            }

            if (!user.getProfileUri().equals("default")) {
                profileImageURI = Uri.parse(user.getProfileUri());
            }

            if (profileImageURI != null) {
                Glide.with(getApplicationContext())
                        .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_user))
                        .load(profileImageURI)
                        .into(profile);
            }
        }
    }

}
