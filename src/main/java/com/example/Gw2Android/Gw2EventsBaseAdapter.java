package com.example.Gw2Android;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import java.util.List;

/**
 * Created by tidus on 25/06/13.
 */
public abstract class Gw2EventsBaseAdapter extends BaseAdapter {
    private List<Gw2Event> events;


    public Gw2EventsBaseAdapter(List<Gw2Event> events){
        super();
        this.events = events;
    }

    @Override
    public int getCount() {
        return this.events.size();
    }

    @Override
    public Object getItem(int i) {
        return this.events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
