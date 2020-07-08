package com.gladys.cybuverse.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.gladys.cybuverse.Adapters.AvatarsViewPagerAdapter;
import com.gladys.cybuverse.Adapters.OnListItemInteractionListener;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ChatBot;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Actor;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ActorsMap;
import com.gladys.cybuverse.Helpers.AlertTechupDialog;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.ProgressTechupDialog;
import com.gladys.cybuverse.Models.Avatar;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.viewpager.widget.ViewPager;

public class AvatarsManagerActivity extends AppCompatActivity {
    private static final int GALLERY_PICK = 20;
    private static final int GALLERY_PICK_PERMISSIONS = 140;

    private AvatarsViewPagerAdapter pagerAdapter;
    private Actor currentActorToChangeAvatar;
    private ViewPager viewPager;
    private View male;
    private View female;
    private View other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_manager);
        initializeViews();
    }

    private void initializeViews() {

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.add_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AvatarsManagerActivity.this, AddAvatarActivity.class));
            }
        });

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        other = findViewById(R.id.other);
        final View actors = findViewById(R.id.actor);
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new AvatarsViewPagerAdapter(getSupportFragmentManager(), new ArrayList<Actor>());
        pagerAdapter.setOnActorItemInteractionListener(new OnListItemInteractionListener<Actor>() {
            @Override
            public void onItemInteraction(Actor actor, int position, Object interactionType) {
                actor.getProperties().set("position", position);
                startChangeActorAvatarProcess(actor);
            }
        });
        pagerAdapter.setOnAvatarItemInteractionListener(new OnListItemInteractionListener<Avatar>() {
            @Override
            public void onItemInteraction(Avatar avatar, int position, Object interactionType) {
                startDeleteUserAvatarProcess(avatar);
            }
        });
        viewPager.setAdapter(pagerAdapter);

        pagerAdapter.setActors(getActorsList());

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male.setBackgroundResource(R.drawable.bg_btn_blue_black);
                female.setBackgroundResource(R.drawable.bg_btn_gold);
                other.setBackgroundResource(R.drawable.bg_btn_gold);
                viewPager.setCurrentItem(0);
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male.setBackgroundResource(R.drawable.bg_btn_gold);
                female.setBackgroundResource(R.drawable.bg_btn_blue_black);
                other.setBackgroundResource(R.drawable.bg_btn_gold);
                viewPager.setCurrentItem(1);
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male.setBackgroundResource(R.drawable.bg_btn_gold);
                female.setBackgroundResource(R.drawable.bg_btn_gold);
                other.setBackgroundResource(R.drawable.bg_btn_blue_black);
                viewPager.setCurrentItem(2);
            }
        });
        actors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male.setBackgroundResource(R.drawable.bg_btn_gold);
                female.setBackgroundResource(R.drawable.bg_btn_gold);
                other.setBackgroundResource(R.drawable.bg_btn_gold);
                viewPager.setCurrentItem(3);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3) {
                    Animation scaleAnim = new ScaleAnimation(1, 0, 1, 0,
                            findViewById(R.id.add_avatar).getPivotX(), findViewById(R.id.add_avatar).getPivotY());
                    scaleAnim.setDuration(200);
                    scaleAnim.setInterpolator(new FastOutSlowInInterpolator());
                    scaleAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            findViewById(R.id.add_avatar).setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    findViewById(R.id.add_avatar).startAnimation(scaleAnim);
                } else {
                    if (findViewById(R.id.add_avatar).getVisibility() == View.GONE) {
                        Animation scaleAnim = new ScaleAnimation(0, 1, 0, 1,
                                findViewById(R.id.add_avatar).getPivotX(), findViewById(R.id.add_avatar).getPivotY());
                        scaleAnim.setDuration(200);
                        scaleAnim.setInterpolator(new FastOutSlowInInterpolator());
                        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                findViewById(R.id.add_avatar).setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        findViewById(R.id.add_avatar).startAnimation(scaleAnim);
                    }
                }

                if (position == 0) male.callOnClick();
                else if (position == 1) female.callOnClick();
                else if (position == 2) other.callOnClick();
                else if (position == 3) actors.callOnClick();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void startChangeActorAvatarProcess(final Actor actor) {
        if (Helper.isNetworkAvailable(getApplicationContext())) {
            confirmChangeActorAvatarAction(actor);
        } else {
            Snackbar.make(findViewById(R.id.root_view), "you need internet connection to complete this action", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startChangeActorAvatarProcess(actor);
                        }
                    })
                    .setTextColor(getResources().getColor(R.color.colorButtonRed))
                    .setActionTextColor(getResources().getColor(R.color.colorFacebook)).show();
        }
    }

    private void startDeleteUserAvatarProcess(final Avatar avatar) {
        if (Helper.isNetworkAvailable(getApplicationContext())) {
            confirmRemoveAvatarAction(avatar);
        } else {
            Snackbar.make(findViewById(R.id.root_view), "you need internet connection to complete this action", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startDeleteUserAvatarProcess(avatar);
                        }
                    })
                    .setTextColor(getResources().getColor(R.color.colorButtonRed))
                    .setActionTextColor(getResources().getColor(R.color.colorFacebook)).show();
        }
    }

    private void confirmRemoveAvatarAction(final Avatar avatar) {
        final AlertTechupDialog techupDialog = new AlertTechupDialog(AvatarsManagerActivity.this);
        techupDialog.setTitleText("Remove Avatar");
        techupDialog.setMessageText("Are you sure you want to remove the avatar?");
        techupDialog.setPositiveButton("No", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
            }
        });
        techupDialog.setNegativeButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                attemptRemoveAvatar(avatar);
            }

        });
        techupDialog.show();
    }

    private void attemptRemoveAvatar(final Avatar avatar) {
        Helper.shortToast(getApplicationContext(), "removing avatar");
        final ProgressTechupDialog techupDialog = new ProgressTechupDialog(AvatarsManagerActivity.this);
        techupDialog.setMessageText("removing avatar");
        techupDialog.setCancelable(false);
        techupDialog.show();
        FirebaseFirestore.getInstance().collection("Avatars")
                .whereEqualTo("imageUri", avatar.getImageUri()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        techupDialog.dismiss();
                        if (task.isSuccessful() && task.getResult().getDocuments().size() == 1) {
                            task.getResult().getDocuments().get(0).getReference().delete();
                            FirebaseStorage.getInstance().getReferenceFromUrl(avatar.getThumbnailUri()).delete();
                            FirebaseStorage.getInstance().getReferenceFromUrl(avatar.getImageUri()).delete();
                        } else {
                            final AlertTechupDialog techupDialog = new AlertTechupDialog(AvatarsManagerActivity.this);
                            techupDialog.setTitleText("Delete Failed");
                            techupDialog.setMessageText("failed to delete avatar: " + task.getException().getMessage());
                            techupDialog.setPositiveButton("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    techupDialog.dismiss();
                                    attemptRemoveAvatar(avatar);
                                }
                            });
                            techupDialog.setNegativeButton("Cancel", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    techupDialog.dismiss();
                                }
                            });
                            techupDialog.show();
                        }
                    }
                });
    }

    private void confirmChangeActorAvatarAction(final Actor actor) {
        final AlertTechupDialog techupDialog = new AlertTechupDialog(AvatarsManagerActivity.this);
        techupDialog.setTitleText("Change Avatar");
        techupDialog.setMessageText("Are you sure you want to change the actors avatar?");
        techupDialog.setPositiveButton("No", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
            }
        });
        techupDialog.setNegativeButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                attemptChangeActorAvatar(actor);
            }

        });
        techupDialog.show();
    }

    private void attemptChangeActorAvatar(Actor actor) {
        Helper.shortToast(getApplicationContext(), "changing avatar");
        this.currentActorToChangeAvatar = actor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(AvatarsManagerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AvatarsManagerActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PICK_PERMISSIONS);
            } else {
                openGalleryPicker();
            }
        } else {
            openGalleryPicker();
        }
    }

    private void openGalleryPicker() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
    }

    private List<Actor> getActorsList() {
        List<Actor> actorList = new ArrayList<>(ActorsMap.getInstance().values());
        actorList.remove(ChatBot.asActor());
        return actorList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(AvatarsManagerActivity.this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = Helper.getThumbnailFromImageAsBitmap(AvatarsManagerActivity.this, result.getUri(), 100, 100);
                attemptAddActorAvatar(Helper.generateRandomString(15, null), bitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Helper.log("Error: Crop Error.\nMessage: " + result.getError().getMessage());
                Helper.longToast(getApplicationContext(), result.getError().getMessage());
            }
        }

    }

    private void attemptAddActorAvatar(final String mediaName, final Bitmap bitmap) {

        final Avatar avatar = new Avatar();
        avatar.getProperties().put("isActor", true);
        avatar.getProperties().put("actorName", currentActorToChangeAvatar.getName().toLowerCase());

        final StorageReference mediaReference = FirebaseStorage.getInstance().getReference("avatars/data").child(mediaName);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        if (bitmap != null) {
            final ProgressTechupDialog techupDialog = new ProgressTechupDialog(AvatarsManagerActivity.this);
            techupDialog.setMessageText("changing avatar for actor: " + currentActorToChangeAvatar.getName());
            techupDialog.setCancelable(false);
            techupDialog.show();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            UploadTask mediaUploadTask = mediaReference.putBytes(outStream.toByteArray());
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
                        FirebaseFirestore.getInstance().collection("Avatars").add(avatar)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            techupDialog.dismiss();
                                            Helper.shortToast(getApplicationContext(), "avatar changed successfully");
                                            pagerAdapter.getFragments().get(3).getAdapter()
                                                    .notifyItemChanged((int) currentActorToChangeAvatar.getProperty("position"));
                                            currentActorToChangeAvatar = null;
                                        } else {
                                            techupDialog.dismiss();
                                            mediaReference.delete();
                                            Helper.longToast(getApplicationContext(), "failed to change actor avatar: " + task.getException().getMessage());
                                        }
                                    }
                                });
                    } else {
                        techupDialog.dismiss();
                        final AlertTechupDialog alertTechupDialog = new AlertTechupDialog(AvatarsManagerActivity.this);
                        alertTechupDialog.setMessageText("failed to change actor avatar: " + task.getException().getMessage());
                        alertTechupDialog.setNeutralButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertTechupDialog.dismiss();
                            }
                        });
                        alertTechupDialog.setPositiveButton("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertTechupDialog.dismiss();
                                attemptAddActorAvatar(mediaName, bitmap);
                            }
                        });
                        techupDialog.show();
                        Helper.shortToast(getApplicationContext(), "failed to change actor avatar: " + task.getException().getMessage());
                    }
                }
            });
        } else {
            currentActorToChangeAvatar = null;
            Helper.shortToast(getApplicationContext(), "unable to change avatar to selected image");
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


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        supportFinishAfterTransition();
    }
}
