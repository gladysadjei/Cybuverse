package com.gladys.cybuverse.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.gladys.cybuverse.Helpers.AlertTechupDialog;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.ProgressTechupDialog;
import com.gladys.cybuverse.Helpers.TechupDialog;
import com.gladys.cybuverse.Models.MatchRequest;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class QuickGameActivity extends AppCompatActivity {

    private TechupDialog techupDialog;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mFirebaseUser;

    private CollectionReference mWaitingRoomRef;
    private CollectionReference mChatRoomRef;
    private ListenerRegistration waitResponseListener;

    private boolean isConnectionProcessActive;
    private String createdMatchRequestID;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_game);

        mHandler = new Handler();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        mWaitingRoomRef = mFirestore.collection("GameWaitingRoom");
        mChatRoomRef = mFirestore.collection("GameChatRoom");

        setConnectionProcessActive(true);
    }

    public boolean isConnectionProcessActive() {
        return isConnectionProcessActive;
    }

    public void setConnectionProcessActive(boolean connectionProcessActive) {
        isConnectionProcessActive = connectionProcessActive;
    }

    private void connectPlayer() {

        mHandler.removeCallbacksAndMessages(null);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isConnectionProcessActive) {
                    techupDialog.dismiss();
                    stopConnectionProcess();
                    showErrorDialog("Sorry...", "There are no players online at the moment... You can switch to story mode or try again. ");
                }
            }
        }, 60000);

        if (Helper.isNetworkAvailable(this)) {

            if (techupDialog != null)
                techupDialog.dismiss();

            showConnectingProgressDialog();
            tryConnectingPlayer();
        } else {
            showErrorDialog("looks like you do not have internet connection!... please check and try again.");
        }
    }

    private void stopAndRetry() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isConnectionProcessActive) {
                    techupDialog.dismiss();
                    stopConnectionProcess();
                    connectPlayer();
                }
            }
        }, 30000);
    }

    private void tryConnectingPlayer() {

        //clean uncompleted requests
        mWaitingRoomRef.whereEqualTo("requester", mFirebaseUser.getUid())
                .whereEqualTo("joiner", "").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Helper.log("requests by me : " + task.getResult().getDocuments().size());
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                mWaitingRoomRef.document(doc.getId()).delete();
                                Helper.log("deleting previously created request by self : " + doc.getId());
                            }


                            //create new request
                            mWaitingRoomRef.whereEqualTo("joiner", "").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                List<DocumentSnapshot> requests = task.getResult().getDocuments();

                                                Helper.log("requests in waiting room: " + requests.size());

                                                if (requests.isEmpty()) {
                                                    addPlayerMatchRequest();
                                                } else {
                                                    joinMatchRequest(requests.get(new Random().nextInt(requests.size())));
                                                }
                                            } else {
                                                showErrorDialog(task.getException().getMessage());
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void joinMatchRequest(final DocumentSnapshot matchRequest) {

        mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull final Transaction transaction) throws FirebaseFirestoreException {

                Helper.log("trying to respond to a request: " + matchRequest.getReference().getId());

                if (matchRequest.exists()) {

                    Helper.log("request still exists");

                    final String requester = transaction.get(matchRequest.getReference()).getString("requester");
                    String joiner = transaction.get(matchRequest.getReference()).getString("joiner");

                    Helper.log("requesterID: " + requester + " -- joinerID: " + mFirebaseUser.getUid());

                    if (joiner.isEmpty() && !requester.equals(mFirebaseUser.getUid())) {

                        joiner = mFirebaseUser.getUid();

                        final String roomName = (requester.compareTo(joiner) > 0) ?
                                requester + "_" + joiner : joiner + "_" + requester;

                        Helper.log("attempting to create chat room: " + roomName);
                        final Map<String, Object> status = new HashMap<>();
                        status.put("isOnline", true);
                        status.put("isTyping", false);

                        mChatRoomRef.document(roomName).collection("properties")
                                .document(requester).set(status)
                                .continueWithTask(new Continuation<Void, Task<Void>>() {
                                    @Override
                                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                                        return mChatRoomRef.document(roomName).collection("properties")
                                                .document(mFirebaseUser.getUid()).set(status);
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Helper.log("created chat room: " + roomName);
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("joiner", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            updates.put("roomName", roomName);
                                            updates.put("isComplete", true);
                                            Helper.log("attempt updating match request: " + roomName);
                                            matchRequest.getReference().update(updates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Helper.log("updating match request success");
                                                                mChatRoomRef.document(roomName)
                                                                        .collection("properties")
                                                                        .document("lastSessionRecord")
                                                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            if (task.getResult().exists()) {
                                                                                mChatRoomRef.document(roomName)
                                                                                        .collection("properties")
                                                                                        .document("lastSessionRecord")
                                                                                        .update("createdAt", FieldValue.serverTimestamp());
                                                                            } else {
                                                                                Map<String, Object> stringObjectMap = new HashMap<>();
                                                                                stringObjectMap.put("createdAt", FieldValue.serverTimestamp());
                                                                                stringObjectMap.put("finishedAt", null);
                                                                                mChatRoomRef.document(roomName)
                                                                                        .collection("properties")
                                                                                        .document("lastSessionRecord")
                                                                                        .set(stringObjectMap);
                                                                                mChatRoomRef.document(roomName)
                                                                                        .collection("properties")
                                                                                        .document("members").set(new String[]{requester, mFirebaseUser.getUid()});
                                                                            }
                                                                        } else {
                                                                            Helper.log(task.getException().getMessage());
                                                                        }
                                                                    }
                                                                });
                                                                sendToChatRoom(roomName);
                                                            } else {
                                                                Helper.log("updating match request failed");
                                                                stopConnectionProcess();
                                                                showErrorDialog(task.getException().getMessage());
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Helper.log("failed to create chat room");
                                            stopConnectionProcess();
                                            showErrorDialog(task.getException().getMessage());
                                        }
                                    }
                                });

                    } else {
                        tryConnectingPlayer();
                    }
                } else {
                    Helper.log("request does not exist exists anymore");
                    tryConnectingPlayer();
                }
                return null;
            }
        });
    }

    private void sendToChatRoom(String roomName) {
        Helper.log("match success -- roomName: " + roomName);
        Intent intent = new Intent(QuickGameActivity.this, GameChatRoomActivity.class);
        intent.putExtra("room", roomName);
        startActivity(intent);
        finish();
    }

    private void addPlayerMatchRequest() {

        if (isConnectionProcessActive()) {
            Helper.log("adding new request");

            mWaitingRoomRef.add(new MatchRequest(mFirebaseUser.getUid()))
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                waitForMatchResponse(task.getResult().getId());
                            } else {
                                showErrorDialog(task.getException().getMessage());
                            }
                        }
                    });
        }
    }


    private void waitForMatchResponse(String id) {
        Helper.log("new request id: " + id + "-- waiting for response");

        createdMatchRequestID = id;

        waitResponseListener = mWaitingRoomRef.document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (isConnectionProcessActive) {

                    if (e != null) {
                        Helper.log("Listening Failed: " + e.getMessage());
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Helper.log("documentSnapshot exists");
                        if (!documentSnapshot.getString("roomName").isEmpty()) {
                            sendToChatRoom(documentSnapshot.getString("roomName"));
                            Helper.log("attempting to delete match request with ID: " + documentSnapshot.getId());
                            mWaitingRoomRef.document(documentSnapshot.getId()).delete();
                        }
                    } else {
                        Helper.log("documentSnapshot not available");
                    }
                }
            }
        });
    }

    private void showErrorDialog(String... messages) {

        String title = "Ops. Error!";
        String message = null;

        if (messages.length > 1) {
            title = messages[0];
            message = messages[1];
        }

        if (messages.length == 1)
            message = messages[0];

        techupDialog = new AlertTechupDialog(QuickGameActivity.this);
        techupDialog.setTitleText(title);
        techupDialog.setMessageText(message);
        ((AlertTechupDialog) techupDialog).setPositiveButton("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                connectPlayer();
            }
        });
        ((AlertTechupDialog) techupDialog).setNegativeButton("Quit", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                supportFinishAfterTransition();
                finish();
            }
        });
        techupDialog.setCancelable(false);
        techupDialog.show();
    }

    private void showConnectingProgressDialog() {
        techupDialog = new ProgressTechupDialog(QuickGameActivity.this);
        techupDialog.setTitleText("Connecting");
        techupDialog.setMessageText("please wait while we connect you to another player.");
        techupDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stopConnectionProcess();
                showCloseQuickGameDialog();
            }
        });
        ((ProgressTechupDialog) techupDialog).setNeutralButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                supportFinishAfterTransition();
                finish();
            }
        });
        techupDialog.show();
    }

    private void showCloseQuickGameDialog() {
        techupDialog = new AlertTechupDialog(QuickGameActivity.this);
        techupDialog.setMessageText("Do you wish to try again?");
        ((AlertTechupDialog) techupDialog).setPositiveButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                connectPlayer();
            }
        });
        ((AlertTechupDialog) techupDialog).setNegativeButton("Quit", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                supportFinishAfterTransition();
                finish();
            }
        });
        techupDialog.setCancelable(false);
        techupDialog.show();
    }

    private void stopConnectionProcess() {
        setConnectionProcessActive(false);

        if (waitResponseListener != null) {
            waitResponseListener.remove();
        }
        if (createdMatchRequestID != null) {
            mWaitingRoomRef.document(createdMatchRequestID).delete();
            Helper.log("deleting match requestID: " + createdMatchRequestID);
        }
    }

    @Override
    protected void onPause() {
        if (techupDialog != null)
            techupDialog.dismiss();
        stopConnectionProcess();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (techupDialog != null)
            techupDialog.dismiss();
        connectPlayer();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (techupDialog != null)
            techupDialog.dismiss();
        super.onDestroy();
    }
}
