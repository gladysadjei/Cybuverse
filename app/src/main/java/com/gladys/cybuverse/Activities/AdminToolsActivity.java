package com.gladys.cybuverse.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gladys.cybuverse.Helpers.TechupApplication;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminToolsActivity extends AppCompatActivity {

    public static final int ADD_LEARNING_POST = 231;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tools);
        initializeViews();
    }

    private void initializeViews() {

        User user = ((TechupApplication) getApplicationContext()).getUser();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (user.hasInfoKey("is-super-admin") && (boolean) user.getInfo("is-super-admin")) {
            findViewById(R.id.manage_admins).setVisibility(View.VISIBLE);
            findViewById(R.id.manage_admins).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AdminToolsActivity.this, AdminsManagerActivity.class));
                }
            });
        }

        findViewById(R.id.manage_avatars).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminToolsActivity.this, AvatarsManagerActivity.class));
            }
        });

        findViewById(R.id.new_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AdminToolsActivity.this, AddLearningMaterialActivity.class), ADD_LEARNING_POST);
            }
        });

        findViewById(R.id.user_progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminToolsActivity.this, UsersProgressActivity.class));
            }
        });

        findViewById(R.id.reply_messages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminToolsActivity.this, MessagesToReplyActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseFirestore.getInstance().collection("Questions")
                .whereEqualTo("response", null).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                            ((TextView) findViewById(R.id.no_reply_messages_count))
                                    .setText(String.valueOf(task.getResult().getDocuments().size()));
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_LEARNING_POST && resultCode == RESULT_OK){
            startActivity(data.setClass(getApplicationContext(), LearningActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        supportFinishAfterTransition();
    }
}
