package com.gladys.cybuverse.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import com.gladys.cybuverse.Helpers.PlaySoundOnClickListener;
import com.gladys.cybuverse.Helpers.TechupAnimationUtils;
import com.gladys.cybuverse.R;

import androidx.appcompat.app.AppCompatActivity;

public class StartUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        setupAnimations();
        setViewListeners();
    }

    private void setViewListeners() {
        findViewById(R.id.btn_login).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                startActivity(new Intent(StartUpActivity.this, LoginActivity.class));
            }
        });

        findViewById(R.id.btn_signup).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                startActivity(new Intent(StartUpActivity.this, SignupActivity.class));
            }
        });

        findViewById(R.id.info).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                startActivity(new Intent(StartUpActivity.this, AboutAppActivity.class));
            }
        });
    }

    private void setupAnimations() {
        findViewById(R.id.btn_login).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_signup).setVisibility(View.INVISIBLE);

        TechupAnimationUtils.slideY(findViewById(R.id.container_top), -700, 0, 500, null);
        TechupAnimationUtils.slideY(findViewById(R.id.container_bottom), 700, 0, 600, null);

        findViewById(R.id.btn_login).postDelayed(new Runnable() {
            @Override
            public void run() {
                TechupAnimationUtils.slideX(findViewById(R.id.btn_login), 2000, 0, 500,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                findViewById(R.id.btn_login).setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
            }
        }, 300);

        findViewById(R.id.btn_signup).postDelayed(new Runnable() {
            @Override
            public void run() {
                TechupAnimationUtils.slideX(findViewById(R.id.btn_signup), -2000, 0, 500,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                findViewById(R.id.btn_signup).setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
            }
        }, 300);


        TechupAnimationUtils.bounceY(findViewById(R.id.btn_login), 8f, 500);

        findViewById(R.id.btn_signup).postDelayed(new Runnable() {
            @Override
            public void run() {
                TechupAnimationUtils.bounceY(findViewById(R.id.btn_signup), 8f, 500);
            }
        }, 200);

        TechupAnimationUtils.scaleInOut(findViewById(R.id.img_logo), 0.9f, 1000);


        //TODO: scale in scale out anim for logo
        //TODO: spring up and down repeatedly for buttons
        //TODO: Slide animation for activity transition
        //TODO: Slide buttons in left or right
    }

}
