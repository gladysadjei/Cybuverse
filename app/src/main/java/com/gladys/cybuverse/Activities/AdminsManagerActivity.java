package com.gladys.cybuverse.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gladys.cybuverse.Fragments.ListFragment;
import com.gladys.cybuverse.Fragments.OnPrepareViewHolder;
import com.gladys.cybuverse.Helpers.AlertTechupDialog;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.ProgressTechupDialog;
import com.gladys.cybuverse.Helpers.TechupDialog;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;

public class AdminsManagerActivity extends AppCompatActivity {

    private FloatingActionButton addAdminFab, closeAdminFab;
    private ListFragment<User> userListFragment, adminsListFragment;
    private TextView searchUserTextView;
    private TechupDialog techupDialog;
    private FirestoreRecyclerAdapter adminListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admins_manager);
        initializeViews();
    }

    private void initializeViews() {

        addAdminFab = findViewById(R.id.add_admin);
        closeAdminFab = findViewById(R.id.close_add_admin);
        searchUserTextView = findViewById(R.id.search_text);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        startAdminsListAdapter();

        Query nonAdminUsers = FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("info.is-admin", false).orderBy("name");

        nonAdminUsers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Helper.log(task.getResult().toObjects(User.class).toString());
                }
            }
        });

        startUserSearchListAdapter(nonAdminUsers);

        setUserSearchTextChangeListener();

        setAdminFabClickListener();

    }

    private void startAdminsListAdapter() {
        Query query = FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("info.is-admin", true).orderBy("name");

        adminsListFragment = new ListFragment<>(R.layout.item_chat_item, getOptionsForQuery(query), new OnPrepareViewHolder<User>() {
            @Override
            public void onPrepare(RecyclerView.ViewHolder holder, final User item, int position) {

                holder.itemView.findViewById(R.id.last_seen).setVisibility(View.GONE);
                ((TextView) holder.itemView.findViewById(R.id.unread_count)).setText("  ");
                ((TextView) holder.itemView.findViewById(R.id.unread_count)).setBackgroundResource(R.drawable.ic_close_yellow);

                item.addInfo("objectID", ((DocumentSnapshot) getAdminListAdapter().getSnapshots().getSnapshot(position)).getId());
                ((TextView) holder.itemView.findViewById(R.id.name)).setText(item.getName());
                ((TextView) holder.itemView.findViewById(R.id.last_message)).setText(item.getEmail());

                if (!item.getProfileUri().equals("default")) {
                    Glide.with(holder.itemView.getContext())
                            .load(Uri.parse(item.getProfileUri()))
                            .into((ImageView) holder.itemView.findViewById(R.id.image));
                }

                holder.itemView.findViewById(R.id.unread_count_card).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startRemoveProcessForUser(item);
                    }
                });

                Animation animation = new ScaleAnimation(0.8f, 1, 0.8f, 1, holder.itemView.getPivotX(), holder.itemView.getPivotY());
                animation.setInterpolator(new BounceInterpolator());
                animation.setDuration(600);
                holder.itemView.startAnimation(animation);

            }
        });

        adminListAdapter = (FirestoreRecyclerAdapter) adminsListFragment.getAdapter();

        getSupportFragmentManager().beginTransaction().replace(R.id.admin_list_container, adminsListFragment).commit();
    }

    public FirestoreRecyclerAdapter getAdminListAdapter() {
        return adminListAdapter;
    }

    private void startUserSearchListAdapter(Query query) {

        userListFragment = new ListFragment<>(R.layout.item_chat_item, getOptionsForQuery(query), new OnPrepareViewHolder<User>() {
            @Override
            public void onPrepare(RecyclerView.ViewHolder holder, final User item, int position) {
                holder.itemView.findViewById(R.id.last_seen).setVisibility(View.GONE);
                holder.itemView.findViewById(R.id.unread_count_card).setVisibility(View.GONE);
                ((TextView) holder.itemView.findViewById(R.id.name)).setText(item.getName());
                ((TextView) holder.itemView.findViewById(R.id.last_message)).setText(item.getEmail());

                if (!item.getProfileUri().equals("default")) {
                    Glide.with(holder.itemView.getContext())
                            .load(Uri.parse(item.getProfileUri()))
                            .into((ImageView) holder.itemView.findViewById(R.id.image));
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchUserTextView.setText(item.getEmail());
                    }
                });
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.search_result_container, userListFragment).commit();
    }

    private void setUserSearchTextChangeListener() {

        searchUserTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    Query query = FirebaseFirestore.getInstance().collection("Users")
                            .whereEqualTo("info.is-admin", false);
                    startUserSearchListAdapter(query);
                } else {
                    Query query = FirebaseFirestore.getInstance().collection("Users")
                            .whereEqualTo("info.is-admin", false)
                            .whereEqualTo("email", s.toString().trim());

                    startUserSearchListAdapter(query);

                    if (Helper.isValidEmailId(s.toString().trim())) {
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult().getDocuments().size() == 1) {
                                    addAdminFab.setImageResource(R.drawable.ic_check_white_24dp);
                                    showFab(closeAdminFab);
                                }
                            }
                        });
                    } else {
                        addAdminFab.setImageResource(R.drawable.ic_close_white_24dp);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setAdminFabClickListener() {
        addAdminFab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                boolean isOpen = (v.getTag() != null && v.getTag().toString().equals("open"));

                FirestoreRecyclerAdapter adapter = (FirestoreRecyclerAdapter) userListFragment.getAdapter();

                if (isOpen) {

                    if (adapter != null && adapter.getItemCount() == 1 &&
                            !searchUserTextView.getText().toString().trim().isEmpty()) {
                        startAddProcessForUser();
                    } else {
                        closeAddAdminView();
                        searchUserTextView.setText("");
                        if (adapter != null) adapter.stopListening();
                    }
                } else {
                    if (adapter != null) adapter.startListening();
                    openAddAdminView();
                }
            }
        });

        closeAdminFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFab(closeAdminFab);
                closeAddAdminView();
            }
        });
    }

    private void closeAddAdminView() {
        searchUserTextView.setText("");
        addAdminFab.setTag("close");

        Animation animation = new TranslateAnimation(0, 0, 0, 700);
        animation.setDuration(200);
        animation.setInterpolator(new FastOutLinearInInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.add_admin_view).setVisibility(View.GONE);
                addAdminFab.setImageResource(R.drawable.ic_add_white_24dp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(R.id.add_admin_view).startAnimation(animation);
    }

    private void openAddAdminView() {
        searchUserTextView.setText("");
        addAdminFab.setTag("open");

        Animation animation = new TranslateAnimation(0, 0, 700, 0);
        animation.setDuration(200);
        animation.setInterpolator(new FastOutLinearInInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.add_admin_view).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addAdminFab.setImageResource(R.drawable.ic_close_white_24dp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(R.id.add_admin_view).startAnimation(animation);
    }

    private FirestoreRecyclerOptions<User> getOptionsForQuery(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
    }

    private void startAddProcessForUser() {
        if (Helper.isNetworkAvailable(getApplicationContext())) {
            attemptAddAdmin();
        } else {
            Helper.shortToast(getApplicationContext(), "you need internet connection to complete this action");
        }
    }

    private void attemptAddAdmin() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        Helper.shortToast(getApplicationContext(), "making " + searchUserTextView.getText().toString() + " an admin");
        final String email = searchUserTextView.getText().toString().trim();

        FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().getDocuments().size() == 1) {
                    task.getResult().getDocuments().get(0).getReference().update("info.is-admin", true)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Helper.shortToast(getApplicationContext(), "you successfully made owner of email " + email + " admin.");
//                                        adminListAdapter.getAdapter().notifyDataSetChaged();
                                        hideFab(closeAdminFab);
                                        closeAddAdminView();
                                    } else {
                                        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                        Helper.shortToast(getApplicationContext(), "could not make user admin: " + task.getException().getMessage());
                                    }
                                }
                            });
                } else {
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    Helper.shortToast(getApplicationContext(), "could not make user admin: " + task.getException().getMessage());
                }
            }
        });
    }

    private void startRemoveProcessForUser(final User user) {
        if (Helper.isNetworkAvailable(getApplicationContext())) {
            validateRemoveAction(user);
        } else {
            Snackbar.make(findViewById(R.id.root_view), "you need internet connection to complete this action", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startRemoveProcessForUser(user);
                        }
                    })
                    .setTextColor(getResources().getColor(R.color.colorButtonRed))
                    .setActionTextColor(getResources().getColor(R.color.colorFacebook)).show();
        }
    }

    private void validateRemoveAction(final User user) {
        techupDialog = new AlertTechupDialog(AdminsManagerActivity.this);
        techupDialog.setTitleText("Remove Admin");
        techupDialog.setMessageText("Are you sure you want to remove user " + user.getEmail() + " as an admin?..");
        ((AlertTechupDialog) techupDialog).setPositiveButton("No", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
            }
        });
        ((AlertTechupDialog) techupDialog).setNegativeButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                attemptRemoveUser(user);
            }

        });
        techupDialog.show();
    }

    private void attemptRemoveUser(final User user) {
        techupDialog = new ProgressTechupDialog(AdminsManagerActivity.this);
        techupDialog.setTitleText("Removing Admin");
        techupDialog.setMessageText("attempting to remove user " + user.getEmail() + " as admin");
        techupDialog.setCancelable(false);
        techupDialog.show();

        FirebaseFirestore.getInstance().collection("Users")
                .document(user.getInfo("objectID").toString())
                .update("info.is-admin", false)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        techupDialog.dismiss();
                        if (task.isSuccessful()) {
//                  adminsListAdapter.notifyItemRemoved(adminsListAdapter.getSnapshots().indexOf(user));
                            Snackbar.make(findViewById(R.id.root_view), "removed user as admin successfully.", Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FirebaseFirestore.getInstance().collection("Users")
                                                    .document(user.getInfo("objectID").toString())
                                                    .update("info.is-admin", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
//                                  if (task.isSuccessful()) adminsListAdapter.notifyDataSetChanged();
                                                }
                                            });

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(R.color.colorFacebook)).show();
                        } else {
                            Snackbar.make(findViewById(R.id.root_view), "failed to complete action", Snackbar.LENGTH_LONG)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startRemoveProcessForUser(user);
                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(R.color.colorButtonRed)).show();
                        }
                    }
                });
    }

    private void hideFab(final View view) {
        Animation scaleAnim = new ScaleAnimation(1, 0, 1, 0,
                view.getPivotX(), view.getPivotY());
        scaleAnim.setDuration(200);
        scaleAnim.setInterpolator(new FastOutSlowInInterpolator());
        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(scaleAnim);
    }

    private void showFab(final View view) {
        Animation scaleAnim = new ScaleAnimation(0, 1, 0, 1,
                view.getPivotX(), view.getPivotY());
        scaleAnim.setDuration(200);
        scaleAnim.setInterpolator(new FastOutSlowInInterpolator());
        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(scaleAnim);
    }

    @Override
    protected void onResume() {
//    adminsListAdapter.startListening();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
//    adminsListAdapter.stopListening();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.add_admin_view).getVisibility() == View.VISIBLE) {
            closeAddAdminView();
        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
            supportFinishAfterTransition();
        }
    }
}


