package com.gladys.cybuverse.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gladys.cybuverse.Helpers.AlertTechupDialog;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Models.Question;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ReplyMessageActivity extends AppCompatActivity {

    private Question question;

    private ImageView imageView;
    private TextView textView, toChatTextView;
    private EditText editText;
    private View proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_reply);
        initializeViews();
    }

    private void initializeViews() {
        question = new Question();
        question.addProperty("objectID", getIntent().getStringExtra("id"));
        question.setChatUID(getIntent().getStringExtra("chatUID"));
        question.setEmail(getIntent().getStringExtra("email"));
        question.setName(getIntent().getStringExtra("name"));
        question.setProfileImageUri(getIntent().getStringExtra("imageUri"));
        question.setMessage(getIntent().getStringExtra("message"));
        question.setResponse(getIntent().getStringExtra("response"));

        proceed = findViewById(R.id.proceed);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.message);
        editText = findViewById(R.id.reply_message);
        toChatTextView = findViewById(R.id.to_chat);

        Glide.with(this)
                .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_user))
                .load(Uri.parse(question.getProfileImageUri()))
                .into(imageView);

        String toChatText = "sent To: "+question.getChatUID().replace("-", " ").trim();
        toChatTextView.setText(toChatText);
        textView.setText(question.getMessage());
        editText.setText((question.getResponse() == null ? "" : question.getResponse()));

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().trim().isEmpty()) {
                    if (Helper.isNetworkAvailable(getApplicationContext())) {
                        final AlertTechupDialog techupDialog = new AlertTechupDialog(ReplyMessageActivity.this);
                        techupDialog.setTitleText("Confirm Submit");
                        techupDialog.setMessageText("Are you sure you want to send this response?");
                        techupDialog.setPositiveButton("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                techupDialog.dismiss();
                                FirebaseFirestore.getInstance().collection("Questions")
                                        .document(question.getProperty("objectID").toString())
                                        .update("seen", false, "hasResponse", true,
                                                "response", editText.getText().toString().trim())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Helper.shortToast(getApplicationContext(), "you successfully sent the reply.");
                                                    finish();
                                                } else {
                                                    Helper.shortToast(getApplicationContext(), "could not send response: " + task.getException().getMessage());
                                                }
                                            }
                                        });
                            }
                        });
                        techupDialog.setNegativeButton("No", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                techupDialog.dismiss();
                            }
                        });
                        techupDialog.show();
                    } else {
                        Helper.shortToast(getApplicationContext(), "you do not have internet connection to send this response!. please check it and try again.");
                    }
                } else {
                    Helper.shortToast(getApplicationContext(), "please you need to type a response for the message.");
                }
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        supportFinishAfterTransition();
    }
}
