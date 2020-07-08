package com.gladys.cybuverse.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gladys.cybuverse.Fragments.ListFragment;
import com.gladys.cybuverse.Fragments.OnPrepareViewHolder;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.PlaySoundOnClickListener;
import com.gladys.cybuverse.Models.Avatar;
import com.gladys.cybuverse.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class AvatarPickerActivity extends AppCompatActivity {

    private Uri imageUri;
    private FirestoreRecyclerAdapter avatarAdapter;
    private int lastSelectedAvaterPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_picker);
        initializeViews();
        populateAvatars();
    }

    public int getLastSelectedAvaterPosition() {
        return lastSelectedAvaterPosition;
    }

    public void setLastSelectedAvaterPosition(int lastSelectedAvaterPosition) {
        this.lastSelectedAvaterPosition = lastSelectedAvaterPosition;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    private void initializeViews() {
        findViewById(R.id.back).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.save).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                if (getImageUri() != null) {
                    setResult(RESULT_OK, new Intent().setData(getImageUri()));
                    finish();
                } else {
                    Helper.shortToast(getApplicationContext(), "please select an image!!!.");
                }
            }
        });
    }

    private void populateAvatars() {

        String gender = getIntent().getExtras().getString("gender");

        ListFragment fragment = new ListFragment<>(R.layout.item_user_avatar, 3, getOptionsForQuery(getUserAvatarQuery(gender)), new OnPrepareViewHolder<Avatar>() {
            @Override
            public void onPrepare(final RecyclerView.ViewHolder holder, final Avatar item, final int position) {
                Glide.with(holder.itemView)
                        .load(Uri.parse(item.getThumbnailUri()))
                        .into((ImageView) holder.itemView.findViewById(R.id.image));

                if (getImageUri() != null && getLastSelectedAvaterPosition() == position) {
                    TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
                    holder.itemView.findViewById(R.id.checked).setVisibility(View.VISIBLE);
                } else {
                    holder.itemView.findViewById(R.id.checked).setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setImageUri(Uri.parse(item.getImageUri()));
                        processNewAvatarSelected(position);
                    }
                });
            }
        });

        avatarAdapter = (FirestoreRecyclerAdapter) fragment.getAdapter();

        TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.root_view));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

    }

    private void processNewAvatarSelected(int position) {
        if (avatarAdapter != null) {
            int tempLastPosition = lastSelectedAvaterPosition;
            lastSelectedAvaterPosition = position;
            avatarAdapter.notifyItemChanged(tempLastPosition);
            avatarAdapter.notifyItemChanged(position);
        } else {
            lastSelectedAvaterPosition = position;
        }
    }


    private Query getUserAvatarQuery(String gender) {
        return FirebaseFirestore.getInstance().collection("Avatars")
                .whereEqualTo("gender", gender);
    }

    private FirestoreRecyclerOptions<Avatar> getOptionsForQuery(Query query) {
        return new FirestoreRecyclerOptions.Builder<Avatar>().setQuery(query, Avatar.class).build();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
