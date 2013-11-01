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

package  com.github.gw2app.events;

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
