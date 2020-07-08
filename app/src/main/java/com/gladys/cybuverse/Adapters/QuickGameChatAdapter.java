package com.gladys.cybuverse.Adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ChatBot;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Models.ChatMessage;
import com.gladys.cybuverse.Models.User;
import com.gladys.cybuverse.R;
import com.gladys.cybuverse.Utils.FileUtils.FileExplorer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.gladys.cybuverse.Fragments.MediaInputManagerFragment.TYPE_VOICE;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ChatMessage} and makes a call to the
 * specified {@link OnListItemInteractionListener <ChatMessage>}.
 * TODO: Replace the implementation with code for your data info.
 */
public class QuickGameChatAdapter extends FirestoreRecyclerAdapter<ChatMessage, QuickGameChatAdapter.ViewHolder> {


    private final int INFO = 0;
    private final int SEND = 1;
    private final int RECEIVE = 2;
    private final int SEND_REPLY = 3;
    private final int RECEIVE_REPLY = 4;
    private final int SEND_MEDIA = 5;
    private final int RECEIVE_MEDIA = 6;
    private final OnListItemInteractionListener<ChatMessage> mListener;


    private User mUser, mFriend;
    private MediaPlayer mediaPlayer;
    private DisplayMetrics deviceSize;
    private Drawable userProfileImageDrawable;
    private Drawable friendProfileImageDrawable;

    private String lastAudioUri;
    private String searchString;

    public QuickGameChatAdapter(Query query, OnListItemInteractionListener<ChatMessage> listener) {
        super(new FirestoreRecyclerOptions.Builder<ChatMessage>().setQuery(query, ChatMessage.class).build());
        this.mListener = listener;
    }

    public static String getFileNameFromUrl(String uri) {
        String arr[] = uri.split("alt=media&token")[0].split("%2F");
        uri = arr[arr.length - 1];
        Helper.log("name: " + uri);
        return uri.substring(0, uri.length() - 1);
    }

    public static File getLocalFile(ChatMessage message) {

        String type = message.getMediaType();
        String fileName = null;

        if (message.getProperty("mediaName") == null) {
            if (message.getMediaUriList().size() > 0) {
                fileName = getFileNameFromUrl(message.getMediaUriList().get(0));
            }
        } else fileName = (String) message.getProperty("mediaName");

        if (type != null && (fileName != null && !fileName.isEmpty())) {

            String saveFolder = (type.equals("file")) ? "Documents" :
                    (type.equals("image") || type.equals("camera")) ? "Images" :
                            (type.equals("audio")) ? "Audio" :
                                    (type.equals("video")) ? "Videos" : "VoiceNotes";

            return new FileExplorer(Environment.getExternalStorageDirectory())
                    .createNewFolder("Cybuverse").openFolder("Cybuverse")
                    .createNewFolder("Media").openFolder("Media")
                    .createNewFolder(saveFolder).openFolder(saveFolder)
                    .getFile(fileName);

        }
        return null;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

    public void setFriend(User friend) {
        this.mFriend = friend;
    }

    private void setProfileImage(final ImageView imageView, User user) {

        if (user == mUser) {
            if (userProfileImageDrawable == null) {
                if (user != null && !user.getProfileUri().equals("default")) {
                    String uri = user.getProfileUri();
                    if (uri != null && !uri.trim().isEmpty()) {
                        Glide.with(imageView.getContext())
                                .load(uri)
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        userProfileImageDrawable = resource;
                                        imageView.setImageDrawable(resource);
                                    }
                                });
                    }
                }
            } else {
                imageView.setImageDrawable(userProfileImageDrawable);
            }
        } else {
            if (friendProfileImageDrawable == null) {
                if (user != null && !user.getProfileUri().equals("default")) {
                    String uri = user.getProfileUri();
                    if (uri != null && !uri.trim().isEmpty()) {
                        Glide.with(imageView.getContext())
                                .load(uri)
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        friendProfileImageDrawable = resource;
                                        imageView.setImageDrawable(resource);
                                    }
                                });
                    }
                }
            } else {
                imageView.setImageDrawable(friendProfileImageDrawable);
            }
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case SEND:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_send, parent, false);
                break;
            case SEND_REPLY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_reply_send, parent, false);
                break;
            case SEND_MEDIA:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_media_send, parent, false);
                break;
            case RECEIVE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_receive, parent, false);
                break;
            case RECEIVE_REPLY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_reply_receive, parent, false);
                break;
            case RECEIVE_MEDIA:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_media_receive, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_in_chat_info, parent, false);
                break;
        }
        ViewHolder holder = new ViewHolder(view);
        holder.setItemViewType(viewType);
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, final int i, @NonNull final ChatMessage chatMessage) {
        holder.mItem = chatMessage;

        int width = (int) Math.floor(this.deviceSize.widthPixels *
                ((this.deviceSize.widthPixels > 1000) ? 0.70 : 0.65));
        if (width > 700) width = 700;

        switch (holder.getViewType()) {
            case SEND:
            case SEND_REPLY:
            case SEND_MEDIA:
                setProfileImage(holder.mImageView, mUser);
                break;
            case RECEIVE:
            case RECEIVE_REPLY:
            case RECEIVE_MEDIA:
                setProfileImage(holder.mImageView, mFriend);
                break;
            case INFO:
                break;
        }

        Helper.log("width: " + this.deviceSize.widthPixels + " calculated: " + width + " viewType: " + holder.getViewType());

        if (deviceSize != null)
            holder.mContentView.setMaxWidth(width);

        if (searchString != null && !searchString.isEmpty()) {
            String message = holder.mItem.getContent();

            SpannableString spannableString = new SpannableString(message);

            String tempMessage = message.toLowerCase();

            int index = tempMessage.indexOf(searchString.toLowerCase(), 0);

            while (index != -1) {
                BackgroundColorSpan colorSpan = new BackgroundColorSpan(holder.itemView.getResources().getColor(R.color.colorFacebook));
                spannableString.setSpan(colorSpan, index, index + searchString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                index = tempMessage.indexOf(searchString.toLowerCase(), index + searchString.length());
            }

            holder.mContentView.setText(spannableString);
        } else {
            holder.mContentView.setText(chatMessage.getContent());
        }

        if (chatMessage.hasProperty("replyTo")) {
            holder.itemView.findViewById(R.id.message).setVisibility(View.VISIBLE);
            useReply(holder, chatMessage);
        }

        if (holder.getViewType() == SEND_MEDIA || holder.getViewType() == RECEIVE_MEDIA) {
            holder.itemView.findViewById(R.id.media_holder).setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.audio_holder).setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.content).setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.message).setVisibility(View.GONE);

            holder.itemView.findViewById(R.id.main_holder).setLayoutParams(
                    new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT)
            );

            holder.itemView.findViewById(R.id.image_holder).setLayoutParams(
                    new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    ));

            Helper.log("mediaContent:'" + chatMessage.getContent() + "' " + chatMessage.getProperty("mediaName"));

            holder.mContentView.setVisibility(View.VISIBLE);

            if (chatMessage.getContent().isEmpty() && !chatMessage.getMediaType().equals(TYPE_VOICE)) {
                if (chatMessage.getProperty("mediaName") != null)
                    holder.mContentView.setText(chatMessage.getProperty("mediaName").toString());
            }

            switch (chatMessage.getMediaType()) {
                case "audio":
                case "voice":
                    setupViewHolderForAudio(holder, chatMessage);
                    break;
                case "image":
                    setupViewHolderForImage(holder, chatMessage);
                    break;
                case "video":
                    setupViewHolderForVideo(holder, chatMessage);
                    break;
                case "file":
                    setupViewHolderForDocument(holder, chatMessage);
                    break;
            }
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onItemInteraction(chatMessage, i, "click");
                }
            }
        });


        if (mListener != null)
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int index = getSnapshots().indexOf(chatMessage);
                    String id = getSnapshots().getSnapshot(index).getId();
                    chatMessage.addProperty("objectID", id);
                    mListener.onItemInteraction(chatMessage, i, "long-click");
                    return false;
                }
            });
    }

    private void useReply(@NonNull ViewHolder holder, @NonNull ChatMessage chatMessage) {
        final Map<String, Object> repliedMessageMap = (HashMap) chatMessage.getProperty("replyTo");
        final ChatMessage replyMessage = new ChatMessage();
        replyMessage.setContent(repliedMessageMap.get("content") != null ?
                (String) repliedMessageMap.get("content") : null);
        replyMessage.setSender((String) repliedMessageMap.get("sender"));
        replyMessage.setMediaType((String) repliedMessageMap.get("mediaType"));
        replyMessage.setMediaUriList(repliedMessageMap.get("mediaUriList") != null ?
                (List<String>) repliedMessageMap.get("mediaUriList") : null);
        replyMessage.setProperties((Map<String, Object>) repliedMessageMap.get("properties"));

        if (deviceSize != null && chatMessage.getMediaType() == null) {
            int width = (int) Math.floor(this.deviceSize.widthPixels * ((this.deviceSize.widthPixels > 1000) ? 0.70 : 0.65));
            if (width > 600) width = 600;
            ((TextView) holder.itemView.findViewById(R.id.message)).setMaxWidth(width);
            holder.mContentView.setMaxWidth(width);
        }

        String content = replyMessage.getContent();
        if (content.isEmpty()) {
            if (replyMessage.getProperty("mediaName") != null)
                content = chatMessage.getMediaType() + ":" + replyMessage.getProperty("mediaName").toString();
            else
                content = chatMessage.getMediaType() + chatMessage.getMediaUriList().get(0);
        }
        ((TextView) holder.itemView.findViewById(R.id.message)).setText(content);

        holder.itemView.findViewById(R.id.message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: show dialog of message
            }
        });
    }

    private void setupViewHolderForDocument(final ViewHolder holder, final ChatMessage chatMessage) {
        holder.itemView.findViewById(R.id.media_holder).setVisibility(View.
                VISIBLE);

        ((ImageView) holder.itemView.findViewById(R.id.image_holder)).setImageResource(R.drawable.bg_document);

        holder.itemView.findViewById(R.id.image_holder)
                .setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, 210
                ));

        final File file = getLocalFile(chatMessage);

        if (!file.exists()) {
            holder.itemView.findViewById(R.id.media_controller).setVisibility(View.VISIBLE);
            ((ImageView) holder.itemView.findViewById(R.id.media_controller)).setImageResource(R.drawable.ic_file_download_blue_24dp);
            holder.itemView.findViewById(R.id.media_controller).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.itemView.findViewById(R.id.media_controller).setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.progressBarMain).setVisibility(View.VISIBLE);
                    MediaContentDownloader downloader = new MediaContentDownloader(new MediaContentDownloader.ProgressListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onProgress(Integer integer) {

                        }

                        @Override
                        public void onCompleter(Boolean result) {
                            holder.itemView.findViewById(R.id.progressBarMain).setVisibility(View.GONE);
                            holder.itemView.findViewById(R.id.media_controller).setVisibility(View.VISIBLE);
                            ((ImageView) holder.itemView.findViewById(R.id.media_controller))
                                    .setImageResource(R.drawable.ic_thumb_up_gold_24dp);
                            holder.itemView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    holder.itemView.findViewById(R.id.media_controller).setVisibility(View.GONE);
                                }
                            }, 2000);
                        }
                    });
                    downloader.execute(chatMessage);
                }
            });
        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getSharableIntent(Uri.fromFile(file));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (intent.resolveActivity(holder.itemView.getContext().getPackageManager()) != null) {
                        try {
                            holder.itemView.getContext().startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Helper.shortToast(holder.itemView.getContext(), "failed to open file");
                        }
                    }
                }
            });
        }

    }

    private void setupViewHolderForVideo(final ViewHolder holder, final ChatMessage chatMessage) {
        holder.itemView.findViewById(R.id.media_holder).setVisibility(View.VISIBLE);
        Glide.with(holder.itemView.getContext())
                .load(Uri.parse(chatMessage.getProperty("thumbnailUri").toString()))
                .into((ImageView) holder.itemView.findViewById(R.id.image_holder));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemInteraction(chatMessage, getSnapshots().indexOf(chatMessage), "click");
                }
            }
        });
    }

    private void setupViewHolderForImage(ViewHolder holder, ChatMessage chatMessage) {
        holder.itemView.findViewById(R.id.media_holder).setVisibility(View.VISIBLE);

        Uri mediaUri = Uri.parse(chatMessage.getMediaUriList().get(0));
        File file = getLocalFile(chatMessage);
        if (file != null && file.exists()) {
            mediaUri = Uri.fromFile(file);
        }

        Glide.with(holder.itemView.getContext())
                .load(mediaUri)
                .into((ImageView) holder.itemView.findViewById(R.id.image_holder));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.log("show image");
            }
        });
    }

    private void setupViewHolderForAudio(final ViewHolder holder, final ChatMessage chatMessage) {
        holder.itemView.findViewById(R.id.audio_holder).setVisibility(View.VISIBLE);

        final ImageView controller = holder.itemView.findViewById(R.id.audio_controller);

        if (chatMessage.getMediaType().equals("audio")) {
            ((ImageView) holder.itemView.findViewById(R.id.audio_type))
                    .setImageResource(R.drawable.ic_audiotrack_24dp);
        } else {
            ((ImageView) holder.itemView.findViewById(R.id.audio_type))
                    .setImageResource(R.drawable.ic_mic_24dp);
        }

        if (chatMessage.getContent().isEmpty())
            holder.mContentView.setVisibility(View.GONE);


        final File localFile = getLocalFile(chatMessage);

        if (localFile == null || !localFile.exists()) {
            controller.setImageResource(R.drawable.ic_file_download_white_24dp);
        } else {
            controller.setImageResource(R.drawable.ic_play_arrow);
        }

        controller.setOnClickListener(new View.OnClickListener() {
            MediaContentDownloader mediaContentDownloader;

            @Override
            public void onClick(final View v) {

                Helper.log("local File: " + localFile + " exists: " + (localFile != null && localFile.exists()) + " downloader:" + mediaContentDownloader + " prgress: " + (mediaContentDownloader != null ? mediaContentDownloader.getProgress() : "null"));

                if ((mediaContentDownloader != null && mediaContentDownloader.getProgress() != 100) || localFile == null || !localFile.exists()) {
                    if (mediaContentDownloader == null) {
                        if (Helper.isNetworkAvailable(controller.getContext())) {
                            mediaContentDownloader = new MediaContentDownloader(new MediaContentDownloader.ProgressListener() {
                                @Override
                                public void onStart() {
                                    holder.itemView.findViewById(R.id.audio_controller).setVisibility(View.GONE);
                                    holder.itemView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onProgress(Integer integer) {
                                    ProgressBar progressBar = holder.itemView.findViewById(R.id.progressBar);
                                    if (progressBar.isIndeterminate())
                                        progressBar.setIndeterminate(false);
                                    progressBar.setProgress(integer);
                                }

                                @Override
                                public void onCompleter(Boolean result) {
                                    if (!result) {
                                        holder.itemView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                                        holder.itemView.findViewById(R.id.audio_controller).setVisibility(View.VISIBLE);
                                        ((ImageView) holder.itemView.findViewById(R.id.audio_controller))
                                                .setImageResource(R.drawable.ic_file_download_white_24dp);
                                    } else {
                                        if (mediaContentDownloader.getProgress() == 100) {
                                            holder.itemView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                                            holder.itemView.findViewById(R.id.audio_controller).setVisibility(View.VISIBLE);
                                            ((ImageView) holder.itemView.findViewById(R.id.audio_controller))
                                                    .setImageResource(R.drawable.ic_play_arrow);
                                        } else {
                                            Helper.shortToast(holder.itemView.getContext(), "download did not complete!");
                                        }
                                    }
                                }
                            });
                            mediaContentDownloader.execute(chatMessage);
                        } else {
                            Helper.shortToast(controller.getContext(), "you do not have and internet connection to download this file!.");
                        }
                    } else if (mediaContentDownloader.getProgress() == -1) {
                        mediaContentDownloader = null;
                        onClick(v);
                    }
                } else {
                    if (localFile.exists()) {
                        controller.setImageResource(R.drawable.ic_play_arrow);
                        playAudio(holder, chatMessage);
                    }
                }
            }

        });

    }

    private void playAudio(final ViewHolder holder, ChatMessage chatMessage) {

        final SeekBar seekBar = holder.itemView.findViewById(R.id.audio_seek);
        final ImageView controller = holder.itemView.findViewById(R.id.audio_controller);

        final String audioUri = chatMessage.getMediaUriList().get(0);

        if (lastAudioUri == null || !lastAudioUri.equals(audioUri)) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                mediaPlayer = null;
            }
            lastAudioUri = audioUri;
        }


        if (mediaPlayer == null) {

            mediaPlayer = new MediaPlayer();

            controller.setImageResource(R.drawable.ic_pause);

            try {
                Uri mediaUri = Uri.parse(audioUri);
                File file = getLocalFile(chatMessage);
                if (file != null && file.exists()) {
                    mediaUri = Uri.fromFile(file);
                }

                mediaPlayer.setDataSource(holder.itemView.getContext(), mediaUri);
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mp) {

                        seekBar.setMax(mp.getDuration());

                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if (fromUser && mediaPlayer != null && audioUri.equals(lastAudioUri))
                                    mediaPlayer.seekTo(progress);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });

                        mediaPlayer.start();
                        mediaPlayer.seekTo(seekBar.getProgress());
                        controller.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                    seekBar.setProgress(mp.getCurrentPosition());
                                    controller.postDelayed(this, 50);
                                }
                            }
                        }, 50);


                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        seekBar.setProgress(0);
                        controller.setImageResource(R.drawable.ic_play_arrow);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                mediaPlayer = null;
                Helper.shortToast(holder.itemView.getContext(),
                        "cannot play this audio... please check your internet connection!");
            }
        } else {
            if (mediaPlayer.isPlaying()) {
                controller.setImageResource(R.drawable.ic_play_arrow);
                mediaPlayer.pause();
            } else {
                controller.setImageResource(R.drawable.ic_pause);
                mediaPlayer.start();
                mediaPlayer.seekTo(seekBar.getProgress());
                controller.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            controller.postDelayed(this, 50);
                        }
                    }
                }, 50);
            }
        }
    }

    private Intent getSharableIntent(Uri uri) {

        Uri contentUri = uri;
//        if (uri.toString().contains("file://"))
//            contentUri = getSharableUriFromUri(uri);

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_STREAM, contentUri); // uri from FileProvider
        intent.setAction(Intent.ACTION_VIEW); //Change if needed

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

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItem(position);
        if (message.getSender().equals(ChatBot.asChat().getName())) {
            return INFO;
        }
        if (message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            if (message.getMediaType() != null)
                return SEND_MEDIA;
            if (message.hasProperty("replyTo"))
                return SEND_REPLY;
            return SEND;
        }
        if (message.getMediaType() != null)
            return RECEIVE_MEDIA;
        if (message.hasProperty("replyTo"))
            return RECEIVE_REPLY;
        return RECEIVE;
    }

    public void setDisplaySize(DisplayMetrics deviceSize) {
        this.deviceSize = deviceSize;
    }

    public void openSearch(String searchString) {
        if (!searchString.equals(this.searchString)) {
            this.searchString = searchString;
            notifyDataSetChanged();
        }
    }

    public static class MediaContentDownloader extends AsyncTask<ChatMessage, Integer, Boolean> {

        private ProgressListener progressListener;
        private int progress;

        MediaContentDownloader() {
        }

        MediaContentDownloader(ProgressListener listener) {
            this.progressListener = listener;
        }

        @Override
        protected void onPreExecute() {
            progress = 0;
            if (progressListener != null)
                progressListener.onStart();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(ChatMessage... chatMessages) {
            ChatMessage chatMessage = chatMessages[0];
            File localFile = getLocalFile(chatMessage);

            Helper.log("local File: " + localFile + " exists: " + (localFile != null && localFile.exists()));

            if (localFile != null && !localFile.exists()) {
                try {
                    URL url = new URL(chatMessage.getMediaUriList().get(0));
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    int totalByteCount = connection.getContentLength();

                    InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
                    FileOutputStream fileOutputStream = new FileOutputStream(localFile);

                    int len;
                    int bytesTransferred = 0;
                    byte[] buffer = new byte[1024];
                    while ((len = inputStream.read(buffer)) > 0) {
                        bytesTransferred += len;
                        fileOutputStream.write(buffer, 0, len);
                        publishProgress((int) Math.floor(bytesTransferred * 100 / totalByteCount));
                    }
                    inputStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Helper.log("download failed: " + e.getMessage());
                }
            } else {
                Helper.log("download failed: null file");
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            setProgress(values[0]);
            if (progressListener != null)
                progressListener.onProgress(values[0]);
            Helper.log("progress: " + values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean)
                setProgress(-1);
            if (progressListener != null)
                progressListener.onCompleter(aBoolean);
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public interface ProgressListener {
            void onStart();

            void onProgress(Integer integer);

            void onCompleter(Boolean result);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final ImageView mImageView;
        public ChatMessage mItem;
        private int itemViewType;

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

        public void setItemViewType(int itemViewType) {
            this.itemViewType = itemViewType;
        }

        public int getViewType() {
            return itemViewType;
        }
    }
}
