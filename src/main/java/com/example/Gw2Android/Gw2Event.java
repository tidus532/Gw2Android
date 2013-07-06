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
        along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.example.Gw2Android;

import android.util.Log;

/**
 * Represents an event from the api call.
 */
public class Gw2Event {
    private int worldId;
    private int mapId;
    private String eventId;
    private String state;
    private String name;

    public Gw2Event(){}

    public Gw2Event(int worldId, int mapId, String eventId, String state, String name) {
        this.worldId = worldId;
        this.mapId = mapId;
        this.eventId = eventId;
        this.state = state;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getWorldId() {
        return worldId;
    }

    public int getMapId() {
        return mapId;
    }


    public String getEventId() {
        return eventId;
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object obj){
        Log.d("Gw2", "CALL!");
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Gw2Event))
            return false;

        Gw2Event rhs = (Gw2Event) obj;
        if(rhs.eventId.equals(this.eventId) && rhs.worldId == this.worldId && rhs.mapId == this.mapId){
            Log.d("Gw2", "EQUAL!");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode(){
        return this.eventId.hashCode() + worldId + mapId;
    }

}
