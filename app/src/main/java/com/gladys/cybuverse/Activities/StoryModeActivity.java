package com.gladys.cybuverse.Activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gladys.cybuverse.Adapters.GameChatAdapter;
import com.gladys.cybuverse.Adapters.OnListItemInteractionListener;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ChatBot;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Scene;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.ScenesCollection;
import com.gladys.cybuverse.Helpers.TechupApplication;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.PlaySoundOnClickListener;
import com.gladys.cybuverse.Models.Chat;
import com.gladys.cybuverse.Models.Question;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.gladys.cybuverse.Activities.GameConversationActivity.CHAT_UID;
import static com.gladys.cybuverse.Activities.GameConversationActivity.CONVERSATION_INDEX;
import static com.gladys.cybuverse.Activities.GameConversationActivity.LAST_MESSAGE_INDEX;
import static com.gladys.cybuverse.Activities.GameConversationActivity.LAST_RESPONDER;
import static com.gladys.cybuverse.Activities.GameConversationActivity.SCENE_INDEX;

//import androidx.appcompat.widget.Toolbar;

public class StoryModeActivity extends AppCompatActivity {
    public static final int DESCRIPTION_INFO_REQUEST_CODE = 120;
    public static final int AIM_INFO_REQUEST_CODE = 121;
    public static final int CONVERSATION_ACTIVITY_REQUEST_CODE = 100;

    private User user;
    private RecyclerView gameChatsRV;
    private GameChatAdapter chatsAdapter;
    private ScenesCollection scenesCollection;
    private Scene scene;

    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private SearchView searchView;

    private Integer sceneIndex,
            conversationIndex,
            lastMessageIndex;

    private TechupApplication preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_mode);
        initializeViews();
        prepareUserStoryIndexes();
        populateViewPagers();
    }

    private void initializeViews() {
        preference = ((TechupApplication) getApplicationContext());
        user = preference.getUser();
        gameChatsRV = findViewById(R.id.chatsRV);
        scenesCollection = new ScenesCollection(user);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Chats");
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updateUserStoryIndexes() {

        user = preference.getUser();

        sceneIndex = Integer.valueOf((user.getInfo(SCENE_INDEX) != null) ?
                user.getInfo(SCENE_INDEX).toString() : "0");
        conversationIndex = Integer.valueOf((user.getInfo(CONVERSATION_INDEX) != null) ?
                user.getInfo(CONVERSATION_INDEX).toString() : "0");
        lastMessageIndex = Integer.valueOf((user.getInfo(LAST_MESSAGE_INDEX) != null) ?
                user.getInfo(LAST_MESSAGE_INDEX).toString() : "0");


        Helper.log("olduser indexes",
                "sceneIndex: " + sceneIndex,
                "conversationIndex: " + conversationIndex,
                "lastMessage: " + lastMessageIndex
        );


        scene = scenesCollection.getScenesList().get(sceneIndex);

        if (conversationIndex < scene.getConversations().size()) {
            Helper.log("updating..");
            Helper.log("messages.."+ scene.getConversations().get(conversationIndex).getMessages().size());

            if (lastMessageIndex >= scene.getConversations().get(conversationIndex).getMessages().size()) {
                if (conversationIndex + 1 < scene.getConversations().size()) {
                    conversationIndex++;
                    lastMessageIndex = 0;
                    Helper.log("updating.. conversation");
                } else {
                    sceneIndex++;
                    conversationIndex = 0;
                    lastMessageIndex = 0;
                    scene = scenesCollection.getScenesList().get(sceneIndex);
                    Helper.log("updating.. scene");
                }
            }
        }

        Helper.log("updated user index",
                "sceneIndex: " + sceneIndex,
                "conversationIndex: " + conversationIndex,
                "lastMessage: " + lastMessageIndex
        );

    }

    private void prepareUserStoryIndexes() {

        updateUserStoryIndexes();

        if (conversationIndex == 0) {
            if (scene.getDescription() != null && !scene.getDescription().trim().isEmpty()) {
                showSceneDescription();
                overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
            } else if (scene.getAim() != null && !scene.getAim().trim().isEmpty()) {
                showSceneAim();
                overridePendingTransition(R.anim.bounce_up, R.anim.slide_up_anim);
            }
        }

    }

    private void populateViewPagers() {

        chatsAdapter = new GameChatAdapter(new ArrayList<Chat>(), new OnListItemInteractionListener<Chat>() {
            @Override
            public void onItemInteraction(Chat chat, int position, Object interactionType) {
                try {
                    Intent intent = new Intent(StoryModeActivity.this, com.gladys.cybuverse.Activities.GameConversationActivity.class);
                    intent.putExtra(CHAT_UID, chat.getId());
                    intent.putExtra(SCENE_INDEX, Integer.valueOf(chat.getProperty(SCENE_INDEX).toString()));
                    intent.putExtra(CONVERSATION_INDEX, Integer.valueOf(chat.getProperty(CONVERSATION_INDEX).toString()));
                    intent.putExtra(LAST_MESSAGE_INDEX, Integer.valueOf(chat.getProperty(LAST_MESSAGE_INDEX).toString()));
                    intent.putExtra(GameConversationActivity.UNREAD_MESSAGES_COUNT, chat.getUnreadMessagesCount());
                    intent.putExtra(LAST_RESPONDER, chat.hasProperty(LAST_RESPONDER) ?
                            chat.getProperty(LAST_RESPONDER).toString() : null);
                    startActivityForResult(intent, CONVERSATION_ACTIVITY_REQUEST_CODE);
                    overridePendingTransition(R.anim.bounce_down, R.anim.slide_down_anim);
                }catch(Exception e){
                    e.printStackTrace();
                    Helper.shortToast(getApplicationContext(), "sorry cant opent this chat at th momment.");
                }
            }
        });

        gameChatsRV.setAdapter(chatsAdapter);
        gameChatsRV.setLayoutManager(new LinearLayoutManager(this));

        loadUserChats();

    }

    private void loadUserChats() {

        findViewById(R.id.loading).setVisibility(View.VISIBLE);

        DocumentReference userStoryModeDetails = mStore.collection("StoryModeChats")
                .document(mAuth.getCurrentUser().getUid());

        userStoryModeDetails.collection("ChatList")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                findViewById(R.id.loading).setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    List<Chat> chatList = task.getResult().toObjects(Chat.class);

                    if (chatList.isEmpty()) {
                        Chat gladys = ChatBot.asChat();
                        gladys.addProperty(SCENE_INDEX, 0);
                        gladys.addProperty(CONVERSATION_INDEX, 0);
                        gladys.addProperty(LAST_MESSAGE_INDEX, 0);
                        gladys.setUnreadMessagesCount(1);
                        chatList.add(gladys);
                    }

                    chatsAdapter.getDataSet().addAll(chatList);
                    chatsAdapter.notifyDataSetChanged();
                    loadUserQuestionsResponses();
                    activateNewOrPreviousChat();

                } else {
                    //show networkError
                    Helper.longToast(getApplicationContext(), task.getException().getMessage());
                }
            }
        });

        userStoryModeDetails.collection("ChatList")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                    refreshChat(documentChange.getDocument().toObject(Chat.class));
                                }
                            }
                        }

                    }
                });
    }

    private void loadUserQuestionsResponses() {
        FirebaseFirestore.getInstance().collection("Questions")
                .whereEqualTo("email", user.getEmail())
                .whereEqualTo("seen", false)
                .whereEqualTo("hasResponse", true)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Question> questionList = task.getResult().toObjects(Question.class);
                    for (Question question: questionList){
                        int index = indexOfChat(question.getChatUID());
                        if (index > -1){
                            chatsAdapter.getDataSet().get(index)
                                    .setUnreadMessagesCount(chatsAdapter.getDataSet().get(index).getUnreadMessagesCount()+1);
                            chatsAdapter.notifyItemChanged(index);
                        }
                    }
                }
            }
        });
    }

    private void refreshChat(Chat chat) {
        int index = indexOfChat(chat.getId());
        if (index != -1) {
            chatsAdapter.getDataSet().remove(index);
            chatsAdapter.getDataSet().add(index, chat);
            chatsAdapter.notifyItemChanged(index);
        }
    }

    private void activateNewOrPreviousChat() {

        Helper.log("checking for new chat or activating previous",
                "sceneIndex: " + sceneIndex,
                "conversationIndex: " + conversationIndex,
                "lastMessage: " + lastMessageIndex
        );

        if (conversationIndex < scene.getConversations().size()) {
            Helper.log("messageSize: " + scene.getConversations().get(conversationIndex).getMessages().size());
            if (lastMessageIndex < scene.getConversations().get(conversationIndex).getMessages().size()) {
                Chat chat = scene.getConversations().get(conversationIndex).getChat();
                chat.addProperty(SCENE_INDEX, sceneIndex);
                chat.addProperty(CONVERSATION_INDEX, conversationIndex);
                chat.addProperty(LAST_MESSAGE_INDEX, lastMessageIndex);

                int indexOfChat = indexOfChat(chat.getId());

                if (indexOfChat == -1) {
                    chat.addProperty("is-active", true);
                    chatsAdapter.getDataSet().add(chat);
                    chatsAdapter.notifyItemInserted(chatsAdapter.getDataSet().size() - 1);
                }else{
                    refreshChat(chat);
                }
                changeChatActivateState(chat.getId(), true);
            } else {

                Chat chat = scene.getConversations().get(conversationIndex).getChat();

                changeChatActivateState(chat.getId(), false);

                if (conversationIndex + 1 < scene.getConversations().size()) {
                    conversationIndex++;
                    lastMessageIndex = 0;
                } else {
                    sceneIndex++;
                    conversationIndex = 0;
                    lastMessageIndex = 0;
                }

                scene = scenesCollection.getScenesList().get(sceneIndex);

                if (conversationIndex < scene.getConversations().size()) {

                    chat = scene.getConversations().get(conversationIndex).getChat();

                    chat.addProperty(SCENE_INDEX, sceneIndex);
                    chat.addProperty(CONVERSATION_INDEX, conversationIndex);
                    chat.addProperty(LAST_MESSAGE_INDEX, lastMessageIndex);

                    int indexOfChat = indexOfChat(chat.getId());

                    if (indexOfChat == -1) {
                        chat.addProperty("is-active", true);
                        chatsAdapter.getDataSet().add(chat);
                        chatsAdapter.notifyItemInserted(chatsAdapter.getDataSet().size() - 1);
                    }else{
                        refreshChat(chat);
                    }
                    changeChatActivateState(chat.getId(), true);

                    user.addInfo(SCENE_INDEX, sceneIndex);
                    user.addInfo(CONVERSATION_INDEX, conversationIndex);
                    user.addInfo(LAST_MESSAGE_INDEX, lastMessageIndex);
                    preference.setUser(user);
                }

            }
        }
    }

    private void changeChatActivateState(String id, boolean state) {
        int i = indexOfChat(id);
        if (i != -1) {
            chatsAdapter.getDataSet().get(i).addProperty("is-active", state);
            chatsAdapter.notifyItemChanged(i);
        }
    }

    private int indexOfChat(String id) {
        for (int i = 0; i < chatsAdapter.getDataSet().size(); i++) {
            if (chatsAdapter.getDataSet().get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void showSceneAim() {
        Intent intent = new Intent(getApplicationContext(), com.gladys.cybuverse.Activities.InformationActivity.class);
        intent.putExtra("title", "Scene Aim");
        intent.putExtra("message", scene.getAim());
        intent.putExtra("proceed", "Okay");
        intent.putExtra("animate_finish", true);
        startActivityForResult(intent, AIM_INFO_REQUEST_CODE);
    }

    private void showSceneDescription() {
        Intent intent = new Intent(getApplicationContext(), InformationActivity.class);
        intent.putExtra("title", "Scene Description");
        intent.putExtra("message", scene.getDescription());
        intent.putExtra("proceed", "Next");
        if (scene.getAim() == null || scene.getAim().trim().isEmpty()) {
            intent.putExtra("animate_finish", true);
        }
        startActivityForResult(intent, DESCRIPTION_INFO_REQUEST_CODE);
    }


    //TODO: create function to update chats

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONVERSATION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Helper.log("conversation completed!");
                if (data != null)
                    updateChat(data);
                prepareUserStoryIndexes();
                activateNewOrPreviousChat();
            } else if (resultCode == RESULT_CANCELED) {
                Helper.log("scene not completed!");
            }
        }

        if (requestCode == DESCRIPTION_INFO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (scene.getAim() != null && !scene.getAim().trim().isEmpty()) {
                    showSceneAim();
                }
            } else {
                supportFinishAfterTransition();
            }
        }

        if (requestCode == AIM_INFO_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                if (scene.getDescription() != null && !scene.getDescription().trim().isEmpty()) {
                    showSceneDescription();
                }
            }
        }
    }

    private void updateChat(Intent intent) {
        int index = indexOfChat(intent.getStringExtra(CHAT_UID));
        chatsAdapter.getDataSet().get(index).setUnreadMessagesCount(0);
        chatsAdapter.getDataSet().get(index).addProperty("is-active", false);
        chatsAdapter.getDataSet().get(index).setLastSeen(intent.getStringExtra("last-seen"));
        chatsAdapter.getDataSet().get(index).addProperty(SCENE_INDEX, intent.getStringExtra(SCENE_INDEX));
        chatsAdapter.getDataSet().get(index).addProperty(CONVERSATION_INDEX, intent.getStringExtra(CONVERSATION_INDEX));
        chatsAdapter.getDataSet().get(index).addProperty(LAST_MESSAGE_INDEX, intent.getStringExtra(LAST_MESSAGE_INDEX));
        chatsAdapter.getDataSet().get(index).addProperty(LAST_RESPONDER, intent.getStringExtra(LAST_RESPONDER));
        if (intent.getStringExtra("last-message") != null)
            chatsAdapter.getDataSet().get(index).setLastMessage(intent.getStringExtra("last-message"));

        chatsAdapter.notifyItemChanged(index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem mActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) mActionMenuItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.clearFocus();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                chatsAdapter.openSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                chatsAdapter.openSearch(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                chatsAdapter.closeSearch();
                return false;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }
}
