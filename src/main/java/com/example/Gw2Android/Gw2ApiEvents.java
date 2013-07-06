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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tidus on 26/05/13.
 */
public class Gw2ApiEvents extends Gw2ApiBase {
    public final static String LANG_EN = "en";
    public final static String LANG_FR = "fr";
    public final static String LANG_DE = "de";
    public final static String LANG_ES = "es";

    private Gw2DB dbhelper;

    /**
     * @param context
     */
    public Gw2ApiEvents(Context context) {
        super();
        this.dbhelper = new Gw2DB(context);
    }

    /**
     * Language is not supported yet.
     *
     * @param context
     * @param lang
     */
    public Gw2ApiEvents(Context context, String lang) {

    }

    private boolean _hasWorldNamesInDB() {
        SQLiteDatabase db = this.dbhelper.getReadableDatabase();
        Cursor cursor;
        if (db != null) {
            cursor = db.query(Gw2DB.WORLD_NAMES_TABLE, null, null, null, null, null, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        }
        return false;
    }

    private HashMap<Integer, String> _getWorldNamesFromDB() {
        Log.d("Gw2", "Fetching world names from DB.");
        SQLiteDatabase db = this.dbhelper.getReadableDatabase();
        Cursor cursor = db.query(Gw2DB.WORLD_NAMES_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();
        HashMap<Integer, String> worldNames = new HashMap<Integer, String>();
        while (!cursor.isLast()) {
            Integer id = cursor.getInt(0);
            String name = cursor.getString(1);
            worldNames.put(id, name);
            cursor.moveToNext();
        }
        cursor.close();
        return worldNames;
    }

    /**
     * Returns all world names and their id's. T
     * his method keeps a cache in memory and is backed by a database.
     */
    public HashMap<Integer, String> getWorldNames() {
        if (this._hasWorldNamesInDB()) {
            return this._getWorldNamesFromDB();
        }
        return null;
    }

    public String getWorldName(int world_id) {
        if (this._hasWorldNamesInDB()) {
            return this._getWorldNamesFromDB().get(world_id);
        }

        return null;
    }

    private boolean _hasMapNamesInDB() {
        SQLiteDatabase db = this.dbhelper.getReadableDatabase();
        Cursor cursor;
        if (db != null) {
            cursor = db.query(Gw2DB.MAP_NAMES_TABLE, null, null, null, null, null, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        }
        return false;
    }

    private HashMap<Integer, String> _getMapNamesFromDB() {
        Log.d("Gw2", "Fetching map names from DB.");
        SQLiteDatabase db = this.dbhelper.getReadableDatabase();
        Cursor cursor = db.query(Gw2DB.MAP_NAMES_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();
        HashMap<Integer, String> mapNames = new HashMap<Integer, String>();

        while (!cursor.isLast()) {
            Integer id = cursor.getInt(0);
            String name = cursor.getString(1);
            mapNames.put(id, name);
            cursor.moveToNext();
        }
        cursor.close();
        return mapNames;
    }

    /**
     * Get all map names with their id.
     *
     * @return
     */
    public HashMap<Integer, String> getMapNames() {

        if (this._hasMapNamesInDB()) {
            return this._getMapNamesFromDB();
        }

        return null;
    }

    /**
     * Returns the map name given a map id.
     *
     * @param map_id
     * @return
     */

    public String getMapName(int map_id) {
        if (this._hasMapNamesInDB()) {
            return this._getMapNamesFromDB().get(map_id);
        }

        return null;
    }

    private List<Gw2Event> _getEvents(String url) {
        Log.d("Gw2", "Fetching event names from DB.");
        SQLiteDatabase db = this.dbhelper.getWritableDatabase();
        Cursor cursor = db.query(Gw2DB.EVENT_NAMES_TABLE, null, null, null, null, null, null);
        HashMap<String, String> eventNames = new HashMap<String, String>();

        if (cursor.getCount() == 0) {
           cursor.close();
           return new ArrayList<Gw2Event>();
        }

        cursor.moveToFirst();
        while (!cursor.isLast()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            eventNames.put(id, name);
            cursor.moveToNext();
        }
        cursor.close();

        Log.d("Gw2", "Fetching event data from JSON");
        ArrayList<Gw2Event> list = new ArrayList<Gw2Event>();
        try {
            //Log.d("Gw2", url);
            String result = this.fetchJSONfromURL(url);
            JSONObject jsData = new JSONObject(result);
            JSONArray jsArray = jsData.getJSONArray("events");

            for (int i = 0; i < jsArray.length(); i++) {
                JSONObject obj = jsArray.getJSONObject(i);
                int world_id = obj.getInt("world_id");
                int map_id = obj.getInt("map_id");
                String event_id = obj.getString("event_id");
                String state = obj.getString("state");
                list.add(new Gw2Event(world_id, map_id, event_id, state, eventNames.get(event_id)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Fetches event data based on the arguments given.
     * Requesting all events across all servers is not a good idea and will result in out of memory errors.
     *
     * @param event_id
     * @param world_id
     * @param map_id
     * @return
     */
    public List<Gw2Event> getEvents(String event_id, int world_id, int map_id) {
        String final_url = event_url;
        boolean first = true;

        if (event_id != null) {
            final_url = final_url + "?event_id=" + event_id;
            first = false;
        }

        if (world_id > 0) {
            if (first) {
                final_url = final_url + "?world_id=" + world_id;
                first = false;
            } else {
                final_url = final_url + "&world_id=" + world_id;
            }
        }

        if (map_id > 0) {
            if (first) {
                final_url = final_url + "?map_id=" + map_id;
            } else {
                final_url = final_url + "&map_id=" + map_id;
            }
        }

        return this._getEvents(final_url);
    }

    public void getEventsStatic(){

    }
}
