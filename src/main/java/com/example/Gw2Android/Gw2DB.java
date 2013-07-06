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

package com.example.Gw2Android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tidus on 26/05/13.
 */
public class Gw2DB extends SQLiteOpenHelper {

    public static final String WORLD_NAMES_TABLE = "world_names";
    public static final String MAP_NAMES_TABLE = "map_names";
    public static final String EVENT_NAMES_TABLE = "event_names";

    private static final String CREATE_WORLD_TABLE = "create table "+WORLD_NAMES_TABLE+" (_id integer, name text not null);";
    private static final String CREATE_MAP_TABLE = "create table "+MAP_NAMES_TABLE+" (_id integer, name text not null);";
    private static final String CREATE_EVENT_TABLE = "create table "+EVENT_NAMES_TABLE+" (_id text not null, name text not null, lang text not null);";

    private final static String DATABASE_NAME = "world_names.db";
    private final static int DATABASE_VERSION = 1;

    public Gw2DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_WORLD_TABLE);
        sqLiteDatabase.execSQL(CREATE_MAP_TABLE);
        sqLiteDatabase.execSQL(CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i1, int i2) {

    }
}
