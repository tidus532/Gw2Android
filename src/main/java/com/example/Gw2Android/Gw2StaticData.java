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
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tidus on 27/06/13.
 */
public class Gw2StaticData extends AsyncTask<Void, Integer, Void>{
    private Context mContext;
    private Gw2DB mDBHelper;
    protected String text;

    public Gw2StaticData(Context context) {
        mContext = context;
        mDBHelper = new Gw2DB(context);
        this.text = new String();
    }

    /**
     * Downloads all static data and stores it in the database.
     */

    private void _delete_all_data() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.delete(Gw2DB.EVENT_NAMES_TABLE, null, null);
        db.delete(Gw2DB.MAP_NAMES_TABLE, null, null);
        db.delete(Gw2DB.WORLD_NAMES_TABLE, null, null);

    }

    private void _update_event_names() {
        if (Gw2JSONDownloader.internetAvailable(mContext)) {
            String result = Gw2JSONDownloader.downloadJSON(Gw2JSONDownloader.event_names_url);

            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.query(Gw2DB.EVENT_NAMES_TABLE, null, null, null, null, null, null);

            JSONArray jsData;

            try {
                jsData = new JSONArray(result);
                Log.d("Gw2", "LENGTH: " + jsData.length());
                Log.d("Gw2", "POCESSING STARTS");
                for (int i = 0; i < jsData.length(); i++) {
                    JSONObject obj = jsData.getJSONObject(i);
                    String id = obj.getString("id");
                    String name = obj.getString("name");
                   //Log.d("Gw2", name);

                    ContentValues row = new ContentValues(3);
                    row.put("_id", id);
                    row.put("name", name);
                    row.put("lang", "en");

                    if (db != null) {
                        db.insert(Gw2DB.EVENT_NAMES_TABLE, null, row);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //TODO: throw exception.
        }
    }

    private void _update_map_names() {
        if (Gw2JSONDownloader.internetAvailable(mContext)) {
            String result = Gw2JSONDownloader.downloadJSON(Gw2JSONDownloader.event_map_names_url);
            SQLiteDatabase db = mDBHelper.getWritableDatabase();

            try {
                JSONArray jsData;
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //TODO: Throw exception.
        }
    }

    private void _update_world_names() {
        if (Gw2JSONDownloader.internetAvailable(mContext)) {
            String result = Gw2JSONDownloader.downloadJSON(Gw2JSONDownloader.event_world_names_url);
            SQLiteDatabase db = mDBHelper.getWritableDatabase();

            try {
                JSONArray jsData;
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //TODO: Throw Exception.
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        this.text = "Downloading event names...";
        publishProgress(0);
        _delete_all_data();
        _update_event_names();

        this.text = "Downloading map names...";
        publishProgress(33);
        _update_map_names();

        this.text = "Downloading world names...";
        publishProgress(66);
        _update_world_names();

        this.text = "Finished";
        publishProgress(100);

        return null;
    }
}
