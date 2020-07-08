package com.gladys.cybuverse.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


//C:\Users\Otc_Chingy\AndroidStudioProjects\Cybuverse\app\src\main\AndroidManifest.xml:24:9-31:50 Warning:
//        activity#com.google.firebase.auth.internal.FederatedSignInActivity@android:launchMode was tagged
// at AndroidManifest.xml:24 to replace other declarations but no other declaration present

public class LoginActivity extends AppCompatActivity {

    private Button login, forgot_pass;
    private View need_account;
    private CheckBox remember;
    private EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private TechupDialog techupDialog;

    private boolean isProcessCancelled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();
        setupAnimations();
        setUpEventHandlers();
        checkForCybuverseNotice();
    }

    private void setupAnimations() {
        TechupAnimationUtils.slideY(login, 700, 0, 600, null);
        login.postDelayed(new Runnable() {
            @Override
            public void run() {
                TechupAnimationUtils.bounceY(login, 8f, 500);
            }
        }, 500);
    }

    private void checkForCybuverseNotice() {
        //TODO : create remote database to record software update notices and important app feature update notice
        //TODO : create manager to record remote cybuverse app notices
    }

    private void initializeViews() {

        login = findViewById(R.id.login);
        remember = findViewById(R.id.remember_me);
        forgot_pass = findViewById(R.id.forgot_password);
        need_account = findViewById(R.id.need_account);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
    }

    private void setUpEventHandlers() {
        need_account.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        login.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                mLoginUser(Helper.getString(mEmail), Helper.getString(mPassword));
            }
        });

        forgot_pass.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));
                overridePendingTransition(R.anim.bounce_right, R.anim.slide_out_left_anim);
            }
        });
    }

    private void mLoginUser(final String email, final String password) {
        try {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_error);

            if (Helper.isEmpty(email, password)) {
                if (Helper.isEmpty(email)) {
                    Helper.longToast(LoginActivity.this, "please enter your email!");
                    mEmail.startAnimation(animation);
                } else {
                    Helper.longToast(LoginActivity.this, "please enter your password!");
                    mPassword.startAnimation(animation);
                }
                ((TechupApplication) getApplicationContext()).deactivateRememberUserPreference();
            } else {

                if (!Helper.isNetworkAvailable(getApplicationContext())) {
                    techupDialog = new AlertTechupDialog(LoginActivity.this);
                    techupDialog.setTitleText("Error");
                    ((AlertTechupDialog) techupDialog).setNeutralButton("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            techupDialog.dismiss();
                            mLoginUser(email, password);
                        }
                    });
                    techupDialog.setMessageText("no internet connection available!");
                    techupDialog.show();
                } else {
                    if (Helper.isValidEmailId(email)) {
                        signInUser(email, password);
                    } else {
                        mEmail.startAnimation(animation);
                        Helper.longToast(getApplicationContext(), "please enter a valid email!");
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signInUser(final String email, final String password) {

        isProcessCancelled = false;
        techupDialog = new ProgressTechupDialog(LoginActivity.this);
        techupDialog.setTitleText("Attempting Login");
        techupDialog.setMessageText("please wait while we check your credentials.");
        techupDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelSignInProcess();
            }
        });
        techupDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (!isProcessCancelled) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Helper.log("signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateApp(user);
                            } else {
                                techupDialog.dismiss();

                                techupDialog = new AlertTechupDialog(LoginActivity.this);
                                techupDialog.setTitleText("Login Failed");

                                if (task.getException().getMessage().contains("network error")) {
                                    ((AlertTechupDialog) techupDialog).setNeutralButton("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            techupDialog.dismiss();
                                            signInUser(email, password);
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

    private void cancelSignInProcess() {
        isProcessCancelled = true;
    }

    private void updateApp(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            String email = Helper.getString(mEmail);
            String password = Helper.getString(mPassword);

            setRememberUserPreference(email, password);
            ((TechupApplication) getApplicationContext()).setCurrentUserPreference(email, password);

            FirebaseFirestore.getInstance().collection("Users")
                    .document(firebaseUser.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            techupDialog.dismiss();
                            if (task.isSuccessful()) {

                                User user = task.getResult().toObject(User.class);
                                ((TechupApplication) getApplicationContext()).setUser(user);

                                if (user.getName() == null) {
                                    Intent intent = new Intent(LoginActivity.this, SetupActivity.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    mPassword.setText("");
                                    mEmail.setText("");
                                    finish();
                                }
                            } else {

                                techupDialog = new AlertTechupDialog(LoginActivity.this);
                                techupDialog.setTitleText("Error");

                                if (task.getException().getMessage().contains("network error")) {
                                    ((AlertTechupDialog) techupDialog).setNeutralButton("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            techupDialog.dismiss();
                                            signInUser(Helper.getString(mEmail), Helper.getString(mPassword));
                                        }
                                    });
                                }

                                techupDialog.setMessageText(task.getException().getMessage());
                                techupDialog.show();
                            }
                        }
                    });
        }
    }

    private void setRememberUserPreference(String email, String password) {
        if (remember.isChecked()) {
            ((TechupApplication) getApplicationContext()).activateRememberUserPreference(email, password);
        } else {
            if (((TechupApplication) getApplicationContext()).rememberUser()) {
                if (!((TechupApplication) getApplicationContext()).getRememberedEmail().equals(email)) {
                    ((TechupApplication) getApplicationContext()).deactivateRememberUserPreference();
                }
            }
        }

    }

}
