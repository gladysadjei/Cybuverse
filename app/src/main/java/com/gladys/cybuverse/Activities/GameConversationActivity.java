package com.gladys.cybuverse.Activities;


import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gladys.cybuverse.Adapters.MediaContentOptionsRecyclerViewAdapter;
import com.gladys.cybuverse.Adapters.OnListItemInteractionListener;
import com.gladys.cybuverse.Adapters.ProgrammedMessagesAdapter;
import com.gladys.cybuverse.Adapters.TextContentOptionsRecyclerViewAdapter;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Actor;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ChatBot;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ChatBotResponse;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Conversation;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ConversationReader;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Message;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageContent;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageGetter;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Scene;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.ProfanityChecker.ProfanityFilter;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.ScenesCollection;
import com.gladys.cybuverse.Helpers.AlertTechupDialog;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.PlaySoundOnClickListener;
import com.gladys.cybuverse.Helpers.ProgressTechupDialog;
import com.gladys.cybuverse.Helpers.TechupAnimationUtils;
import com.gladys.cybuverse.Helpers.TechupApplication;
import com.gladys.cybuverse.Helpers.TechupDialog;
import com.gladys.cybuverse.Models.Avatar;
import com.gladys.cybuverse.Models.Chat;
import com.gladys.cybuverse.Models.ParcelableMessage;
import com.gladys.cybuverse.Models.Question;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.gladys.cybuverse.Utils.APIClientTool.NLPRestApiClient.Document;
import com.gladys.cybuverse.Utils.APIClientTool.NLPRestApiClient.NLPClient;
import com.gladys.cybuverse.Utils.GameBase.EventListener;
import com.gladys.cybuverse.Utils.GeneralUtils.Funcs;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.Variable;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NULL;
import static android.text.InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
import static android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;
import static java.util.Objects.requireNonNull;


public class GameConversationActivity extends AppCompatActivity {

    public static final int LETTER_TYPING_DELAY = 250;
    public static final int READ_DELAY = 4000;

    public static final String LAST_MESSAGE_INDEX = "last-message-index";
    public static final String CONVERSATION_INDEX = "conversation-index";
    public static final String UNREAD_MESSAGES_COUNT = "unread-messages-count";
    public static final String LAST_RESPONDER = "last-responder";
    public static final String SCENE_INDEX = "scene-index";
    public static final String CHAT_UID = "chat-uid";

    private User mUser;
    private Conversation conversation;
    private ConversationReader reader;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore mFireStore;
    private GridLayout gallerySelectorOptions;
    private RecyclerView messagesRV, textContentOptionsRV, mediaContentOptionsRV;

    private Handler mHandler;
    private Message lastMessageReceived;
    private ProfanityFilter profanityFilter;
    private ScenesCollection scenesCollection;

    private TechupDialog techupDialog;


    private View typing;
    private CardView selectors;
    private EditText messageTextInput, searchTextInput;
    private ImageButton content_option_toggle_button, sendButton;
    private ImageView home, settings, search;
    private ProgrammedMessagesAdapter messagesAdapter;
    private TextContentOptionsRecyclerViewAdapter textContentOptionsAdapter;
    private MediaContentOptionsRecyclerViewAdapter mediaContentOptionsAdapter;

    private MessageGetter lastMessageGetting;
    private Integer currentContentOptionType;
    private boolean isWaitingForMessage;
    private boolean isChatWithGladys;
    private boolean hasInterrupt;
    private int scene_index,
            conversation_index,
            last_message_index, mPoints;

    private String last_responder;

    private Runnable mSceneRunnerRunnable;
    private Runnable mNextMessageReaderRunnable;

    private List<Runnable> mResumableRunnableList;

    private boolean mIsRunning;
    private TechupApplication mPreference;
    private Drawable chatProfileImageDrawable;
    private boolean isEmpyConversation;
    private boolean isKeyboardShowing;

    private Chat mChat;
    private int mNextReadDelayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_conversation);

        initializeViews();
    }

    private void initializeViews() {
        mNextReadDelayTime = 0;
        mHandler = new Handler();
        mResumableRunnableList = new ArrayList<>();
        mFireStore = FirebaseFirestore.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mPreference = ((TechupApplication) getApplicationContext());
        mUser = mPreference.getUser();
        scenesCollection = new ScenesCollection(mUser);

        String backgroundUri = mPreference.getWallPaperPreference();
        if (!backgroundUri.isEmpty()) {
            File file = new File(Uri.parse(backgroundUri).getPath());
            if (file.exists()) {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), Uri.parse(backgroundUri).getPath());
                findViewById(R.id.root_view).setBackground(bitmapDrawable);
            } else mPreference.setAppSettings(TechupApplication.WALLPAPER_URI, "");
        }

        scene_index = getIntent().getExtras().getInt(SCENE_INDEX);
        conversation_index = getIntent().getExtras().getInt(CONVERSATION_INDEX);
        last_message_index = getIntent().getExtras().getInt(LAST_MESSAGE_INDEX);
        last_responder = getIntent().getExtras().getString(LAST_RESPONDER);

        Helper.log("updated user index",
                "sceneIndex: " + scene_index,
                "conversationIndex: " + conversation_index,
                "lastMessage: " + last_message_index
        );

        mChat = scenesCollection.get(scene_index).getConversations()
                .get(conversation_index).getChat();
        Helper.log("chat: "+mChat);
        Helper.log("chatUID: "+mChat.getId());

        if (mChat == ChatBot.asChat()) {
            ((TextView) findViewById(R.id.name)).setText(ChatBot.asChat().getName());
            ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.ic_gladys);
            isChatWithGladys = true;
        } else {
            ((TextView) findViewById(R.id.name)).setText(mChat.getName());

            FirebaseFirestore.getInstance().collection("Avatars")
                    .whereEqualTo("properties.isActor", true)
                    .whereEqualTo("properties.actorName", mChat.getName().toLowerCase().trim())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() == 1) {
                        Avatar avatar = task.getResult().getDocuments().get(0).toObject(Avatar.class);
                        Glide.with(getApplicationContext())
                                .load(Uri.parse(avatar.getImageUri()))
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        chatProfileImageDrawable = resource;
                                        if (messagesAdapter != null) {
                                            messagesAdapter.setChatDrawable(chatProfileImageDrawable);
                                            messagesAdapter.notifyDataSetChanged();
                                        }
                                        ((ImageView) findViewById(R.id.image)).setImageDrawable(resource);
                                    }
                                });
                    }
                }
            });
        }


        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);
        search = findViewById(R.id.search);

        messagesRV = findViewById(R.id.conversationRV);
        textContentOptionsRV = findViewById(R.id.text_content_selection);
        mediaContentOptionsRV = findViewById(R.id.media_horizontal_selection);
        gallerySelectorOptions = findViewById(R.id.media_grid_selection);

        typing = findViewById(R.id.typing);
        selectors = findViewById(R.id.selectors);
        content_option_toggle_button = findViewById(R.id.content_option_selector);
        messageTextInput = findViewById(R.id.message_input_text_area);
        searchTextInput = findViewById(R.id.search_text);
        sendButton = findViewById(R.id.message_send_btn);
        isWaitingForMessage = false;
        hasInterrupt = false;

        textContentOptionsAdapter = new TextContentOptionsRecyclerViewAdapter(new OnListItemInteractionListener<MessageContent>() {
            public void onItemInteraction(MessageContent item, int position, Object interactionType) {
                messageTextInput.setText(item.getData().toString());
            }
        });

        mediaContentOptionsAdapter = new MediaContentOptionsRecyclerViewAdapter(new OnListItemInteractionListener<MessageContent>() {
            public void onItemInteraction(MessageContent item, int position, Object interactionType) {

            }
        });

        textContentOptionsRV.setLayoutManager(new LinearLayoutManager(this));
        mediaContentOptionsRV.setLayoutManager(new LinearLayoutManager(this));
        textContentOptionsRV.setAdapter(textContentOptionsAdapter);
        mediaContentOptionsRV.setAdapter(mediaContentOptionsAdapter);

        startMessagesAdapter();

        home.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                gotoHome();
            }
        });

        settings.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                gotoSettings();
            }
        });

        search.setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            boolean isOpen = false;

            @Override
            public void runOnClick(View v) {
                if (isOpen) {
                    closeSearchView();
                } else {
                    openSearchView();
                }
                isOpen = !isOpen;
            }
        });

        messageTextInput.setFocusable(true);
        searchTextInput.setFocusable(true);
        messageTextInput.requestFocus();

        searchTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                messagesAdapter.openSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        messageTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.log("In messageText OnClick");

                if (selectors.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    hideSelector();
                    selectors.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showKeyboard(messageTextInput);
                        }
                    }, 210);
                }
            }
        });

        messageTextInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Helper.log("In messageText FocusChange: hasFocus: " + hasFocus);

                if (hasFocus && selectors.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    hideSelector();
                    selectors.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showKeyboard(messageTextInput);
                        }
                    }, 210);
                }
            }
        });

        searchTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.log("In searchText OnClick");
                if (selectors.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    hideSelector();
                    selectors.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showKeyboard(searchTextInput);
                        }
                    }, 210);
                }
            }
        });


        searchTextInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Helper.log("In searchText FocusChange: hasFocus: " + hasFocus);
                if (hasFocus && selectors.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    hideSelector();
                    selectors.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showKeyboard(searchTextInput);
                        }
                    }, 210);
                }
            }
        });

        content_option_toggle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentContentOptionType != null) {
                    if (selectors.getVisibility() == View.GONE) {
                        showSelector(currentContentOptionType);
                    } else {
                        hideSelector();
                    }
                }
            }
        });

        getWindow().getDecorView().getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                getWindow().getDecorView().getRootView().getWindowVisibleDisplayFrame(r);
                int screenHeight = getWindow().getDecorView().getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                // 0.15 ratio is perhaps enough to determine keypad height.
                isKeyboardShowing = keypadHeight > screenHeight * 0.15;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = messageTextInput.getText().toString().trim();
                if (!content.trim().isEmpty()) {

                    messageTextInput.setText("");

                    messagesAdapter.addMessage(
                            new Message(new Actor(mUser.getName()), new Actor(mChat.getName()),
                                    new MessageContent(content, MessageContent.TYPE_TEXT)));
                    messagesRV.scrollToPosition(messagesAdapter.getDataSet().size() - 1);

                    sendQuestionToAdmins(content);
                    profanityFilterize(content);
                    analyzeMessageContentForPoints(content);

                }
            }

        });

        profanityFilter = TechupApplication.getProfanityFilter();

        mSceneRunnerRunnable = new Runnable() {
            @Override
            public void run() {
                runScene();
            }
        };

        mNextMessageReaderRunnable = new Runnable() {
            @Override
            public void run() {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        readNextMessage();
                    }
                }, mNextReadDelayTime);
            }
        };


    }

    private void startMessagesAdapter() {

        messagesAdapter = new ProgrammedMessagesAdapter(new ArrayList<Message>(),
                new OnListItemInteractionListener<Message>() {
                    public void onItemInteraction(Message item, int position, Object interactionType) {
                        Message message = item;
                        //TODO: show info about sender //name role relationship job etc
                    }
                });

        messagesAdapter.setUser(mUser);
        messagesAdapter.setDisplaySize(Helper.getDeviceSize(this));
        if (chatProfileImageDrawable != null)
            messagesAdapter.setChatDrawable(chatProfileImageDrawable);
        messagesRV.setLayoutManager(new LinearLayoutManager(this));
        messagesRV.setAdapter(messagesAdapter);

        //read previous messages and set last messages size
        mFireStore.collection("StoryModeChats")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection(mChat.getId()).orderBy("id").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<ParcelableMessage> messages = task.getResult().toObjects(ParcelableMessage.class);
                            for (ParcelableMessage message : messages) {
                                messagesAdapter.addMessage(new Message(
                                        new Actor(message.getSender()),
                                        new Actor("receiver"),
                                        new MessageContent(message.getContent(), MessageContent.TYPE_TEXT)));
                            }
                        }

                        showUnreadMessage();
                        postDelayed(mSceneRunnerRunnable, 1000);
                    }
                });

    }

    private void showUnreadMessage() {
        //TODO: show unread messages
        int unreadMessagesCount = getIntent().getExtras().getInt(UNREAD_MESSAGES_COUNT);
        if (unreadMessagesCount > 0) {
            for (int i = 0; i < unreadMessagesCount; i++) {
                List<Message> messages = scenesCollection.get(scene_index).getConversations().get(conversation_index).getMessages();
                if (last_message_index < messages.size()) {
                    Message message = messages.get(last_message_index);
                    last_message_index++;
                    messagesAdapter.addMessage(message);
                    messagesAdapter.notifyItemInserted(messagesAdapter.getDataSet().size() - 1);
                } else {
                    break;
                }
            }
        }
    }

    private void gotoSettings() {
        //TODO: do conversation cancel
        Intent intent = new Intent(GameConversationActivity.this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
    }

    private void gotoHome() {
        //TODO: do conversation cancel
        if (conversation == null || (isChatWithGladys && last_message_index >= conversation.getMessages().size())) {
            setResult(RESULT_CANCELED);
            startActivity(new Intent(GameConversationActivity.this, MainActivity.class));
            finish();
            overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
        } else {
            if (isEmpyConversation) {
                setResult(RESULT_CANCELED);
                startActivity(new Intent(GameConversationActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
            } else {
                startEndProcess(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_CANCELED);
                        startActivity(new Intent(GameConversationActivity.this, MainActivity.class));
                        finish();
                        overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
                    }
                });
            }
        }

    }

    private void openSearchView() {
        Animation animation = new ScaleAnimation(0, 1, 1, 1,
                messageTextInput.getX() + messageTextInput.getWidth(),
                messageTextInput.getY() + messageTextInput.getWidth());
        animation.setDuration(500);
        animation.setInterpolator(new LinearOutSlowInInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                searchTextInput.setVisibility(View.VISIBLE);
                Animation transAnimation = new TranslateAnimation(0, -600, 0, 0);
                transAnimation.setDuration(500);
                transAnimation.setInterpolator(new FastOutSlowInInterpolator());
                transAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        findViewById(R.id.nav_details).setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                findViewById(R.id.nav_details).startAnimation(transAnimation);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        searchTextInput.startAnimation(animation);
        search.setImageResource(R.drawable.ic_close_yellow);

    }

    private void closeSearchView() {
        searchTextInput.setText("");

        Animation animation = new ScaleAnimation(1, 0, 1, 1,
                messageTextInput.getX() + messageTextInput.getWidth(),
                messageTextInput.getY() + messageTextInput.getWidth());

        animation.setDuration(500);
        animation.setInterpolator(new LinearOutSlowInInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                Animation transAnimation = new TranslateAnimation(-600, 0, 0, 0);
                transAnimation.setDuration(500);
                transAnimation.setInterpolator(new FastOutSlowInInterpolator());
                transAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        findViewById(R.id.nav_details).setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                findViewById(R.id.nav_details).startAnimation(transAnimation);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                searchTextInput.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        searchTextInput.startAnimation(animation);

        search.setImageResource(R.drawable.ic_search_white_large);

    }

    private void enableGladysRandomTextReply() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = messageTextInput.getText().toString().trim();

                if (!content.trim().isEmpty()) {

                    messageTextInput.setText("");

                    profanityFilterize(content);

                    messagesAdapter.addMessage(
                            new Message(new Actor(mUser.getName()), ChatBot.asActor(),
                                    new MessageContent(content, MessageContent.TYPE_TEXT)));
                    messagesRV.scrollToPosition(messagesAdapter.getDataSet().size() - 1);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gladysReplyToMessage(content);
                        }
                    }, 2000);

                }

            }

        });
    }

    private void gladysReplyToMessage(final String content) {

        final ChatBotResponse responses = ChatBot.getInstance().respondToMessage(new MessageContent(content, MessageContent.TYPE_TEXT));

        List<ChatBotResponse.Response> messages = responses.getResponses();

        int i;

        for (i = 0; i < messages.size(); i++) {
            final ChatBotResponse.Response response = messages.get(i);

            postDelayed(new Runnable() {
                @Override
                public void run() {

                    final Variable<String> message = new Variable<>(response.getMessage());

                    Runnable mGladysResponseRunnable = new Runnable() {
                        @Override
                        public void run() {

                            for (String replacement : response.getReplacements()) {
                                switch (replacement) {
                                    case "<user-name>":
                                        message.setValue(message.getValue().replaceAll("<user-name>", mUser.getName()));
                                        break;
                                    case "<user-email>":
                                        message.setValue(message.getValue().replaceAll("<user-email>", mUser.getEmail()));
                                        break;
                                    case "<user-points>":
                                        message.setValue(message.getValue().replaceAll("<user-points>", mUser.getInfo("points").toString()));
                                        break;
                                    case "<user-progress>":
                                        message.setValue(message.getValue().replaceAll("<user-progress>", getUserProgress(mUser)));
                                        break;
                                    case "<gladys-name>":
                                        message.setValue(message.getValue().replaceAll("<gladys-name>", "Gladys"));
                                        break;
                                    case "<gladys-email>":
                                        message.setValue(message.getValue().replaceAll("<gladys-email>", "gladysadjei@gmail.com"));
                                        break;
                                    case "<gladys-hobby>":
                                        message.setValue(message.getValue().replaceAll("<gladys-hobby>", "reading story books and watching memes."));
                                        break;
                                    case "<gladys-job>":
                                        message.setValue(message.getValue().replaceAll("<gladys-job>", "assist you in whatever way i can."));
                                }
                            }

                            hideTypingMessage();

                            messagesAdapter.addMessage(
                                    new Message(ChatBot.asActor(), new Actor(mUser.getName()),
                                            new MessageContent(message.getValue(), MessageContent.TYPE_TEXT)));
                            messagesRV.scrollToPosition(messagesAdapter.getDataSet().size() - 1);
                        }
                    };

                    showTypingMessage();

                    postDelayed(mGladysResponseRunnable,
                            message.getValue().length() * 40 < 5000 ?
                                    message.getValue().length() * 40 : 5000);
                }
            }, 3000 * i + 1);
        }

        if (responses.getFunction() != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    String functionName = responses.getFunction();
                    switch (functionName) {
                        case "logout-user":
                            logoutUser();
                            break;
                        case "send-to-admins":
                            sendQuestionToAdmins(content);
                            break;
                    }
                }
            }, (i + 1) * 5000);
        }
    }

    private void sendQuestionToAdmins(String userMessage) {
        Helper.log("chatUID: "+mChat.getId());
        FirebaseFirestore.getInstance().collection("Questions")
                .add(new Question(mChat.getId(), mUser, userMessage));
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        TechupApplication preference = ((TechupApplication) getApplicationContext());
        preference.setUser(null);
        preference.deactivateRememberUserPreference();
        preference.resetCurrentUser();
        Intent intent = new Intent(GameConversationActivity.this, StartUpActivity.class);
        startActivity(intent);
        finish();
    }

    private String getUserProgress(User user) {
        if (Integer.valueOf(user.getInfo("scene-index").toString()) == 0) {
            return "you are a new user please play the game to see your progress. " +
                    "you could play the story mode or chat a new friend with quick game";
        }

        Scene scene = scenesCollection.get(Integer.valueOf(user.getInfo("scene-index").toString()));

        return "you are currently playing on the scene " + scene.getName() +
                ". you have your next chat with " + scene.getConversations()
                .get(Integer.valueOf(user.getInfo("conversation-index")
                        .toString())).getChat().getName();
    }

    private void runScene() {

        Scene scene = scenesCollection.getScenesList().get(scene_index);
        conversation = scene.getConversations().get(conversation_index);

        Helper.log("info: " + mUser.getInfo());
        Helper.log("scene_index: " + scene_index + " -- conversation_index: " + conversation_index + " last-message-index: " + last_message_index);

        if (last_message_index < conversation.getMessages().size()) {
            Helper.log("conversation size: " + conversation.getMessages().size());
            reader = new ConversationReader(conversation);

            if (last_responder == null || last_responder.equals(mCurrentUser.getUid()))
                reader.setReadIndex(last_message_index);
            else
                reader.setReadIndex(last_message_index - 1);

            postNow(mNextMessageReaderRunnable);
        } else {
            if (isChatWithGladys) {
                disableContentOption();
                enableGladysRandomTextReply();
                loadRepliesFromAdmins();
            } else {
                isEmpyConversation = true;
                Helper.shortToast(getApplicationContext(), "user not online!");
            }
        }

    }

    private void loadRepliesFromAdmins() {
        FirebaseFirestore.getInstance().collection("Questions")
                .whereEqualTo("chatUID", mChat.getId())
                .whereEqualTo("email", mUser.getEmail())
                .whereEqualTo("hasResponse", true)
                .whereEqualTo("seen", false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : requireNonNull(task.getResult()).getDocuments()) {
                                messagesAdapter.addQuestionReply(requireNonNull(doc.toObject(Question.class)));
                                doc.getReference().update("seen", true);
                            }
                        }
                    }
                });
    }

    private void readNextMessage() {
        if (reader.hasNext()) {
            Helper.log("reading next conversations message");
            if (hasInterrupt) {
                postDelayed(mNextMessageReaderRunnable, 1000);
            } else {
                final Message message = reader.readNext();
                if (!(message instanceof MessageGetter)){
                    mNextReadDelayTime = message.getContent().getData().toString().length() * 5;
                    if  (mNextReadDelayTime > 20000) mNextReadDelayTime = 20000;
                }else{
                    mNextReadDelayTime = 0;
                }
                postNow(new Runnable() {
                    @Override
                    public void run() {
                        readMessage(message);
                    }
                });
            }
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    conversationEnded();
                }
            }, 2500);
        }
    }

    private void readMessage(final Message message) {

        if (message instanceof MessageGetter) {
            postNow(new Runnable() {
                @Override
                public void run() {
                    proceedReadMessage(message);
                }
            });
        } else {
            Runnable mProcessReadMessageRunnable = new Runnable() {
                @Override
                public void run() {
                    postNow(new Runnable() {
                        @Override
                        public void run() {
                            proceedReadMessage(message);
                        }
                    });
                }
            };
            postDelayed(mProcessReadMessageRunnable, READ_DELAY);
        }
    }

    private void proceedReadMessage(final Message message) {

        if (message != null) {
            if (message.hasProperty("is_interrupt") &&
                    (boolean) message.getProperty("is_interrupt")) {
                Helper.log("reading interrupt message");

                showTypingMessage();
                hasInterrupt = true;
                final int delayTime = message.getContent().getData().toString().length() * LETTER_TYPING_DELAY;

                //TODO: end animate typing
                //                              TODO: decide to keep line - lastMessageReceived = message;
                Runnable mProcessInterruptRunnable = new Runnable() {
                    @Override
                    public void run() {
                        //TODO: end animate typing
                        hideTypingMessage();

                        if (message.hasProperty("owner")) {

                            if (((MessageGetter) message.getProperty("owner")).isEmptyContent()) {
                                messagesAdapter.getDataSet().add(message);
                                messagesAdapter.notifyItemInserted(messagesAdapter.getDataSet().size() - 1);
                                messagesRV.scrollToPosition(messagesAdapter.getDataSet().size() - 1);
                                hideSelector();
//                              TODO: decide to keep line - lastMessageReceived = message;
                            }
                        } else {
                            messagesAdapter.getDataSet().add(message);
                            messagesAdapter.notifyItemInserted(messagesAdapter.getDataSet().size() - 1);
                            messagesRV.scrollToPosition(messagesAdapter.getDataSet().size() - 1);
                            hideSelector();
                        }

                        hasInterrupt = false;
                    }

                };

                postDelayed(mProcessInterruptRunnable, (delayTime < 10000) ? delayTime : 10000);

            } else {
                Helper.log("processing message: " + message);

                if (message instanceof MessageGetter) {
                    //TODO: get message
                    MessageGetter messageGetter = (MessageGetter) message;
                    processMessageGetter(messageGetter);
                } else {

                    currentContentOptionType = null;

                    if (!message.hasProperty("gladys-info")) {
                        showTypingMessage();
                    }

                    final int delayTime = message.getContent().getData().toString().length() * LETTER_TYPING_DELAY;

                    //TODO: end animate typing
                    Runnable mReadMessageRunnable = new Runnable() {
                        @Override
                        public void run() {
                            //TODO: end animate typing
                            hideTypingMessage();

                            messagesAdapter.getDataSet().add(message);
                            messagesAdapter.notifyItemInserted(messagesAdapter.getDataSet().size() - 1);
                            messagesRV.scrollToPosition(messagesAdapter.getDataSet().size() - 1);
                            lastMessageReceived = message;
                            hideSelector();

                            postNow(mNextMessageReaderRunnable);

                        }
                    };

                    postDelayed(mReadMessageRunnable, (delayTime < 10000) ? delayTime : 10000);
                }
            }
        }

    }

    private void showTypingMessage() {

        Animation animation = new ScaleAnimation(0, 1f, 0, 1f,
                typing.getPivotX(), typing.getPivotY());
        animation.setDuration(200);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                typing.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TechupAnimationUtils.bounceY(typing, 8f, 200);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        typing.startAnimation(animation);
    }

    private void hideTypingMessage() {

        Animation animation = new ScaleAnimation(1f, 0, 1f, 0,
                typing.getPivotX(), typing.getPivotY());
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                typing.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        typing.startAnimation(animation);
    }

    private void processMessageGetter(final MessageGetter messageGetter) {
        Helper.log("called processMessageGetter");
        lastMessageGetting = messageGetter;

        messageGetter.setOnInterruptListener(new MessageGetter.OnInterruptListener(){

            @Override
            public void onInterrupt(Message message, boolean isReadNextMessage) {
                proceedReadMessage(message);
                if (isReadNextMessage){
                    postNow(mNextMessageReaderRunnable);
                }
            }

        });

        List<MessageContent> options = new ArrayList<>();

        if (messageGetter.hasContentOptions()) {
            options = messageGetter.getContentOptions();
        } else if (lastMessageReceived != null){
            options = ChatBot.getInstance().generateContentOptions(lastMessageReceived.getContent());
        }

//        enableContentOption();
//        enableTextInputField();

        //setting up content options
        if (!options.isEmpty()) {
            switch (messageGetter.getExpectedContentType()) {
                case MessageContent.TYPE_TEXT:
                    enableContentOption();
//                    if (!messageGetter.isContentMustBeInOptions()) {
//                        disableTextInputField();
//                    }
                    showSelector(R.id.text_content_selection);
                    currentContentOptionType = R.id.text_content_selection;
                    textContentOptionsAdapter.getDataSet().clear();
                    textContentOptionsAdapter.getDataSet().addAll(options);
                    textContentOptionsAdapter.notifyDataSetChanged();
                    break;
                case MessageContent.TYPE_MEDIA:
//                    disableTextInputField();
//                    if (!messageGetter.isContentMustBeInOptions()) {
//                        disableContentOption();
//                    }
                    showSelector(R.id.media_type_selector);
                    currentContentOptionType = R.id.media_type_selector;
                    mediaContentOptionsAdapter.getDataSet().clear();
                    mediaContentOptionsAdapter.getDataSet().addAll(options);
                    mediaContentOptionsAdapter.notifyDataSetChanged();
                    break;
                default:
                    showSelector(R.id.text_and_media_selection);
                    currentContentOptionType = R.id.text_and_media_selection;
                    textContentOptionsAdapter.getDataSet().clear();
                    mediaContentOptionsAdapter.getDataSet().clear();

                    for (MessageContent content : options) {
                        switch (content.getType()) {
                            case MessageContent.TYPE_TEXT:
                                textContentOptionsAdapter.getDataSet().add(content);
                                textContentOptionsAdapter.notifyItemInserted(messagesAdapter.getDataSet().size() - 1);
                                break;
                            case MessageContent.TYPE_MEDIA:
                                mediaContentOptionsAdapter.getDataSet().add(content);
                                mediaContentOptionsAdapter.notifyItemInserted(messagesAdapter.getDataSet().size() - 1);
                                break;
                            default:
                                //TYPE ALL ANY
                                textContentOptionsAdapter.getDataSet().add(content);
                                textContentOptionsAdapter.notifyItemInserted(messagesAdapter.getDataSet().size() - 1);
                                break;
                        }
                    }
                    break;
            }

        } else {
            currentContentOptionType = null;
        }

        //starting listener for message
        waitForMessage(true);

        messageGetter.setListener(new EventListener() {
            @Override
            public void processEvent(Event event) {
                switch (event.getName()) {
                    case MessageGetter.START_GET_MESSAGE_EVENT:
                        //prepareActivityForMessageType
                        Helper.log("get message from mUser process started");
                        break;
                    case MessageGetter.WAIT_TIMER_START_EVENT:
//                        Helper.log("started timing for message content");
                        break;
                    case MessageGetter.WAIT_TIMER_TICK_EVENT:
//                        Helper.log("timer event: waiting for message : TIMEOUT = " +
//                                event.getProperty(MessageGetter.DATA_TIMEOUT) + " : COUNTDOWN = " +
//                                event.getProperty(MessageGetter.DATA_MILLIS));
                        break;
                    case MessageGetter.WAIT_TIMER_END_EVENT:
                        break;
                    case MessageGetter.MESSAGE_NOT_RECEIVED_EVENT:
                        Helper.log("failed to receive message");
                        Helper.longToast(getApplicationContext(), "you did'nt enter any message");
                        break;
                    case MessageGetter.MESSAGE_RECEIVED_EVENT:
                        Helper.log("received message: " + event.getProperty(MessageGetter.DATA_CONTENT));
                        currentContentOptionType = null;
                        hideSelector();
                        break;
                    case MessageGetter.WRONG_MESSAGE_RECEIVED_EVENT:
                        Helper.log("Error: " + event.getName());
                        Helper.longToast(getApplicationContext(), "your response is not valid!");
                        break;
                    case MessageGetter.END_GET_MESSAGE_EVENT:
                        Helper.log("get message process ended");
                        hideSelector();
                        break;
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageContent currentMessageContent;
                String content = messageTextInput.getText().toString().trim();
                ArrayList<Uri> mediaUriList = getSelectedMediaItemsUriList();

                if (isWaitingForMessage) {
                    Helper.log("send button pressed");

                    if (messageGetter.getExpectedContentType().equals(MessageContent.TYPE_TEXT) ||
                            messageGetter.getExpectedContentType().equals(MessageContent.TYPE_ALL)) {
                        if (!content.trim().isEmpty()) {
                            currentMessageContent = new MessageContent(content, MessageContent.TYPE_TEXT);
                            Helper.log("messageContent: " + currentMessageContent.toString());
                            profanityFilterize(content);
                            messageGetter.setContent(currentMessageContent);
                            if (messageGetter.isEmptyContent()) {
                                Helper.shortToast(getApplicationContext(), "please select message from list");
                                Helper.log("cant send content: " + content);
                            } else {
                                messageTextInput.setText("");
                                messagesAdapter.getDataSet().add(messageGetter);
                                messagesAdapter.notifyItemInserted(messagesAdapter.getDataSet().size() - 1);
                                messagesRV.scrollToPosition(messagesAdapter.getDataSet().size() - 1);
                                analyzeMessageContentForPoints(content);
                                currentContentOptionType = null;
                                waitForMessage(false);
                                hideSelector();

                                postNow(mNextMessageReaderRunnable);
                            }
                        }
                    }
//                    else {
                    //TODO: getValueInTheMediaView
//                    }
                } else {
                    Message newMessage = new Message();
                    newMessage.setSender(messageGetter.getSender());
                    if (!content.isEmpty()) {
                        newMessage.setContent(new MessageContent(content));
                    }
                    if (!mediaUriList.isEmpty()) {
                        messageGetter.setProperty("uri-list", mediaUriList);
                    }
                    if (!newMessage.isEmptyContent() || newMessage.hasProperty("uri-list")) {
                        sendQuestionToAdmins(content);
                        profanityFilterize(content);
                        analyzeMessageContentForPoints(content);
                        messagesAdapter.getDataSet().add(newMessage);
                        messagesAdapter.notifyItemInserted(messagesAdapter.getDataSet().size() - 1);
                        messagesRV.scrollToPosition(messagesAdapter.getDataSet().size() - 1);
                        messageTextInput.setText("");
                    }
                }
            }
        });

        messageGetter.waitForMessage();

    }

    private void runOrSetAsLastRunnable(Runnable runnable) {
        if (mIsRunning) {
            runnable.run();
        } else {
            mResumableRunnableList.add(runnable);
        }
    }

    private void postDelayed(final Runnable runnable, long millis) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOrSetAsLastRunnable(runnable);
            }
        }, millis);
    }

    private void postNow(final Runnable runnable) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                runOrSetAsLastRunnable(runnable);
            }
        });
    }

    private void analyzeMessageContentForPoints(String content) {
        NLPAsyncTask nlpAsyncTask = new NLPAsyncTask(getString(R.string.google_api_key));
        nlpAsyncTask.execute(content);
    }

    private void profanityFilterize(String content) {
        ProfanityFilterer profanityFilter = new ProfanityFilterer();
        profanityFilter.execute(content);
    }

    private void showSelector(int selection_view_id) {
        if (selectors.getVisibility() == View.GONE) {
            if (isKeyboardShowing) {
                hideKeyboard();
            }
            TranslateAnimation animation = new TranslateAnimation(0, 0, 300, 0);
            animation.setInterpolator(new FastOutLinearInInterpolator());
            animation.setDuration(250);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    selectors.setVisibility(View.VISIBLE);
                    content_option_toggle_button.setImageResource(R.drawable.ic_close_yellow);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    messagesRV.scrollToPosition(messagesAdapter.getDataSet().size()-1);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            selectors.startAnimation(animation);
        }
        if (selection_view_id == R.id.text_content_selection) {
            hideSelector(R.id.media_type_selector);
            hideSelector(R.id.text_and_media_selection);
            hideSelector(R.id.media_grid_selection);
            hideSelector(R.id.media_horizontal_selection);
        } else if (selection_view_id == R.id.media_type_selector) {
            hideSelector(R.id.text_content_selection);
            hideSelector(R.id.text_and_media_selection);
            hideSelector(R.id.media_grid_selection);
            hideSelector(R.id.media_horizontal_selection);
        } else if (selection_view_id == R.id.text_and_media_selection) {
            hideSelector(R.id.text_content_selection);
            hideSelector(R.id.media_type_selector);
            hideSelector(R.id.media_grid_selection);
            hideSelector(R.id.media_horizontal_selection);
        }
        selectors.findViewById(selection_view_id).setVisibility(View.VISIBLE);
    }

    private void hideSelector(int selection_view_id) {
        selectors.findViewById(selection_view_id).setVisibility(View.GONE);
    }

    private void hideSelector() {
        if (selectors.getVisibility() == View.VISIBLE) {
            TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 600);
            animation.setInterpolator(new LinearInterpolator());
            animation.setDuration(200);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    selectors.setVisibility(View.GONE);
                    content_option_toggle_button.setImageResource(R.drawable.ic_menu_animated);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            selectors.startAnimation(animation);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (messageTextInput.hasFocus())
                imm.hideSoftInputFromWindow(messageTextInput.getWindowToken(), 0);
            else
                imm.hideSoftInputFromWindow(searchTextInput.getWindowToken(), 0);
        }
    }

    private void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            editText.requestFocus();
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private ArrayList<Uri> getSelectedMediaItemsUriList() {
        return new ArrayList<>();
    }

    private void waitForMessage(boolean wait) {
        isWaitingForMessage = wait;
    }

    private void disableContentOption() {
        content_option_toggle_button.setEnabled(false);
    }

    private void enableContentOption() {
        content_option_toggle_button.setEnabled(true);
    }

    private void disableTextInputField() {
        messageTextInput.clearFocus();
        messageTextInput.setInputType(TYPE_NULL);
        messageTextInput.setFocusable(true);
    }

    private void enableTextInputField() {
        messageTextInput.setFocusable(true);
        messageTextInput.requestFocus();
        messageTextInput.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_NORMAL | TYPE_TEXT_FLAG_AUTO_CORRECT);
    }

    private void conversationEnded() {

        if (isChatWithGladys && last_message_index >= conversation.getMessages().size()) {
            setResult(RESULT_CANCELED);
            finish();
            overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
        } else {
            trySaveChatOrExit();
        }
    }

    private void trySaveChatOrExit() {

        if (Helper.isNetworkAvailable(this)) {

            techupDialog = new AlertTechupDialog(GameConversationActivity.this);
            techupDialog.setMessageText("You scored " + mPoints + " today...Do you want to save");
            ((AlertTechupDialog) techupDialog).setPositiveButton("Save Game", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    techupDialog.dismiss();
                    updateChatMessages();
                }
            });
            ((AlertTechupDialog) techupDialog).setNeutralButton("Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    techupDialog.dismiss();
                    onResume();
                }
            });
            ((AlertTechupDialog) techupDialog).setNegativeButton("Exit", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    techupDialog.dismiss();
                    setResult(RESULT_CANCELED);
                    finish();
                    overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
                }
            });
            techupDialog.setCancelable(false);
            techupDialog.show();
        } else {
            techupDialog = new AlertTechupDialog(GameConversationActivity.this);
            techupDialog.setTitleText("Save Error");
            techupDialog.setMessageText("You do not have any internet connection. please connect and try again.");
            ((AlertTechupDialog) techupDialog).setPositiveButton("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    techupDialog.dismiss();
                    trySaveChatOrExit();
                }
            });
            ((AlertTechupDialog) techupDialog).setNeutralButton("Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    techupDialog.dismiss();
                    onResume();
                }
            });
            ((AlertTechupDialog) techupDialog).setNegativeButton("Exit", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    techupDialog.dismiss();
                    setResult(RESULT_CANCELED);
                    finish();
                    overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
                }
            });
            techupDialog.setCancelable(false);
            techupDialog.show();
        }
    }

    private void updateChatMessages() {

        techupDialog = new ProgressTechupDialog(GameConversationActivity.this);
        techupDialog.setMessageText("Saving...");
        techupDialog.setCancelOnTouchOutside(false);
        techupDialog.show();

        if (isChatWithGladys) {
            updateUserDetails();
        } else {
            //update chat messages
            mFireStore.collection("StoryModeChats")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection(mChat.getId()).orderBy("id").limitToLast(1)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<ParcelableMessage> lastMessage = task.getResult().toObjects(ParcelableMessage.class);
                    if (task.isSuccessful()) {
                        int index = 0;
                        if (!lastMessage.isEmpty()) {
                            index = lastMessage.get(0).getId() + 1;
                        }
                        saveMessagesFromIndex(index);
                    }
                }

            });

        }
    }

    private void saveMessagesFromIndex(final int index) {
        List<ParcelableMessage> messages = getParcelableMessage(messagesAdapter.getDataSet());
        Task<Void> task = null;

        for (int i = index; i < messages.size(); i++) {
            final ParcelableMessage message = messages.get(i);
            message.setId(i);

            if (i == index)
                task = addChatMessage(message);
            else
                task = task.continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) {
                        return addChatMessage(message);
                    }
                });
        }

        if (task != null) {
            task.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        updateUserDetails();
                    } else {
                        techupDialog.dismiss();
                        savingProcessFailed("failed to save game: " + task.getException().getMessage(), new Runnable() {
                            @Override
                            public void run() {
                                saveMessagesFromIndex(index);
                            }
                        });
                    }
                }
            });
        } else {
            techupDialog.dismiss();
            Helper.shortToast(getApplicationContext(), "save successful");
            setResult(RESULT_CANCELED);
            finish();
            overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
        }

    }

    public Task<Void> addChatMessage(ParcelableMessage message) {
        return mFireStore.collection("StoryModeChats")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection(mChat.getId())
                .document(String.valueOf(message.getId())).set(message);
    }

    private void updateUserDetails() {
        mUser.addInfo("scene-index", scene_index);
        mUser.addInfo("conversation-index", conversation_index);
        mUser.addInfo("last-message-index", reader.getReadIndex());

        Helper.shortToast(getApplicationContext(), "you earned " + mPoints + " points");

        mPoints = mPoints + Integer.valueOf(mUser.getInfo("points").toString());

        Helper.log("currentUser" + mUser.getInfo().toString());

        mFireStore.collection("Users")
                .document(mCurrentUser.getUid())
                .update("info.scene-index", scene_index,
                        "info.conversation-index", conversation_index,
                        "info.last-message-index", reader.getReadIndex(),
                        "info.points", mPoints)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateChat();
                        } else {
                            techupDialog.dismiss();
                            savingProcessFailed("failed to save game: " + task.getException().getMessage(), new Runnable() {
                                @Override
                                public void run() {
                                    updateUserDetails();
                                }
                            });
                        }
                    }
                });
    }

    private void updateChat() {
        final Chat chat = getCurrentChatUpdates();

        //update chat info
        mFireStore.collection("StoryModeChats")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("ChatList").document(chat.getId())
                .set(chat)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        techupDialog.dismiss();
                        if (task.isSuccessful()) {
                            mPreference.setUser(mUser);
                            Helper.shortToast(getApplicationContext(), "you have " + mPoints + " total points now");
                            Intent intent = new Intent();
                            Chat chat = getCurrentChatUpdates();
                            intent.putExtra(CHAT_UID, chat.getId());
                            intent.putExtra("last-seen", chat.getLastSeen());
                            intent.putExtra("last-message", chat.getLastMessage());
                            intent.putExtra(SCENE_INDEX, (int) chat.getProperty(SCENE_INDEX));
                            intent.putExtra(CONVERSATION_INDEX, (int) chat.getProperty(CONVERSATION_INDEX));
                            intent.putExtra(LAST_MESSAGE_INDEX, (int) chat.getProperty(LAST_MESSAGE_INDEX));
                            intent.putExtra(LAST_RESPONDER, chat.getProperty(LAST_RESPONDER).toString());
                            intent.putExtra(UNREAD_MESSAGES_COUNT, 0);
                            Helper.shortToast(getApplicationContext(), "save successful");
                            setResult(RESULT_OK, intent);
                            finish();
                            overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
                        } else {
                            savingProcessFailed("failed to save game: " + task.getException().getMessage(), new Runnable() {
                                @Override
                                public void run() {
                                    updateChat();
                                }
                            });
                        }
                    }
                });
    }

    private List<ParcelableMessage> getParcelableMessage(List<Message> dataSet) {
        List<ParcelableMessage> chatMessages = new ArrayList<>();
        for (Message message : dataSet) {
            if (!message.hasProperty("gladys-info"))
                chatMessages.add(new ParcelableMessage(message));
        }
        return chatMessages;
    }

    private void savingProcessFailed(String message, final Runnable onRetryRunnable) {
        techupDialog = new AlertTechupDialog(GameConversationActivity.this);
        techupDialog.setMessageText(message);
        ((AlertTechupDialog) techupDialog).setPositiveButton("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                techupDialog = new ProgressTechupDialog(GameConversationActivity.this);
                techupDialog.setMessageText("Saving...");
                techupDialog.setCancelOnTouchOutside(false);
                techupDialog.show();
                onRetryRunnable.run();
            }
        });
        ((AlertTechupDialog) techupDialog).setNeutralButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                onResume();
            }
        });
        ((AlertTechupDialog) techupDialog).setNegativeButton("Exit", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                setResult(RESULT_CANCELED);
                finish();
                overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
            }
        });
        techupDialog.setCancelable(false);
        techupDialog.show();
    }

    private Chat getCurrentChatUpdates() {

        Chat chat = mChat;

        if (isChatWithGladys) {
            chat.setLastSeen("online");
            chat.addProperty(SCENE_INDEX, scene_index);
            chat.addProperty(CONVERSATION_INDEX, conversation_index);
            chat.getProperties().remove("is-active");
            if (reader != null)
                chat.addProperty(LAST_MESSAGE_INDEX, reader.getReadIndex());
            else
                chat.addProperty(LAST_MESSAGE_INDEX, scenesCollection.get(0).getConversations().get(0).getMessages().size());
        } else {
            chat.setLastSeen(Funcs.datetime("day.number", "month", "year"));
            chat.addProperty(SCENE_INDEX, scene_index);
            chat.addProperty(CONVERSATION_INDEX, conversation_index);
            chat.addProperty(LAST_MESSAGE_INDEX, reader.getReadIndex());

            String lastMessage = (!messagesAdapter.getDataSet().isEmpty()) ? messagesAdapter.getDataSet()
                    .get(messagesAdapter.getDataSet().size() - 1)
                    .getContent().getData().toString() : "";

            if (!lastMessage.trim().isEmpty()) {
                chat.setLastMessage(lastMessage);
            }

        }

        String lastResponder = (!messagesAdapter.getDataSet().isEmpty()) ? messagesAdapter.getDataSet()
                .get(messagesAdapter.getDataSet().size() - 1)
                .getSender().getName() : null;

        if (lastResponder != null) {
            chat.addProperty(LAST_RESPONDER,
                    lastResponder.equals(mUser.getName()) ? mCurrentUser.getUid() : lastResponder);
        }

        chat.setUnreadMessagesCount(0);

        return chat;
    }

    private void startEndProcess(final Runnable onConfirmExit) {
        onPause();
        final AlertTechupDialog techupDialog = new AlertTechupDialog(this);
        techupDialog.setMessageText("Are you sure you want to exit this conversation?..");
        techupDialog.setPositiveButton("No", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                onResume();
            }
        });
        techupDialog.setNeutralButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                Helper.log("isChatWithGladys: " + isChatWithGladys, "conversation: " + conversation);
                if (conversation == null || (isChatWithGladys && last_message_index >= conversation.getMessages().size())) {
                    onConfirmExit.run();
                } else {
                    trySaveChatOrExit();
                }
            }
        });
        techupDialog.setCancelable(false);
        techupDialog.show();
    }

    @Override
    public void onBackPressed() {
        Helper.log("in onBacPressed");
        if (conversation == null || (isChatWithGladys && last_message_index >= conversation.getMessages().size())) {
            setResult(RESULT_CANCELED);
            finish();
            overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
        } else {
            if (isEmpyConversation) {
                setResult(RESULT_CANCELED);
                finish();
                overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
            } else {
                startEndProcess(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_CANCELED);
                        finish();
                        overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
                    }
                });
            }

        }
    }

    @Override
    protected void onResume() {
        Helper.log("in onResume");
        setIsRunning(true);

        if (lastMessageGetting != null && isWaitingForMessage) {
            processMessageGetter(lastMessageGetting);
            lastMessageGetting = null;
        } else if (!mResumableRunnableList.isEmpty() && mHandler != null) {
            for (Runnable runnable : mResumableRunnableList)
                runnable.run();

            mResumableRunnableList.clear();
        }

        super.onResume();
    }

    public void setIsRunning(boolean isRunning) {
        this.mIsRunning = isRunning;
    }

    @Override
    protected void onPause() {
        Helper.log("in onPause");
        setIsRunning(false);

        if (lastMessageGetting != null && isWaitingForMessage) {
            if (lastMessageGetting.getTimer() != null) {
                lastMessageGetting.getTimer().cancel();
            }
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (lastMessageGetting != null && lastMessageGetting.getTimer() != null) {
            lastMessageGetting.getTimer().cancel();
        }

        lastMessageReceived = null;
        lastMessageGetting = null;

        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }

    private class NLPAsyncTask extends AsyncTask<String, String, Integer> {

        private final String apiKey;

        NLPAsyncTask(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getApiKey() {
            return apiKey;
        }

        @Override
        protected Integer doInBackground(final String... strings) {
            String content = strings[0];
            Document document = new Document(Document.PLAIN_TEXT, content);
            Call apiClient = null;
            try {
                NLPClient nlpClient = new NLPClient(getApiKey());
                apiClient = nlpClient.analyzeSentiments(document);
                apiClient.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Helper.log("some error occurred with request: " + request.urlString());
                        mPoints += 3;
                    }

                    @Override
                    public void onResponse(Response response) {
                        Helper.log("DATA: " + response.message());
                        try {
                            String responseString = response.body().string();
                            Helper.log("JSON DATA: " + responseString);
                            //{"input": "ben", "results": {"model": "Google NLP", "extra": "model returns -1 to +1", "url": "https://cloud.google.com/natural-language/", "rScore": 0.2, "nScore": 0.2}}
                            JSONObject jsonObject = new JSONObject(responseString).getJSONObject("results");
                            double rScore = jsonObject.getDouble("rScore");
                            double mScore = jsonObject.getDouble("nScore");
                            double tScore = Math.ceil(((rScore * mScore) * 10) + 3);
                            mPoints = Funcs.toInteger(tScore) + mPoints;
                            Helper.shortToast(getApplicationContext(), "scored " + mPoints + " points");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Helper.log("could not set point: " + e.getMessage());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Helper.log("error calculating points");
            }

            return null;
        }
    }

    private class ProfanityFilterer extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            if (profanityFilter != null) {
                final List<String> matches = new ArrayList<>();
                for (String word : strings[0].split(" ")) {
                    if (profanityFilter.isMatchForWord(word.toLowerCase(), true)) {
                        matches.add(word);
                    }
                }
                if (matches.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Helper.shortToast(getApplicationContext(), "probable hurtful words: " +
                                    matches.toString(), "top");
                            Helper.log("profanity filtered: " + matches.toString());
                        }
                    });
                }
            } else {
                Helper.log("profanity filter is null");
            }
            return null;
        }
    }
}
