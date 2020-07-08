package com.gladys.cybuverse.Helpers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;
import java.util.regex.Pattern;

public class Helper {

    private Helper() {
    }

    ;

    public static String getString(EditText field) {
        return field.getText().toString().trim();
    }

    public static boolean isEmpty(String... words) {
        for (String word : words) {
            if (TextUtils.isEmpty(word)) {
                return true;
            }
        }
        return false;
    }

    public static void shortToast(Context context, String message, String... position) {
        String location = "";
        if (position.length > 0) {
            location = position[0];
        }
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        switch (location) {
            case "center":
                toast.setGravity(Gravity.CENTER, 0, 0);
                break;
            case "top":
                toast.setGravity(Gravity.TOP, 0, 0);
                break;
            case "left":
                toast.setGravity(Gravity.START, 0, 0);
                break;
            case "right":
                toast.setGravity(Gravity.END, 0, 0);
                break;
            case "bottom":
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                break;
            default:
                break;
        }
        toast.show();
    }

    public static void longToast(Context context, String message, String... position) {
        String location = "";
        if (position.length > 0) {
            location = position[0];
        }
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        switch (location) {
            case "center":
                toast.setGravity(Gravity.CENTER, 0, 0);
                break;
            case "top":
                toast.setGravity(Gravity.TOP, 0, 0);
                break;
            case "left":
                toast.setGravity(Gravity.LEFT, 0, 0);
                break;
            case "right":
                toast.setGravity(Gravity.RIGHT, 0, 0);
                break;
            case "bottom":
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                break;
            default:
                break;
        }
        toast.show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.d("NetworkCheck", "isNetworkAvailable: No");
            return false;
        }

        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean isInternetConnectionAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            Log.d("NetworkCheck", "isNetworkAvailable: No");
            return false;
        }

        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean isValidEmailId(String email) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static void log(String... messages) {
        for (String message : messages) {
            Log.d("TechupStudio.Cybuverse", message);
        }
    }

    public static DisplayMetrics getDeviceSize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static String getPathFromUri(Activity activity, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public static String getMimeType(Activity activity, Uri uri) {
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT))
            return activity.getContentResolver().getType(uri);
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        if (extension != null)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        return null;
    }

    public static Bitmap getThumbnailFromVideoAsBitmap(Activity activity, Uri uri) {
        String[] fpc = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, fpc, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(fpc[0]);
        String path = cursor.getString(columnIndex);
        cursor.close();
        return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
    }

    public static Bitmap getThumbnailFromImageAsBitmap(Activity activity, Uri uri, int width, int height) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            return ThumbnailUtils.extractThumbnail(bitmap, width, height);
        } catch (IOException e) {
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(uri.getPath()), width, height);
        }
    }


    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            stringBuilder.append((char) (random.nextInt(96) + 32));
        }

        return stringBuilder.toString().replace(" ", "_");
    }

    public static String generateRandomString(int length, char[] acceptableChars) {

        if (acceptableChars == null) {
            String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            acceptableChars = (alphabet + alphabet.toLowerCase() + "1234567890" + "_$").toCharArray();
        }

        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            stringBuilder.append(acceptableChars[(random.nextInt(acceptableChars.length))]);
        }

        return stringBuilder.toString();
    }
}
