package com.gladys.cybuverse.Helpers.GameHelpers;

import com.gladys.cybuverse.Helpers.Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirebasePlayerMatchMaker {

    public static final String RANDOM_ROOM_ID = "/Globl";
    public static final String ROOM_ID = "/GameRooms";
    public static final String GAMES_RECORD = "/OpenGameMoves";
    public String mOpener;
    public String mGamePath;
    public int mLocalPlayerIndex;
    protected Matcher mMatcher;
    protected SelfChallengeManager mSelfChallengeManager;
    protected SelfChallengeCanceller mSelfChallengeCanceller;
    protected boolean mIsThisOpener;
    private DatabaseReference mUserRoomRef;
    private DatabaseReference mOwnChallengeRef;
    private boolean mClosed = false;
    private OnMatchMadeCallback mOnComplete;

    private FirebasePlayerMatchMaker(DatabaseReference userRoomRef, OnMatchMadeCallback onComplete) {
        mUserRoomRef = userRoomRef;
        mOnComplete = onComplete;
    }

    public static FirebasePlayerMatchMaker newInstance(String userRoom, OnMatchMadeCallback onComplete) {
        return new FirebasePlayerMatchMaker(
                FirebaseDatabase.getInstance().getReference(ROOM_ID + "/" + userRoom), onComplete);
    }

    public boolean isClosed() {
        return mClosed;
    }

    public void findMatch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OnFailCallback onMatchNotFoundFallback = new OnFailCallback() {
                    @Override
                    public void onFail() {
                        mMatcher = null;
                        mSelfChallengeManager = new SelfChallengeManager();
                        mUserRoomRef.runTransaction(mSelfChallengeManager);
                    }
                };

                mMatcher = new Matcher(onMatchNotFoundFallback);
                mUserRoomRef.runTransaction(mMatcher);
            }
        }).start();
    }

    public void stop() {
        if (mSelfChallengeManager == null ||
                mSelfChallengeCanceller != null) {
            return;
        }

        mSelfChallengeCanceller = new SelfChallengeCanceller(mSelfChallengeManager);
        mUserRoomRef.runTransaction(mSelfChallengeCanceller);
    }

    private boolean isChallengeCompat(com.gladys.cybuverse.Helpers.GameHelpers.Challenge oppoChallenge) {
        return true;
    }

    protected void onMatchFound(boolean isOpener) {
        mIsThisOpener = isOpener;
        mLocalPlayerIndex = (isOpener) ? 1 : 0;
        mOnComplete.run(this);
    }

    public boolean isThisOpener() {
        return mIsThisOpener;
    }

    public static interface OnMatchMadeCallback {
        public void run(FirebasePlayerMatchMaker c);
    }

    public interface OnFailCallback {
        public void onFail();
    }

    protected class Matcher implements Transaction.Handler {

        private final FirebasePlayerMatchMaker.OnFailCallback mFailCallback;
        private com.gladys.cybuverse.Helpers.GameHelpers.Challenge mSelectedChallenge = null;

        protected Matcher(@Nullable FirebasePlayerMatchMaker.OnFailCallback failCallback) {
            mFailCallback = failCallback;
        }

        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
            for (MutableData challengeData : mutableData.getChildren()) {
                final com.gladys.cybuverse.Helpers.GameHelpers.Challenge postedChallenge = challengeData.getValue(com.gladys.cybuverse.Helpers.GameHelpers.Challenge.class);

                if (isChallengeCompat(postedChallenge)) {
                    mSelectedChallenge = postedChallenge;
                    challengeData.setValue(null);
                    return Transaction.success(mutableData);
                }
            }

            Helper.log("MatchMaker.Matcher", "Didn't find any matching challenge");
            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError databaseError,
                               boolean b, @Nullable DataSnapshot dataSnapshot) {
            if (mSelectedChallenge != null) {
                mOpener = mSelectedChallenge.opener;
                mGamePath = mSelectedChallenge.gameRef;
                Helper.log("MatchMaker.Matcher", "Found match, onComplete");
                onMatchFound(false);
            } else if (mFailCallback != null) {
                mFailCallback.onFail();
            }
        }


    }


    protected class SelfChallengeCanceller implements Transaction.Handler {

        private final SelfChallengeManager mChallenger;

        private SelfChallengeCanceller(SelfChallengeManager challenger) {
            Helper.log("MatchMaker.Cancel", "Opened cancel request");
            mChallenger = challenger;
        }

        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
            mChallenger.mChallengeRef.removeEventListener(mChallenger);
            final String challengeKey = mChallenger.mChallengeRef.getKey();

            for (MutableData challengeNode : mutableData.getChildren()) {
                if (challengeNode.getKey().contentEquals(challengeKey)) {
                    challengeNode.setValue(null);
                }
            }

            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError databaseError, boolean b,
                               @Nullable DataSnapshot dataSnapshot) {
            mChallenger.mChallengeRef = null;
            final DatabaseReference gameRecordRef = mChallenger.mGameRecordRef;

            if (gameRecordRef != null) {
                gameRecordRef.setValue(null);
            }
        }
    }


    public class SelfChallengeManager implements Transaction.Handler, ValueEventListener {

        protected final com.gladys.cybuverse.Helpers.GameHelpers.Challenge mUploadedChallenge;
        protected DatabaseReference mChallengeRef;
        protected DatabaseReference mGameRecordRef;

        protected SelfChallengeManager() {
            mUploadedChallenge = new Challenge(
                    FirebaseAuth.getInstance().getCurrentUser().getUid(), null);
        }

        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
            mGameRecordRef = FirebaseDatabase.getInstance().getReference()
                    .child(GAMES_RECORD)
                    .push();
            mUploadedChallenge.gameRef =
                    GAMES_RECORD + "/" + mGameRecordRef.getKey();
            mGamePath = mUploadedChallenge.gameRef;

            mChallengeRef = mUserRoomRef.push();
            mChallengeRef.setValue(mUploadedChallenge);
            mChallengeRef.addValueEventListener(this);

            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError databaseError, boolean b,
                               @Nullable DataSnapshot dataSnapshot) {
            Helper.log("MatchMaker", "Published player challenge");
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() == null) {
                mChallengeRef = null;
                mGameRecordRef = null;
                mSelfChallengeManager = null;
                onMatchFound(true);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Helper.log("MatchMaker", "Cancelled: " + databaseError.getMessage());
        }
    }

}
