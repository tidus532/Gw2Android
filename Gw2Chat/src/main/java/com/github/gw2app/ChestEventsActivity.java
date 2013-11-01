package com.github.gw2app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.github.gw2app.events.Gw2ApiEvents;
import com.github.gw2app.events.Gw2Event;

import java.util.List;

;

/**
 * Created by tidus on 26/05/13.
 */
public class ChestEventsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get world_name and world_id
        Intent intent = getIntent();
        String world_name = intent.getStringExtra("world_name");
        Integer id = intent.getIntExtra("world_id", 0);

        setContentView(R.layout.chest_events);
        TextView textView = (TextView) findViewById(R.id.worldName);
        textView.setText(world_name);

        ListView listView = (ListView) findViewById(R.id.eventList);
        Gw2ApiEvents eventAPI = new Gw2ApiEvents(this);
        List<Gw2Event> eventList = eventAPI.getEvents(null, id, -1);
        Gw2EventsAdapter adapter = new Gw2EventsAdapter(eventList, this);

        listView.setAdapter(adapter);

    }
}