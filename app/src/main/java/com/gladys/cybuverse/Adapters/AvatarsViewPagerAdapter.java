package com.gladys.cybuverse.Adapters;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Actor;
import com.gladys.cybuverse.Fragments.ListFragment;
import com.gladys.cybuverse.Fragments.OnPrepareViewHolder;
import com.gladys.cybuverse.Models.Avatar;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class AvatarsViewPagerAdapter extends FragmentPagerAdapter {

    private List<ListFragment> mListFragments;
    private List<Actor> mActors;
    private OnListItemInteractionListener<Avatar> onAvatarItemInteractionListener;
    private OnListItemInteractionListener<Actor> onActorItemInteractionListener;

    public AvatarsViewPagerAdapter(@NonNull FragmentManager fm, List<Actor> actors) {
        super(fm);
        mListFragments = new ArrayList<>();
        mActors = actors;
        setupListFragments();
    }

    public AvatarsViewPagerAdapter(@NonNull FragmentManager fm, List<Actor> actors, int behavior) {
        super(fm, behavior);
        mListFragments = new ArrayList<>();
        mActors = actors;
        setupListFragments();
    }

    private void setupListFragments() {
        mListFragments.add(getFragmentForUserAvatar(getOptionsForQuery(getUserAvatarQuery("male"))));
        mListFragments.add(getFragmentForUserAvatar(getOptionsForQuery(getUserAvatarQuery("female"))));
        mListFragments.add(getFragmentForUserAvatar(getOptionsForQuery(getUserAvatarQuery("other"))));
        mListFragments.add(getFragmentForActorAvatar(mActors));
    }

    private Query getUserAvatarQuery(String gender) {
        return FirebaseFirestore.getInstance().collection("Avatars")
                .whereEqualTo("gender", gender);
    }

    private FirestoreRecyclerOptions<Avatar> getOptionsForQuery(Query query) {
        return new FirestoreRecyclerOptions.Builder<Avatar>().setQuery(query, Avatar.class).build();
    }

    private ListFragment getFragmentForActorAvatar(final List<Actor> actors) {
        return new ListFragment<>(R.layout.item_actor_avatar, actors, new OnPrepareViewHolder<Actor>() {
            @Override
            public void onPrepare(final RecyclerView.ViewHolder holder, final Actor item, final int position) {

                ((TextView) holder.itemView.findViewById(R.id.name)).setText(item.getName());
                ((TextView) holder.itemView.findViewById(R.id.role)).setText(item.getRole().replace("-", " "));

                FirebaseFirestore.getInstance().collection("Avatars")
                        .whereEqualTo("properties.isActor", true)
                        .whereEqualTo("properties.actorName", item.getName().toLowerCase().trim())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().getDocuments().size() == 1) {
                            Avatar avatar = task.getResult().getDocuments().get(0).toObject(Avatar.class);
                            Glide.with(holder.itemView.getContext())
                                    .load(Uri.parse(avatar.getImageUri()))
                                    .into((ImageView) holder.itemView.findViewById(R.id.image));
                        }
                    }
                });

                holder.itemView.findViewById(R.id.image_picker).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onActorItemInteractionListener != null)
                            onActorItemInteractionListener.onItemInteraction(item, position, null);
                    }
                });

            }
        });
    }

    private ListFragment getFragmentForUserAvatar(FirestoreRecyclerOptions<Avatar> options) {
        return new ListFragment<>(R.layout.item_user_avatar, 3, options, new OnPrepareViewHolder<Avatar>() {
            @Override
            public void onPrepare(RecyclerView.ViewHolder holder, final Avatar item, final int position) {

                Glide.with(holder.itemView.getContext())
                        .load(Uri.parse(item.getThumbnailUri()))
                        .into((ImageView) holder.itemView.findViewById(R.id.image));

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (onAvatarItemInteractionListener != null)
                            onAvatarItemInteractionListener.onItemInteraction(item, position, null);
                        return false;
                    }
                });
            }
        });
    }


    public void setActors(List<Actor> actorsList) {
        mActors = actorsList;
        mListFragments.get(3).setDataSet(mActors);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mListFragments.get(position);
    }

    @Override
    public int getCount() {
        return mListFragments.size();
    }

    public OnListItemInteractionListener<Avatar> getOnAvatarItemInteractionListener() {
        return onAvatarItemInteractionListener;
    }

    public void setOnAvatarItemInteractionListener(OnListItemInteractionListener<Avatar> onAvatarItemInteractionListener) {
        this.onAvatarItemInteractionListener = onAvatarItemInteractionListener;
    }

    public OnListItemInteractionListener<Actor> getOnActorItemInteractionListener() {
        return onActorItemInteractionListener;
    }

    public void setOnActorItemInteractionListener(OnListItemInteractionListener<Actor> onActorItemInteractionListener) {
        this.onActorItemInteractionListener = onActorItemInteractionListener;
    }

    public List<ListFragment> getFragments() {
        return mListFragments;
    }
}
