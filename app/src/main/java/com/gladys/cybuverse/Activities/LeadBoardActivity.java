package com.gladys.cybuverse.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gladys.cybuverse.Fragments.ListFragment;
import com.gladys.cybuverse.Fragments.OnPrepareViewHolder;
import com.gladys.cybuverse.Helpers.PlaySoundOnClickListener;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class LeadBoardActivity extends AppCompatActivity {

    private int[] colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_board);
        initializeViews();
    }

    private void initializeViews() {

        findViewById(R.id.back).setOnClickListener(new PlaySoundOnClickListener(R.raw.button_click_mid_level_pitch) {
            @Override
            public void runOnClick(View v) {
                onBackPressed();
            }
        });

        colorList = new int[]{
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.t_blue_light),
                getResources().getColor(R.color.t_reddish_brown),
                getResources().getColor(R.color.light_green),
                getResources().getColor(R.color.t_purple),
                getResources().getColor(R.color.colorFacebook),
                getResources().getColor(R.color.accessibility_focus_highlight),
                getResources().getColor(R.color.colorButtonRed)
        };

        ListFragment<User> fragment = new ListFragment<>(R.layout.item_lead_user, getOptionsForQuery(getUserQuery()), new OnPrepareViewHolder<User>() {
            @Override
            public void onPrepare(RecyclerView.ViewHolder holder, User item, int position) {
                holder.itemView.findViewById(R.id.position_image).setVisibility(View.GONE);
                holder.itemView.findViewById(R.id.position_text).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.points).setVisibility(View.VISIBLE);
                ((TextView) holder.itemView.findViewById(R.id.name)).setText(item.getName());
                String points = item.getInfo("points").toString() + " points";
                ((TextView) holder.itemView.findViewById(R.id.points)).setText(points);
                String userPosition = (position + 1) + "" + getOrder(position + 1);
                ((TextView) holder.itemView.findViewById(R.id.position_text)).setText(userPosition);
                ((TextView) holder.itemView.findViewById(R.id.position_text))
                        .setTextColor(colorList[new Random().nextInt(colorList.length)]);
                if (!item.getProfileUri().equals("default")) {
                    Glide.with(getApplicationContext())
                            .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_user))
                            .load(item.getProfileUri())
                            .into((ImageView) holder.itemView.findViewById(R.id.image));
                }

            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

    }

    private String getOrder(int position) {
        char lastNumber = String.valueOf(position).charAt(String.valueOf(position).length() - 1);
        return (lastNumber == '1') ? "st" : (lastNumber == '2') ? "nd" : (lastNumber == '3') ? "rd" : "th";
    }

    private Query getUserQuery() {
        return FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("info.is-new-user", false)
                .orderBy("info.points", Query.Direction.DESCENDING)
                .limit(30);
    }

    private FirestoreRecyclerOptions<User> getOptionsForQuery(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
