package com.gladys.cybuverse.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;


public class LocalDatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    public LocalDatabaseHelper(Context context) {
        super(context, "CybuverseDB.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user(id integer primary key autoincrement, userid integer, " +
                "firstname text, lastname text, email text unique, phone text, " +
                "username text unique,  password text, gender text, about text, " +
                "confirmed_email text, confirmed_phone text, verified_status text, user_status text, " +
                "profilePicUri text, dateofbirth text);");

        db.execSQL("CREATE TABLE stories(id integer primary key autoincrement, userid integer, " +
                "story_content text, timestamp text, privacy text, edited text);");

        db.execSQL("CREATE TABLE chats(id integer primary key autoincrement, friend integer, " +
                "friend_name text, profile_pic text, last_text text, last_text_seen text);");

        db.execSQL("CREATE TABLE friends(id integer primary key autoincrement, firendid integer, " +
                "friend_name text, about text, profilePicUri text);");

        db.execSQL("CREATE TABLE notification(id integer primary key autoincrement," +
                "notice_type text, title text, icon_uri text, source_from text, receiver text," +
                " content text, timestamp text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS user");
    }

    public boolean exist_email(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email=?;", new String[]{email});
        com.gladys.cybuverse.Helpers.Helper.log("checking email : " + email, "cursor count = " + cursor.getCount());
        return cursor.getCount() > 0;
    }

    public boolean exist_username(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username=?;", new String[]{username});
        com.gladys.cybuverse.Helpers.Helper.log("checking username : " + username, "cursor count = " + cursor.getCount());
        return cursor.getCount() > 0;
    }

    public boolean checkUser(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username=?", new String[]{username});
        com.gladys.cybuverse.Helpers.Helper.log("logging in with username : |" + username + "|");
        Helper.log("cursor count = " + cursor.getCount());
        return cursor.getCount() > 0;
    }

    public boolean setCurrentUser(JSONObject currentUser) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            values.put("userid", currentUser.getInt("user_id"));
            values.put("firstname", currentUser.getString("firstname"));
            values.put("lastname", currentUser.getString("lastname"));
            values.put("email", currentUser.getString("email"));
            values.put("phone", currentUser.getString("phone"));
            values.put("username", currentUser.getString("username"));
            values.put("password", currentUser.getString("password"));
            values.put("gender", currentUser.getString("gender"));
            values.put("about", currentUser.getString("about"));
            values.put("dateofbirth", currentUser.getString("dateofbirth"));
            values.put("confirmed_email", currentUser.getString("confirmed_email"));
            values.put("confirmed_phone", currentUser.getString("confirmed_phone"));
            values.put("verified_status", currentUser.getString("status"));
            values.put("user_status", currentUser.getString("user_status"));
            values.put("profilePicUri", currentUser.getString("profile_pic"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        long checker = db.insert("user", null, values);

        return checker != -1;
    }

}
