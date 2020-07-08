package com.gladys.cybuverse.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gladys.cybuverse.Helpers.PlaySoundOnClickListener;
import com.gladys.cybuverse.R;

import androidx.appcompat.app.AppCompatActivity;

public class InformationActivity extends AppCompatActivity {

    private TextView title, message;
    private Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        initializeViews();
    }

    private void initializeViews() {
        title = findViewById(R.id.title);
        message = findViewById(R.id.info_message);
        proceed = findViewById(R.id.proceed);

        String _title = getIntent().getExtras().getString("title");
        String _message = getIntent().getExtras().getString("message");
        String _proceed = getIntent().getExtras().getString("proceed");
        final Boolean _animate_finish = getIntent().getExtras().getBoolean("animate_finish");

        title.setText(_title);
        message.setText(_message);
        proceed.setText(_proceed);

        proceed.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                setResult(RESULT_OK);
                if (_animate_finish) {
                    finish();
                    overridePendingTransition(R.anim.bounce_down, R.anim.slide_out_down_anim);
                } else {
                    supportFinishAfterTransition();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        supportFinishAfterTransition();
    }
}
