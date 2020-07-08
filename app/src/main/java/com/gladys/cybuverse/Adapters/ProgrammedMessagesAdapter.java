package com.gladys.cybuverse.Adapters;

import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ChatBot;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Message;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageContent;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Models.Question;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.gladys.cybuverse.Utils.GeneralUtils.Funcs;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Message} and makes a call to the
 * specified {@link OnListItemInteractionListener <Message>}.
 * TODO: Replace the implementation with code for your data info.
 */
public class ProgrammedMessagesAdapter extends RecyclerView.Adapter<ProgrammedMessagesAdapter.ViewHolder> {

    public static final int GLADYS_REPLY = 3;
    private static final int INFO = 0;
    private static final int SEND = 1;
    private static final int RECEIVE = 2;
    private final List<Message> mValues;
    private final OnListItemInteractionListener<Message> mListener;

    private User mUser;
    private Drawable profileImageDrawable,
            chatImageDrawable;
    private DisplayMetrics deviceSize;
    private String searchString;

    public ProgrammedMessagesAdapter(OnListItemInteractionListener<Message> listener) {
        mValues = new ArrayList<>();
        mListener = listener;
    }

    public ProgrammedMessagesAdapter(List<Message> items, OnListItemInteractionListener<Message> listener) {
        mValues = items;
        mListener = listener;
    }

    public ProgrammedMessagesAdapter(User user, List<Message> items, OnListItemInteractionListener<Message> listener) {
        setUser(user);
        mValues = items;
        mListener = listener;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

    private void setProfileImage(final ImageView imageView) {

        if (profileImageDrawable == null) {
            if (mUser != null) {

                if (mUser.hasInfoKey("profile-drawable")) {
                    profileImageDrawable = (Drawable) mUser.getInfo("profile-drawable");
                    Glide.with(imageView.getContext())
                            .load(profileImageDrawable)
                            .into(imageView);
                } else if (!mUser.getProfileUri().equals("default")) {
                    String uri = getUser().getProfileUri();
                    if (uri != null && !uri.trim().isEmpty()) {
                        Glide.with(imageView.getContext())
                                .load(uri)
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        profileImageDrawable = resource;
                                        imageView.setImageDrawable(resource);
                                    }
                                });
                    }
                }
            }
        } else {
            imageView.setImageDrawable(profileImageDrawable);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case SEND:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_send, parent, false);
                break;
            case RECEIVE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_receive, parent, false);
                break;
            case GLADYS_REPLY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_reply_receive, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_in_chat_info, parent, false);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        if (holder.mItem.getSender() == ChatBot.asActor()) {
            if (holder.getItemViewType() != INFO) {
                holder.mImageView.setImageDrawable(holder.mView.getContext()
                        .getResources().getDrawable(R.drawable.ic_gladys)
                );
            }
        } else if (holder.getItemViewType() == SEND) {
            setProfileImage(holder.mImageView);
        } else if (holder.getItemViewType() == RECEIVE) {
            if (chatImageDrawable != null)
                holder.mImageView.setImageDrawable(chatImageDrawable);
        }

        if (holder.mItem.hasProperty("gladys-reply"))
            ((TextView) holder.itemView.findViewById(R.id.message))
                    .setText(holder.mItem.getProperty("gladys-reply").toString());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onItemInteraction(holder.mItem, position, null);
                }
            }
        });

        if (searchString != null && !searchString.isEmpty()) {
            String message = holder.mItem.getContent().getData().toString().trim();

            SpannableString spannableString = new SpannableString(message);

            String tempMessage = message.toLowerCase();

            int index = tempMessage.indexOf(searchString.toLowerCase(), 0);

            Helper.log("first index: " + index);

            while (index != -1) {
                BackgroundColorSpan colorSpan = new BackgroundColorSpan(holder.itemView.getResources().getColor(R.color.colorFacebook));
                spannableString.setSpan(colorSpan, index, index + searchString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                index = tempMessage.indexOf(searchString.toLowerCase(), index + searchString.length());
                Helper.log("next-index: " + index);
            }

            holder.mContentView.setText(spannableString);
        } else {
            if (mValues.get(position).getContent().getData() != null)
                holder.mContentView.setText(mValues.get(position).getContent().getData().toString());
        }

        if (deviceSize != null)
            holder.mContentView.setMaxWidth(Funcs.toInteger(this.deviceSize.widthPixels * 0.65));
    }


    public void setDisplaySize(DisplayMetrics deviceSize) {
        this.deviceSize = deviceSize;
    }


    @Override
    public int getItemViewType(int position) {
        Message message = getDataSet().get(position);
        if (message.getSender().getName().equals(getUser().getName())) {
            return SEND;
        } else if (message.getSender() == ChatBot.asActor() &&
                message.hasProperty("gladys-info")) {
            return INFO;
        } else if (message.getSender() == ChatBot.asActor() &&
                message.hasProperty("gladys-reply")) {
            return GLADYS_REPLY;
        }
        return RECEIVE;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<Message> getDataSet() {
        return mValues;
    }

    public void addMessage(Message message) {
        if (message != null) {
            getDataSet().add(message);
            notifyItemInserted(getDataSet().size() - 1);
        }
    }

    public void removeMessage(Message message) {
        if (message != null) {
            getDataSet().remove(getDataSet().indexOf(message));
            notifyItemInserted(getDataSet().size() - 1);
        }
    }

    public void addQuestionReply(Question question) {
        Message message = new Message();
        message.setSender(ChatBot.asActor());
        message.setContent(new MessageContent(question.getResponse()));
        message.setProperty("gladys-reply", question.getMessage());
        addMessage(message);
    }

    public void openSearch(String searchString) {
        if (!searchString.equals(this.searchString)) {
            this.searchString = searchString;
            notifyDataSetChanged();
        }
    }

    public Drawable getChatDrawable() {
        return chatImageDrawable;
    }

    public void setChatDrawable(Drawable chatDrawable) {
        this.chatImageDrawable = chatDrawable;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final ImageView mImageView;
        public Message mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.image);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
