package com.example.Gw2AndroidTest;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.Gw2Android.Gw2ApiEvents;
import com.example.Gw2Android.Gw2DB;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;


/**
 * Created by tidus on 29/05/13.
 */
public class Gw2EventApiTest extends ActivityUnitTestCase {
    Gw2ApiEvents gw2;
    Gw2DB dbhelper;

    public Gw2EventApiTest() {
        super(TestActivity.class);
    }

    @Override
    protected void setUp() {
        Activity activity = getActivity();
        Context context = getInstrumentation().getContext();
        this.gw2 = new Gw2ApiEvents(context);
        this.dbhelper = new Gw2DB(context);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    @SmallTest
    public void testWorldNames() {
        //First call to get world names, should fetch from internet fill DB and populate cache.
        try {
            new WorldNamesTask().execute(gw2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Should fetch data from local cache.
        try {
            new WorldNamesTask().execute(gw2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Invalidate local cache, should fetch from DB.
        try {
            new WorldNamesTask().execute(gw2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Should have rebuild local cache.
        try {
            new WorldNamesTask().execute(gw2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //Checking if data is present in DB.
        SQLiteDatabase db = this.dbhelper.getReadableDatabase();
        Cursor cursor = db.query(Gw2DB.WORLD_NAMES_TABLE, null, null, null, null, null, null);
        HashMap<Integer, String> worldNames = new HashMap<Integer, String>();
        cursor.moveToFirst();
        while (!cursor.isLast() || (cursor.isFirst())) {
            Integer id = cursor.getInt(0);
            String name = cursor.getString(1);
            worldNames.put(id, name);
            cursor.moveToNext();
        }
        assertEquals(worldNames.get(1018), "Northern Shiverpeaks");
        assertEquals(worldNames.get(2105), "Arborstone [FR]");
        cursor.close();


    }

    @SmallTest
    public void testMapNames() {
        //First call, should fetch from JSON.
        try {
            new MapNamesTask().execute(gw2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //First call, should fetch from local cache.
        try {
            new MapNamesTask().execute(gw2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Invalidate local cache

        //Should fetch from DB now.
        try {
            new MapNamesTask().execute(gw2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Should have rebuild local cache.
        try {
            new MapNamesTask().execute(gw2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    private class WorldNamesTask extends AsyncTask<Gw2ApiEvents, Void, HashMap<Integer, String>> {

        @Override
        protected HashMap<Integer, String> doInBackground(Gw2ApiEvents... gw2) {
            HashMap<Integer, String> worldNames = gw2[0].getWorldNames();
            return worldNames;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, String> worldNames) {
            assertEquals(worldNames.get(1018), "Northern Shiverpeaks");
            assertEquals(worldNames.get(2105), "Arborstone [FR]");
        }
    }

    private class MapNamesTask extends AsyncTask<Gw2ApiEvents, Void, HashMap<Integer, String>> {

        @Override
        protected HashMap<Integer, String> doInBackground(Gw2ApiEvents... gw2) {
            HashMap<Integer, String> worldNames = gw2[0].getMapNames();
            return worldNames;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, String> mapNames) {
            assertEquals("Southsun Cove", mapNames.get(873));
            assertEquals("Mount Maelstrom", mapNames.get(39));
            assertEquals("Blazeridge Steppes", mapNames.get(20));
        }
    }
}

