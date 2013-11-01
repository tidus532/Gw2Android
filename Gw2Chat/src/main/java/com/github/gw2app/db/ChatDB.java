package com.github.gw2app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tidus on 9/10/13.
 */
public class ChatDB {
    private Gw2DB mDBHelper;
    private Map<String, String> mChatNames;
    private final Object mChatNamesLock = new Object();

    public ChatDB(Context context) {
        mDBHelper = Gw2DB.getInstance(context);
        mChatNames = new HashMap<String, String>();
    }

    public void getChatHistory(String user_id) {

    }

    public void getChatHistory(String user_id, int limit) {
        final String sql = " SELECT * FROM chat_history WHERE user_id = " + user_id + " LIMIT " + limit;
    }

    public void addMessage(String message, String user_id) {
        ContentValues values = new ContentValues();
        values.put(Gw2DB.COLUMN_CHAT_USER_ID, user_id);
        values.put(Gw2DB.COLUMN_CHAT_MESSAGE, message);
        mDBHelper.getWritableDatabase().insert(Gw2DB.TABLE_CHAT_HISTORY, null, values);
    }

    /**
     * Returns a list containing the contact info.
     *
     * @return A list containing the contact info. An empty list is returned if there are no contacts in the DB.
     */
    public AbstractList<Contact> getContacts() {
        //Fetch all contact info from DB with the last message sent (if available).
        final String sql = "  SELECT " + Gw2DB.COLUMN_CHAT_ID + ", " + Gw2DB.COLUMN_CHAT_USER_ID + ", " + Gw2DB.COLUMN_CHAT_TIMESTAMP + ", " + Gw2DB.COLUMN_CHAT_MESSAGE +
                "       FROM " + Gw2DB.TABLE_CHAT_HISTORY +
                "       LEFT OUTER JOIN (" +
                "           SELECT max(" + Gw2DB.COLUMN_CHAT_ID + ") as id, " + Gw2DB.COLUMN_CHAT_USER_ID + " as user" +
                "           from " + Gw2DB.TABLE_CHAT_HISTORY +
                "           group by " + Gw2DB.COLUMN_CHAT_USER_ID +
                "           ) as max on max.user = " + Gw2DB.TABLE_CHAT_HISTORY + "." + Gw2DB.COLUMN_CHAT_USER_ID + " and max.id = " + Gw2DB.TABLE_CHAT_HISTORY + "." + Gw2DB.COLUMN_CHAT_ID;
        Cursor cursor = mDBHelper.getReadableDatabase().rawQuery(sql, null);
        Map<String, String> names = getContactNames();

        //Retrieve result
        ArrayList<Contact> result = new ArrayList<Contact>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String message = cursor.getString(cursor.getColumnIndex(Gw2DB.COLUMN_CHAT_MESSAGE));
                    String timestamp = cursor.getString(cursor.getColumnIndex(Gw2DB.COLUMN_CHAT_TIMESTAMP));
                    String user_id = cursor.getString(cursor.getColumnIndex(Gw2DB.COLUMN_CHAT_USER_ID));

                    Message msg = new Message(message, user_id, timestamp);
                    Contact contact = new Contact(user_id, names.get(user_id), message);
                    result.add(contact);
                    Log.d("Gw2", "getContacts result : " + message + " " + timestamp + " " + user_id);

                } while (cursor.moveToNext());
                cursor.close();
            }
        }

        return result;
    }

    /**
     * Adds a chat name to the datbase.
     * NOTE: This method is threadsafe.
     *
     * @param userID
     * @param chatName
     */
    public void addContact(String userID, String chatName) {
        Log.d("Gw2", "ADDING CHAT NAME MQSOKN");
        ContentValues values = new ContentValues();
        values.put(Gw2DB.COLUMN_CHAT_NAME, chatName);
        values.put(Gw2DB.COLUMN_CHAT_USER_ID, userID);
        String sql = "INSERT OR IGNORE INTO " + Gw2DB.TABLE_CHAT_CONTACTS + " (" + Gw2DB.COLUMN_CHAT_USER_ID + ", " + Gw2DB.COLUMN_CHAT_NAME + ") VALUES (" + DatabaseUtils.sqlEscapeString(userID) + ", " + DatabaseUtils.sqlEscapeString(chatName) + ")";
        Log.d("Gw2", "THIS IS THE FUCKING SQL SHIT " + sql);
        mDBHelper.getWritableDatabase().execSQL(sql);
        synchronized (mChatNamesLock) {
            mChatNames.put(userID, chatName);
        }
    }

    /**
     * Gets a map containing the user id as the key with the corresponding user name.
     *
     * @return
     */
    public Map<String, String> getContactNames() {
        synchronized (mChatNamesLock) {

            Cursor cursor = mDBHelper.getReadableDatabase().query(Gw2DB.TABLE_CHAT_CONTACTS, new String[]{Gw2DB.COLUMN_CHAT_USER_ID, Gw2DB.COLUMN_CHAT_NAME}, null, null, null, null, null);
            mChatNames = new HashMap<String, String>();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String userID = cursor.getString(0);
                        String chatName = cursor.getString(1);
                        mChatNames.put(userID, chatName);
                    } while (cursor.moveToNext());
                }
            }

            return mChatNames;
        }
    }

    /**
     * Removes all data from the database.
     */
    public void cleanDatabase() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.delete(Gw2DB.TABLE_CHAT_HISTORY, null, null);
        db.delete(Gw2DB.TABLE_CHAT_CONTACTS, null, null);
    }
}
