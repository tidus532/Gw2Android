package com.github.gw2app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.gw2app.chat.Gw2LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

;
//import android.R.*;
//import android.R;

public class MainActivity extends Activity {
    private HashMap<Integer, String> worlds;
    private ArrayList<String> names;

    public final static String GW2_PREFS = "Gw2Preferences";

    protected void onListItemClick(ListView l, View v, int position, long id) {
        String name = this.names.get(position);
        Iterator iter = this.worlds.entrySet().iterator();
        int world_id = 0;
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            if (pairs.getValue().equals(name)) {
                world_id = (Integer) pairs.getKey();
            }
        }

        Log.e("gw2", "Clicked an item " + this.names.get(position) + " " + world_id);
        //Set preference
        SharedPreferences settings = getSharedPreferences(GW2_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("world_id", world_id);
        editor.commit();

        //Launch ChestEventsActivity
        Intent intent = new Intent(this, ChestEventsActivity.class);
        intent.putExtra("world_name", name);
        intent.putExtra("world_id", world_id);
        startActivity(intent);

    }

    private void fillList(ArrayList<String> data, ListView list) {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        list.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //TODO: change this so we check if there is a valid token present before presenting the login screen.
        //Check if user has installed the application already.
        SharedPreferences settings = getSharedPreferences(this.GW2_PREFS, 0);
        boolean installed = settings.getBoolean("Installed", false);
        if(!installed){
            //Call install activity.
            Intent intent = new Intent(this, Gw2LoginActivity.class);
            startActivity(intent);
        }

        /*
        setContentView(R.layout.activity_main);

        //Check if user chose server already.
        int world_id = settings.getInt("world_id", -1);
        Gw2ApiEvents events = new Gw2ApiEvents(this);


        if (world_id == -1) {
            Log.w("gw2", "User has not chosen server yet.");
            //Download static data.
            Gw2StaticData staticData = new Gw2StaticData(this);
            staticData.update();

            this.worlds = events.getWorldNames();

            //Add fixed header.
            TextView header = new TextView(this);

            this.names = new ArrayList<String>(this.worlds.values());
            Collections.sort(names);

            ListView listview = (ListView) findViewById(R.id.listView);

            listview.setOnItemClickListener(this.mOnClickListener);
            this.fillList(names, listview);
        } else {
            Log.w("gw2", "Launching ChestEventActivity.");

            this.worlds = events.getWorldNames();
            String world_name = this.worlds.get(world_id);
            //Launch ChestEventsActivity
            Intent intent = new Intent(this, ChestEventsActivity.class);
            intent.putExtra("world_name", world_name);
            intent.putExtra("world_id", world_id);
            startActivity(intent);
        }*/
    }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            onListItemClick((ListView) parent, v, position, id);
        }
    };

}
