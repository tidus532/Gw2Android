package com.example.Gw2Android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tidus on 26/05/13.
 */
public class Gw2DB extends SQLiteOpenHelper {

    public static final String WORLD_NAMES_TABLE = "world_names";
    private static final String DATABASE_CREATE = "create table "+WORLD_NAMES_TABLE+" (_id integer, name text not null);";

    private final static String DATABASE_NAME = "world_names.db";
    private final static int DATABASE_VERSION = 1;

    public Gw2DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
