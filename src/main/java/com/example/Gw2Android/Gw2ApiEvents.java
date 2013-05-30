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
    private SQLiteDatabase db;

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

    private HashMap<Integer, String> _getWorldNamesFromJSON() {
        Log.d("Gw2", "Fetching world names via JSON.");
        String result = this.fetchJSONfromURL(event_world_names_url);
        db = this.dbhelper.getWritableDatabase();
        HashMap<Integer, String> worldNames = new HashMap<Integer, String>();
        JSONArray jsData;

        try {
            jsData = new JSONArray(result);

            for (int i = 0; i < jsData.length(); i++) {
                JSONObject obj = jsData.getJSONObject(i);
                Integer id = obj.getInt("id");
                String name = obj.getString("name");

                ContentValues row = new ContentValues(2);
                row.put("_id", id);
                row.put("name", name);

                if (db != null) {
                    db.insert(Gw2DB.WORLD_NAMES_TABLE, null, row);
                }

                worldNames.put(id, name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return worldNames;
    }

    /**
     * Returns all world names and their id's. T
     * his method keeps a cache in memory and is backed by a database.
     */
    public HashMap<Integer, String> getWorldNames() {
        if (this._hasWorldNamesInDB()) {
            return this._getWorldNamesFromDB();
        } else {
            return this._getWorldNamesFromJSON();
        }
    }

    public String getWorldName(int world_id) {
        if (this._hasWorldNamesInDB()) {
            return this._getWorldNamesFromDB().get(world_id);
        } else {
            return this._getWorldNamesFromJSON().get(world_id);
        }
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

    private HashMap<Integer, String> _getMapNamesFromJSON() {
        Log.d("Gw2", "Fetching map names from JSON.");
        String result = this.fetchJSONfromURL(event_map_names_url);
        db = this.dbhelper.getWritableDatabase();
        HashMap<Integer, String> mapNames = new HashMap<Integer, String>();
        JSONArray jsData;

        try {
            jsData = new JSONArray(result);

            for (int i = 0; i < jsData.length(); i++) {
                JSONObject obj = jsData.getJSONObject(i);
                Integer id = obj.getInt("id");
                String name = obj.getString("name");

                ContentValues row = new ContentValues(2);
                row.put("_id", id);
                row.put("name", name);

                if (db != null) {
                    db.insert(Gw2DB.MAP_NAMES_TABLE, null, row);
                }

                mapNames.put(id, name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        } else {
            return this._getMapNamesFromJSON();
        }

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
        } else {
            return this._getMapNamesFromJSON().get(map_id);
        }
    }

    private List<Gw2Event> _getEvents(String url) {
        ArrayList<Gw2Event> list = new ArrayList<Gw2Event>();
        try {
            String result = this.fetchJSONfromURL(url);
            JSONObject jsData = new JSONObject(result);
            JSONArray jsArray = jsData.getJSONArray("events");

            for (int i = 0; i < jsArray.length(); i++) {
                JSONObject obj = jsArray.getJSONObject(i);
                int world_id = obj.getInt("world_id");
                int map_id = obj.getInt("map_id");
                String event_id = obj.getString("event_id");
                String state = obj.getString("state");
                list.add(new Gw2Event(world_id, map_id, event_id, state));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Gets all events of a world or map.
     * Set isWorldId to true to get all events of a world.
     * Set isWorldId to false to get all events of a certain map across all worlds.
     * This method is not cached and not backed by a database (state of an event can change).
     *
     * @param id
     * @param isWorldId
     * @return
     */
    public List<Gw2Event> getEvents(int id, boolean isWorldId) {
        if (isWorldId) {
            return this._getEvents(event_url + "?world_id=" + id);
        } else {
            return this._getEvents(event_url + "?map_id=" + id);
        }
    }

    /**
     * Gets a specific event across all worlds.
     *
     * @param event_id
     * @return
     */
    public List<Gw2Event> getEvents(String event_id) {
        return this._getEvents(event_url + "?event_id=" + event_id);
    }

    /**
     * Gets all events on a certain map of a world.
     *
     * @param world_id
     * @param map_id
     * @return
     */
    public List<Gw2Event> getEvents(int world_id, int map_id) {
        return this._getEvents(event_url + "?world_id=" + world_id + "&map_id=" + map_id);
    }

    /**
     * Gets a certain event on a world.
     *
     * @param world_id
     * @param event_id
     * @return
     */
    public List<Gw2Event> getEvents(int world_id, String event_id) {
        return this._getEvents(event_url + "?world_id" + world_id + "&event_id=" + event_id);
    }


}
