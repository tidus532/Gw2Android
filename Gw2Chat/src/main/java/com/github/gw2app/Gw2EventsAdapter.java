package com.github.gw2app;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.gw2app.events.Gw2Event;
import com.github.gw2app.events.Gw2EventsBaseAdapter;

import java.util.List;

;

/**
 * Created by tidus on 26/06/13.
 */
public class Gw2EventsAdapter extends Gw2EventsBaseAdapter {
    private Context context;
    private List<Gw2Event> events;

    public Gw2EventsAdapter(List<Gw2Event> events, Context context) {
        super(events);
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Gw2Event event = this.events.get(i);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView rowView = (TextView) inflater.inflate(R.layout.simple_list_item_1, viewGroup, false);
        rowView.setText(event.getName());
        return rowView;
    }
}
