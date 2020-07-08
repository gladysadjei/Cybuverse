package com.gladys.cybuverse.Activities;


import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.gladys.cybuverse.Adapters.OnListItemInteractionListener;
import com.gladys.cybuverse.Adapters.QuickGameChatAdapter;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Actor;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.ProfanityChecker.ProfanityFilter;
import com.gladys.cybuverse.Fragments.MediaInputManagerFragment;
import com.gladys.cybuverse.Fragments.MediaPlayerDialogFragment;
import com.gladys.cybuverse.Helpers.AlertTechupDialog;
import com.gladys.cybuverse.Helpers.TechupApplication;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.PlaySoundOnClickListener;
import com.gladys.cybuverse.Helpers.TechupAnimationUtils;
import com.gladys.cybuverse.Models.ChatMessage;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.gladys.cybuverse.Utils.APIClientTool.NLPRestApiClient.Document;
import com.gladys.cybuverse.Utils.APIClientTool.NLPRestApiClient.NLPClient;
import com.gladys.cybuverse.Utils.FileUtils.FileExplorer;
import com.gladys.cybuverse.Utils.GeneralUtils.Funcs;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.Variable;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.TransitionManager;

import static com.gladys.cybuverse.Fragments.MediaInputManagerFragment.TYPE_AUDIO;
import static com.gladys.cybuverse.Fragments.MediaInputManagerFragment.TYPE_FILE;
import static com.gladys.cybuverse.Fragments.MediaInputManagerFragment.TYPE_VOICE;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.toInteger;


public class GameChatRoomActivity extends AppCompatActivity {

    public static final int MESSAGES_TO_LOAD_LIMIT = 15;

    private User user, friend;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore mFireStore;
    private RecyclerView messagesRV;
    private SwipeRefreshLayout swipeRefreshLayout;

    private View typing;
    private CardView selectors;
    private EditText messageTextInput, searchTextInput;
    private ImageButton galleryPicker, sendButton;
    private ImageView home, settings, search;
    private QuickGameChatAdapter messagesAdapter;

    private Integer mMessageLoadMagnitude;
    private Actor actorPlayer, actorFriend;
    private DocumentReference mChatRoomReference;

    private boolean isFriendOnline,
            isNewIncomingMessage,
            isProcessFriendOffline;

    private ChatMessage chatToReply;
    private Handler mHandler;
    private AlertTechupDialog techupDialog;
    private ProfanityFilter profanityFilter;
    private int mPoints;

    private MediaInputManagerFragment mediaInputManagerFragment;
    private boolean isKeyboardOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_chat_room);

        initializeViews();

        prepareActivity();
    }

    private void initializeViews() {

        isFriendOnline = true;
        isNewIncomingMessage = false;
        isProcessFriendOffline = false;
        mMessageLoadMagnitude = 1;

        mHandler = new Handler();
        mFireStore = FirebaseFirestore.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        user = ((TechupApplication) getApplication()).getUser();

        String backgroundUri = ((TechupApplication) getApplicationContext()).getWallPaperPreference();

        if (!backgroundUri.isEmpty()) {
            File file = new File(Uri.parse(backgroundUri).getPath());
            if (file.exists()) {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), Uri.parse(backgroundUri).getPath());
                findViewById(R.id.root_view).setBackground(bitmapDrawable);
            } else
                ((TechupApplication) getApplicationContext()).setAppSettings(TechupApplication.WALLPAPER_URI, "");
        }

        String roomName = getIntent().getStringExtra("room");

        mChatRoomReference = mFireStore.collection("GameChatRoom").document(roomName);

        actorPlayer = new Actor(mCurrentUser.getUid());

        String friendUID = roomName.replace(mCurrentUser.getUid(), "")
                .replace("_", "");

        actorFriend = new Actor(friendUID, "random-friend");

        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);
        search = findViewById(R.id.search);

        messagesRV = findViewById(R.id.conversationRV);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        typing = findViewById(R.id.typing);
        selectors = findViewById(R.id.selectors);
        galleryPicker = findViewById(R.id.message_gallery_item_selector);
        messageTextInput = findViewById(R.id.message_input_text_area);
        searchTextInput = findViewById(R.id.search_text);
        sendButton = findViewById(R.id.message_send_btn);

        mediaInputManagerFragment = new MediaInputManagerFragment(new MediaInputManagerFragment.OnVisibilityChangeListener() {
            @Override
            public void onStart(boolean isVisible, MediaInputManagerFragment fragment) {

            }

            @Override
            public void onEnd(boolean isVisible, MediaInputManagerFragment fragment) {
                if (isVisible) {
                    galleryPicker.setImageResource(R.drawable.ic_close_yellow);
                } else {
                    if (fragment.hasMedia()) {
                        galleryPicker.setImageResource(R.drawable.ic_media_exists);
                    } else {
                        galleryPicker.setImageResource(R.drawable.ic_gallery_item_picker_white);
                    }
                }
            }
        });

        messageTextInput.setFocusable(true);
        messageTextInput.requestFocus();

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
                // keyboard is opened
                isKeyboardOpened = keypadHeight > screenHeight * 0.15;

            }
        });


        galleryPicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Helper.log("in GalleryPicker OnClick");

                if (!messageTextInput.hasFocus() && !searchTextInput.hasFocus()) {
                    messageTextInput.requestFocus();
                    hideKeyboard(messageTextInput);
                }

                if (mediaInputManagerFragment.isMediaInputVisible()) {
                    mediaInputManagerFragment.hide();
                    Helper.log("Hiding");
                } else {
                    Helper.log("Showing");
                    mediaInputManagerFragment.show(new MediaInputManagerFragment.OnVisibilityChangeListener() {
                        @Override
                        public void onStart(boolean isVisible, MediaInputManagerFragment fragment) {
                            Helper.log("In OnStart Animation");
                            Helper.log("keyboard is opened");
                            if (isKeyboardOpened) {
                                if (searchTextInput.hasFocus())
                                    hideKeyboard(searchTextInput);
                                else if (messageTextInput.hasFocus())
                                    hideKeyboard(messageTextInput);
                            } else if (!messageTextInput.hasFocus() && !searchTextInput.hasFocus()) {
                                messageTextInput.requestFocus();
                                hideKeyboard(messageTextInput);
                            }
                        }

                        @Override
                        public void onEnd(boolean isVisible, MediaInputManagerFragment fragment) {
                            Helper.log("In OnEnd Animation");
                        }
                    });
                }
            }
        });

        messageTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.log("In messageText OnClick");

                if (mediaInputManagerFragment.isMediaInputVisible()) {
                    hideKeyboard(messageTextInput);
                    mediaInputManagerFragment.hide(new MediaInputManagerFragment.OnVisibilityChangeListener() {
                        @Override
                        public void onStart(boolean isVisible, MediaInputManagerFragment fragment) {
                            Helper.log("In OnStart Animation");
                        }

                        @Override
                        public void onEnd(boolean isVisible, MediaInputManagerFragment fragment) {
                            Helper.log("In OnEnd Animation");
                            messageTextInput.requestFocus();
                            showKeyboard(messageTextInput);
                        }
                    });
                }
            }
        });

        messageTextInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Helper.log("In messageText FocusChange: hasFocus: " + hasFocus);

                if (hasFocus && mediaInputManagerFragment.isMediaInputVisible()) {
                    hideKeyboard(messageTextInput);
                    mediaInputManagerFragment.hide(new MediaInputManagerFragment.OnVisibilityChangeListener() {
                        @Override
                        public void onStart(boolean isVisible, MediaInputManagerFragment fragment) {
                            Helper.log("In OnStart Animation");
                        }

                        @Override
                        public void onEnd(boolean isVisible, MediaInputManagerFragment fragment) {
                            Helper.log("In OnEnd Animation");
                            messageTextInput.requestFocus();
                            showKeyboard(messageTextInput);
                        }
                    });
                }
            }
        });

        searchTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.log("In searchText OnClick");

                if (mediaInputManagerFragment.isMediaInputVisible()) {
                    hideKeyboard(searchTextInput);
                    mediaInputManagerFragment.hide(new MediaInputManagerFragment.OnVisibilityChangeListener() {
                        @Override
                        public void onStart(boolean isVisible, MediaInputManagerFragment fragment) {
                            Helper.log("In OnStart Animation");
                        }

                        @Override
                        public void onEnd(boolean isVisible, MediaInputManagerFragment fragment) {
                            Helper.log("In OnEnd Animation");
                            searchTextInput.requestFocus();
                            showKeyboard(searchTextInput);
                        }
                    });
                }
            }
        });


        searchTextInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Helper.log("In searchText FocusChange: hasFocus: " + hasFocus);
                if (hasFocus && mediaInputManagerFragment.isMediaInputVisible()) {
                    hideKeyboard(searchTextInput);
                    mediaInputManagerFragment.hide(new MediaInputManagerFragment.OnVisibilityChangeListener() {
                        @Override
                        public void onStart(boolean isVisible, MediaInputManagerFragment fragment) {
                            Helper.log("In OnStart Animation");
                        }

                        @Override
                        public void onEnd(boolean isVisible, MediaInputManagerFragment fragment) {
                            Helper.log("In OnEnd Animation");
                            searchTextInput.requestFocus();
                            showKeyboard(searchTextInput);
                        }
                    });
                }
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.mediaInputContainer, mediaInputManagerFragment).commit();

        profanityFilter = ((TechupApplication) getApplicationContext()).getProfanityFilter();

    }

    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        Helper.log("Hiding KeyBoard");
    }

    private void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        Helper.log("Showing KeyBoard");
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

    private void prepareActivity() {
        mFireStore.collection("Users").document(actorFriend.getName()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            friend = task.getResult().toObject(User.class);
                            ((TextView) findViewById(R.id.name)).setText(friend.getName());

                            Glide.with(getApplicationContext())
                                    .load(friend.getProfileUri())
                                    .into(((ImageView) findViewById(R.id.image)));

                            startAppListeners(user, friend);
                        } else {
                            techupDialog = new AlertTechupDialog(GameChatRoomActivity.this);
                            techupDialog.setTitleText("Ops. Sorry");
                            techupDialog.setMessageText(task.getException().getMessage());
                            techupDialog.setPositiveButton("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    prepareActivity();
                                }
                            });
                            techupDialog.setNegativeButton("Quit", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    techupDialog.dismiss();
                                    supportFinishAfterTransition();
                                    finish();
                                }
                            });
                            techupDialog.show();
                        }
                    }
                });

    }

    private void startAppListeners(User user, User friend) {
        gladysShowIntroMessage();
        runAdapter(user, friend);
        startLoadMoreMessagesOnSwipeRefresh();
        startTypingAndStatusEventListener();
        setKeyBoardTypingListener();
        startSendButtonClickListener();
        noNewMessagesListener();
    }

    private void gladysShowIntroMessage() {
        final View infoView = findViewById(R.id.info_view);
        final TextView infoText = findViewById(R.id.info);
        final String message = "Hello... i am gladys, i will be observing you text and award marks for good behaviour. ";
        mChatRoomReference.collection("properties").document("lastSessionRecord")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String info = message;

                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Object timestamp = task.getResult().get("finishedAt");
                        if (timestamp != null) {
                            Timestamp chatEndTime = (Timestamp) timestamp;
                            String dateFormat = new SimpleDateFormat("dd MM yyyy", Locale.US)
                                    .format(chatEndTime.toDate());
                            info = message + " your last chat with this user was at " + dateFormat;
                        }
                    }
                }

                infoText.setText(info);

                TechupAnimationUtils.slideY(infoView, -500f, 0, 600,
                        new AnticipateOvershootInterpolator(), new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                infoView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        infoView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TechupAnimationUtils.slideY(infoView, 0, -500f, 600,
                                        new AnticipateOvershootInterpolator(), new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                infoView.setVisibility(View.INVISIBLE);
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                            }
                        });
                    }
                };

                mHandler.postDelayed(runnable, 3000);

                infoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runnable.run();
                        if (mHandler != null)
                            mHandler.removeCallbacks(runnable);
                    }
                });

            }
        });
    }

    private void runAdapter(User user, User friend) {
        Query query = mChatRoomReference.collection("Messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .limitToLast(mMessageLoadMagnitude * MESSAGES_TO_LOAD_LIMIT);

        messagesAdapter = new QuickGameChatAdapter(query, new OnListItemInteractionListener<ChatMessage>() {
            public void onItemInteraction(final ChatMessage item, int position, Object interactionType) {
                //show dialog reply or cancel
                if (interactionType.toString().equals("long-click")) {
                    techupDialog = new AlertTechupDialog(GameChatRoomActivity.this);
                    if (item.getMediaType() != null && !QuickGameChatAdapter.getLocalFile(item).exists()) {
                        techupDialog.setNegativeButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                techupDialog.dismiss();
                            }
                        });
                        techupDialog.setNeutralButton("Reply", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                techupDialog.dismiss();
                                replyChatItem(item);
                            }
                        });
                        techupDialog.setPositiveButton("Save", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                techupDialog.dismiss();
                                if (Helper.isNetworkAvailable(getApplicationContext())) {
                                    saveChatItemMedia(item);
                                } else {
                                    Helper.shortToast(getApplicationContext(), "no internet connection!. please check and try again.");
                                }
                            }
                        });
                    } else {
                        techupDialog.setNeutralButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                techupDialog.dismiss();
                            }
                        });
                        techupDialog.setPositiveButton("Reply", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                techupDialog.dismiss();
                                replyChatItem(item);
                            }
                        });
                    }
                    techupDialog.show();
                } else if (interactionType.toString().equals("click")) {
                    if (item.getMediaType() != null &&
                            !item.getMediaType().equals(TYPE_VOICE) &&
                            !item.getMediaType().equals(TYPE_AUDIO) &&
                            !item.getMediaType().equals(TYPE_FILE)) {
                        MediaPlayerDialogFragment fragment = new MediaPlayerDialogFragment(item);
                        fragment.show(getSupportFragmentManager(), "media-view");
                    } else if (item.getMediaType().equals(TYPE_FILE)) {
                        File file = QuickGameChatAdapter.getLocalFile(item);
                        Intent intent = getSharableIntent(Uri.fromFile(file));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Helper.shortToast(getApplicationContext(), "failed to open file");
                            }
                        }
                    }
                }
            }
        });

        messagesAdapter.setUser(user);
        messagesAdapter.setFriend(friend);
        messagesAdapter.setDisplaySize(Helper.getDeviceSize(this));
        messagesRV.setLayoutManager(new LinearLayoutManager(this));
        messagesRV.setAdapter(messagesAdapter);

        messagesAdapter.startListening();
        startIncomingMessagesEventListener();
    }

    private void saveChatItemMedia(ChatMessage item) {
        String type = item.getMediaType();

        Object fileName = item.getProperty("mediaName");
        if (fileName == null || fileName.toString().isEmpty()) {
            fileName = QuickGameChatAdapter.getFileNameFromUrl(item.getMediaUriList().get(0));
        }

        String saveFolder = (type.equals("file")) ? "Documents" :
                (type.equals("image") || type.equals("camera")) ? "Images" :
                        (type.equals("audio")) ? "Audio" :
                                (type.equals("video")) ? "Videos" : "VoiceNotes";

        File file = new FileExplorer(Environment.getExternalStorageDirectory())
                .createNewFolder("Cybuverse").openFolder("Cybuverse")
                .createNewFolder("Media").openFolder("Media")
                .createNewFolder(saveFolder).openFolder(saveFolder)
                .getFile(fileName.toString());

        if (!file.exists()) {

            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.getMediaUriList().get(0)));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle(fileName.toString());
            request.setDescription(item.getMediaType() + " from cybuverse");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setDestinationUri(Uri.fromFile(file));
            downloadManager.enqueue(request);
            Helper.shortToast(getApplicationContext(), "download started...");
        } else {
            Helper.shortToast(getApplicationContext(), "already saved...");
        }
    }

    private void replyChatItem(final ChatMessage item) {
        chatToReply = item;
        TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.input_box));
        findViewById(R.id.reply_box).setVisibility(View.VISIBLE);
        findViewById(R.id.cancel_reply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatToReply = null;
                hideReplyView();
            }
        });

        String content = item.getContent();
        if (content.isEmpty()) {
            if (item.hasProperty("mediaName"))
                content = item.getProperty("mediaName").toString();
            else
                content = item.getMediaType() + item.getMediaUriList().get(0);
        }

        ((TextView) findViewById(R.id.reply_message)).setText(content);
        findViewById(R.id.reply_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = messagesAdapter.getSnapshots().indexOf(item);
                messagesRV.scrollToPosition(index);
            }
        });
    }

    private void hideReplyView() {
        ((TextView) findViewById(R.id.reply_message)).setText("");
        TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.input_box));
        findViewById(R.id.reply_box).setVisibility(View.GONE);
    }

    private void startLoadMoreMessagesOnSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMessageLoadMagnitude++;
                runAdapter(user, friend);
                messagesRV.scrollToPosition(MESSAGES_TO_LOAD_LIMIT);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void noNewMessagesListener() {
        //set timer to wait for friend ..if no new message available send sorry message and ask to connect to anothr user
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //check if received new message

                if (isNewIncomingMessage) {
                    isNewIncomingMessage = false;
                    noNewMessagesListener();
                } else {
                    techupDialog = new AlertTechupDialog(GameChatRoomActivity.this);
                    techupDialog.setTitleText("Wait Too Long");
                    techupDialog.setMessageText("Sorry. looks like your friend is not available at the moment... do you wish to connect with another player or keep waiting?.");
                    techupDialog.setPositiveButton("Wait", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            techupDialog.dismiss();
                            noNewMessagesListener();
                        }
                    });
                    techupDialog.setNeutralButton("QuickGame", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            techupDialog.dismiss();
                            startActivity(new Intent(GameChatRoomActivity.this, QuickGameActivity.class));
                            finish();
                        }
                    });
                    techupDialog.setNegativeButton("Quit", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            techupDialog.dismiss();
                            supportFinishAfterTransition();
                            finish();
                        }
                    });
                    techupDialog.show();
                }
            }
        }, 60000);
    }

    private void setKeyBoardTypingListener() {
        messageTextInput.addTextChangedListener(new TextWatcher() {

            boolean isTypingActive = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    if (isTypingActive) {
                        isTypingActive = false;
                        mChatRoomReference.collection("properties")
                                .document(actorPlayer.getName())
                                .update("isTyping", isTypingActive);
                    }
                } else {
                    if (!isTypingActive) {
                        isTypingActive = true;
                        mChatRoomReference.collection("properties")
                                .document(actorPlayer.getName())
                                .update("isTyping", isTypingActive);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void startTypingAndStatusEventListener() {
        mChatRoomReference.collection("properties").document(actorFriend.getName())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {

                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        Helper.log("chatRoomUserPropertyUpdateEvent");
                        if (e != null) {
                            Helper.log("Listening Failed: " + e.getMessage());
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {

                            boolean isFriendTyping = documentSnapshot.getBoolean("isTyping");
                            isFriendOnline = documentSnapshot.getBoolean("isOnline");

                            if (isFriendTyping) {
                                //show typing
                                if (typing.getVisibility() == View.GONE)
                                    showTypingMessage();
                            } else {
                                //check visibility of typing then disable if visible
                                if (typing.getVisibility() == View.VISIBLE)
                                    hideTypingMessage();
                            }

                            if (isFriendOnline) {
                                ((ImageView) findViewById(R.id.onlineStatus))
                                        .setImageResource(R.color.t_green);
                                endOfflineFriendProcess();
                            } else {
                                ((ImageView) findViewById(R.id.onlineStatus))
                                        .setImageResource(R.color.t_ash);
                                //start end timer to display message to user //other player went offline
                                startOfflineFriendProcess();
                            }

                        }
                    }
                });
    }

    private void endOfflineFriendProcess() {
        if (isProcessFriendOffline)
            isProcessFriendOffline = false;
        ;
        mChatRoomReference.collection("properties").document("lastSessionRecord")
                .update("finishedAt", FieldValue.serverTimestamp());
    }

    private void startOfflineFriendProcess() {
        if (!isProcessFriendOffline) {
            isProcessFriendOffline = true;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isProcessFriendOffline) {
                        isProcessFriendOffline = false;
                        if (techupDialog != null)
                            techupDialog.dismiss();
                        techupDialog = new AlertTechupDialog(GameChatRoomActivity.this);
                        techupDialog.setTitleText("Friend Offline");
                        techupDialog.setMessageText("Sorry. looks like the other user is offline... do you wish to connect with another player?.");
                        techupDialog.setPositiveButton("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                techupDialog.dismiss();
                                startActivity(new Intent(GameChatRoomActivity.this, QuickGameActivity.class));
                                finish();
                            }
                        });
                        techupDialog.setNegativeButton("Quit", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                techupDialog.dismiss();
                                supportFinishAfterTransition();
                                finish();
                            }
                        });
                        try {
                            techupDialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 30000);
        }
    }

    private void startIncomingMessagesEventListener() {
        messagesAdapter.getSnapshots().addChangeEventListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                if (type == ChangeEventType.ADDED) {
                    isNewIncomingMessage = true;
                    messagesRV.scrollToPosition(messagesAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onDataChanged() {

            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {

            }
        });

    }

    private void startSendButtonClickListener() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String content = messageTextInput.getText().toString().trim();

                if (!content.trim().isEmpty() || mediaInputManagerFragment.hasMedia()) {
                    messageTextInput.setText("");

                    ChatMessage message = new ChatMessage(mCurrentUser.getUid(), content);

                    if (chatToReply != null) {
                        message.addProperty("replyTo", chatToReply);
                        hideReplyView();
                    }

                    if (mediaInputManagerFragment.hasMedia()) {
                        sendMediaMessage(message);
                    } else {
                        mChatRoomReference.collection("Messages").add(message)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            isNewIncomingMessage = true;
                                            if (chatToReply != null)
                                                chatToReply = null;
                                        } else {
                                            messageTextInput.setText(content);
                                            if (chatToReply != null)
                                                replyChatItem(chatToReply);
                                            Helper.shortToast(getApplicationContext(), "cannot send message...please check your network!");
                                        }
                                    }
                                });
                    }

                    if (!content.trim().isEmpty()) {
                        profanityFilterize(content);
                        analyzeMessageContentForPoints(content);
                    }
                }
            }

        });
    }

    private void gotoSettings() {
        //TODO: do conversation cancel
        Intent intent = new Intent(GameChatRoomActivity.this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
    }

    private void gotoHome() {
        final AlertTechupDialog techupDialog = new AlertTechupDialog(this);
        techupDialog.setMessageText("Are you sure you want to exit this conversation?..");
        techupDialog.setPositiveButton("No", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
            }
        });
        techupDialog.setNeutralButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                startActivity(new Intent(GameChatRoomActivity.this, MainActivity.class));
                conversationEnded();
            }
        });
        techupDialog.show();
    }

    private void showTypingMessage() {

        Animation animation = new ScaleAnimation(0, 1f, 0, 1f, typing.getPivotX(), typing.getPivotY());
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

        Animation animation = new ScaleAnimation(1f, 0, 1f, 0, typing.getPivotX(), typing.getPivotY());
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

    private void analyzeMessageContentForPoints(String content) {
        NLPAsyncTask nlpAsyncTask = new NLPAsyncTask(getString(R.string.google_api_key));
        nlpAsyncTask.execute(content);
    }

    private void profanityFilterize(String content) {
        ProfanityFilterer profanityFilter = new ProfanityFilterer();
        profanityFilter.execute(content);
    }

    private ArrayList<Uri> getSelectedMediaItemsUriList() {
        return new ArrayList<>();
    }

    private void conversationEnded() {
        Helper.shortToast(getApplicationContext(), "you earned " + mPoints + " points");

        mPoints = mPoints + Integer.valueOf(user.getInfo("points").toString());

        mFireStore.collection("Users")
                .document(mCurrentUser.getUid())
                .update("info.points", mPoints)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Helper.shortToast(getApplicationContext(), "you have " + mPoints + " total points now");
                        }
                    }
                });

        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
    }

    @Override
    public void onBackPressed() {
        if (mediaInputManagerFragment.isMediaInputVisible()) {
            mediaInputManagerFragment.hide();
        } else {
            gotoHome();
        }
    }

    @Override
    protected void onPause() {
        endOfflineFriendProcess();
        mChatRoomReference.collection("properties")
                .document(actorPlayer.getName())
                .update("isOnline", false, "isTyping", false);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (!isFriendOnline)
            startOfflineFriendProcess();
        mChatRoomReference.collection("properties")
                .document(actorPlayer.getName())
                .update("isOnline", true);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (messagesAdapter != null)
            messagesAdapter.stopListening();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void sendMediaMessage(final ChatMessage chatMessage) {

        mediaInputManagerFragment.hide();

        findViewById(R.id.upload_view).setVisibility(View.VISIBLE);

        final String name = mediaInputManagerFragment.getMediaName();
        final Uri uri = mediaInputManagerFragment.getMediaUri();
        final String type = mediaInputManagerFragment.getMediaType();

        final ProgressBar progressBar = findViewById(R.id.upload_progress);
        progressBar.setIndeterminate(true);

        final FloatingActionButton uploadController = findViewById(R.id.uploadController);
        uploadController.setImageResource(R.drawable.ic_close_yellow);

        if (type.equals("video")) {
            Bitmap thumbnail = Helper.getThumbnailFromVideoAsBitmap(GameChatRoomActivity.this, uri);

            final StorageReference thumbnailsReference = FirebaseStorage.getInstance()
                    .getReference("message_media")
                    .child(mCurrentUser.getUid())
                    .child(actorFriend.getName())
                    .child("thumbnails")
                    .child(name);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            thumbnailsReference.putBytes(outputStream.toByteArray())
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            return thumbnailsReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        chatMessage.addProperty("thumbnailUri", task.getResult().toString());
                        uploadMediaMessage(chatMessage);
                    } else {
                        uploadError(new Runnable() {
                            @Override
                            public void run() {
                                sendMediaMessage(chatMessage);
                            }
                        }, null);
                    }
                }
            });
        } else {
            uploadMediaMessage(chatMessage);
        }


    }

    private void uploadMediaMessage(final ChatMessage chatMessage) {

        final FloatingActionButton uploadController = findViewById(R.id.uploadController);
        final ProgressBar progressBar = findViewById(R.id.upload_progress);

        final Uri uri = mediaInputManagerFragment.getMediaUri();
        final Bitmap bitmap = mediaInputManagerFragment.getMediaBitmap();
        final String type = mediaInputManagerFragment.getMediaType();
        final String name = mediaInputManagerFragment.getMediaName();

        final Variable<Boolean> cancelUpload = new Variable<>(false);

        chatMessage.addProperty("mediaName", name);
        chatMessage.setMediaType(type);

        final File file = QuickGameChatAdapter.getLocalFile(chatMessage);

        uploadController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelUpload.setValue(false);
                findViewById(R.id.upload_view).setVisibility(View.GONE);
            }
        });

        final StorageReference mediaReference = FirebaseStorage.getInstance()
                .getReference("message_media")
                .child(mCurrentUser.getUid())
                .child(actorFriend.getName())
                .child(name);


        mediaReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    chatMessage.addMediaUriList(task.getResult().toString());
                    Helper.log("copying file : " + file.getName());
                    if (!file.exists() || (file.length() < new File(uri.getPath()).length())) {
                        new CopyFileToMediaFolder().execute(file);
                    }
                    saveMediaMessage(chatMessage, mediaReference);
                } else {
                    final UploadTask mediaUploadTask;

                    if (uri != null)
                        mediaUploadTask = mediaReference.putFile(uri);
                    else {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        mediaUploadTask = mediaReference.putBytes(outputStream.toByteArray());
                    }

                    progressBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setIndeterminate(false);
                            progressBar.setProgress(0);
                            mediaUploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                    long totalByteCount = taskSnapshot.getTotalByteCount();
                                    long bytesTransferred = taskSnapshot.getBytesTransferred();
                                    Object intVal = Math.floor((100 * bytesTransferred) / totalByteCount);
                                    int progress = toInteger(intVal);
                                    progressBar.setProgress(progress);
                                    if (cancelUpload.getValue()) {
                                        mediaUploadTask.cancel();
                                    } else if (progress == 100) {
                                        uploadController.setImageResource(R.drawable.ic_thumb_up_gold_24dp);
                                        uploadController.setOnClickListener(null);
                                        progressBar.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                findViewById(R.id.upload_view).setVisibility(View.GONE);
                                            }
                                        }, 2000);
                                    }
                                }
                            });
                        }
                    }, 2000);

                    mediaUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            return mediaReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                chatMessage.addMediaUriList(task.getResult().toString());
                                Helper.log("copying file : " + file.getName());
                                if (!file.exists() || (file.length() < new File(uri.getPath()).length())) {
                                    new CopyFileToMediaFolder().execute(file);
                                }
                                saveMediaMessage(chatMessage, mediaReference);
                            } else {
                                if (!mediaUploadTask.isCanceled()) {
                                    uploadError(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendMediaMessage(chatMessage);
                                        }
                                    }, null);
                                } else {
                                    Helper.shortToast(getApplicationContext(), task.getException().getMessage());
                                }
                            }
                        }
                    });


                }
            }

            class CopyFileToMediaFolder extends AsyncTask<File, Void, Void> {

                @Override
                protected Void doInBackground(File... files) {
                    Helper.log("attempting file : " + files[0]);
                    if (!chatMessage.getMediaType().equals(TYPE_VOICE)) {
                        File localFile = files[0];
                        try {
                            Helper.log("starting ..");
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            FileOutputStream fileOutputStream = new FileOutputStream(localFile);
                            int len;
                            byte[] buffer = new byte[1024];
                            while ((len = inputStream.read(buffer)) > 0) {
                                fileOutputStream.write(buffer, 0, len);
                            }
                            inputStream.close();
                            fileOutputStream.close();
                            Helper.log("copy complete");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Helper.log("failed to copy file");
                        }
                    }
                    return null;
                }
            }
        });
    }

    private void saveMediaMessage(final ChatMessage chatMessage, final StorageReference reference) {
        mChatRoomReference.collection("Messages")
                .add(chatMessage)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            mediaInputManagerFragment.reset();
                            galleryPicker.setImageResource(R.drawable.ic_gallery_item_picker_white);
                            findViewById(R.id.upload_view).setVisibility(View.GONE);
                        } else {
                            uploadError(new Runnable() {
                                @Override
                                public void run() {
                                    saveMediaMessage(chatMessage, reference);
                                }
                            }, new Runnable() {
                                @Override
                                public void run() {
                                    reference.delete();
                                }
                            });
                        }
                    }
                });
    }

    private void uploadError(final Runnable onRetry, final Runnable onCancel) {
        techupDialog = new AlertTechupDialog(GameChatRoomActivity.this);
        techupDialog.setMessageText("Media message did not send successfully... Do you want to try again?");
        techupDialog.setNeutralButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                if (onCancel != null)
                    onCancel.run();
            }
        });
        techupDialog.setPositiveButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                onRetry.run();
            }
        });
        techupDialog.show();
    }


    public Intent getSharableIntent(Uri uri) {

        Uri contentUri = uri;
        if (uri.toString().contains("file://"))
            contentUri = getSharableUriFromUri(uri);

        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setStream(contentUri) // uri from FileProvider
                .getIntent()
                .setAction(Intent.ACTION_VIEW); //Change if needed

        if (contentUri.toString().contains(".doc") || contentUri.toString().contains(".docx")) {
            intent.setDataAndType(contentUri, "application/msword");
        } else if (contentUri.toString().contains(".pdf")) {
            intent.setDataAndType(contentUri, "application/pdf");
        } else if (contentUri.toString().contains(".ppt") || contentUri.toString().contains(".pptx")) {
            intent.setDataAndType(contentUri, "application/vnd.ms-powerpoint");
        } else if (contentUri.toString().contains(".xls") || contentUri.toString().contains(".xlsx")) {
            intent.setDataAndType(contentUri, "application/vnd.ms-excel");
        } else if (contentUri.toString().contains(".zip") || contentUri.toString().contains(".rar")) {
            intent.setDataAndType(contentUri, "application/zip");
        } else if (contentUri.toString().contains(".rtf")) {
            intent.setDataAndType(contentUri, "application/rtf");
        } else if (contentUri.toString().contains(".wav") || contentUri.toString().contains(".mp3")) {
            intent.setDataAndType(contentUri, "audio/x-wav");
        } else if (contentUri.toString().contains(".gif")) {
            intent.setDataAndType(contentUri, "image/gif");
        } else if (contentUri.toString().contains(".jpg") || contentUri.toString().contains(".jpeg") || contentUri.toString().contains(".png")) {
            intent.setDataAndType(contentUri, "image/jpeg");
        } else if (contentUri.toString().contains(".txt")) {
            intent.setDataAndType(contentUri, "text/plain");
        } else if (contentUri.toString().contains(".3gp") || contentUri.toString().contains(".mpg") || contentUri.toString().contains(".mpeg") || contentUri.toString().contains(".mpe") || contentUri.toString().contains(".mp4") || contentUri.toString().contains(".avi")) {
            intent.setDataAndType(contentUri, "video/*");
        } else {
            intent.setDataAndType(contentUri, "*/*");
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    private Uri getSharableUriFromUri(Uri uri) {
        String path = Helper.getPathFromUri(this, uri);
        if (path == null) path = uri.getPath();
        return FileProvider.getUriForFile(getApplicationContext(),
                getPackageName() + ".provider", new File(path));
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
        protected Integer doInBackground(String... strings) {
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
