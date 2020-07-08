package com.gladys.cybuverse.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gladys.cybuverse.Helpers.PlaySoundOnClickListener;
import com.gladys.cybuverse.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class AboutAppActivity extends AppCompatActivity {

    private TextView aboutText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        findViewById(R.id.back).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                onBackPressed();
            }
        });

        aboutText = findViewById(R.id.about_text);

        aboutText.setText(getString(R.string.about_game));

    }

}
