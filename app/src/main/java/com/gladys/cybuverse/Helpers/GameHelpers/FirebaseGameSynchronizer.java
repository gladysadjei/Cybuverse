package com.gladys.cybuverse.Helpers.GameHelpers;

import com.gladys.cybuverse.Helpers.Helper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayDeque;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirebaseGameSynchronizer implements ChildEventListener {

    boolean isSynced = false;
    private DatabaseReference mMovesRecordList;
    private Modulator mMessageModulator;
    private int mMoveIndex;
    private int mSelfMoveSoph;
    private boolean mSynced;
    private ArrayDeque<String> mUnsyncBuffer = new ArrayDeque<>();

    private FirebaseGameSynchronizer(DatabaseReference movesRecordList,
                                     Modulator messageModulator) {
        mMovesRecordList = movesRecordList;
        mMessageModulator = messageModulator;
        mMoveIndex = 0;
        mSelfMoveSoph = 0;
        mMovesRecordList.addChildEventListener(this);
    }

    public static FirebaseGameSynchronizer newInstance(String moveListRecordPath,
                                                       Modulator modulator) {
        return new FirebaseGameSynchronizer(FirebaseDatabase.getInstance()
                .getReference(moveListRecordPath), modulator);
    }

    private void resyncAll() {
        while (!mUnsyncBuffer.isEmpty())
            mMessageModulator.onReceiveMove(true, mUnsyncBuffer.pop());
    }

    public int moveCount() {
        return mMoveIndex;
    }

    public String recordPath() {
        return mMovesRecordList.getPath().toString();
    }

    public void attachModulator(Modulator modulator) {
        mMessageModulator = modulator;
    }

    public void detachModulator() {
        mMessageModulator = null;
    }

    public void startSync() {
        if (!isSynced) {
            resyncAll();
            isSynced = true;
            return;
        }
    }

    public void stopSync() {
        isSynced = false;
    }

    public void flush() {
        mMovesRecordList.removeEventListener(this);
    }

    public void sendMoveMsg(String moveValue) {
        ++mSelfMoveSoph;
        FirebaseDatabase.getInstance().getReference(mMovesRecordList.getPath() + "/M" + mMoveIndex)
                .setValue(moveValue);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (mSelfMoveSoph > 0) {
            --mSelfMoveSoph;
            ++mMoveIndex;
            return;
        }

        if (!isSynced && dataSnapshot.getKey().charAt(0) == 'M') {
            mUnsyncBuffer.add(dataSnapshot.getValue(String.class));
            ++mMoveIndex;
            return;
        }

        Helper.log("FirebaseGameSync", "Real-sync");
        mMessageModulator.onReceiveMove(false, dataSnapshot.getValue(String.class));
        ++mMoveIndex;
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Helper.log("FirebaseGameSync", "onChildChanged not supported");
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Helper.log("FirebaseGameSync", "onChildRemoved not supported");
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Helper.log("FirebaseGameSync", "onChildMoved not supported");
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Helper.log("FirebaseGameSync", "onCancelled not supported");
    }

    public interface Modulator {

        public void onReceiveMove(boolean isSyncingPast, String encodedMsg);

    }

}