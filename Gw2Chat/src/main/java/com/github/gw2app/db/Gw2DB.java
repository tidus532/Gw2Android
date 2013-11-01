/*      This file is part of Gw2Android.

        Gw2Android is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        Gw2Android is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with Gw2Android.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.github.gw2app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tidus on 26/05/13.
 */
public class Gw2DB extends SQLiteOpenHelper {

    //Tables.
    public static final String WORLD_NAMES_TABLE = "world_names";
    public static final String MAP_NAMES_TABLE = "map_names";
    public static final String EVENT_NAMES_TABLE = "event_names";
    public static final String TABLE_CHAT_HISTORY = "chat_history";
    public static final String TABLE_CHAT_CONTACTS = "chat_contacts";

    //Columns
    public static final String COLUMN_CHAT_ID = "_id";
    public static final String COLUMN_CHAT_USER_ID = "user_id";
    public static final String COLUMN_CHAT_TIMESTAMP = "msg_timestamp";
    public static final String COLUMN_CHAT_MESSAGE = "message";
    public static final String COLUMN_CHAT_NAME = "name";

    private static final String CREATE_WORLD_TABLE = "create table " + WORLD_NAMES_TABLE + " (_id integer, name text not null);";
    private static final String CREATE_MAP_TABLE = "create table " + MAP_NAMES_TABLE + " (_id integer, name text not null);";
    private static final String CREATE_EVENT_TABLE = "create table " + EVENT_NAMES_TABLE + " (_id text not null, name text not null, lang text not null);";
    private static final String CREATE_CHAT_HISTORY_TABLE = "create table " + TABLE_CHAT_HISTORY + " " +
            "                                                   (" + COLUMN_CHAT_ID + "  integer primary key autoincrement, " +
            COLUMN_CHAT_USER_ID + " text not null, " +
            "                                                   " + COLUMN_CHAT_TIMESTAMP + " date default CURRENT_DATE," +
            "                                                   " + COLUMN_CHAT_MESSAGE + " text not null)";
    private static final String CREATE_CHAT_NAME_TABLE = "create table "+ TABLE_CHAT_CONTACTS +" ("+COLUMN_CHAT_USER_ID+" text not null unique, "+ COLUMN_CHAT_NAME +" text not null)";

    private final static String DATABASE_NAME = "world_names.db";
    private final static int DATABASE_VERSION = 1;

    private static Gw2DB mInstance = null;

    public static Gw2DB getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Gw2DB(context.getApplicationContext());
        }
        return mInstance;
    }

    private Gw2DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_WORLD_TABLE);
        sqLiteDatabase.execSQL(CREATE_MAP_TABLE);
        sqLiteDatabase.execSQL(CREATE_EVENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_CHAT_HISTORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_CHAT_NAME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i1, int i2) {

    }
}
