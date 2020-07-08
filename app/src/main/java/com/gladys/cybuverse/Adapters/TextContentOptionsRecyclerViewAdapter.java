package com.gladys.cybuverse.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Message;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageContent;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.TaggedMessageContent;
import com.gladys.cybuverse.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Message} and makes a call to the
 * specified {@link com.gladys.cybuverse.Adapters.OnListItemInteractionListener <MessageContent>}.
 * TODO: Replace the implementation with code for your data info.
 */
public class TextContentOptionsRecyclerViewAdapter extends RecyclerView.Adapter<TextContentOptionsRecyclerViewAdapter.ViewHolder> {

    private final List<MessageContent> mValues;
    private final com.gladys.cybuverse.Adapters.OnListItemInteractionListener<MessageContent> mListener;

    public TextContentOptionsRecyclerViewAdapter(com.gladys.cybuverse.Adapters.OnListItemInteractionListener listener) {
        mValues = new ArrayList<>();
        mListener = listener;
    }

    public TextContentOptionsRecyclerViewAdapter(List<MessageContent> items, OnListItemInteractionListener<MessageContent> listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_text_content_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        if (holder.mItem instanceof TaggedMessageContent)
            holder.mContentView.setText(((TaggedMessageContent) holder.mItem).getTag());
        else holder.mContentView.setText(holder.mItem.getData().toString());

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
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<MessageContent> getDataSet() {
        return mValues;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public MessageContent mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
