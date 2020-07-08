package com.gladys.cybuverse.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.gladys.cybuverse.Adapters.LearningPostsAdapter;
import com.gladys.cybuverse.Adapters.OnListItemInteractionListener;
import com.gladys.cybuverse.Helpers.AlertTechupDialog;
import com.gladys.cybuverse.Helpers.TechupApplication;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Helpers.TechupAnimationUtils;
import com.gladys.cybuverse.Models.Post;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class LearningActivity extends AppCompatActivity {

    public static final int NEW_POST_REQUEST_CODE = 300;
    public static final int NEXT_POST_LOAD_SIZE = 10;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LearningPostsAdapter postsAdapter;
    private RecyclerView recyclerView;
    private User user;

    private DocumentSnapshot lastDocumentSnapshot;
    private boolean isLoading;
    private boolean isAllPostLoaded;
    private boolean isFirstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        isAllPostLoaded = false;
        isFirstRun = true;

        user = ((TechupApplication) getApplicationContext()).getUser();
        recyclerView = findViewById(R.id.learningPostsRv);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        postsAdapter = new LearningPostsAdapter(this, new ArrayList<Post>(), new OnListItemInteractionListener<Post>() {
            @Override
            public void onItemInteraction(Post post, int position, Object interactionType) {
                Intent intent = new Intent(LearningActivity.this, PostItemActivity.class);
                intent.putExtra("fileType", post.getType());
                intent.putExtra("fileUri", post.getMediaUri());
                intent.putExtra("linkUri", post.getLink());
                intent.putExtra("message", post.getMessage());
                Date date = post.getTimestamp();
                intent.putExtra("timestamp", new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(date == null ? new Date() : post.getTimestamp()));
                if (post.hasProperty("thumbnailByteArray")) {
                    intent.putExtra("thumbnailByteArray", (byte[]) post.getProperty("thumbnailByteArray"));
                } else if (post.hasProperty("thumbnailUri")) {
                    intent.putExtra("thumbnailUri", post.getProperty("thumbnailUri").toString());
                }
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(postsAdapter);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupRecyclerViewOnScrollListener();

        setupSwipeToRefresh();

        tryLoadPosts();
    }

    private void setupRecyclerViewOnScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager != null && manager.findLastVisibleItemPosition() == postsAdapter.getDataSet().size() - 1) {
                    if (!isLoading && !isAllPostLoaded) {
                        isLoading = true;
                        loadMorePost();
                    }
                }
            }
        });
    }

    private void loadMorePost() {
        showLoadMoreView();
        loadMorePosts();
    }

    private void setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isAllPostLoaded = false;
                postsAdapter.getDataSet().clear();
                postsAdapter.notifyDataSetChanged();
                tryLoadPosts();
            }
        });
    }

    private void tryLoadPosts() {

        if (!Helper.isNetworkAvailable(this)) {
            showError("No Internet", "you do not have any internet connection available to load the posts!. please check you connection and try again");
        } else {
            loadPosts();
        }
    }

    private void loadPosts() {
        FirebaseFirestore.getInstance().collection("LearningPosts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(NEXT_POST_LOAD_SIZE)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                swipeRefreshLayout.setRefreshing(false);
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();

                    if (documents.isEmpty()) {
                        showNoPost();
                    } else {
                        hideShimmer();
                        for (DocumentSnapshot doc : documents) {
                            Post post = doc.toObject(Post.class);
                            post.addProperty("objectID", doc.getId());
                            postsAdapter.addPost(post);
                        }
                        lastDocumentSnapshot = documents.get(documents.size() - 1);
                    }


                    if (isFirstRun && user.hasInfoKey("is-admin") && (boolean) user.getInfo("is-admin")) {
                        isFirstRun = false;
                        findViewById(R.id.add_new_material).setVisibility(View.VISIBLE);
                        findViewById(R.id.add_new_material).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivityForResult(
                                        new Intent(LearningActivity.this, AddLearningMaterialActivity.class),
                                        NEW_POST_REQUEST_CODE);
                            }
                        });
                        if (getIntent().getExtras() != null && getIntent().getStringExtra("fileType") != null){
                            onActivityResult(NEW_POST_REQUEST_CODE, RESULT_OK, getIntent());
                        }
                    }

                } else {
                    showError("Problem", task.getException().getMessage());
                    Helper.log(task.getException().getMessage());
                }
            }
        });
    }

    private void loadMorePosts() {

        Query query = FirebaseFirestore.getInstance().collection("LearningPosts")
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(NEXT_POST_LOAD_SIZE);

        if (lastDocumentSnapshot != null)
            query = query.startAfter(lastDocumentSnapshot);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                hideLoadMoreView();
                isLoading = false;

                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();

                    if (documents.isEmpty()) {
                        isAllPostLoaded = true;
                        Helper.shortToast(getApplicationContext(), "you are at the end of the posts.");
                    } else {
                        for (DocumentSnapshot doc : documents) {
                            Post post = doc.toObject(Post.class);
                            post.addProperty("objectID", doc.getId());
                            postsAdapter.addPost(post);
                        }
                        lastDocumentSnapshot = documents.get(documents.size() - 1);
                    }

                } else {
                    Helper.shortToast(getApplication(), "could not load more post!." + task.getException().getMessage());
                }
            }
        });
    }

    public void showLoadMoreView() {
        Post post = new Post();
        post.setType("null");
        postsAdapter.addPost(post);
    }

    private void hideLoadMoreView() {
        int loaderIndex = postsAdapter.getDataSet().size() - 1;
        postsAdapter.getDataSet().remove(loaderIndex);
        postsAdapter.notifyItemRemoved(loaderIndex);
    }

    private void showError(String title, String message) {
        findViewById(R.id.shimmerView).setVisibility(View.GONE);
        final AlertTechupDialog techupDialog = new AlertTechupDialog(this);
        techupDialog.setTitleText(title);
        techupDialog.setMessageText(message);
        techupDialog.setNeutralButton("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                techupDialog.dismiss();
                tryLoadPosts();
            }
        });
        techupDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                techupDialog.dismiss();
                hideShimmer();
            }
        });
        techupDialog.show();
    }

    private void hideShimmer() {
        TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.root_view));
        findViewById(R.id.shimmerContainer).setVisibility(View.GONE);
        ((ShimmerFrameLayout) findViewById(R.id.shimmerView)).stopShimmer();
    }

    private void showNoPost() {
        TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.root_view));
        ((ShimmerFrameLayout) findViewById(R.id.shimmerView)).stopShimmer();
        findViewById(R.id.shimmerView).setVisibility(View.GONE);
        final View info = findViewById(R.id.info_no_post);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, info.getPivotX(), info.getPivotY());
        scaleAnimation.setInterpolator(new FastOutSlowInInterpolator());
        scaleAnimation.setDuration(1000);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                info.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TechupAnimationUtils.bounceY(info, 8f, 600);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        info.startAnimation(scaleAnimation);
    }

    @Override
    protected void onResume() {

        if (user == null)
            user = ((TechupApplication) getApplicationContext()).getUser();

        if (!isFirstRun && user.hasInfoKey("is-admin") && (boolean) user.getInfo("is-admin")) {
            findViewById(R.id.add_new_material).setVisibility(View.VISIBLE);
            findViewById(R.id.add_new_material).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(
                            new Intent(LearningActivity.this, AddLearningMaterialActivity.class),
                            NEW_POST_REQUEST_CODE);
                }
            });
        }

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_POST_REQUEST_CODE && resultCode == RESULT_OK) {

            if (findViewById(R.id.shimmerContainer).getVisibility() == View.VISIBLE) {
                findViewById(R.id.shimmerContainer).setVisibility(View.GONE);
            }

            String fileType = data.getExtras().getString("fileType");
            String message = data.getExtras().getString("message");
            String fileUriString = data.getExtras().getString("fileUri");
            String linkUriString = data.getExtras().getString("linkUri");
            Uri fileUri = fileUriString == null ? null : Uri.parse(fileUriString);
            Uri linkUri = linkUriString == null ? null : Uri.parse(linkUriString);
            byte[] byteArray = data.getExtras().getByteArray("thumbnailBitmap");

            Helper.log("posting message: " + message);
            Helper.log("posting fileType: " + fileType);
            Helper.log("posting fileUri: " + (fileUri == null ? "null" : fileUri.toString()));
            Helper.log("posting linkUri: " + (linkUri == null ? "null" : linkUri.toString()));
            Helper.log("posting thumbnail: " + (byteArray == null ? "null" : byteArray.length));

            Post post = new Post();
            post.setLink((linkUri == null ? null : linkUri.toString()));
            post.setMediaUri((fileUri == null ? null : fileUri.toString()));
            post.setMessage(message);
            post.setType(fileType);
            post.addProperty("thumbnailByteArray", byteArray);
            post.addProperty("isNewPost", true);


            postsAdapter.addPost(0, post);
            recyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }
}
