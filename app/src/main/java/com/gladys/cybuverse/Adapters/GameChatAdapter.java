package com.gladys.cybuverse.Adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ChatBot;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.TechupAnimationUtils;
import com.gladys.cybuverse.Models.Avatar;
import com.gladys.cybuverse.Models.Chat;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Chat} and makes a call to the
 * specified {@link com.gladys.cybuverse.Adapters.OnListItemInteractionListener <Chat>}.
 * TODO: Replace the implementation with code for your data info.
 */
public class GameChatAdapter extends RecyclerView.Adapter<GameChatAdapter.ViewHolder> {

    private final OnListItemInteractionListener<Chat> mListener;
    private List<Chat> mValues, mDATA;

    public GameChatAdapter(List<Chat> items, OnListItemInteractionListener<Chat> listener) {
        mValues = items;
        mDATA = items;
        mListener = listener;
    }

    public List<Chat> getDataSet() {
        return mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
//        holder.mImageView;

        Helper.log("binding item: " + mValues.get(position).toString());

        holder.mUsername.setText(mValues.get(position).getName());
        holder.mLastMessage.setText(mValues.get(position).getLastMessage());
        holder.mLastSeen.setText(mValues.get(position).getLastSeen());
        if (mValues.get(position).getUnreadMessagesCount() > 0)
            holder.mUnreadMessagesCount.setText(String.valueOf(mValues.get(position).getUnreadMessagesCount()));
        else
            holder.mUnreadMessagesCard.setVisibility(View.INVISIBLE);

        //set profile image
        if (mValues.get(position).getId().equals(ChatBot.getUID())) {
            holder.mImageView.setImageResource(R.drawable.ic_gladys);
        } else {
            Chat chat = mValues.get(position);
            FirebaseFirestore.getInstance().collection("Avatars")
                    .whereEqualTo("properties.isActor", true)
                    .whereEqualTo("properties.actorName", chat.getName().toLowerCase().trim())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() == 1) {
                        Avatar avatar = task.getResult().getDocuments().get(0).toObject(Avatar.class);
                        Glide.with(holder.itemView.getContext())
                                .load(Uri.parse(avatar.getImageUri()))
                                .into(holder.mImageView);
                    }
                }
            });
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onItemInteraction(holder.mItem, position, null);
                }
            }
        });

        if (holder.mItem.hasProperty("is-active") &&
                (boolean) holder.mItem.getProperty("is-active")) {
            holder.mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TechupAnimationUtils.bounceX(holder.mView, 8f, 600);
                }
            }, 1000);
        }

        Animation animation = new ScaleAnimation(0.8f, 1, 0.8f, 1, holder.mView.getPivotX(), holder.mView.getPivotY());
        animation.setInterpolator(new BounceInterpolator());
        animation.setDuration(600);
        holder.mView.startAnimation(animation);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void openSearch(String query) {

        if (mDATA.size() < mValues.size())
            mDATA = new ArrayList<>(mValues);
        else
            mValues = new ArrayList<>(mDATA);

        query = query.toLowerCase().trim();
        List<Chat> matches = new ArrayList<>();
        for (Chat chat : mValues) {
            if (chat.getName().toLowerCase().contains(query) || chat.getLastMessage() != null &&
                    chat.getLastMessage().toLowerCase().contains(query)) {
                matches.add(chat);
            }
        }
        mValues = matches;
        notifyDataSetChanged();
    }

    public void closeSearch() {
        mValues = new ArrayList<>(mDATA);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView mImageView;
        public TextView mUsername;
        public TextView mLastMessage;
        public TextView mLastSeen;
        public TextView mUnreadMessagesCount;
        public CardView mUnreadMessagesCard;

        public View mView;
        public Chat mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.image);
            mUsername = view.findViewById(R.id.name);
            mLastSeen = view.findViewById(R.id.last_seen);
            mLastMessage = view.findViewById(R.id.last_message);
            mUnreadMessagesCount = view.findViewById(R.id.unread_count);
            mUnreadMessagesCard = view.findViewById(R.id.unread_count_card);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUsername.getText() + "'";
        }
    }
}
