package com.gladys.cybuverse.Activities;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ChatBot;
import com.gladys.cybuverse.Helpers.TechupApplication;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.PlaySoundOnClickListener;
import com.gladys.cybuverse.Helpers.TechupAnimationUtils;
import com.gladys.cybuverse.Models.Chat;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.gladys.cybuverse.Activities.StoryModeActivity.CONVERSATION_ACTIVITY_REQUEST_CODE;

public class MainActivity extends AppCompatActivity {

    public static final int GLADYS_INTRO_REQUEST_CODE = 101;

    private User user;
    private Button story_mode, quick_game;
    private ImageView see_all, logout, learning, settings, gladys;
    private View admin_tools;
    private Chat gladysChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setupAnimations();
        startEventListeners();
    }

    private void initializeViews() {
        story_mode = findViewById(R.id.btn_story_mode);
        quick_game = findViewById(R.id.btn_quick_game);
        see_all = findViewById(R.id.btn_see_all);
        logout = findViewById(R.id.btn_logout);
        user = ((TechupApplication) getApplicationContext()).getUser();
        learning = findViewById(R.id.learning);
        gladys = findViewById(R.id.gladys);
        settings = findViewById(R.id.settings);
        admin_tools = findViewById(R.id.admin_tools);

        ((TextView) findViewById(R.id.name)).setText(user.getName());

        Helper.log("user-info: " + user.getInfo());

        if (!user.getProfileUri().equals("default")) {
            Glide.with(getApplicationContext()).load(user.getProfileUri()).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    user.addInfo("profile-drawable", resource);
                    ((TechupApplication) getApplicationContext()).setUser(user);
                    ((ImageView) findViewById(R.id.image)).setImageDrawable(resource);
                }
            });
        }

        showMiniLeadBoard();
    }

    private void showMiniLeadBoard() {
        FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("info.is-new-user", false)
                .orderBy("info.points", Query.Direction.DESCENDING).limit(3).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            LinearLayout linearLayout = findViewById(R.id.lead_players);
                            TransitionManager.beginDelayedTransition(linearLayout);

                            List<User> userList = task.getResult().toObjects(User.class);
                            int i = 0;
                            int[] resources = {R.drawable.txt_first, R.drawable.txt_second, R.drawable.txt_third};
                            for (User user : userList) {
                                View view = getLayoutInflater().inflate(R.layout.item_lead_user, null);
                                ((TextView) view.findViewById(R.id.name)).setText(user.getName());
                                ((ImageView) view.findViewById(R.id.position_image)).setImageResource(resources[i]);
                                if (!user.getProfileUri().equals("default")) {
                                    Glide.with(getApplicationContext())
                                            .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_user))
                                            .load(user.getProfileUri())
                                            .into((ImageView) view.findViewById(R.id.image));
                                }
                                linearLayout.addView(view);
                                i++;
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        gladysChat = ChatBot.asChat();
        gladysChat.addProperty("scene-index", 0);
        gladysChat.addProperty("conversation-index", 0);
        gladysChat.addProperty("last-message-index", 0);
        gladysChat.setUnreadMessagesCount(1);

        FirebaseFirestore.getInstance().collection("StoryModeChats")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("ChatList")
                .document(gladysChat.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        gladysChat = task.getResult().toObject(Chat.class);
                    }
                }
            }
        });

    }

    private void gotoSeeAllLeadBoard() {
        Intent intent = new Intent(MainActivity.this, LeadBoardActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
    }

    private void gotoAdminTools() {
        Intent intent = new Intent(MainActivity.this, AdminToolsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
    }

    private void startChatWithGladys() {
        Intent intent = new Intent(MainActivity.this, GameConversationActivity.class);
        intent.putExtra(GameConversationActivity.CHAT_UID, gladysChat.getId());
        intent.putExtra(GameConversationActivity.SCENE_INDEX, Integer.valueOf(gladysChat.getProperty("scene-index").toString()));
        intent.putExtra(GameConversationActivity.CONVERSATION_INDEX, Integer.valueOf(gladysChat.getProperty("conversation-index").toString()));
        intent.putExtra(GameConversationActivity.LAST_MESSAGE_INDEX, Integer.valueOf(gladysChat.getProperty("last-message-index").toString()));
        intent.putExtra(GameConversationActivity.UNREAD_MESSAGES_COUNT, gladysChat.getUnreadMessagesCount());
        intent.putExtra(GameConversationActivity.LAST_RESPONDER,
                gladysChat.hasProperty("last-responder") ? gladysChat.getProperty("last-responder").toString() : null);
        startActivityForResult(intent, CONVERSATION_ACTIVITY_REQUEST_CODE);
        overridePendingTransition(R.anim.bounce_down, R.anim.slide_out_down_anim);
    }

    private void gotoSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.bounce_right, R.anim.slide_out_left_anim);
    }

    private void gotoLearning() {
        Intent intent = new Intent(MainActivity.this, LearningActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.bounce_down, R.anim.slide_out_down_anim);
    }

    private void gotoProfile() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.bounce_left, R.anim.slide_out_right_anim);
    }


    private void setupAnimations() {
        TechupAnimationUtils.slideX(story_mode, -2000, 0, 500, null);
        TechupAnimationUtils.slideX(quick_game, 2000, 0, 500, null);
        if (user.hasInfoKey("is-admin") && (boolean) user.getInfo("is-admin"))
            TechupAnimationUtils.slideY(admin_tools, 500, 0, 500, null);
        else
            TechupAnimationUtils.slideY(see_all, 500, 0, 500, null);

        TechupAnimationUtils.slideY(logout, 500, 0, 500, null);

        story_mode.postDelayed(new Runnable() {
            @Override
            public void run() {
                TechupAnimationUtils.bounceY(story_mode, 8f, 500);
                TechupAnimationUtils.bounceY(logout, 8f, 500);
            }
        }, 300);

        quick_game.postDelayed(new Runnable() {
            @Override
            public void run() {
                TechupAnimationUtils.bounceY(quick_game, 8f, 500);
                TechupAnimationUtils.bounceY(see_all, 8f, 500);
            }
        }, 400);

        TechupAnimationUtils.scaleInOut(findViewById(R.id.lead_board), 0.9f, 1000);

    }

    private void startEventListeners() {
        story_mode.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                Intent intent = new Intent(MainActivity.this, StoryModeActivity.class);
                startActivity(intent);
            }
        });

        quick_game.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                Intent intent = new Intent(MainActivity.this, QuickGameActivity.class);
                startActivity(intent);
            }
        });

        gladys.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                startChatWithGladys();
            }
        });

        settings.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                gotoSettings();
            }
        });

        learning.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                gotoLearning();
            }
        });

        see_all.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                gotoSeeAllLeadBoard();
            }
        });

        findViewById(R.id.image).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                gotoProfile();
            }
        });

        findViewById(R.id.name_frame).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                gotoProfile();
            }
        });

        logout.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                FirebaseAuth.getInstance().signOut();
                TechupApplication preference = ((TechupApplication) getApplicationContext());
                preference.setUser(null);
                preference.deactivateRememberUserPreference();
                preference.resetCurrentUser();

                Intent intent = new Intent(MainActivity.this, StartUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (user == null)
            user = ((TechupApplication) getApplicationContext()).getUser();

        if (user.hasInfoKey("is-admin") && (boolean) user.getInfo("is-admin")) {
            see_all.setVisibility(View.GONE);
            admin_tools.setVisibility(View.VISIBLE);
            admin_tools.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
                @Override
                public void runOnClick(View v) {
                    gotoAdminTools();
                }
            });
        }

        FirebaseFirestore.getInstance().collection("Questions")
                .whereEqualTo("chatUID", ChatBot.getUID())
                .whereEqualTo("email", user.getEmail())
                .whereEqualTo("seen", false)
                .whereEqualTo("hasResponse", true)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() > 0)
                        ((TextView) findViewById(R.id.response_alert))
                                .setText(String.valueOf(task.getResult().getDocuments().size()));
                    else findViewById(R.id.response_alert).setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GLADYS_INTRO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Helper.shortToast(getApplicationContext(), "You completed the introduction.");
            } else {
                Helper.shortToast(getApplicationContext(), "You canceled the introduction.");
            }
        }

    }
}
