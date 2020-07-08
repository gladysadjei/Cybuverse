package com.gladys.cybuverse.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gladys.cybuverse.Helpers.TechupApplication;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.TechupAnimationUtils;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

public class SplashActivity extends AppCompatActivity {

    public Handler mHandler = new Handler();
    private TextView loading;
    private ImageView chat_gold, chat_blue, chat_purple, diamond, img_logo, txt_logo;
    private ProgressBar progressBar;
    private TechupApplication preference;
    private boolean stopTextLoading = false;
    private int stringTextIndex = 0;
    private String[] loadingStrings = new String[]{"loading.  ", "loading.. ", "loading..."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initializeViews();
        setupAnimations();
    }

    private void initializeViews() {
        loading = findViewById(R.id.loading);
        chat_gold = findViewById(R.id.chat_gold);
        chat_blue = findViewById(R.id.chat_blue);
        chat_purple = findViewById(R.id.chat_purple);
        diamond = findViewById(R.id.ic_diamond);
        img_logo = findViewById(R.id.img_logo);
        txt_logo = findViewById(R.id.txt_logo);
        progressBar = findViewById(R.id.load_progress);

        loading.setVisibility(View.INVISIBLE);
        chat_gold.setVisibility(View.INVISIBLE);
        chat_blue.setVisibility(View.INVISIBLE);
        chat_purple.setVisibility(View.INVISIBLE);
        diamond.setVisibility(View.INVISIBLE);
        img_logo.setVisibility(View.INVISIBLE);
        txt_logo.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        preference = ((TechupApplication) getApplicationContext());
    }

    public void signInRememberedUser() {
        if (preference.rememberUser()) {
            String rem_email = preference.getRememberedEmail();
            String rem_password = preference.getRememberedPassword();

            if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                    FirebaseAuth.getInstance().getCurrentUser().getEmail() != null &&
                    FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(rem_email)) {
                updateApp(FirebaseAuth.getInstance().getCurrentUser());
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(rem_email, rem_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Helper.log("signInWithEmail:success");
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    updateApp(user);
                                } else {
                                    Helper.log("signInWithEmail:failure " + task.getException());
                                    updateApp(null);
                                }
                            }
                        });
            }
        } else {
            gotoStartUp();
        }
    }

    private void updateApp(FirebaseUser firebaseUser) {
        if (firebaseUser == null) {
            //  If sign in fails, display a message to the user.
            Toast.makeText(getApplicationContext(), "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
            gotoStartUp();
        } else {

            FirebaseFirestore.getInstance().collection("Users")
                    .document(firebaseUser.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                User user = task.getResult().toObject(User.class);
                                preference.setUser(user);

                                if (user.getName() == null) {
                                    stopTextLoading = true;
                                    Intent intent = new Intent(getApplicationContext(), SetupActivity.class);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.scale_in_fade, R.anim.fade_out);
                                } else {
                                    stopTextLoading = true;
                                    Helper.shortToast(getApplicationContext(), "Login Successful!");
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.scale_in_fade, R.anim.fade_out);
                                }
                            } else {
                                Helper.shortToast(getApplicationContext(), task.getException().getMessage());
                                gotoStartUp();
                            }
                        }
                    });
        }
    }

    private void gotoStartUp() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopTextLoading = true;
                Intent intent = new Intent(getApplicationContext(), StartUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.scale_in_fade, R.anim.fade_out);
            }
        }, 3000);
    }

    private void setupAnimations() {
        ScaleAnimation diamondAmin = new ScaleAnimation(0, 1f, 0, 1f, diamond.getPivotX(), diamond.getPivotY());
        diamondAmin.setDuration(1000);
        diamondAmin.setInterpolator(new BounceInterpolator());


        diamondAmin.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                diamond.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //TODO: slide gold in right
                TechupAnimationUtils.slideX(chat_blue, 2000, 0, 600, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        chat_blue.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        TechupAnimationUtils.slideX(chat_gold, -2000, 0, 600, new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                chat_gold.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                TechupAnimationUtils.slideX(chat_purple, 2000, 0, 600, new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        chat_purple.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        TechupAnimationUtils.slideY(txt_logo, -500, 0, 1000, new BounceInterpolator(), new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                                txt_logo.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1f);
                                                alphaAnimation.setDuration(600);
                                                alphaAnimation.setInterpolator(new LinearOutSlowInInterpolator());
                                                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                                                    @Override
                                                    public void onAnimationStart(Animation animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animation animation) {
                                                        progressBar.setVisibility(View.VISIBLE);
                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animation animation) {

                                                    }
                                                });

                                                progressBar.startAnimation(alphaAnimation);

                                                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, loading.getPivotX(), loading.getPivotY());
                                                scaleAnimation.setDuration(600);
                                                scaleAnimation.setInterpolator(new LinearOutSlowInInterpolator());
                                                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                                    @Override
                                                    public void onAnimationStart(Animation animation) {
                                                        loading.setVisibility(View.VISIBLE);
                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animation animation) {
                                                        signInRememberedUser();
                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animation animation) {

                                                    }
                                                });

                                                loading.startAnimation(scaleAnimation);

                                                startTextLoading();

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        diamond.startAnimation(diamondAmin);
    }

    private void startTextLoading() {
        loading.setText(loadingStrings[stringTextIndex]);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (stringTextIndex + 1 < loadingStrings.length)
                    stringTextIndex++;
                else
                    stringTextIndex = 0;

                if (!stopTextLoading)
                    startTextLoading();

            }
        }, 200);
    }

}
