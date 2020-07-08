package com.gladys.cybuverse.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.gladys.cybuverse.Helpers.AlertTechupDialog;
import com.gladys.cybuverse.Helpers.TechupApplication;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Models.Post;
import com.gladys.cybuverse.R;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.Variable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.toInteger;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Post} and makes a call to the
 * specified {@link com.gladys.cybuverse.Adapters.OnListItemInteractionListener <Post>}.
 * TODO: Replace the implementation with code for your data info.
 */
public class LearningPostsAdapter extends RecyclerView.Adapter<LearningPostsAdapter.ViewHolder> {

    public static final int LOADER_VIEW_TYPE = 204;
    private final List<Post> mDataSet;
    private final com.gladys.cybuverse.Adapters.OnListItemInteractionListener<Post> mListener;
    private static Activity mActivity;

    private int[] colorList;
    private OnPlayVideoAudioListener onPlayVideoAudioListener;


    public LearningPostsAdapter(Activity activity, List<Post> items, OnListItemInteractionListener<Post> listener) {
        mDataSet = items;
        mListener = listener;
        mActivity = activity;
        Resources resources = getActivity().getResources();
        colorList = new int[]{
                resources.getColor(R.color.colorPrimaryDark),
                resources.getColor(R.color.colorAccent),
                resources.getColor(R.color.colorPrimary),
                resources.getColor(R.color.t_blue_light),
                resources.getColor(R.color.t_reddish_brown),
                resources.getColor(R.color.t_reddish_orange)
        };
    }

    public void setOnPlayVideoAudioListener(OnPlayVideoAudioListener onPlayVideoAudioListener) {
        this.onPlayVideoAudioListener = onPlayVideoAudioListener;
    }

    public List<Post> getDataSet() {
        return mDataSet;
    }

    public static Activity getActivity() {
        return mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LOADER_VIEW_TYPE) {
            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setPadding(16, 16, 16, 16);
            ProgressBar progressBar = new ProgressBar(getActivity());
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.addView(progressBar);
            return new ViewHolder(linearLayout);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_learning_post_card, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (holder.getItemViewType() == LOADER_VIEW_TYPE)
            return;
        holder.setItemPosition(position);
    }

    @Override
    public int getItemCount() {
        return getDataSet().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getDataSet().get(position).getType().equals("null"))
            return LOADER_VIEW_TYPE;
        return super.getItemViewType(position);
    }

    public void addPost(Post post) {
        getDataSet().add(post);
        notifyItemInserted(getDataSet().size() - 1);
    }

    public void addPost(int index, Post post) {
        getDataSet().add(index, post);
        notifyItemInserted(index);
    }

    private void actionDeletePost(Post post) {
        int index = getDataSet().indexOf(post);
        getDataSet().remove(index);
        notifyItemRemoved(index);
        //TODO: delete  media and thumbnail and post using mediaReference and thumbnailReference
        if (post.getMediaUri() != null)
            FirebaseStorage.getInstance().getReferenceFromUrl(post.getMediaUri()).delete();
        if (post.hasProperty("thumbnailUri"))
            FirebaseStorage.getInstance().getReferenceFromUrl(post.getProperty("thumbnailUri").toString()).delete();
        FirebaseFirestore.getInstance().collection("LearningPosts").document(post.getProperty("objectID").toString()).delete();
        Helper.shortToast(getActivity(), "post deletion successful");
    }

    private void startPostUploadProcess(ViewHolder holder, int position) {
        //TODO: upload all post info
        //mediaUri
        //thumbnail
        //remove property bitmap-thumbnail
        //update post media uri to download uri from fstorage
        //add thumbnail uri property if has thumbnail
        //if file add mimetypes property
        //upload post

        Post post = getDataSet().get(position);

        if (post.getMediaUri() != null) {
            uploadPostProperty(holder, post, "media");
        } else if (post.hasProperty("thumbnailByteArray")) {
            uploadPostProperty(holder, post, "thumbnail", Helper.generateRandomString(15, null) + ".jpg");
        } else {
            uploadPostProperty(holder, post, "upload");
        }

    }

    private void uploadPostProperty(final ViewHolder holder, final Post post, String property, final Object... args) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        switch (property) {
            case "media": //60
                String extension;
                try {
                    extension = Helper.getMimeType(getActivity(), Uri.parse(post.getMediaUri())).split("/")[1];
                } catch (Exception e) {
                    extension = (post.getType().equals("audio")) ? "mp3" :
                            (post.getType().equals("image")) ? "jpg" :
                                    (post.getType().equals("video")) ? "mp4" : "file";
                }
                final String mediaName = Helper.generateRandomString(15, null) + "." + extension;
                final StorageReference mediaReference = firebaseStorage.getReference("post_media/data")
                        .child(mediaName);
                final UploadTask mediaUploadTask = mediaReference.putFile(Uri.parse(post.getMediaUri()));

                mediaUploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        long totalByteCount = taskSnapshot.getTotalByteCount();
                        long bytesTransferred = taskSnapshot.getBytesTransferred();
                        Object intVal = Math.floor((60 * bytesTransferred) / totalByteCount);
                        int progress = toInteger(intVal);
                        holder.uploadProgressBar.setProgress(progress);
                        int postIndex = getDataSet().indexOf(post);
                        if (postIndex < 0) mediaUploadTask.cancel();
                    }
                });

                mediaUploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            mediaReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if (task.isSuccessful()) {

                                        post.addProperty("mimeType", Helper.getMimeType(getActivity(), Uri.parse(post.getMediaUri())));

                                        post.setMediaUri(task.getResult().toString());

                                        if (post.hasProperty("thumbnailByteArray")) {
                                            uploadPostProperty(holder, post, "thumbnail", mediaName);
                                        } else {
                                            uploadPostProperty(holder, post, "upload");
                                        }
                                    } else {
                                        Helper.log(task.getException().getMessage());
                                    }
                                }
                            });
                        } else {
                            Helper.log(task.getException().getMessage());
                        }
                    }
                });
                break;
            case "thumbnail": //30
                final StorageReference thumbnailReference = firebaseStorage
                        .getReference("post_media/thumbnail").child(args[0].toString());
                final UploadTask thumbnailUploadTask = thumbnailReference.putBytes((byte[]) post.getProperty("thumbnailByteArray"));

                thumbnailUploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        long totalByteCount = taskSnapshot.getTotalByteCount();
                        long bytesTransferred = taskSnapshot.getBytesTransferred();
                        Object intVal = Math.floor((30 * bytesTransferred) / totalByteCount) + 60;
                        int progress = toInteger(intVal);
                        holder.uploadProgressBar.setProgress(progress);
                        int postIndex = getDataSet().indexOf(post);
                        if (postIndex < 0) thumbnailUploadTask.cancel();
                    }
                });

                thumbnailUploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            thumbnailReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if (task.isSuccessful()) {
                                        if (post.hasProperty("thumbnailByteArray"))
                                            post.getProperties().remove("thumbnailByteArray");

                                        post.addProperty("thumbnailUri", task.getResult().toString());
                                        uploadPostProperty(holder, post, "upload");
                                    } else {
                                        Helper.log(task.getException().getMessage());
                                    }
                                }
                            });
                        } else {
                            Helper.log(task.getException().getMessage());
                        }
                    }
                });
                break;
            case "upload": //10
                post.getProperties().remove("isNewPost");
                post.getProperties().remove("uploadProgress");
                post.addProperty("author", ((TechupApplication) getActivity().getApplicationContext()).getUser().getName());
                FirebaseFirestore.getInstance().collection("LearningPosts")
                        .add(post).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            post.addProperty("isNewPost", false);
                            post.addProperty("objectID", task.getResult().getId());
                            holder.uploadProgressBar.setProgress(100);
                            final int postIndex = getDataSet().indexOf(post);
                            if (postIndex != -1) {
                                holder.itemView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
                                        holder.uploadView.setVisibility(View.GONE);
                                        if (post.getType().equals(Post.TYPE_AUDIO) || post.getType().equals(Post.TYPE_VIDEO)){
                                            holder.useVideoOrAudio();
                                        }
                                    }
                                }, 1000);
                            } else actionDeletePost(post);
                            Helper.log("post upload successful");
                        } else {
                            Helper.log(task.getException().getMessage());
                        }
                    }
                });
                break;
        }

    }

    public interface OnPlayVideoAudioListener {
        void onPlay(Post post);
    }

    public static class LinkScrapper extends AsyncTask<String, Void, Map<String, String>> {

        private final TextView textView;
        private final ImageView imageView;

        public LinkScrapper(TextView textView, ImageView imageView){
            this.textView = textView;
            this.imageView = imageView;
        }

        @Override
        protected Map<String, String> doInBackground(String... strings) {

            String url = strings[0];

            try {
                Helper.log("getting overview for link: " + url);

                Document document = Jsoup.connect(url).get();

                String title = document.title() + "\n" + url,
                        imgUrl = "null";
                Element imgElement = document.select("img").first();
                if (imgElement != null)
                    imgUrl = imgElement.absUrl("src");

                Map<String, String> map = new HashMap<>();
                map.put("image", imgUrl);
                map.put("title", title.replace("  ", " "));

                return map;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Map<String, String> map) {
            if (map != null){
                if (textView != null && imageView != null) {
                    textView.setText(map.get("title"));
                    if (!map.get("image").equals("null")) {
                        Glide.with(getActivity().getApplicationContext()).load(map.get("image")).into(imageView);
                    }
                }
            }
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView type, timestamp, content;
        public final ImageView image;
        public final View playButton;
        public final View mediaContent;
        public final View linkView;
        public final ImageView linkImage;
        public final TextView linkContent;
        public final View uploadView;
        public final ImageButton uploadController;
        public final ProgressBar uploadProgressBar;
        private Post mItem;
        private int itemPosition;


        public ViewHolder(View view) {
            super(view);
            type = view.findViewById(R.id.type);
            timestamp = view.findViewById(R.id.timestamp);
            content = view.findViewById(R.id.content);
            playButton = view.findViewById(R.id.btn_play);
            image = view.findViewById(R.id.image);

            mediaContent = view.findViewById(R.id.media_content);
            linkView = view.findViewById(R.id.link_overview);
            linkImage = view.findViewById(R.id.link_photo);
            linkContent = view.findViewById(R.id.link_content);

            uploadView = view.findViewById(R.id.upload_view);
            uploadController = view.findViewById(R.id.upload_control);
            uploadProgressBar = view.findViewById(R.id.upload_progress);
        }

        public Post getItem() {
            return mItem;
        }

        public void setItem(Post item) {
            this.mItem = item;

            uploadView.setVisibility(View.GONE);
            mediaContent.setVisibility(View.GONE);
            linkView.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);

            initializeViews();
        }

        public int getItemPosition() {
            return itemPosition;
        }

        public void setItemPosition(int position) {
            this.itemPosition = position;
            this.setItem(getDataSet().get(position));
        }

        private void initializeViews() {

            type.setText(getType());
            timestamp.setText(getTimeAgo());

            useTextMessage();

            useLinkContent();

            useOpenPostViewOnClick();

            if (getItem().getType().equals(Post.TYPE_NO_MEDIA)) {
                useThumbnail();
            } else {
                switch (getItem().getType()) {
                    case Post.TYPE_VIDEO:
                    case Post.TYPE_AUDIO:
                        useThumbnail();
                        useVideoOrAudio();
                        break;
                    case Post.TYPE_IMAGE:
                        useThumbnail();
                        break;
                    case Post.TYPE_FILE:
                        useThumbnail();
                        useFile();
                        break;
                }

            }

            if (getItem().hasProperty("isNewPost") && (boolean) getItem().getProperty("isNewPost")) {

                if (!getItem().getType().equals(Post.TYPE_NO_MEDIA))
                    uploadView.setVisibility(View.VISIBLE);

                uploadController.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertTechupDialog techupDialog = new AlertTechupDialog(getActivity());
                        techupDialog.setTitleText("Cancel Upload");
                        techupDialog.setMessageText("Are you sure you want to cancel the upload process?");
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
                                int index = getDataSet().indexOf(getItem());
                                getDataSet().remove(index);
                                notifyItemRemoved(index);
                            }
                        });
                        techupDialog.dismiss();
                    }
                });

                Helper.log("starting upload process");
                //TODO: start upload process
                //save post files locally and upload

                tryUploadingPost();

            } else {
                Object isAdmin = ((TechupApplication) getActivity().getApplicationContext()).getUser().getInfo("is-admin");
                if (isAdmin != null && (boolean) isAdmin) {
                    useLongClickToDelete();
                }
            }
        }

        private void tryUploadingPost() {
            if (Helper.isNetworkAvailable(getActivity())) {
                startPostUploadProcess(this, getItemPosition());
                Helper.shortToast(getActivity(), "you have internet connection: " + Helper.isInternetConnectionAvailable(getActivity()));
                Helper.log("you have internet connection: " + Helper.isInternetConnectionAvailable(getActivity()));
            } else {

                final AlertTechupDialog techupDialog = new AlertTechupDialog(getActivity());
                techupDialog.setTitleText("No Internet");
                techupDialog.setMessageText("Ops... looks like you do not have internet connection to upload this post.");
                techupDialog.setPositiveButton("Try Again", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        techupDialog.dismiss();
                        tryUploadingPost();
                    }
                });
                techupDialog.setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        techupDialog.dismiss();
                        int index = getDataSet().indexOf(getItem());
                        getDataSet().remove(index);
                        notifyItemRemoved(index);
                    }
                });
                techupDialog.show();
            }
        }

        private String getType() {

            String type = "";

            if (!getItem().getType().equals(Post.TYPE_NO_MEDIA)) {
                type += String.valueOf(getItem().getType().charAt(0)).toUpperCase() +
                        getItem().getType().substring(1);
            } else {
                if (getItem().getLink() != null && !getItem().getMessage().isEmpty()) {
                    type = "Feed";
                } else if (getItem().getLink() != null) {
                    type = "Link";
                } else if (!getItem().getMessage().isEmpty()) {
                    type = "News";
                }
            }

            return type;
        }

        private String getTimeAgo() {
            Date date = getItem().getTimestamp();
            return new SimpleDateFormat("dd/MM/yyyy", Locale.US)
                    .format(date == null ? new Date() : getItem().getTimestamp());
        }

        private void useTextMessage() {
            if (!getItem().getMessage().isEmpty()) {
                content.setVisibility(View.VISIBLE);
                String message = getItem().getMessage().replace("\n", " ");
                if (message.length() > 200) {
                    message = message.substring(0, 200)
                            .replace("  ", " ") + " ...";
                }
                content.setText(message);
            }
        }

        private void useVideoOrAudio() {

            if (!(getItem().hasProperty("isNewPost") && (boolean) getItem().getProperty("isNewPost"))) {

                playButton.setVisibility(View.VISIBLE);
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Initialize a new instance of popup window
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.popup_video_audio_player);
                        dialog.show();
                        // Inflate the custom layout/view
                        final VideoView videoView = dialog.findViewById(R.id.video);
                        videoView.setVideoURI(Uri.parse(getItem().getMediaUri()));
                        videoView.setKeepScreenOn(true);
                        videoView.seekTo(1);
                        videoView.requestFocus();

                        int defaultDialogSize = toInteger(Math.floor(Helper.getDeviceSize(getActivity()).heightPixels * 0.4));

                        videoView.setLayoutParams(new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, defaultDialogSize));

                        if (getItem().getType().equals(Post.TYPE_AUDIO)) {

                            ImageView imageView = dialog.findViewById(R.id.image);
                            imageView.setVisibility(View.VISIBLE);
                            if (getItem().hasProperty("isNewPost") &&
                                    (boolean) getItem().getProperty("isNewPost") &&
                                    getItem().hasProperty("thumbnailByteArray")) {
                                //post to upload
                                Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(
                                        (byte[]) getItem().getProperty("thumbnailByteArray")));
                                imageView.setImageBitmap(bitmap);
                            } else if (getItem().hasProperty("thumbnailUri")) {
                                //downloaded post
                                Glide.with(itemView.getContext())
                                        .load(getItem().getProperty("thumbnailUri"))
                                        .into(imageView);
                            } else {
                                imageView.setBackgroundResource(R.drawable.bg_audio);
                            }
                        }

                        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);

                        final Variable<Boolean> isPlaying = new Variable<>(false);

                        final Runnable progressColorChangerRunnable = new Runnable() {

                            int i = 0;

                            @Override
                            public void run() {
                                if (i == colorList.length)
                                    i = 0;
                                progressBar.getIndeterminateDrawable().setColorFilter(colorList[i], PorterDuff.Mode.SRC_IN);
                                i++;

                                if (!isPlaying.getValue())
                                    progressBar.postDelayed(this, 1000);
                            }
                        };

                        progressBar.postDelayed(progressColorChangerRunnable, 1000);

                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {

                                if (getItem().getType().equals(Post.TYPE_VIDEO)) {
                                    videoView.setLayoutParams(new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.MATCH_PARENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT));
                                }

                                isPlaying.setValue(false);
                                progressBar.setVisibility(View.GONE);
                                videoView.start();
                            }
                        });

                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                dialog.findViewById(R.id.btn_play).setVisibility(View.VISIBLE);
                            }
                        });


                        dialog.findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                videoView.seekTo(1);
                                dialog.findViewById(R.id.btn_play).setVisibility(View.GONE);
                                videoView.start();
                            }
                        });

                        dialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                videoView.stopPlayback();
                                dialog.cancel();
                            }
                        });

                    }
                });
            }
        }

        private void useThumbnail() {

            mediaContent.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);

            if (getItem().hasProperty("isNewPost") &&
                    (boolean) getItem().getProperty("isNewPost") &&
                    getItem().hasProperty("thumbnailByteArray")) {
                //post to upload
                Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(
                        (byte[]) getItem().getProperty("thumbnailByteArray")));
                image.setImageBitmap(bitmap);
            } else if (getItem().hasProperty("thumbnailUri")) {
                //downloaded post
                Glide.with(itemView.getContext())
                        .load(getItem().getProperty("thumbnailUri"))
                        .into(image);
            } else {

                switch (getItem().getType()) {
                    case Post.TYPE_AUDIO:
                        image.setImageResource(R.drawable.bg_audio);
                        break;
                    case Post.TYPE_FILE:
                        image.setImageResource(R.drawable.bg_document);
                        break;
                    case Post.TYPE_IMAGE:
                        image.setImageResource(R.drawable.ic_image_white_24dp);
                        break;
                    default:
                        mediaContent.setVisibility(View.GONE);
                }
            }

        }

        private void useLinkContent() {
            if (getItem().getLink() != null) {
                linkView.setVisibility(View.VISIBLE);
                //TODO: get link photo | content
                linkView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(getItem().getLink()));
                        getActivity().startActivity(i);
                    }
                });

                linkContent.setText(getItem().getLink());
                new LinkScrapper(linkContent, linkImage).execute(getItem().getLink());

            } else {
                linkView.setVisibility(View.GONE);
            }
        }

        private void useOpenPostViewOnClick() {
            if ((getItem().hasProperty("thumbnailUri") || getItem().hasProperty("thumbnailUri")) ||
                    (getItem().getMessage() != null && getItem().getMessage().length() > 200)) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            mListener.onItemInteraction(getItem(), getItemPosition(), null);
                        }
                    }
                });
            }
        }

        private void useFile() {
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getSharableIntent(Uri.parse(getItem().getMediaUri()));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        try {
                            getActivity().startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Helper.shortToast(getActivity().getApplicationContext(), "failed to open file");
                        }
                    }
                }
            });
        }

        private void useLongClickToDelete() {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertTechupDialog techupDialog = new AlertTechupDialog(getActivity());
                    techupDialog.setTitleText("Delete Post");
                    techupDialog.setMessageText("Are you sure you want to delete the post");
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
                            tryDeletingPost();
                        }

                        private void tryDeletingPost() {
                            if (Helper.isNetworkAvailable(getActivity())) {
                                actionDeletePost(getItem());
                            } else {
                                final AlertTechupDialog techupDialog = new AlertTechupDialog(getActivity());
                                techupDialog.setTitleText("No Internet");
                                techupDialog.setMessageText("Ops... looks like you do not have internet connection to complete this action.");
                                techupDialog.setPositiveButton("Try Again", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        techupDialog.dismiss();
                                        tryDeletingPost();
                                    }
                                });
                                techupDialog.setNegativeButton("Cancel", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        techupDialog.dismiss();
                                    }
                                });
                                techupDialog.show();
                            }
                        }
                    });
                    techupDialog.show();
                    return false;
                }
            });
        }

        public Intent getSharableIntent(Uri uri) {

            Uri contentUri = uri;
            if (uri.toString().contains("file://"))
                contentUri = getSharableUriFromUri(uri);

            Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                    .setStream(contentUri) // uri from FileProvider
                    .getIntent()
                    .setAction(Intent.ACTION_VIEW); //Change if needed

            if (contentUri.toString().contains(".doc") || contentUri.toString().contains(".docx")) {
                intent.setDataAndType(contentUri, "application/msword");
            } else if (contentUri.toString().contains(".pdf")) {
                intent.setDataAndType(contentUri, "application/pdf");
            } else if (contentUri.toString().contains(".ppt") || contentUri.toString().contains(".pptx")) {
                intent.setDataAndType(contentUri, "application/vnd.ms-powerpoint");
            } else if (contentUri.toString().contains(".xls") || contentUri.toString().contains(".xlsx")) {
                intent.setDataAndType(contentUri, "application/vnd.ms-excel");
            } else if (contentUri.toString().contains(".zip") || contentUri.toString().contains(".rar")) {
                intent.setDataAndType(contentUri, "application/zip");
            } else if (contentUri.toString().contains(".rtf")) {
                intent.setDataAndType(contentUri, "application/rtf");
            } else if (contentUri.toString().contains(".wav") || contentUri.toString().contains(".mp3")) {
                intent.setDataAndType(contentUri, "audio/x-wav");
            } else if (contentUri.toString().contains(".gif")) {
                intent.setDataAndType(contentUri, "image/gif");
            } else if (contentUri.toString().contains(".jpg") || contentUri.toString().contains(".jpeg") || contentUri.toString().contains(".png")) {
                intent.setDataAndType(contentUri, "image/jpeg");
            } else if (contentUri.toString().contains(".txt")) {
                intent.setDataAndType(contentUri, "text/plain");
            } else if (contentUri.toString().contains(".3gp") || contentUri.toString().contains(".mpg") || contentUri.toString().contains(".mpeg") || contentUri.toString().contains(".mpe") || contentUri.toString().contains(".mp4") || contentUri.toString().contains(".avi")) {
                intent.setDataAndType(contentUri, "video/*");
            } else {
                intent.setDataAndType(contentUri, "*/*");
            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return intent;
        }

        private Uri getSharableUriFromUri(Uri uri) {
            String path = Helper.getPathFromUri(getActivity(), uri);
            if (path == null) path = uri.getPath();
            return FileProvider.getUriForFile(getActivity().getApplicationContext(),
                    getActivity().getPackageName() + ".provider", new File(path));
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + "<" + getItem() + ">";
        }

    }
}
