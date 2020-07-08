package com.gladys.cybuverse.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gladys.cybuverse.Fragments.ListFragment;
import com.gladys.cybuverse.Fragments.OnPrepareViewHolder;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class UsersProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_progress);
        initializeViews();
    }

    private void initializeViews() {

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ListFragment<User> fragment = new ListFragment<>(R.layout.item_chat_item, getUserQuery(), new OnPrepareViewHolder<User>() {
            @Override
            public void onPrepare(RecyclerView.ViewHolder holder, User item, int position) {
                holder.itemView.findViewById(R.id.unread_count_card).setVisibility(View.GONE);
                ((TextView) holder.itemView.findViewById(R.id.name)).setText(item.getName());
                String points = "" + item.getInfo().get("points") + " points";
                ((TextView) holder.itemView.findViewById(R.id.last_seen)).setText(points);
                ((TextView) holder.itemView.findViewById(R.id.last_message)).setText(item.getEmail());

                if (!item.getProfileUri().equals("default")) {
                    Glide.with(holder.itemView.getContext())
                            .load(Uri.parse(item.getProfileUri()))
                            .into((ImageView) holder.itemView.findViewById(R.id.image));
                }

            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();


    }


    private FirestoreRecyclerOptions<User> getUserQuery() {
        return getOptionsForQuery(FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("info.is-new-user", false)
                .orderBy("name"));
    }

    private FirestoreRecyclerOptions<User> getOptionsForQuery(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        supportFinishAfterTransition();
    }
}
