package com.gladys.cybuverse.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private Button signup;
    private View have_account;
    private EditText mEmail, mPassword, mConfirmPassword;
    private FirebaseAuth mAuth;
    private TechupDialog techupDialog;
    //    private AlertDialog alertDialog;
    private boolean isProcessCanceled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initializeViews();
        setupAnimations();
        setUpEventHandlers();
    }

    private void initializeViews() {
        signup = findViewById(R.id.register);
        have_account = findViewById(R.id.have_account);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirm_password);
        mAuth = FirebaseAuth.getInstance();

    }

    private void setupAnimations() {
        TechupAnimationUtils.slideY(signup, 700, 0, 600, null);
        signup.postDelayed(new Runnable() {
            @Override
            public void run() {
                TechupAnimationUtils.bounceY(signup, 8f, 500);
            }
        }, 500);
    }


    private void setUpEventHandlers() {
        have_account.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                startActivity(new Intent(SignupActivity.this, com.gladys.cybuverse.Activities.LoginActivity.class));
                SignupActivity.this.finish();
            }
        });

        signup.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                mRegisterUser();
            }
        });

    }

    private void mRegisterUser() {

//        alertView.dismiss();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        String email = Helper.getString(mEmail);
        String password = Helper.getString(mPassword);
        String c_password = Helper.getString(mConfirmPassword);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_error);

        if (Helper.isEmpty(password, c_password, email)) {
            if (Helper.isEmpty(email)) {
                Helper.longToast(SignupActivity.this, "please enter you email!");
                mEmail.startAnimation(animation);
            } else if (Helper.isEmpty(password)) {
                Helper.longToast(SignupActivity.this, "password field is empty!");
                mPassword.startAnimation(animation);
            } else if (Helper.isEmpty(c_password)) {
                Helper.longToast(SignupActivity.this, "please confirm your password!");
                mConfirmPassword.startAnimation(animation);
            }

        } else if (!password.equals(c_password)) {
            Helper.longToast(SignupActivity.this, "passwords do not match!");
        } else if (password.length() < 6 || password.contains(" ")) {
            if (password.length() < 6) {
                Helper.longToast(SignupActivity.this, "password length must be greater than 5!");
            } else {
                Helper.longToast(SignupActivity.this, "password cannot contain space!");
            }
        } else if (!Helper.isNetworkAvailable(getApplicationContext())) {
            techupDialog = new AlertTechupDialog(SignupActivity.this);
            techupDialog.setTitleText("Error");
            ((AlertTechupDialog) techupDialog).setNeutralButton("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    techupDialog.dismiss();
                    mRegisterUser();
                }
            });
            techupDialog.setMessageText("no internet connection available!");
            techupDialog.show();
        } else {
            if (Helper.isValidEmailId(email)) {
                signUpUser(email, password);
            } else {
                Helper.shortToast(getApplicationContext(), "please enter a valid email!");
                mEmail.startAnimation(animation);
            }
        }
    }

    private void signUpUser(final String email, final String password) {
        //TODO : open progressDialog show signing up

        isProcessCanceled = false;

        techupDialog = new ProgressTechupDialog(SignupActivity.this);
        techupDialog.setTitleText("Attempting Signup");
        techupDialog.setMessageText("please wait while we create your account...");
        techupDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelSingUpProcess();
            }
        });
        techupDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //TODO : hideProgressDialog
                techupDialog.dismiss();

                if (!isProcessCanceled) {

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Helper.log("createUserWithEmail:success");
                        Helper.shortToast(SignupActivity.this, "You registered successfully.");

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    User user = new User();
                                    user.setEmail(email);

                                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                    firestore.collection("Users").document(mAuth.getCurrentUser().getUid()).set(user);

                                    ((TechupApplication) getApplicationContext()).setCurrentUserPreference(email, password);
                                    ((TechupApplication) getApplicationContext()).setUser(user);


                                    Intent intent = new Intent(SignupActivity.this, SetupActivity.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                    Helper.shortToast(SignupActivity.this, "Please login now!");
                                    startActivity(intent);
                                }

                                finish();
                            }
                        });

                    } else {

                        techupDialog = new AlertTechupDialog(SignupActivity.this);
                        techupDialog.setTitleText("Error");
                        if (task.getException().getMessage().contains("network error")) {
                            ((AlertTechupDialog) techupDialog).setNeutralButton("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    techupDialog.dismiss();
                                    signUpUser(email, password);
                                }
                            });
                        }
                        techupDialog.setMessageText(task.getException().getMessage());

                        techupDialog.show();

                    }
                }
            }
        });
    }

    private void cancelSingUpProcess() {
        isProcessCanceled = true;
    }


}
