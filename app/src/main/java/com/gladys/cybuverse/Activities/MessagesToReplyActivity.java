package com.gladys.cybuverse.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gladys.cybuverse.Fragments.ListFragment;
import com.gladys.cybuverse.Fragments.OnPrepareViewHolder;
import com.gladys.cybuverse.Helpers.AlertTechupDialog;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Models.Question;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesToReplyActivity extends AppCompatActivity {

    private FirestoreRecyclerAdapter questionsListAdapter;
    List<Question> SELECTED = new ArrayList<>();
    boolean isSelectionActive = false;
    boolean isSelectionAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_to_reply);
        initializeViews();
    }

    private void initializeViews() {

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final int colorGreen = getResources().getColor(R.color.t_green);

        ListFragment<Question> fragment = new ListFragment<>(R.layout.item_chat_item, getQuestionsQuery(), new OnPrepareViewHolder<Question>() {


            @Override
            public void onPrepare(final RecyclerView.ViewHolder holder, final Question item, final int position) {
                item.addProperty("objectID", ((DocumentSnapshot) getQuestionsListAdapter().getSnapshots().getSnapshot(position)).getId());
                final CheckBox checkBox = holder.itemView.findViewById(R.id.checkbox);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (!SELECTED.contains(item))SELECTED.add(item);
                        }
                        else SELECTED.remove(item);
                    }
                });

                ((TextView) holder.itemView.findViewById(R.id.name)).setText(item.getName());
                Date date = new Date();
                if (item.getTimestamp() != null) {
                    date = item.getTimestamp().toDate();
                }
                String timestamp = new SimpleDateFormat("dd MM yyyy", Locale.US).format(date);
                ((TextView) holder.itemView.findViewById(R.id.last_seen)).setText(timestamp);
                ((TextView) holder.itemView.findViewById(R.id.last_message)).setText(item.getMessage());

                if (!item.getProfileImageUri().equals("default")) {
                    Glide.with(holder.itemView.getContext())
                            .load(Uri.parse(item.getProfileImageUri()))
                            .into((ImageView) holder.itemView.findViewById(R.id.image));
                }

                if (item.getResponse() == null) {
                    ((TextView) holder.itemView.findViewById(R.id.unread_count)).setText("");
                    ((CardView) holder.itemView.findViewById(R.id.unread_count_card))
                            .setCardBackgroundColor(colorGreen);
                } else {
                    holder.itemView.findViewById(R.id.unread_count_card).setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isSelectionActive) {
                            checkBox.setChecked(!checkBox.isChecked());
                        } else {
                            Intent intent = new Intent(MessagesToReplyActivity.this, ReplyMessageActivity.class);
                            intent.putExtra("id", item.getProperty("objectID").toString());
                            intent.putExtra("email", item.getEmail());
                            intent.putExtra("name", item.getName());
                            intent.putExtra("message", item.getMessage());
                            intent.putExtra("imageUri", item.getProfileImageUri());
                            intent.putExtra("response", item.getResponse());
                            intent.putExtra("chatUID", item.getChatUID());
                            startActivity(intent);
                        }
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (!isSelectionActive) {
                            isSelectionActive = true;
                            selectionControllerHeaderVisible(true);
                            ((CheckBox) findViewById(R.id.globalCheckBox)).setChecked(false);
                            questionsListAdapter.notifyDataSetChanged();
                        }
                        holder.itemView.callOnClick();

                        return false;
                    }
                });

                checkBox.setVisibility(isSelectionActive ? View.VISIBLE : View.GONE);
                if (isSelectionAll){
                    if (!SELECTED.contains(item)) SELECTED.add(item);
                }
                checkBox.setChecked(isSelectionActive && SELECTED.contains(item));


            }
        });

        questionsListAdapter = (FirestoreRecyclerAdapter) fragment.getAdapter();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        ((CheckBox) findViewById(R.id.globalCheckBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSelectionAll = isChecked;
                if (!isChecked) SELECTED.clear();
                questionsListAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertTechupDialog techupDialog = new AlertTechupDialog(MessagesToReplyActivity.this);
                techupDialog.setMessageText("Are you certain you want to delete the selected messages?");
                techupDialog.setPositiveButton("No", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        techupDialog.dismiss();
                    }
                });
                techupDialog.setNegativeButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        techupDialog.dismiss();
                        for (Question question: SELECTED){
                            FirebaseFirestore.getInstance().collection("Questions")
                                    .document(question.getProperty("objectID").toString())
                                    .delete();
                        }
                        isSelectionActive = false;
                        isSelectionAll = false;
                        SELECTED.clear();
                        questionsListAdapter.notifyDataSetChanged();
                        selectionControllerHeaderVisible(false);
                    }
                });
                techupDialog.show();

            }
        });

    }

    private void selectionControllerHeaderVisible(boolean visible) {
        findViewById(R.id.title).setVisibility(visible ? View.GONE : View.VISIBLE);
        findViewById(R.id.selectionController).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public FirestoreRecyclerAdapter getQuestionsListAdapter() {
        return questionsListAdapter;
    }

    private FirestoreRecyclerOptions<Question> getQuestionsQuery() {
        return getOptionsForQuery(FirebaseFirestore.getInstance().collection("Questions")
                .orderBy("timestamp", Query.Direction.DESCENDING));
    }

    private FirestoreRecyclerOptions<Question> getOptionsForQuery(Query query) {
        return new FirestoreRecyclerOptions.Builder<Question>().setQuery(query, Question.class).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        questionsListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        questionsListAdapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        if (isSelectionActive){
            SELECTED.clear();
            isSelectionAll = false;
            isSelectionActive = false;
            questionsListAdapter.notifyDataSetChanged();
            selectionControllerHeaderVisible(false);
        }
        else {
            setResult(RESULT_CANCELED);
            supportFinishAfterTransition();
        }
    }
}
