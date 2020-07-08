package com.gladys.cybuverse.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.gladys.cybuverse.Helpers.AlertTechupDialog;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.ProgressTechupDialog;
import com.gladys.cybuverse.Helpers.TechupAnimationUtils;
import com.gladys.cybuverse.Helpers.TechupDialog;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    private Button send;
    private EditText email;
    private View ic_email;
    private FirebaseAuth mAuth;
    private TechupDialog techupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initializeViews();
    }

    private void initializeViews() {
        send = findViewById(R.id.reset);
        email = findViewById(R.id.email);
        ic_email = findViewById(R.id.ic_email);
        mAuth = FirebaseAuth.getInstance();

        TechupAnimationUtils.scaleInOut(ic_email, 0.9f, 1000);
        TechupAnimationUtils.bounceY(send, 8f, 500);

        resetButtonOnClick();

    }

    private void resetButtonOnClick() {

        final Animation animation = AnimationUtils
                .loadAnimation(getApplicationContext(), R.anim.shake_error);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_text = email.getText().toString().trim();


                if (Helper.isEmpty(email_text)) {
                    Helper.shortToast(getApplicationContext(), "email field is required!.");
                    email.startAnimation(animation);
                } else if (!Helper.isValidEmailId(email_text)) {
                    Helper.shortToast(getApplicationContext(), "please enter an email address.");
                    email.startAnimation(animation);
                }
                //TODO: validate password length > 6
                else if (!Helper.isNetworkAvailable(getApplicationContext())) {

                    techupDialog = new AlertTechupDialog(ResetPasswordActivity.this);
                    techupDialog.setTitleText("Error");
                    techupDialog.setMessageText("no internet connection available!");
                    techupDialog.show();

                } else {

                    techupDialog = new ProgressTechupDialog(ResetPasswordActivity.this);
                    techupDialog.setTitleText("Sending email");
                    techupDialog.setMessageText("please wait while we send the email.");
                    techupDialog.setCancelable(false);
                    techupDialog.show();

                    mAuth.sendPasswordResetEmail(email_text).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            techupDialog.dismiss();

                            techupDialog = new AlertTechupDialog(ResetPasswordActivity.this);

                            if (task.isSuccessful()) {

                                techupDialog.setTitleText("Success");
                                techupDialog.setMessageText("your reset email has been sent successfully. please check inbox.");
                                techupDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        finish();
                                        overridePendingTransition(R.anim.bounce_left, R.anim.slide_out_right_anim);
                                    }
                                });
                            } else {
                                techupDialog.setTitleText("Failure");
                                if (task.getException().getMessage().contains("network error")) {
                                    ((AlertTechupDialog) techupDialog).setNeutralButton("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            techupDialog.dismiss();
                                            send.callOnClick();
                                        }
                                    });
                                }
                                techupDialog.setMessageText(task.getException().getMessage());
                            }

                            techupDialog.show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.bounce_left, R.anim.slide_out_right_anim);
    }
}
