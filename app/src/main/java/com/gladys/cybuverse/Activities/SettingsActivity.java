package com.gladys.cybuverse.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gladys.cybuverse.Helpers.AlertTechupDialog;
import com.gladys.cybuverse.Helpers.TechupApplication;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.PlaySoundOnClickListener;
import com.gladys.cybuverse.Helpers.TechupAnimationUtils;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.gladys.cybuverse.Utils.FileUtils.FileExplorer;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;
//import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 20;
    private static final int GALLERY_PICK_PERMISSIONS = 140;

    private User user;
    private TechupApplication preference;
    private TextView name, email;
    private View logout;
    private FileExplorer fileExplorer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initializeViews();
        setupAnimations();
    }

    private void initializeViews() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        preference = ((TechupApplication) getApplicationContext());
        user = preference.getUser();
        name.setText(user.getName());
        email.setText(user.getEmail());
        logout = findViewById(R.id.logout);
        fileExplorer = new FileExplorer(Environment.getExternalStorageDirectory())
                .createNewFolder("Cybuverse").openFolder("Cybuverse")
                .createNewFolder("WallPapers").openFolder("WallPapers");

        findViewById(R.id.back).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.home).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                supportFinishAfterTransition();
            }
        });

        if (user.hasInfoKey("profile-drawable")) {
            ((CircleImageView) findViewById(R.id.image))
                    .setImageDrawable((Drawable) user.getInfo("profile-drawable"));
        }

        logout.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                FirebaseAuth.getInstance().signOut();
                TechupApplication preference = ((TechupApplication) getApplicationContext());
                preference.setUser(null);
                preference.deactivateRememberUserPreference();
                preference.resetCurrentUser();

                Intent intent = new Intent(SettingsActivity.this, StartUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        setupCheckButton();

    }

    private void setupCheckButton() {

        final ImageButton daily_notice_btn = findViewById(R.id.daily_notification_check_btn);
        final ImageButton chat_notice_btn = findViewById(R.id.chat_notification_check_btn);
        final ImageButton use_sound_btn = findViewById(R.id.use_sound_check_btn);

        toggleCheckButton(daily_notice_btn, preference.getDailyNotificationPreference());
        toggleCheckButton(chat_notice_btn, preference.getChatNotificationPreference());
        toggleCheckButton(use_sound_btn, preference.getUsesSoundPreference());

        daily_notice_btn.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                boolean isChecked = (boolean) v.getTag();
                toggleCheckButton((ImageButton) v, !isChecked);
                preference.setAppSettings(TechupApplication.DAILY_NOTIFICATION, !isChecked);
            }
        });
        chat_notice_btn.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                boolean isChecked = (boolean) v.getTag();
                toggleCheckButton((ImageButton) v, !isChecked);
                preference.setAppSettings(TechupApplication.CHAT_NOTIFICATION, !isChecked);
            }
        });
        use_sound_btn.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                boolean isChecked = (boolean) v.getTag();
                toggleCheckButton((ImageButton) v, !isChecked);
                preference.setAppSettings(TechupApplication.USES_SOUND, !isChecked);
            }
        });

        findViewById(R.id.daily_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daily_notice_btn.callOnClick();
            }
        });

        findViewById(R.id.chat_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat_notice_btn.callOnClick();
            }
        });

        findViewById(R.id.use_sound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                use_sound_btn.callOnClick();
            }
        });

        findViewById(R.id.wallpaper_img).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                if (!preference.getWallPaperPreference().isEmpty()) {
                    final AlertTechupDialog alertTechupDialog = new AlertTechupDialog(SettingsActivity.this);
                    alertTechupDialog.setMessageText("Do you want to remove this wallpaper?");
                    alertTechupDialog.setPositiveButton("No", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertTechupDialog.dismiss();
                        }
                    });
                    alertTechupDialog.setNeutralButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            preference.setAppSettings(TechupApplication.WALLPAPER_URI, "");
                            ((ImageView) findViewById(R.id.wallpaper_img)).setImageResource(R.drawable.bg_plain);
                            alertTechupDialog.dismiss();
                        }
                    });
                    alertTechupDialog.show();
                }
            }
        });

        findViewById(R.id.wallpaper).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PICK_PERMISSIONS);
                    } else {
                        openGalleryPicker();
                    }
                } else {
                    openGalleryPicker();
                }
            }
        });

        if (!preference.getWallPaperPreference().isEmpty()) {
            File file = new File(Uri.parse(preference.getWallPaperPreference()).getPath());
            if (file.exists()) {
                ((ImageView) findViewById(R.id.wallpaper_img)).setImageURI(Uri.parse(preference.getWallPaperPreference()));
            } else
                ((TechupApplication) getApplicationContext()).setAppSettings(TechupApplication.WALLPAPER_URI, "");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            ((ImageView) findViewById(R.id.wallpaper_img)).setImageURI(imageUri);

            String path = Helper.getPathFromUri(SettingsActivity.this, imageUri);
            if (path == null) path = imageUri.getPath();
            Helper.log("filePath: " + path);
            File file = new File(path);
            if (file.exists()) {
                if (!path.contains("Cybuverse/WallPapers")) {
                    try {
                        FileInputStream inputStream = new FileInputStream(file);
                        FileOutputStream fileOutputStream = new FileOutputStream(fileExplorer.createNewFile(file.getName()).getFile(file.getName()));
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = inputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, len);
                        }
                        inputStream.close();
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                preference.setAppSettings(TechupApplication.WALLPAPER_URI, Uri.fromFile(fileExplorer.getFile(file.getName())));
                Helper.log("copied file to: " + fileExplorer.getFile(file.getName()).getAbsolutePath());
            } else
                Helper.log("file does not exist!");

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


    private void toggleCheckButton(ImageButton imageButton, boolean isChecked) {
        imageButton.setImageResource(isChecked ? R.drawable.ic_checked : R.drawable.ic_unchecked);
        imageButton.setTag(isChecked);
    }

    private void openGalleryPicker() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
    }

    private void setupAnimations() {
        logout.postDelayed(new Runnable() {
            @Override
            public void run() {
                TechupAnimationUtils.bounceY(logout, 8f, 500);
            }
        }, 200);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.bounce_left, R.anim.slide_out_right_anim);
    }

}
